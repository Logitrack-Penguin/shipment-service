package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.impl;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.request.EnvioRequestDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.response.ConfigTarifaBaseResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.response.CourierResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.response.EnvioCourierResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.response.EnvioResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.enums.EstadoEnvio;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.CourierNoDisponibleException;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.OperacionInvalidaException;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.RecursoNoEncontradoException;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.Cliente;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.Dimensiones;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.Direccion;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.Envio;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.HistoricoEstadoEnvio;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.repository.ClienteRepository;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.repository.EnvioRepository;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.repository.HistoricoEstadoEnvioRepository;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.client.CourierFeignClient;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.interfaces.ConfigTarifaBaseService;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.interfaces.GeocodingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Suite de pruebas unitarias para la clase {@link EnvioServiceImpl}.
 *
 * <p>
 * Valida la lógica de negocio, el ciclo de estados del envío
 * (PENDIENTE_ASIGNACION → REPARTIDOR_ASIGNADO → EN_RUTA → ENTREGADO, o CANCELADO),
 * el cálculo de tarifas y la asignación de repartidores.
 * La comunicación con courier-service se simula mockeando {@link CourierFeignClient},
 * sin realizar llamadas HTTP reales, incluyendo la respuesta de fallback.
 * </p>
 *
 * @author Logitrack-Penguin
 * @version 1.2.0
 * @since 19-10-2026
 * @see EnvioServiceImpl
 * @see CourierFeignClient
 */
@ExtendWith(MockitoExtension.class)
class EnvioServiceImplTest {

    private static final String CODIGO_SEGUIMIENTO = "TRK-12345678";
    private static final String CODIGO_RECEPCION = "ABC123";
    private static final String DNI_CLIENTE = "40123456";

    @Mock
    private EnvioRepository envioRepository;

    @Mock
    private HistoricoEstadoEnvioRepository historicoEstadoEnvioRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private CourierFeignClient courierFeignClient;

    @Mock
    private GeocodingService geocodingService;

    @Mock
    private ConfigTarifaBaseService configTarifaBaseService;

    @InjectMocks
    private EnvioServiceImpl envioService;

    private Cliente clienteMock;
    private Envio envioMock;
    private EnvioRequestDto requestDto;
    private UUID idEnvio;
    private String dniRepartidor;

    /**
     * Configuración inicial del entorno de prueba antes de la ejecución de cada test.
     */
    @BeforeEach
    void setUp() {
        idEnvio = UUID.randomUUID();
        dniRepartidor = "35123456";

        clienteMock = Cliente.builder()
                .idCliente(UUID.randomUUID())
                .dni(DNI_CLIENTE)
                .nombre("Juan")
                .apellido("Perez")
                .email("juan.perez@example.com")
                .telefono("3881234567")
                .build();

        Dimensiones dimensiones = new Dimensiones(10.0, 10.0, 10.0);

        Direccion direccionOrigen = new Direccion();
        direccionOrigen.setCalle("Av. Bolivia");
        direccionOrigen.setNumero("123");
        direccionOrigen.setCiudad("San Salvador de Jujuy");
        direccionOrigen.setCodigoPostal("4600");

        Direccion direccionDestino = new Direccion();
        direccionDestino.setCalle("Av. Italia");
        direccionDestino.setNumero("456");
        direccionDestino.setCiudad("Palpalá");
        direccionDestino.setCodigoPostal("4612");

        requestDto = EnvioRequestDto.builder()
                .dniCliente(DNI_CLIENTE)
                .peso(5.0)
                .dimensiones(dimensiones)
                .direccionOrigen(direccionOrigen)
                .direccionDestino(direccionDestino)
                .build();

        envioMock = Envio.builder()
                .idEnvio(idEnvio)
                .codigoSeguimiento(CODIGO_SEGUIMIENTO)
                .cliente(clienteMock)
                .peso(5.0)
                .dimensiones(dimensiones)
                .tarifa(550.0)
                .estadoEnvio(EstadoEnvio.PENDIENTE_ASIGNACION)
                .codigoRecepcion(CODIGO_RECEPCION)
                .direccionOrigen(direccionOrigen)
                .direccionDestino(direccionDestino)
                .build();
    }

