package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.mapper;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.response.HistoricoEstadoEnvioResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.HistoricoEstadoEnvio;

/**
 * Clase utilitaria que transforma {@link HistoricoEstadoEnvio} en {@link HistoricoEstadoEnvioResponseDto}.
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 09-11-2026
 * @see HistoricoEstadoEnvio
 * @see HistoricoEstadoEnvioResponseDto
 */
public class HistoricoEstadoEnvioMapper {

    /**
     * Constructor privado que previene la instanciación de una clase utilitaria.
     */
    private HistoricoEstadoEnvioMapper() {
        throw new UnsupportedOperationException("HistoricoEstadoEnvioMapper es una clase utilitaria estática y no debe ser instanciada.");
    }

    /**
     * Transforma un registro del historial en su contrato de salida.
     *
     * @param historico Registro persistido de un cambio de estado.
     * @return {@link HistoricoEstadoEnvioResponseDto}, o {@code null} si el registro es nulo.
     */
    public static HistoricoEstadoEnvioResponseDto toResponseDto(HistoricoEstadoEnvio historico) {
        if (historico == null) return null;

        return HistoricoEstadoEnvioResponseDto.builder()
                .estadoEnvioAnterior(historico.getEstadoEnvioAnterior())
                .estadoEnvioNuevo(historico.getEstadoEnvioNuevo())
                .fechaCambioEstado(historico.getFechaCambioEstado())
                .build();
    }
}
