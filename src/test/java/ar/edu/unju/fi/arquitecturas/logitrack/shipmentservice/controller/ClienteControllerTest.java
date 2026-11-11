package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.controller;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.response.ClienteResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.security.UsuarioActual;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.interfaces.ClienteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de la capa web de {@link ClienteController}: un CLIENTE solo ve su propia ficha y la
 * búsqueda por email que usa security-service.
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 11-11-2026
 */
@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    private static final String BASE = "/api/v1/clientes";
    private static final String DNI_CLIENTE = "30111222";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClienteService clienteService;

    /** {@code @EnableJpaAuditing} en la clase principal lo requiere; este test no levanta JPA. */
    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @DisplayName("Un CLIENTE ve su propia ficha")
    void obtenerPorDni_clientePropio_devuelve200() throws Exception {
        when(clienteService.buscarPorDni(DNI_CLIENTE))
                .thenReturn(ClienteResponseDto.builder().dni(DNI_CLIENTE).build());

        mockMvc.perform(get(BASE + "/{dni}", DNI_CLIENTE)
                        .header(UsuarioActual.HEADER_ROLES, "CLIENTE")
                        .header(UsuarioActual.HEADER_DNI, DNI_CLIENTE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dni").value(DNI_CLIENTE));
    }

    @Test
    @DisplayName("Un CLIENTE que pide la ficha de otro recibe 404, igual que con un DNI inexistente")
    void obtenerPorDni_clienteAjeno_devuelve404() throws Exception {
        mockMvc.perform(get(BASE + "/{dni}", "38987654")
                        .header(UsuarioActual.HEADER_ROLES, "CLIENTE")
                        .header(UsuarioActual.HEADER_DNI, DNI_CLIENTE))
                .andExpect(status().isNotFound());

        verify(clienteService, never()).buscarPorDni(any());
    }

    @Test
    @DisplayName("ADMIN y las llamadas internas (sin cabeceras) ven cualquier ficha")
    void obtenerPorDni_adminOSinCabeceras_devuelve200() throws Exception {
        when(clienteService.buscarPorDni("38987654"))
                .thenReturn(ClienteResponseDto.builder().dni("38987654").build());

        mockMvc.perform(get(BASE + "/{dni}", "38987654").header(UsuarioActual.HEADER_ROLES, "ADMIN"))
                .andExpect(status().isOk());
        mockMvc.perform(get(BASE + "/{dni}", "38987654"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /clientes?email= busca el cliente por email")
    void listar_conEmail_buscaPorEmail() throws Exception {
        when(clienteService.buscarPorEmail("ana@example.com"))
                .thenReturn(List.of(ClienteResponseDto.builder().dni(DNI_CLIENTE).build()));

        mockMvc.perform(get(BASE).param("email", "ana@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].dni").value(DNI_CLIENTE));

        verify(clienteService, never()).listarClientes();
    }
}
