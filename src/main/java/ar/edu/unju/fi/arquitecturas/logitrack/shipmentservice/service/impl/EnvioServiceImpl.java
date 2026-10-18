package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.impl;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.EnvioResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.OperacionInvalidaException;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.RecursoNoEncontradoException;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.Envio;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.mapper.EnvioMapper;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.EstadoEnvio;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.HistoricoEstadoEnvio;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.repository.EnvioRepository;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.repository.HistoricoEstadoEnvioRepository;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.EnvioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

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

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public EnvioResponseDto cambiarEstado(String codigoSeguimiento, String codigoRecepcion) {
        log.info("Cambiando el estado del envío con código de seguimiento: {}", codigoSeguimiento);
        Envio envio = obtenerEnvioEntityPorCodigoSeguimiento(codigoSeguimiento);
        EstadoEnvio estadoEnvioAnterior = envio.getEstadoEnvio();
        if (envio.getEstadoEnvio().equals(EstadoEnvio.PENDIENTE)){
            envio.setEstadoEnvio(EstadoEnvio.EN_RUTA);
        } else if (envio.getEstadoEnvio().equals(EstadoEnvio.EN_RUTA)) {
            if(codigoRecepcion == null){
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
    @Transactional(readOnly = true)
    public EnvioResponseDto obtenerEnvioPorCodigoSeguimiento(String codigoSeguimiento) {
        Envio envio = obtenerEnvioEntityPorCodigoSeguimiento(codigoSeguimiento);
        return EnvioMapper.toResponseDto(envio);
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
