package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.impl;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.request.EnvioRequestDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.response.EnvioResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.enums.EstadoEnvio;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.enums.EstadoRepartidor;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.OperacionInvalidaException;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.RecursoNoEncontradoException;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.Cliente;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.Dimensiones;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.Direccion;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.Envio;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.HistoricoEstadoEnvio;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.Repartidor;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.repository.ClienteRepository;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.repository.EnvioRepository;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.repository.HistoricoEstadoEnvioRepository;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.repository.RepartidorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
 * Valida la lógica de negocio, reglas de transición de estado, cálculo de tarifas,
 * orquestación de repositorios y lanzamiento de excepciones de dominio,
 * aislando la capa de servicio mediante dobles de prueba (mocks).
 * </p>
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 19-10-2026
 * @see EnvioServiceImpl
 * @see EnvioRepository
 * @see HistoricoEstadoEnvioRepository
 * @see ClienteRepository
 * @see RepartidorRepository
 */
@ExtendWith(MockitoExtension.class)
class EnvioServiceImplTest {

    @Mock
    private EnvioRepository envioRepository;

    @Mock
    private HistoricoEstadoEnvioRepository historicoEstadoEnvioRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private RepartidorRepository repartidorRepository;

    @InjectMocks
    private EnvioServiceImpl envioService;

    private Cliente clienteMock;
    private Repartidor repartidorMock;
    private Envio envioMock;
    private EnvioRequestDto requestDto;
    private UUID idCliente;
    private UUID idEnvio;
    private UUID idRepartidor;

    /**
     * Configuración inicial del entorno de prueba antes de la ejecución de cada test.
     * Prepara las instancias de objetos de dominio y DTOs simulados alineados a las entidades reales.
     */
    @BeforeEach
    void setUp() {
        idCliente = UUID.randomUUID();
        idEnvio = UUID.randomUUID();
        idRepartidor = UUID.randomUUID();

        clienteMock = Cliente.builder()
                .idCliente(idCliente)
                .dni("40123456")
                .nombre("Juan")
                .apellido("Perez")
                .email("juan.perez@example.com")
                .telefono("3881234567")
                .build();

        repartidorMock = Repartidor.builder()
                .idRepartidor(idRepartidor)
                .dni("35123456")
                .nombre("Carlos")
                .apellido("Gomez")
                .telefono("3884567890")
                .tipoVehiculo("Motocicleta")
                .estadoRepartidor(EstadoRepartidor.ACTIVO)
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
                .idCliente(idCliente)
                .peso(5.0)
                .dimensiones(dimensiones)
                .direccionOrigen(direccionOrigen)
                .direccionDestino(direccionDestino)
                .build();

        envioMock = Envio.builder()
                .idEnvio(idEnvio)
                .codigoSeguimiento("TRK-12345678")
                .cliente(clienteMock)
                .peso(5.0)
                .dimensiones(dimensiones)
                .tarifa(550.0)
                .estadoEnvio(EstadoEnvio.PENDIENTE)
                .codigoRecepcion("ABC123")
                .direccionOrigen(direccionOrigen)
                .direccionDestino(direccionDestino)
                .build();
    }

    /**
     * Valida el registro exitoso de un envío cuando el cliente existe.
     */
    @Test
    @DisplayName("Debe registrar un envío exitosamente cuando el cliente existe")
    void registrarEnvio_Exitoso() {
        // Given
        when(clienteRepository.findById(idCliente)).thenReturn(Optional.of(clienteMock));
        when(envioRepository.save(any(Envio.class))).thenReturn(envioMock);
        when(historicoEstadoEnvioRepository.save(any(HistoricoEstadoEnvio.class))).thenReturn(new HistoricoEstadoEnvio());

        // When
        EnvioResponseDto response = envioService.registrarEnvio(requestDto);

        // Then
        assertNotNull(response);
        assertEquals(EstadoEnvio.PENDIENTE, response.getEstadoEnvio());
        verify(clienteRepository, times(1)).findById(idCliente);
        verify(envioRepository, times(1)).save(any(Envio.class));
        verify(historicoEstadoEnvioRepository, times(1)).save(any(HistoricoEstadoEnvio.class));
    }

    /**
     * Valida que se lance una excepción {@link RecursoNoEncontradoException} al intentar registrar un envío para un cliente inexistente.
     */
    @Test
    @DisplayName("Debe lanzar RecursoNoEncontradoException al registrar envío con cliente inexistente")
    void registrarEnvio_LanzaExcepcion_ClienteNoEncontrado() {
        // Given
        when(clienteRepository.findById(idCliente)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RecursoNoEncontradoException.class, () -> envioService.registrarEnvio(requestDto));

        verify(envioRepository, never()).save(any(Envio.class));
        verify(historicoEstadoEnvioRepository, never()).save(any(HistoricoEstadoEnvio.class));
    }

