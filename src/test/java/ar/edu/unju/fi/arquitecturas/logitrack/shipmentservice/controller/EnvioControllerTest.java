package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.controller;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.response.EnvioResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.response.HistoricoEstadoEnvioResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.enums.EstadoEnvio;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.CourierNoDisponibleException;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.OperacionInvalidaException;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.RecursoNoEncontradoException;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.interfaces.EnvioService;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.security.UsuarioActual;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.interfaces.HistoricoEnvioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de la capa web de {@link EnvioController} para los endpoints de LT4-15
 * (cambio de estado, historial y manejo de errores), sin base de datos ni servicios externos.
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 09-11-2026
 */
@WebMvcTest(EnvioController.class)
class EnvioControllerTest {

    private static final String BASE = "/api/v1/envios";
    private static final String CODIGO_SEGUIMIENTO = "TRK-2026-000002";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EnvioService envioService;

    @MockitoBean
    private HistoricoEnvioService historicoEnvioService;

    /** {@code @EnableJpaAuditing} en la clase principal lo requiere; este test no levanta JPA. */
    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    private UUID envioExistente() {
        UUID id = UUID.randomUUID();
        when(envioService.obtenerPorId(id))
                .thenReturn(EnvioResponseDto.builder().idEnvio(id).codigoSeguimiento(CODIGO_SEGUIMIENTO).build());
        return id;
    }

    @Test
    @DisplayName("PATCH /estado con código de recepción correcto devuelve 200 y el envío ENTREGADO")
    void cambiarEstado_entregaConCodigo_devuelve200() throws Exception {
        UUID id = envioExistente();
        when(envioService.cambiarEstado(CODIGO_SEGUIMIENTO, "REC-002"))
                .thenReturn(EnvioResponseDto.builder().estadoEnvio(EstadoEnvio.ENTREGADO).build());

        mockMvc.perform(patch(BASE + "/{id}/estado", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigoRecepcion\":\"REC-002\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoEnvio").value("ENTREGADO"));
    }