    /**
     * Arma la respuesta simulada de courier-service para el repartidor de prueba.
     */
    private CourierResponseDto repartidorConEstado(String estado) {
        CourierResponseDto repartidor = new CourierResponseDto();
        repartidor.setIdRepartidor(UUID.randomUUID());
        repartidor.setDni(dniRepartidor);
        repartidor.setNombreCompleto("Gomez, Carlos");
        repartidor.setTelefono("3884567890");
        repartidor.setTipoVehiculo("Motocicleta");
        repartidor.setEstadoRepartidor(estado);
        repartidor.setFallback(false);
        return repartidor;
    }

    @Test
    @DisplayName("Debe registrar un envío en PENDIENTE_ASIGNACION con la tarifa calculada")
    void registrarEnvio_Exitoso() {
        // Given
        when(clienteRepository.findByDni(DNI_CLIENTE)).thenReturn(Optional.of(clienteMock));

        // Tarifa vigente: 500 + 10 * 5kg + 0.05 * 1000cm3 = 600
        ConfigTarifaBaseResponseDto config = new ConfigTarifaBaseResponseDto();
        config.setTarifaBase(500.0);
        config.setCostoPorKg(10.0);
        config.setCostoPorCmCubo(0.05);
        when(configTarifaBaseService.obtenerConfiguracionVigente()).thenReturn(config);

        when(envioRepository.save(any(Envio.class))).thenReturn(envioMock);
        when(historicoEstadoEnvioRepository.save(any(HistoricoEstadoEnvio.class))).thenReturn(new HistoricoEstadoEnvio());

        // When
        EnvioResponseDto response = envioService.registrarEnvio(requestDto);

        // Then
        assertNotNull(response);

        ArgumentCaptor<Envio> envioCaptor = ArgumentCaptor.forClass(Envio.class);
        verify(envioRepository, times(1)).save(envioCaptor.capture());
        Envio envioGuardado = envioCaptor.getValue();
        assertEquals(600.0, envioGuardado.getTarifa(), 0.001);
        assertEquals(EstadoEnvio.PENDIENTE_ASIGNACION, envioGuardado.getEstadoEnvio());

        verify(clienteRepository, times(1)).findByDni(DNI_CLIENTE);
        verify(configTarifaBaseService, times(1)).obtenerConfiguracionVigente();
        verify(historicoEstadoEnvioRepository, times(1)).save(any(HistoricoEstadoEnvio.class));
    }

    @Test
    @DisplayName("Debe lanzar RecursoNoEncontradoException al registrar envío con cliente inexistente")
    void registrarEnvio_LanzaExcepcion_ClienteNoEncontrado() {
        // Given
        when(clienteRepository.findByDni(DNI_CLIENTE)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RecursoNoEncontradoException.class, () -> envioService.registrarEnvio(requestDto));
        verify(envioRepository, never()).save(any(Envio.class));
        verify(historicoEstadoEnvioRepository, never()).save(any(HistoricoEstadoEnvio.class));
    }

    @Test
    @DisplayName("Debe obtener un envío por su ID correctamente")
    void obtenerPorId_Exitoso() {
        // Given
        when(envioRepository.findById(idEnvio)).thenReturn(Optional.of(envioMock));

        // When
        EnvioResponseDto response = envioService.obtenerPorId(idEnvio);

        // Then
        assertNotNull(response);
        assertEquals(CODIGO_SEGUIMIENTO, response.getCodigoSeguimiento());
        verify(envioRepository, times(1)).findById(idEnvio);
    }

    @Test
    @DisplayName("Debe lanzar RecursoNoEncontradoException al buscar un ID inexistente")
    void obtenerPorId_LanzaExcepcion_SiNoExiste() {
        // Given
        when(envioRepository.findById(idEnvio)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RecursoNoEncontradoException.class, () -> envioService.obtenerPorId(idEnvio));
    }