    /**
     * Valida la consulta exitosa de un envío a partir de su identificador único (UUID).
     */
    @Test
    @DisplayName("Debe obtener un envío por su ID correctamente")
    void obtenerPorId_Exitoso() {
        // Given
        when(envioRepository.findById(idEnvio)).thenReturn(Optional.of(envioMock));

        // When
        EnvioResponseDto response = envioService.obtenerPorId(idEnvio);

        // Then
        assertNotNull(response);
        assertEquals("TRK-12345678", response.getCodigoSeguimiento());
        verify(envioRepository, times(1)).findById(idEnvio);
    }

    /**
     * Valida que se lance una excepción {@link RecursoNoEncontradoException} cuando no exista un envío registrado para el ID consultado.
     */
    @Test
    @DisplayName("Debe lanzar RecursoNoEncontradoException al buscar un ID inexistente")
    void obtenerPorId_LanzaExcepcion_SiNoExiste() {
        // Given
        when(envioRepository.findById(idEnvio)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RecursoNoEncontradoException.class, () -> envioService.obtenerPorId(idEnvio));
    }

    /**
     * Valida la transición de estado válida de PENDIENTE a EN_RUTA sin requerir código de recepción.
     */
    @Test
    @DisplayName("Debe cambiar el estado del envío de PENDIENTE a EN_RUTA exitosamente")
    void cambiarEstado_DePendienteAEnRuta_Exitoso() {
        // Given
        envioMock.setEstadoEnvio(EstadoEnvio.PENDIENTE);
        when(envioRepository.findByCodigoSeguimiento("TRK-12345678")).thenReturn(Optional.of(envioMock));
        when(envioRepository.save(any(Envio.class))).thenReturn(envioMock);
        when(historicoEstadoEnvioRepository.save(any(HistoricoEstadoEnvio.class))).thenReturn(new HistoricoEstadoEnvio());

        // When
        EnvioResponseDto response = envioService.cambiarEstado("TRK-12345678", null);

        // Then
        assertNotNull(response);
        assertEquals(EstadoEnvio.EN_RUTA, response.getEstadoEnvio());
        verify(envioRepository, times(1)).save(envioMock);
        verify(historicoEstadoEnvioRepository, times(1)).save(any(HistoricoEstadoEnvio.class));
    }

    /**
     * Valida la transición de estado válida de EN_RUTA a ENTREGADO requiriendo y verificando el código de recepción correcto.
     */
    @Test
    @DisplayName("Debe cambiar el estado de EN_RUTA a ENTREGADO con código de recepción válido")
    void cambiarEstado_DeEnRutaAEntregado_Exitoso() {
        // Given
        envioMock.setEstadoEnvio(EstadoEnvio.EN_RUTA);
        when(envioRepository.findByCodigoSeguimiento("TRK-12345678")).thenReturn(Optional.of(envioMock));
        when(envioRepository.save(any(Envio.class))).thenReturn(envioMock);
        when(historicoEstadoEnvioRepository.save(any(HistoricoEstadoEnvio.class))).thenReturn(new HistoricoEstadoEnvio());

        // When
        EnvioResponseDto response = envioService.cambiarEstado("TRK-12345678", "ABC123");

        // Then
        assertNotNull(response);
        assertEquals(EstadoEnvio.ENTREGADO, response.getEstadoEnvio());
        verify(envioRepository, times(1)).save(envioMock);
    }

    /**
     * Valida que se lance una excepción {@link OperacionInvalidaException} al intentar pasar de EN_RUTA a ENTREGADO omitiendo el código de recepción.
     */
    @Test
    @DisplayName("Debe lanzar OperacionInvalidaException si falta el código de recepción al pasar a ENTREGADO")
    void cambiarEstado_DeEnRutaAEntregado_LanzaExcepcion_SiFaltaCodigoRecepcion() {
        // Given
        envioMock.setEstadoEnvio(EstadoEnvio.EN_RUTA);
        when(envioRepository.findByCodigoSeguimiento("TRK-12345678")).thenReturn(Optional.of(envioMock));

        // When & Then
        assertThrows(OperacionInvalidaException.class, () -> envioService.cambiarEstado("TRK-12345678", null));

        verify(envioRepository, never()).save(any(Envio.class));
    }

    /**
     * Valida que se lance una excepción {@link OperacionInvalidaException} al proporcionar un código de recepción incorrecto durante la entrega.
     */
    @Test
    @DisplayName("Debe lanzar OperacionInvalidaException si el código de recepción es erróneo")
    void cambiarEstado_DeEnRutaAEntregado_LanzaExcepcion_SiCodigoRecepcionIncorrecto() {
        // Given
        envioMock.setEstadoEnvio(EstadoEnvio.EN_RUTA);
        when(envioRepository.findByCodigoSeguimiento("TRK-12345678")).thenReturn(Optional.of(envioMock));

        // When & Then
        assertThrows(OperacionInvalidaException.class, () -> envioService.cambiarEstado("TRK-12345678", "WRONG_CODE"));

        verify(envioRepository, never()).save(any(Envio.class));
    }

    /**
     * Valida el listado de envíos filtrando por un estado específico.
     */
    @Test
    @DisplayName("Debe listar envíos filtrados por estado")
    void listarPorEstado_ConEstadoEspecifico() {
        // Given
        when(envioRepository.findByEstadoEnvio(EstadoEnvio.PENDIENTE)).thenReturn(List.of(envioMock));

        // When
        List<EnvioResponseDto> resultado = envioService.listarPorEstado(EstadoEnvio.PENDIENTE);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(envioRepository, times(1)).findByEstadoEnvio(EstadoEnvio.PENDIENTE);
    }