    @Test
    @DisplayName("PATCH /estado sin body avanza a EN_RUTA y devuelve 200")
    void cambiarEstado_sinBody_devuelve200() throws Exception {
        UUID id = envioExistente();
        when(envioService.cambiarEstado(CODIGO_SEGUIMIENTO, null))
                .thenReturn(EnvioResponseDto.builder().estadoEnvio(EstadoEnvio.EN_RUTA).build());

        mockMvc.perform(patch(BASE + "/{id}/estado", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoEnvio").value("EN_RUTA"));

        verify(envioService).cambiarEstado(org.mockito.ArgumentMatchers.eq(CODIGO_SEGUIMIENTO), isNull());
    }

    @Test
    @DisplayName("PATCH /estado con código de recepción incorrecto devuelve 400 con mensaje")
    void cambiarEstado_codigoIncorrecto_devuelve400() throws Exception {
        UUID id = envioExistente();
        when(envioService.cambiarEstado(CODIGO_SEGUIMIENTO, "MAL"))
                .thenThrow(new OperacionInvalidaException("El código de recepción ingresado es incorrecto."));

        mockMvc.perform(patch(BASE + "/{id}/estado", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigoRecepcion\":\"MAL\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El código de recepción ingresado es incorrecto."));
    }

    @Test
    @DisplayName("PATCH /estado de un envío inexistente devuelve 404")
    void cambiarEstado_envioInexistente_devuelve404() throws Exception {
        UUID id = UUID.randomUUID();
        when(envioService.obtenerPorId(id)).thenThrow(new RecursoNoEncontradoException("Envío no encontrado con ID: " + id));

        mockMvc.perform(patch(BASE + "/{id}/estado", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Envío no encontrado con ID: " + id));
    }

    @Test
    @DisplayName("Un UUID mal formado en la ruta devuelve 400 en lugar de 500")
    void cambiarEstado_uuidInvalido_devuelve400() throws Exception {
        mockMvc.perform(patch(BASE + "/no-es-un-uuid/estado"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Valor inválido para el parámetro 'idEnvio'."));
    }

    @Test
    @DisplayName("GET /historial devuelve 200 con los cambios de estado")
    void historial_devuelve200ConLista() throws Exception {
        UUID id = UUID.randomUUID();
        when(historicoEnvioService.obtenerHistorial(id)).thenReturn(List.of(
                HistoricoEstadoEnvioResponseDto.builder()
                        .estadoEnvioAnterior(EstadoEnvio.REPARTIDOR_ASIGNADO)
                        .estadoEnvioNuevo(EstadoEnvio.EN_RUTA)
                        .fechaCambioEstado(LocalDateTime.of(2026, 11, 9, 10, 30, 0))
                        .build()));

        mockMvc.perform(get(BASE + "/{id}/historial", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].estadoEnvioAnterior").value("REPARTIDOR_ASIGNADO"))
                .andExpect(jsonPath("$[0].estadoEnvioNuevo").value("EN_RUTA"))
                .andExpect(jsonPath("$[0].fechaCambioEstado").value("2026-11-09 10:30:00"));
    }

    @Test
    @DisplayName("GET /historial de un envío inexistente devuelve 404")
    void historial_envioInexistente_devuelve404() throws Exception {
        UUID id = UUID.randomUUID();
        when(historicoEnvioService.obtenerHistorial(id))
                .thenThrow(new RecursoNoEncontradoException("Envío no encontrado con ID: " + id));

        mockMvc.perform(get(BASE + "/{id}/historial", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Asignar un repartidor que no está ACTIVO devuelve 409 en lugar de 500")
    void asignarRepartidor_inactivo_devuelve409() throws Exception {
        UUID id = UUID.randomUUID();
        CourierNoDisponibleException ex = new CourierNoDisponibleException("28111222", "INACTIVO");
        when(envioService.asignarRepartidor(id, "28111222")).thenThrow(ex);

        mockMvc.perform(put(BASE + "/{id}/asignarRepartidor", id).param("dniRepartidor", "28111222"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value(ex.getMessage()));
    }

    // ---------- Envíos visibles para un REPARTIDOR (cabeceras del api-gateway) ----------

    private static final String DNI_REPARTIDOR = "28111222";

    @Test
    @DisplayName("GET /envios de un REPARTIDOR devuelve solo los asignados a él")
    void listar_repartidor_devuelveSoloLosSuyos() throws Exception {
        when(envioService.listarDeRepartidor(DNI_REPARTIDOR, null))
                .thenReturn(List.of(EnvioResponseDto.builder().codigoSeguimiento(CODIGO_SEGUIMIENTO).build()));

        mockMvc.perform(get(BASE)
                        .header(UsuarioActual.HEADER_ROLES, "REPARTIDOR")
                        .header(UsuarioActual.HEADER_DNI, DNI_REPARTIDOR))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].codigoSeguimiento").value(CODIGO_SEGUIMIENTO));

        verify(envioService, never()).listarPorEstado(any());
    }

    @Test
    @DisplayName("GET /envios?estado= de un REPARTIDOR filtra también por estado")
    void listar_repartidorConEstado_filtraPorEstado() throws Exception {
        when(envioService.listarDeRepartidor(DNI_REPARTIDOR, EstadoEnvio.EN_RUTA)).thenReturn(List.of());

        mockMvc.perform(get(BASE).param("estado", "EN_RUTA")
                        .header(UsuarioActual.HEADER_ROLES, "REPARTIDOR")
                        .header(UsuarioActual.HEADER_DNI, DNI_REPARTIDOR))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /envios de un ADMIN sigue devolviendo todos")
    void listar_admin_devuelveTodos() throws Exception {
        when(envioService.listarPorEstado(isNull())).thenReturn(List.of());

        mockMvc.perform(get(BASE).header(UsuarioActual.HEADER_ROLES, "ADMIN"))
                .andExpect(status().isOk());

        verify(envioService).listarPorEstado(isNull());
        verify(envioService, never()).listarDeRepartidor(any(), any());
    }

    @Test
    @DisplayName("GET /envios/{id} de un envío de otro repartidor devuelve 404")
    void obtenerPorId_envioAjeno_devuelve404() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new RecursoNoEncontradoException("Envío no encontrado con ID: " + id))
                .when(envioService).verificarAcceso(eq(id), any(UsuarioActual.class));

        mockMvc.perform(get(BASE + "/{id}", id)
                        .header(UsuarioActual.HEADER_ROLES, "REPARTIDOR")
                        .header(UsuarioActual.HEADER_DNI, DNI_REPARTIDOR))
                .andExpect(status().isNotFound());

        verify(envioService, never()).obtenerPorId(id);
    }

    @Test
    @DisplayName("GET /envios/tracking/{codigo} de un envío de otro repartidor devuelve 404")
    void tracking_envioAjeno_devuelve404() throws Exception {
        doThrow(new RecursoNoEncontradoException("Envío no encontrado"))
                .when(envioService).verificarAccesoPorCodigo(eq(CODIGO_SEGUIMIENTO), any(UsuarioActual.class));

        mockMvc.perform(get(BASE + "/tracking/{codigo}", CODIGO_SEGUIMIENTO)
                        .header(UsuarioActual.HEADER_ROLES, "REPARTIDOR")
                        .header(UsuarioActual.HEADER_DNI, DNI_REPARTIDOR))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /historial de un envío de otro repartidor devuelve 404")
    void historial_envioAjeno_devuelve404() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new RecursoNoEncontradoException("Envío no encontrado con ID: " + id))
                .when(envioService).verificarAcceso(eq(id), any(UsuarioActual.class));

        mockMvc.perform(get(BASE + "/{id}/historial", id)
                        .header(UsuarioActual.HEADER_ROLES, "REPARTIDOR")
                        .header(UsuarioActual.HEADER_DNI, DNI_REPARTIDOR))
                .andExpect(status().isNotFound());

        verify(historicoEnvioService, never()).obtenerHistorial(id);
    }

    @Test
    @DisplayName("PATCH /estado de un envío de otro repartidor devuelve 404 y no cambia nada")
    void cambiarEstado_envioAjeno_devuelve404() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new RecursoNoEncontradoException("Envío no encontrado con ID: " + id))
                .when(envioService).verificarAcceso(eq(id), any(UsuarioActual.class));

        mockMvc.perform(patch(BASE + "/{id}/estado", id)
                        .header(UsuarioActual.HEADER_ROLES, "REPARTIDOR")
                        .header(UsuarioActual.HEADER_DNI, DNI_REPARTIDOR))
                .andExpect(status().isNotFound());

        verify(envioService, never()).cambiarEstado(any(), any());
    }

    @Test
    @DisplayName("Las cabeceras del gateway llegan al servicio como UsuarioActual")
    void obtenerPorId_pasaLasCabecerasAlServicio() throws Exception {
        UUID id = envioExistente();

        mockMvc.perform(get(BASE + "/{id}", id)
                        .header(UsuarioActual.HEADER_ROLES, "REPARTIDOR")
                        .header(UsuarioActual.HEADER_DNI, DNI_REPARTIDOR))
                .andExpect(status().isOk());

        verify(envioService).verificarAcceso(id, new UsuarioActual(Set.of("REPARTIDOR"), DNI_REPARTIDOR));
    }
}