    @Test
    @DisplayName("Debe lanzar OperacionInvalidaException al cambiar estado de un envío sin repartidor asignado")
    void cambiarEstado_LanzaExcepcion_SinRepartidorAsignado() {
        // Given
        envioMock.setEstadoEnvio(EstadoEnvio.PENDIENTE_ASIGNACION);
        when(envioRepository.findByCodigoSeguimiento(CODIGO_SEGUIMIENTO)).thenReturn(Optional.of(envioMock));

        // When & Then
        assertThrows(OperacionInvalidaException.class, () -> envioService.cambiarEstado(CODIGO_SEGUIMIENTO, null));
        verify(envioRepository, never()).save(any(Envio.class));
    }

    @Test
    @DisplayName("Debe cambiar el estado de REPARTIDOR_ASIGNADO a EN_RUTA exitosamente")
    void cambiarEstado_DeRepartidorAsignadoAEnRuta_Exitoso() {
        // Given
        envioMock.setEstadoEnvio(EstadoEnvio.REPARTIDOR_ASIGNADO);
        when(envioRepository.findByCodigoSeguimiento(CODIGO_SEGUIMIENTO)).thenReturn(Optional.of(envioMock));
        when(envioRepository.save(any(Envio.class))).thenReturn(envioMock);
        when(historicoEstadoEnvioRepository.save(any(HistoricoEstadoEnvio.class))).thenReturn(new HistoricoEstadoEnvio());

        // When
        EnvioResponseDto response = envioService.cambiarEstado(CODIGO_SEGUIMIENTO, null);

        // Then
        assertNotNull(response);
        assertEquals(EstadoEnvio.EN_RUTA, response.getEstadoEnvio());
        verify(envioRepository, times(1)).save(envioMock);
        verify(historicoEstadoEnvioRepository, times(1)).save(any(HistoricoEstadoEnvio.class));
    }

    @Test
    @DisplayName("Debe cambiar el estado de EN_RUTA a ENTREGADO con código de recepción válido")
    void cambiarEstado_DeEnRutaAEntregado_Exitoso() {
        // Given
        envioMock.setEstadoEnvio(EstadoEnvio.EN_RUTA);
        when(envioRepository.findByCodigoSeguimiento(CODIGO_SEGUIMIENTO)).thenReturn(Optional.of(envioMock));
        when(envioRepository.save(any(Envio.class))).thenReturn(envioMock);
        when(historicoEstadoEnvioRepository.save(any(HistoricoEstadoEnvio.class))).thenReturn(new HistoricoEstadoEnvio());

        // When
        EnvioResponseDto response = envioService.cambiarEstado(CODIGO_SEGUIMIENTO, CODIGO_RECEPCION);

        // Then
        assertNotNull(response);
        assertEquals(EstadoEnvio.ENTREGADO, response.getEstadoEnvio());
        verify(envioRepository, times(1)).save(envioMock);
    }

    @Test
    @DisplayName("Debe lanzar OperacionInvalidaException si falta el código de recepción al pasar a ENTREGADO")
    void cambiarEstado_DeEnRutaAEntregado_LanzaExcepcion_SiFaltaCodigoRecepcion() {
        // Given
        envioMock.setEstadoEnvio(EstadoEnvio.EN_RUTA);
        when(envioRepository.findByCodigoSeguimiento(CODIGO_SEGUIMIENTO)).thenReturn(Optional.of(envioMock));

        // When & Then
        assertThrows(OperacionInvalidaException.class, () -> envioService.cambiarEstado(CODIGO_SEGUIMIENTO, null));
        verify(envioRepository, never()).save(any(Envio.class));
    }

    @Test
    @DisplayName("Debe lanzar OperacionInvalidaException si el código de recepción es erróneo")
    void cambiarEstado_DeEnRutaAEntregado_LanzaExcepcion_SiCodigoRecepcionIncorrecto() {
        // Given
        envioMock.setEstadoEnvio(EstadoEnvio.EN_RUTA);
        when(envioRepository.findByCodigoSeguimiento(CODIGO_SEGUIMIENTO)).thenReturn(Optional.of(envioMock));

        // When & Then
        assertThrows(OperacionInvalidaException.class, () -> envioService.cambiarEstado(CODIGO_SEGUIMIENTO, "WRONG_CODE"));
        verify(envioRepository, never()).save(any(Envio.class));
    }

