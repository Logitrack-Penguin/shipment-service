package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.request;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.Dimensiones;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.Direccion;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Objeto de Transferencia de Datos correspondiente a los envíos para la recepción y validación de la información a
 * la hora de registrar un nuevo envío o actualizar uno ya existente.
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 16-10-2026
 * @see ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.Envio
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnvioRequestDto {

    /**
     * Peso registrado en kilogramos del paquete a ser enviado.
     */
    @NotNull(message = "El peso es un campo obligatorio.")
    private Double peso;

    /**
     * Dimensiones del paquete en centímetros.
     */
    @NotNull(message = "Las dimensiones son un campo obligatorio.")
    private Dimensiones dimensiones;

    /**
     * Dirección de origen del envío.
     * @see Direccion
     */
    @NotNull(message = "La dirección de origen es un campo obligatorio.")
    private Direccion direccionOrigen;

    /**
     * Dirección de destino del envío.
     * @see Direccion
     */
    @NotNull(message = "La dirección de destino es un campo obligatorio.")
    private Direccion direccionDestino;
}
