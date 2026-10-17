package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.audit.EntidadAuditable;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

/**
 * Representa al repartidor del envío dentro del sistema. Cada repartidor tiene un listado de los envíos a los que fue
 * asignado. Hereda de {@link EntidadAuditable}
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 16-10-2026
 * @see EntidadAuditable
 * @see Envio
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "repartidores")
public class Repartidor extends EntidadAuditable{

    /**
     * Identificador único universal del repartidor dentro del sistema.
     * <p>
     * El valor se genera automáticamente mediante la configuración
     * {@code @GeneratedValue(strategy = GenerationType.UUID)}.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private UUID idRepartidor;

    /**
     * Documento Nacional de Identidad del repartidor.
     */
    @Column(name = "dni", nullable = false, updatable = false, unique = true)
    private String dni;

    /**
     * Nombre de pila del repartidor.
     */
    @Column(name = "nombre", nullable = false)
    private String nombre;

    /**
     * Apellido del repartidor.
     */
    @Column(name = "apellido", nullable = false)
    private String apellido;

    /**
     * Número telefónico de contacto del repartidor.
     */
    @Column(name = "teléfono", nullable = false, unique = true)
    private String telefono;

    /**
     * Tipo de vehículo de trabajo del repartidor.
     */
    @Column(name = "tipo_vehículo", nullable = false)
    private String tipoVehiculo;

    /**
     * Enumerado del estado del repartidor.
     * @see EstadoRepartidor
     */
    @Column(name = "estado_repartidor", nullable = false)
    private EstadoRepartidor estadoRepartidor;

    /**
     * Relación de 1 a N con Envíos.
     * <ul>
     *     <li>mappedBy = "repartidor": Señala que el campo de repartidor dentro de envío es el dueño dentro de la BDD </li>
     *
     *     <li>fetch = FetchType.LAZY: Estrategia de carga que indica que las entidades relacionadas no se cargarán
     *     de la base de datos hasta que se acceda a ellas explícitamente</li>
     *
     *     <li>cascade = CascadeType.ALL: Cualquier operación que le hagas a la entidad padre se aplicará automáticamente
     *     a las entidades hijas.</li>
     *
     *     <li>List<Envio> enviosAsignados: Listado de los envíos asignados al repartidor.</li>
     * </ul>
     */
    @OneToMany(mappedBy = "repartidor", fetch = FetchType.LAZY)
    private List<Envio> enviosAsignados;
}
