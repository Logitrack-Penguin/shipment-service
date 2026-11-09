package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Contrato de entrada para avanzar el estado de un envío
 * (REPARTIDOR_ASIGNADO → EN_RUTA → ENTREGADO).
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 09-11-2026
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CambioEstadoRequestDto {

    /**
     * Código que el destinatario le entrega al repartidor.
     * Obligatorio solo para pasar de EN_RUTA a ENTREGADO.
     */
    private String codigoRecepcion;
}
