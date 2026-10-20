package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.response;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.EntidadAuditable;
import lombok.*;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.ConfigTarifaBase;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Objeto de Transferencia de Datos correspondiente la configuración de tarifas base registrada en el sistema. Es un objeto plano que
 * define exactamente qué datos se envían al cliente en una API REST.
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
public class ConfigTarifaBaseResponseDto {

    /**
     * Identificador único universal de la configuración dentro del sistema.
     */
    private UUID idConfigTarifaBase;

    /**
     * Tarifa mínima o base.
     */
    private double tarifaBase;

    /**
     * Costo en ARS por cada kilogramo del paquete.
     */
    private double costoPorKg;

    /**
     * Costo en ARS por cada kilómetro de distancia entre la dirección origen y destino registradas en el envío.
     */
    private double costoPorKm;

    /**
     * Costo en ARS por cada centímetro cubo del volumen del paquete.
     */
    private double costoPorCmCubo;

    /**
     * Fecha de la última modificación/actualización de la configuración de tarifas base en el sistema.
     * {@link EntidadAuditable}
     */
    private LocalDateTime ultimaModificacion;
}
