package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.impl;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.enums.EstadoEnvio;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.Envio;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.HistoricoEstadoEnvio;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.repository.EnvioRepository;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.repository.HistoricoEstadoEnvioRepository;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.interfaces.LiberacionEnviosService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementación de {@link LiberacionEnviosService} (LT4-34). La invoca el consumidor de eventos de
 * courier cuando un repartidor se desactiva o se elimina.
 *
 * @author Elías Maciel Villalva
 * @version 1.0.0
 * @since 10-11-2026
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LiberacionEnviosServiceImpl implements LiberacionEnviosService {

    private final EnvioRepository envioRepository;
    private final HistoricoEstadoEnvioRepository historicoEstadoEnvioRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public int liberarEnviosDeRepartidor(String dniRepartidor) {
        List<Envio> envios = envioRepository.findByDniRepartidorAndEstadoEnvio(dniRepartidor,
                EstadoEnvio.REPARTIDOR_ASIGNADO);

        for (Envio envio : envios) {
            envio.setEstadoEnvio(EstadoEnvio.PENDIENTE_ASIGNACION);
            envio.setDniRepartidor(null);
            envioRepository.save(envio);
            historicoEstadoEnvioRepository.save(HistoricoEstadoEnvio.builder()
                    .codigoSeguimiento(envio.getCodigoSeguimiento())
                    .estadoEnvioAnterior(EstadoEnvio.REPARTIDOR_ASIGNADO)
                    .estadoEnvioNuevo(EstadoEnvio.PENDIENTE_ASIGNACION)
                    .fechaCambioEstado(LocalDateTime.now())
                    .build());
        }

        long enRuta = envioRepository.countByDniRepartidorAndEstadoEnvio(dniRepartidor, EstadoEnvio.EN_RUTA);
        if (enRuta > 0) {
            log.warn("El repartidor {} tiene {} envío(s) EN_RUTA que no se modificaron", dniRepartidor, enRuta);
        }
        log.info("Se liberaron {} envío(s) del repartidor {}", envios.size(), dniRepartidor);
        return envios.size();
    }
}
