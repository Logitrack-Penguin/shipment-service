package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.request;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.ConfigTarifaBase;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Objeto de Transferencia de Datos correspondiente la configuración de tarifas base para la recepción de la información a
 * la hora de actualizar la configuración vigente.
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 20-10-2026
 * @see ConfigTarifaBase
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfigTarifaBaseRequestDto {

    /**
     * Tarifa mínima o base.
     */
    @Size(min = 1, message = "Si envías una nueva tarifa base, no puede estar vacía")
    private double tarifaBase;

    /**
     * Costo en ARS por cada kilogramo del paquete.
     */
    @Size(min = 1, message = "Si envías un nuevo costo por kg, no puede estar vacío")
    private double costoPorKg;

    /**
     * Costo en ARS por cada kilómetro de distancia entre la dirección origen y destino registradas en el envío.
     */
    @Size(min = 1, message = "Si envías un nuevo costo por km, no puede estar vacío")
    private double costoPorKm;

    /**
     * Costo en ARS por cada centímetro cubo del volumen del paquete.
     */
    @Size(min = 1, message = "Si envías un nuevo costo por cm cubo, no puede estar vacío")
    private double costoPorCmCubo;
}