    @Test
    @DisplayName("Debe listar envíos filtrados por estado")
    void listarPorEstado_ConEstadoEspecifico() {
        // Given
        when(envioRepository.findByEstadoEnvio(EstadoEnvio.PENDIENTE_ASIGNACION)).thenReturn(List.of(envioMock));

        // When
        List<EnvioResponseDto> resultado = envioService.listarPorEstado(EstadoEnvio.PENDIENTE_ASIGNACION);

        // Then
        assertEquals(1, resultado.size());
        verify(envioRepository, times(1)).findByEstadoEnvio(EstadoEnvio.PENDIENTE_ASIGNACION);
    }

    @Test
    @DisplayName("Debe listar todos los envíos si el estado filtrado es nulo")
    void listarPorEstado_EstadoNull_DevuelveTodos() {
        // Given
        when(envioRepository.findAll()).thenReturn(List.of(envioMock));

        // When
        List<EnvioResponseDto> resultado = envioService.listarPorEstado(null);

        // Then
        assertEquals(1, resultado.size());
        verify(envioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe cancelar un envío en PENDIENTE_ASIGNACION exitosamente")
    void cancelarEnvio_Exitoso_PendienteAsignacion() {
        // Given
        envioMock.setEstadoEnvio(EstadoEnvio.PENDIENTE_ASIGNACION);
        when(envioRepository.findById(idEnvio)).thenReturn(Optional.of(envioMock));
        when(envioRepository.save(any(Envio.class))).thenReturn(envioMock);
        when(historicoEstadoEnvioRepository.save(any(HistoricoEstadoEnvio.class))).thenReturn(new HistoricoEstadoEnvio());

        // When
        EnvioResponseDto response = envioService.cancelarEnvio(idEnvio);

        // Then
        assertEquals(EstadoEnvio.CANCELADO, response.getEstadoEnvio());
        verify(envioRepository, times(1)).save(envioMock);
        verify(historicoEstadoEnvioRepository, times(1)).save(any(HistoricoEstadoEnvio.class));
    }

    @Test
    @DisplayName("Debe cancelar un envío con REPARTIDOR_ASIGNADO exitosamente")
    void cancelarEnvio_Exitoso_RepartidorAsignado() {
        // Given
        envioMock.setEstadoEnvio(EstadoEnvio.REPARTIDOR_ASIGNADO);
        when(envioRepository.findById(idEnvio)).thenReturn(Optional.of(envioMock));
        when(envioRepository.save(any(Envio.class))).thenReturn(envioMock);
        when(historicoEstadoEnvioRepository.save(any(HistoricoEstadoEnvio.class))).thenReturn(new HistoricoEstadoEnvio());

        // When
        EnvioResponseDto response = envioService.cancelarEnvio(idEnvio);

        // Then
        assertEquals(EstadoEnvio.CANCELADO, response.getEstadoEnvio());
        verify(envioRepository, times(1)).save(envioMock);
    }

    @Test
    @DisplayName("Debe lanzar OperacionInvalidaException al cancelar un envío que ya está EN_RUTA")
    void cancelarEnvio_LanzaExcepcion_SiEstaEnRuta() {
        // Given
        envioMock.setEstadoEnvio(EstadoEnvio.EN_RUTA);
        when(envioRepository.findById(idEnvio)).thenReturn(Optional.of(envioMock));

        // When & Then
        assertThrows(OperacionInvalidaException.class, () -> envioService.cancelarEnvio(idEnvio));
        verify(envioRepository, never()).save(any(Envio.class));
        verify(historicoEstadoEnvioRepository, never()).save(any(HistoricoEstadoEnvio.class));
    }

    @Test
    @DisplayName("Debe asignar un repartidor ACTIVO y pasar el envío a REPARTIDOR_ASIGNADO")
    void asignarRepartidor_Exitoso() {
        // Given
        when(envioRepository.findById(idEnvio)).thenReturn(Optional.of(envioMock));
        when(courierFeignClient.obtenerPorDni(dniRepartidor)).thenReturn(repartidorConEstado("ACTIVO"));
        when(envioRepository.save(any(Envio.class))).thenReturn(envioMock);

        // When
        EnvioCourierResponseDto response = envioService.asignarRepartidor(idEnvio, dniRepartidor);

        // Then
        assertNotNull(response);
        assertEquals(dniRepartidor, response.getDniRepartidor());
        assertEquals("Gomez, Carlos", response.getNombreCompletoRepartidor());
        assertEquals(EstadoEnvio.REPARTIDOR_ASIGNADO, response.getEstadoEnvio());
        assertEquals(EstadoEnvio.REPARTIDOR_ASIGNADO, envioMock.getEstadoEnvio());
        assertEquals(dniRepartidor, envioMock.getDniRepartidor());
        verify(courierFeignClient, times(1)).obtenerPorDni(dniRepartidor);
        verify(envioRepository, atLeastOnce()).save(envioMock);
    }

    @Test
    @DisplayName("Debe lanzar OperacionInvalidaException si el envío no está en PENDIENTE_ASIGNACION")
    void asignarRepartidor_LanzaExcepcion_EnvioNoPendiente() {
        // Given
        envioMock.setEstadoEnvio(EstadoEnvio.EN_RUTA);
        when(envioRepository.findById(idEnvio)).thenReturn(Optional.of(envioMock));

        // When & Then
        assertThrows(OperacionInvalidaException.class,
                () -> envioService.asignarRepartidor(idEnvio, dniRepartidor));
        verifyNoInteractions(courierFeignClient);   // ni siquiera llama a courier-service
        verify(envioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar RecursoNoEncontradoException si el envío no existe")
    void asignarRepartidor_LanzaExcepcion_EnvioNoEncontrado() {
        // Given
        when(envioRepository.findById(idEnvio)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RecursoNoEncontradoException.class,
                () -> envioService.asignarRepartidor(idEnvio, dniRepartidor));
        verifyNoInteractions(courierFeignClient);
    }

    @Test
    @DisplayName("Debe lanzar CourierNoDisponibleException si el repartidor no está ACTIVO")
    void asignarRepartidor_LanzaExcepcion_RepartidorNoDisponible() {
        // Given
        when(envioRepository.findById(idEnvio)).thenReturn(Optional.of(envioMock));
        when(courierFeignClient.obtenerPorDni(dniRepartidor)).thenReturn(repartidorConEstado("INACTIVO"));

        // When & Then
        assertThrows(CourierNoDisponibleException.class,
                () -> envioService.asignarRepartidor(idEnvio, dniRepartidor));
        assertEquals(EstadoEnvio.PENDIENTE_ASIGNACION, envioMock.getEstadoEnvio());
        verify(envioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe responder sin interrumpir el flujo cuando courier-service no está disponible (fallback)")
    void asignarRepartidor_CourierNoDisponible_DevuelveRespuestaFallback() {
        // Given: el fallback del Feign client devuelve un DTO marcado como fallback
        CourierResponseDto respuestaFallback = new CourierResponseDto();
        respuestaFallback.setFallback(true);

        when(envioRepository.findById(idEnvio)).thenReturn(Optional.of(envioMock));
        when(courierFeignClient.obtenerPorDni(dniRepartidor)).thenReturn(respuestaFallback);
        lenient().when(envioRepository.save(any(Envio.class))).thenReturn(envioMock);

        // When
        EnvioCourierResponseDto response = assertDoesNotThrow(
                () -> envioService.asignarRepartidor(idEnvio, dniRepartidor));

        // Then: no hay excepción, el envío sigue sin repartidor y se informa el motivo
        assertNotNull(response);
        assertNotNull(response.getMensajeAsignacion());
        assertTrue(response.getMensajeAsignacion().contains("No se ha podido asignar"));
        assertNull(response.getDniRepartidor());
        assertEquals(EstadoEnvio.PENDIENTE_ASIGNACION, envioMock.getEstadoEnvio());
        assertNull(envioMock.getDniRepartidor());
    }
}