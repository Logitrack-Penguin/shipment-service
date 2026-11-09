package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.impl;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.response.HistoricoEstadoEnvioResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.RecursoNoEncontradoException;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.mapper.HistoricoEstadoEnvioMapper;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.Envio;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.repository.EnvioRepository;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.repository.HistoricoEstadoEnvioRepository;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.interfaces.HistoricoEnvioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Implementación de la consulta del historial de estados de un envío.
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 09-11-2026
 * @see HistoricoEnvioService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HistoricoEnvioServiceImpl implements HistoricoEnvioService {

    private final EnvioRepository envioRepository;
    private final HistoricoEstadoEnvioRepository historicoEstadoEnvioRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<HistoricoEstadoEnvioResponseDto> obtenerHistorial(UUID idEnvio) {
        log.debug("Buscando historial de estados del envío con ID: {}", idEnvio);
        Envio envio = envioRepository.findById(idEnvio)
                .orElseThrow(() -> new RecursoNoEncontradoException("Envío no encontrado con ID: " + idEnvio));

        return historicoEstadoEnvioRepository
                .findByCodigoSeguimientoOrderByFechaCambioEstadoAsc(envio.getCodigoSeguimiento())
                .stream()
                .map(HistoricoEstadoEnvioMapper::toResponseDto)
                .toList();
    }
}