    /**
     * Valida el listado completo de todos los envíos registrados cuando el parámetro de estado es nulo.
     */
    @Test
    @DisplayName("Debe listar todos los envíos si el estado filtrado es nulo")
    void listarPorEstado_EstadoNull_DevuelveTodos() {
        // Given
        when(envioRepository.findAll()).thenReturn(List.of(envioMock));

        // When
        List<EnvioResponseDto> resultado = envioService.listarPorEstado(null);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(envioRepository, times(1)).findAll();
    }

    /**
     * Valida la cancelación exitosa de un envío registrado que se encuentra en estado PENDIENTE.
     */
    @Test
    @DisplayName("Debe cancelar un envío exitosamente si se encuentra en estado PENDIENTE")
    void cancelarEnvio_ExitosoSiEstaPendiente() {
        // Given
        envioMock.setEstadoEnvio(EstadoEnvio.PENDIENTE);
        when(envioRepository.findById(idEnvio)).thenReturn(Optional.of(envioMock));
        when(envioRepository.save(any(Envio.class))).thenReturn(envioMock);
        when(historicoEstadoEnvioRepository.save(any(HistoricoEstadoEnvio.class))).thenReturn(new HistoricoEstadoEnvio());

        // When
        EnvioResponseDto response = envioService.cancelarEnvio(idEnvio);

        // Then
        assertNotNull(response);
        assertEquals(EstadoEnvio.CANCELADO, response.getEstadoEnvio());
        verify(envioRepository, times(1)).save(envioMock);
        verify(historicoEstadoEnvioRepository, times(1)).save(any(HistoricoEstadoEnvio.class));
    }

    /**
     * Valida que se lance una excepción {@link OperacionInvalidaException} al intentar cancelar un envío que no está en estado PENDIENTE.
     */
    @Test
    @DisplayName("Debe lanzar OperacionInvalidaException al intentar cancelar un envío que no está PENDIENTE")
    void cancelarEnvio_LanzaExcepcionSiNoEstaPendiente() {
        // Given
        envioMock.setEstadoEnvio(EstadoEnvio.EN_RUTA);
        when(envioRepository.findById(idEnvio)).thenReturn(Optional.of(envioMock));

        // When & Then
        assertThrows(OperacionInvalidaException.class, () -> envioService.cancelarEnvio(idEnvio));

        verify(envioRepository, never()).save(any(Envio.class));
        verify(historicoEstadoEnvioRepository, never()).save(any(HistoricoEstadoEnvio.class));
    }

    // ==========================================
    // NUEVAS PRUEBAS PARA ASIGNACIÓN DE REPARTIDOR
    // ==========================================

    @Test
    @DisplayName("Debe asignar un repartidor activo a un envío PENDIENTE exitosamente")
    void asignarRepartidor_Exitoso() {
        // Given
        envioMock.setEstadoEnvio(EstadoEnvio.PENDIENTE);
        when(envioRepository.findById(idEnvio)).thenReturn(Optional.of(envioMock));
        when(repartidorRepository.findById(idRepartidor)).thenReturn(Optional.of(repartidorMock));
        when(envioRepository.save(any(Envio.class))).thenReturn(envioMock);

        // When
        EnvioResponseDto response = envioService.asignarRepartidor(idEnvio, idRepartidor);

        // Then
        assertNotNull(response);
        verify(envioRepository, times(1)).findById(idEnvio);
        verify(repartidorRepository, times(1)).findById(idRepartidor);
        verify(envioRepository, times(1)).save(envioMock);
    }

    @Test
    @DisplayName("Debe lanzar OperacionInvalidaException si se intenta asignar repartidor a un envío que no está PENDIENTE")
    void asignarRepartidor_LanzaExcepcion_EnvioNoPendiente() {
        // Given
        envioMock.setEstadoEnvio(EstadoEnvio.EN_RUTA);
        when(envioRepository.findById(idEnvio)).thenReturn(Optional.of(envioMock));

        // When & Then
        assertThrows(OperacionInvalidaException.class, () -> envioService.asignarRepartidor(idEnvio, idRepartidor));
        verify(repartidorRepository, never()).findById(any());
        verify(envioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar OperacionInvalidaException si el repartidor no se encuentra ACTIVO")
    void asignarRepartidor_LanzaExcepcion_RepartidorNoActivo() {
        // Given
        envioMock.setEstadoEnvio(EstadoEnvio.PENDIENTE);
        repartidorMock.setEstadoRepartidor(EstadoRepartidor.INACTIVO);

        when(envioRepository.findById(idEnvio)).thenReturn(Optional.of(envioMock));
        when(repartidorRepository.findById(idRepartidor)).thenReturn(Optional.of(repartidorMock));

        // When & Then
        assertThrows(OperacionInvalidaException.class, () -> envioService.asignarRepartidor(idEnvio, idRepartidor));
        verify(envioRepository, never()).save(any());
    }
}