package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.response;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.enums.EstadoEnvio;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Contrato de salida con un cambio de estado del historial de un envío.
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 09-11-2026
 * @see ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.HistoricoEstadoEnvio
 */
@Getter
@Builder
public class HistoricoEstadoEnvioResponseDto {

    private EstadoEnvio estadoEnvioAnterior;

    private EstadoEnvio estadoEnvioNuevo;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaCambioEstado;
}
