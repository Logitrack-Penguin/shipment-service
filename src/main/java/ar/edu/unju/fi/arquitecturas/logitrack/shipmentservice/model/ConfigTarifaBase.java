package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

/**
 * Entidad destinada a guardar la configuración de las tarifas base y que permite actualizarlas de manera global cuando
 * se desee. Hereda de {@link EntidadAuditable}
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 20-10-2026
 * @see EntidadAuditable
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "configuración_tarifa_base")
public class ConfigTarifaBase extends EntidadAuditable {

    /**
     * Identificador único universal de la configuración dentro del sistema.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idConfigTarifaBase;

    /**
     * Tarifa mínima o base.
     */
    @Column(name = "tarifa_base", precision = 2)
    private double tarifaBase;

    /**
     * Costo en ARS por cada kilogramo del paquete.
     */
    @Column(name = "costo_por_kg", precision = 2)
    private double costoPorKg;

    /**
     * Costo en ARS por cada kilómetro de distancia entre la dirección origen y destino registradas en el envío.
     */
    @Column(name = "costo_por_km", precision = 2)
    private double costoPorKm;

    /**
     * Costo en ARS por cada centímetro cubo del volumen del paquete.
     */
    @Column(name = "costo_por_cm_cubo", precision = 2)
    private double costoPorCmCubo;
}
