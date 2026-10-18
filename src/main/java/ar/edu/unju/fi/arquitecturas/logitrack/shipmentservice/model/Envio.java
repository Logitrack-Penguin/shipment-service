package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.enums.EstadoEnvio;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Representa a los envios realizados por cada cliente dentro del sistema. Hereda de {@link EntidadAuditable}
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 16-10-2026
 * @see EntidadAuditable
 * @see Cliente
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "envíos")
public class Envio extends EntidadAuditable{

    /**
     * Identificador único universal del envío dentro del sistema.
     * <p>
     * El valor se genera automáticamente mediante la configuración
     * {@code @GeneratedValue(strategy = GenerationType.UUID)}.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private UUID idEnvio;

    /**
     * Código de seguimiento del envío, este mismo es único y no se puede actualizar.
     */
    @Column(name = "código_seguimiento", nullable = false, updatable = false, unique = true)
    private String codigoSeguimiento;

    /**
     * Peso del paquete. Una vez registrado no se puede actualizar.
     */
    @Column(name = "peso", nullable = false, updatable = false)
    private Double peso;

    /**
     * Embebido de las dimensiones del paquete (ancho, largo, alto). Una vez registrada no se puede actualizar.
     * @see Dimensiones
     */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "ancho", column = @Column(name = "ancho", nullable = false, updatable = false, precision = 2)),
            @AttributeOverride(name = "alto", column = @Column(name = "alto", nullable = false, updatable = false, scale = 2)),
            @AttributeOverride(name = "largo", column = @Column(name = "largo", nullable = false, updatable = false, scale = 2))
    })
    private Dimensiones dimensiones;

    /**
     * Tarifa correspondiente al envío en función a la distancia, peso y dimensiones del paquete.
     */
    @Column(name = "tarifa", nullable = false, updatable = false)
    private Double tarifa;

    /**
     * Enumerado del estado del envío.
     * @see EstadoEnvio
     */
    @Column(name = "estado_envío", nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoEnvio estadoEnvio;

    /**
     * Fecha estimada para la entrega del envío.
     * @see LocalDate
     */
    @Column(name = "fecha_estimada_entrega", nullable = false)
    private LocalDate fechaEstimadaEntrega;

    /**
     * Código único de recepción necesario para actualizar el estado del envío a ENTREGADO.
     */
    @Column(name = "código_recepción", nullable = false, updatable = false, unique = true)
    private String codigoRecepcion;

    /**
     * Embebido de la dirección origen del envío. Una vez registrada no se puede modificar a excepción de las
     * observaciones de la misma.
     * @see Direccion
     */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "calle", column = @Column(name = "calle_origen", nullable = false, updatable = false)),
            @AttributeOverride(name = "numero", column = @Column(name = "numero_origen", nullable = false, updatable = false)),
            @AttributeOverride(name = "ciudad", column = @Column(name = "ciudad_origen", nullable = false, updatable = false)),
            @AttributeOverride(name = "codigoPostal", column = @Column(name = "codigo_postal_origen", nullable = false, updatable = false)),
            @AttributeOverride(name = "observaciones", column = @Column(name = "observaciones_origen"))
    })
    private Direccion direccionOrigen;

    /**
     * Embebido de la dirección de destino del envío. Una vez registrada no se puede modificar a excepción de las
     * observaciones de la misma.
     * @see Direccion
     */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "calle", column = @Column(name = "calle_destino", nullable = false, updatable = false)),
            @AttributeOverride(name = "numero", column = @Column(name = "numero_destino", nullable = false, updatable = false)),
            @AttributeOverride(name = "ciudad", column = @Column(name = "ciudad_destino", nullable = false, updatable = false)),
            @AttributeOverride(name = "codigoPostal", column = @Column(name = "codigo_postal_destino", nullable = false, updatable = false)),
            @AttributeOverride(name = "observaciones", column = @Column(name = "observaciones_destino"))
    })
    private Direccion direccionDestino;

    /**
     * Relación N a 1 con la entidad Cliente.
     * <ul>
     *     <li>fetch = FetchType.LAZY: Estrategia de carga que indica que las entidades relacionadas no se cargarán
     *     de la base de datos hasta que se acceda a ellas explícitamente</li>
     *
     *     <li>@JoinColumn: Define el ID del cliente como clave foránea dentro de Envío</li>
     * </ul>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    /**
     * Relación N a 1 con la entidad Repartidor.
     * <ul>
     *     <li>fetch = FetchType.LAZY: Estrategia de carga que indica que las entidades relacionadas no se cargarán
     *     de la base de datos hasta que se acceda a ellas explícitamente</li>
     *
     *     <li>@JoinColumn: Define el ID del repartidor como clave foránea dentro de Envío</li>
     * </ul>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repartidor_id")
    private Repartidor repartidor;
}
