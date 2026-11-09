package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.interfaces;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.response.HistoricoEstadoEnvioResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.RecursoNoEncontradoException;

import java.util.List;
import java.util.UUID;

/**
 * Contrato de consulta del historial de cambios de estado de un envío.
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 09-11-2026
 */
public interface HistoricoEnvioService {

    /**
     * Obtiene los cambios de estado de un envío en orden cronológico.
     *
     * @param idEnvio Identificador del envío.
     * @return Listado de cambios de estado, del más antiguo al más reciente.
     * @throws RecursoNoEncontradoException Si no existe un envío con ese ID.
     */
    List<HistoricoEstadoEnvioResponseDto> obtenerHistorial(UUID idEnvio);
}
