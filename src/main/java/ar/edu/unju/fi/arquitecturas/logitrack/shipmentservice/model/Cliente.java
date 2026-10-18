package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

/**
 * Representa al Remitente dentro del sistema de gestión y seguimiento de Envíos. Cada Cliente tiene un listado de
 * envíos realizados. Hereda de {@link EntidadAuditable}
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
@Entity
@Builder
@Table(name = "clientes")
public class Cliente extends EntidadAuditable{

    /**
     * Identificador único universal del cliente dentro del sistema.
     * <p>
     * El valor se genera automáticamente mediante la configuración
     * {@code @GeneratedValue(strategy = GenerationType.UUID)}.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID idCliente;

    /**
     * Documento Nacional de Identidad del cliente.
     */
    @Column(name = "dni", nullable = false, updatable = false, unique = true)
    private String dni;

    /**
     * Nombre de pila del cliente.
     */
    @Column(name = "nombre", nullable = false)
    private String nombre;

    /**
     * Apellido del cliente.
     */
    @Column(name = "apellido", nullable = false)
    private String apellido;

    /**
     * Correo Electrónico de contacto del cliente.
     */
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /**
     * Número telefónico de contacto del cliente.
     */
    @Column(name = "teléfono", nullable = false, unique = true)
    private String telefono;

    /**
     * Relación de 1 a N con Envíos.
     * <ul>
     *     <li>mappedBy = "cliente": Señala que el campo de cliente dentro de envío es el dueño dentro de la BDD </li>
     *
     *     <li>fetch = FetchType.LAZY: Estrategia de carga que indica que las entidades relacionadas no se cargarán
     *     de la base de datos hasta que se acceda a ellas explícitamente</li>
     *
     *     <li>cascade = CascadeType.ALL: Cualquier operación que le hagas a la entidad padre se aplicará automáticamente
     *     a las entidades hijas.</li>
     *
     *     <li>List<Envio> envios: Listado de los envíos realizados por el cliente.</li>
     * </ul>
     */
    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Envio> envios;
}
