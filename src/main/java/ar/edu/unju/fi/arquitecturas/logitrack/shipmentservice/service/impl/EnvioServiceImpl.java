package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.impl;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.response.ConfigTarifaBaseResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.request.EnvioRequestDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.response.EnvioResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.enums.EstadoEnvio;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.OperacionInvalidaException;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.RecursoNoEncontradoException;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.*;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.mapper.EnvioMapper;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.Envio;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.HistoricoEstadoEnvio;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.repository.ClienteRepository;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.repository.EnvioRepository;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.repository.HistoricoEstadoEnvioRepository;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.repository.RepartidorRepository;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.interfaces.ConfigTarifaBaseService;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.interfaces.GeocodingService;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.interfaces.EnvioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Implementación transaccional del servicio de aplicación {@link EnvioService}.
 * <p>
 * Centraliza la orquestación de operaciones de negocio para la gestión de envíos,
 * garantizando integridad transaccional ACID, inmutabilidad de componentes y auditoría vía logs.
 * </p>
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 17-10-2026
 * @see EnvioService
 * @see EnvioRepository
 * @see EnvioMapper
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class EnvioServiceImpl implements EnvioService {

    private final EnvioRepository envioRepository;
    private final HistoricoEstadoEnvioRepository historicoEstadoEnvioRepository;
    private final ClienteRepository clienteRepository;
    private final RepartidorRepository repartidorRepository;
    private final GeocodingService geocodingService;
    private final ConfigTarifaBaseService configTarifaBaseService;
    private final static double radioTierraKm = 6371.0;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public EnvioResponseDto registrarEnvio(EnvioRequestDto request) {
        log.info("Iniciando registro de nuevo envío para cliente ID: {}", request.getIdCliente());

        Cliente cliente = clienteRepository.findById(request.getIdCliente())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con ID: " + request.getIdCliente()));

        String codigoSeguimiento = "TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String codigoRecepcion = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        double volumen = request.getDimensiones().getAncho() * request.getDimensiones().getAlto() * request.getDimensiones().getLargo();
        double tarifa = (calcularTarifa(request.getDireccionOrigen(), request.getDireccionDestino(), request.getPeso(), volumen));

        Envio envio = Envio.builder()
                .codigoSeguimiento(codigoSeguimiento)
                .cliente(cliente)
                .peso(request.getPeso())
                .dimensiones(request.getDimensiones())
                .tarifa(tarifa)
                .estadoEnvio(EstadoEnvio.PENDIENTE)
                .fechaEstimadaEntrega(LocalDate.now().plusDays(3))
                .codigoRecepcion(codigoRecepcion)
                .direccionOrigen(request.getDireccionOrigen())
                .direccionDestino(request.getDireccionDestino())
                .build();

        Envio envioGuardado = envioRepository.save(envio);

        HistoricoEstadoEnvio historico = HistoricoEstadoEnvio.builder()
                .codigoSeguimiento(codigoSeguimiento)
                .estadoEnvioAnterior(EstadoEnvio.PENDIENTE)
                .estadoEnvioNuevo(EstadoEnvio.PENDIENTE)
                .fechaCambioEstado(LocalDateTime.now())
                .build();
        historicoEstadoEnvioRepository.save(historico);

        log.info("Envío registrado exitosamente con código de seguimiento: {}", codigoSeguimiento);
        return EnvioMapper.toResponseDto(envioGuardado);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public EnvioResponseDto obtenerPorId(UUID idEnvio) {
        log.debug("Buscando envío por ID: {}", idEnvio);
        Envio envio = envioRepository.findById(idEnvio)
                .orElseThrow(() -> new RecursoNoEncontradoException("Envío no encontrado con ID: " + idEnvio));
        return EnvioMapper.toResponseDto(envio);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public EnvioResponseDto cambiarEstado(String codigoSeguimiento, String codigoRecepcion) {
        log.info("Cambiando el estado del envío con código de seguimiento: {}", codigoSeguimiento);
        Envio envio = obtenerEnvioEntityPorCodigoSeguimiento(codigoSeguimiento);
        EstadoEnvio estadoEnvioAnterior = envio.getEstadoEnvio();

        if (envio.getEstadoEnvio().equals(EstadoEnvio.PENDIENTE)) {
            envio.setEstadoEnvio(EstadoEnvio.EN_RUTA);
        } else if (envio.getEstadoEnvio().equals(EstadoEnvio.EN_RUTA)) {
            if (codigoRecepcion == null) {
                throw new OperacionInvalidaException("Debe ingresar el código de recepción.");
            } else if (!codigoRecepcion.equals(envio.getCodigoRecepcion())) {
                throw new OperacionInvalidaException("El código de recepción ingresado es incorrecto.");
            }
            envio.setEstadoEnvio(EstadoEnvio.ENTREGADO);
        }

        Envio envioActualizado = envioRepository.save(envio);

        HistoricoEstadoEnvio historico = HistoricoEstadoEnvio.builder()
                .codigoSeguimiento(codigoSeguimiento.trim())
                .estadoEnvioAnterior(estadoEnvioAnterior)
                .estadoEnvioNuevo(envioActualizado.getEstadoEnvio())
                .fechaCambioEstado(LocalDateTime.now())
                .build();

        historicoEstadoEnvioRepository.save(historico);

        return EnvioMapper.toResponseDto(envioActualizado);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public double calcularTarifa(Direccion direccionOrigen, Direccion direccionDestino, double peso, double volumen) {
        var coordenadasOrigen = geocodingService.obtenerCoordenadas(
                direccionOrigen.getCalle(),
                direccionOrigen.getNumero(),
                direccionOrigen.getCiudad()
        );
        var coordenadasDestino = geocodingService.obtenerCoordenadas(
                direccionDestino.getCalle(),
                direccionDestino.getNumero(),
                direccionDestino.getCiudad()
        );
        double distanciaTotalAproximada = calcularDistanciaKm(
                coordenadasOrigen.latitud(),
                coordenadasOrigen.longitud(),
                coordenadasDestino.latitud(),
                coordenadasDestino.longitud()
        );
        ConfigTarifaBaseResponseDto configActualResponseDto = configTarifaBaseService.obtenerConfiguracionVigente();
        return configActualResponseDto.getTarifaBase() +
                configActualResponseDto.getCostoPorKm() * distanciaTotalAproximada +
                configActualResponseDto.getCostoPorKg() * peso +
                configActualResponseDto.getCostoPorCmCubo() * volumen;
    }

    /**
     * {@inheritDoc}
     */

    @Override
    @Transactional(readOnly = true)
    public EnvioResponseDto obtenerEnvioPorCodigoSeguimiento(String codigoSeguimiento) {
        Envio envio = obtenerEnvioEntityPorCodigoSeguimiento(codigoSeguimiento);
        return EnvioMapper.toResponseDto(envio);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<EnvioResponseDto> listarPorEstado(EstadoEnvio estado) {
        log.debug("Listando envíos por estado: {}", estado);
        List<Envio> envios = (estado != null)
                ? envioRepository.findByEstadoEnvio(estado)
                : envioRepository.findAll();

        return envios.stream()
                .map(EnvioMapper::toResponseDto)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public EnvioResponseDto cancelarEnvio(UUID idEnvio) {
        log.info("Iniciando cancelación para el envío con ID: {}", idEnvio);

        Envio envio = envioRepository.findById(idEnvio)
                .orElseThrow(() -> new RecursoNoEncontradoException("Envío no encontrado con ID: " + idEnvio));

        if (!EstadoEnvio.PENDIENTE.equals(envio.getEstadoEnvio())) {
            throw new OperacionInvalidaException("Solo se pueden cancelar envíos en estado PENDIENTE. Estado actual: " + envio.getEstadoEnvio());
        }

        EstadoEnvio estadoAnterior = envio.getEstadoEnvio();
        envio.setEstadoEnvio(EstadoEnvio.CANCELADO);
        Envio envioActualizado = envioRepository.save(envio);

        // Registro del cambio en el histórico de auditoría
        HistoricoEstadoEnvio historico = HistoricoEstadoEnvio.builder()
                .codigoSeguimiento(envio.getCodigoSeguimiento())
                .estadoEnvioAnterior(estadoAnterior)
                .estadoEnvioNuevo(EstadoEnvio.CANCELADO)
                .fechaCambioEstado(LocalDateTime.now())
                .build();
        historicoEstadoEnvioRepository.save(historico);

        log.info("Envío con ID {} cancelado exitosamente", idEnvio);
        return EnvioMapper.toResponseDto(envioActualizado);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public double calcularDistanciaKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return c * radioTierraKm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public EnvioResponseDto asignarRepartidor(UUID idEnvio, UUID idRepartidor) {
        log.info("Iniciando asignación de repartidor ID: {} al envío ID: {}", idRepartidor, idEnvio);

        Envio envio = envioRepository.findById(idEnvio)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el Envío con ID: " + idEnvio));

        if (!EstadoEnvio.PENDIENTE.equals(envio.getEstadoEnvio())) {
            throw new OperacionInvalidaException(
                    "Solo se pueden asignar repartidores a envíos en estado PENDIENTE. Estado actual: " + envio.getEstadoEnvio()
            );
        }

        ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.Repartidor repartidor = repartidorRepository.findById(idRepartidor)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el Repartidor con ID: " + idRepartidor));

        if (!ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.enums.EstadoRepartidor.ACTIVO.equals(repartidor.getEstadoRepartidor())) {
            throw new OperacionInvalidaException(
                    "No se puede asignar un repartidor que no esté en estado ACTIVO. Estado actual: " + repartidor.getEstadoRepartidor()
            );
        }

        envio.setRepartidor(repartidor);
        Envio envioActualizado = envioRepository.save(envio);

        log.info("Repartidor con ID {} asignado exitosamente al envío {}", idRepartidor, idEnvio);
        return EnvioMapper.toResponseDto(envioActualizado);
    }

    /**
     * Busca y recupera la entidad {@link Envio} correspondiente en la base de datos
     * utilizando su código de seguimiento.
     *
     * @param codigoSeguimiento Código de seguimiento único del envío a buscar.
     * @return La instancia de la entidad {@link Envio} encontrada.
     * @throws RecursoNoEncontradoException si no existe ningún envío registrado con el código proporcionado.
     */

    protected Envio obtenerEnvioEntityPorCodigoSeguimiento(String codigoSeguimiento) {
        log.debug("Ejecutando consulta de envío con código de seguimiento: {}", codigoSeguimiento);
        return envioRepository.findByCodigoSeguimiento(codigoSeguimiento)
                .orElseThrow(() -> {
                    log.warn("Búsqueda infructuosa: No existe un envío con el código de seguimiento: {}", codigoSeguimiento);
                    return new RecursoNoEncontradoException("Envío no encontrado");
                });
    }
}