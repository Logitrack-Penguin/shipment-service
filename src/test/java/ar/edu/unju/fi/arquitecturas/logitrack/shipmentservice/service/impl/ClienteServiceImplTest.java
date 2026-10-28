package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.impl;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.request.ClienteRequestDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.response.ClienteResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.RecursoDuplicadoException;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.RecursoNoEncontradoException;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.Cliente;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceImplTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    private ClienteRequestDto requestDto;
    private Cliente clienteEntity;
    private UUID idCliente;

    @BeforeEach
    void setUp() {
        idCliente = UUID.randomUUID();

        requestDto = ClienteRequestDto.builder()
                .dni("40123456")
                .nombre("Juan")
                .apellido("Perez")
                .email("juan.perez@example.com")
                .telefono("3881234567")
                .build();

        clienteEntity = Cliente.builder()
                .idCliente(idCliente)
                .dni("40123456")
                .nombre("Juan")
                .apellido("Perez")
                .email("juan.perez@example.com")
                .telefono("3881234567")
                .build();
    }

    @Test
    @DisplayName("Crear cliente exitosamente cuando no hay duplicados")
    void crearCliente_Exitoso() {
        // Given
        when(clienteRepository.existsByDni(any())).thenReturn(false);
        when(clienteRepository.existsByTelefono(any())).thenReturn(false);
        when(clienteRepository.existsByEmail(any())).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteEntity);

        // When
        ClienteResponseDto response = clienteService.crearCliente(requestDto);

        // Then
        assertNotNull(response);
        assertEquals(idCliente, response.getIdCliente());
        assertEquals("40123456", response.getDni());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Lanzar RecursoDuplicadoException cuando el DNI ya existe")
    void crearCliente_DniDuplicado() {
        // Given
        when(clienteRepository.existsByDni("40123456")).thenReturn(true);

        // When & Then
        assertThrows(RecursoDuplicadoException.class, () -> {
            clienteService.crearCliente(requestDto);
        });

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Lanzar RecursoDuplicadoException cuando el email ya existe")
    void crearCliente_EmailDuplicado() {
        // Given
        when(clienteRepository.existsByDni(any())).thenReturn(false);
        when(clienteRepository.existsByTelefono(any())).thenReturn(false);
        when(clienteRepository.existsByEmail("juan.perez@example.com")).thenReturn(true);

        // When & Then
        assertThrows(RecursoDuplicadoException.class, () -> {
            clienteService.crearCliente(requestDto);
        });

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Buscar cliente por ID exitosamente")
    void buscarPorId_Exitoso() {
        // Given
        when(clienteRepository.findById(idCliente)).thenReturn(Optional.of(clienteEntity));

        // When
        ClienteResponseDto response = clienteService.buscarPorId(idCliente);

        // Then
        assertNotNull(response);
        assertEquals(idCliente, response.getIdCliente());
        verify(clienteRepository, times(1)).findById(idCliente);
    }

    @Test
    @DisplayName("Lanzar RecursoNoEncontradoException cuando el cliente no existe al buscar por ID")
    void buscarPorId_NoEncontrado() {
        // Given
        when(clienteRepository.findById(idCliente)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RecursoNoEncontradoException.class, () -> {
            clienteService.buscarPorId(idCliente);
        });
    }
}