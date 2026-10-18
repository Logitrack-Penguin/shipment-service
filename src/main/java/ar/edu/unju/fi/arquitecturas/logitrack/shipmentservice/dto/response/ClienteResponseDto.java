package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.response;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.EntidadAuditable;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Objeto de Transferencia de Datos correspondiente los clientes registrados en el sistema. Es un objeto plano que
 * define exactamente qué datos se envían al cliente en una API REST.
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 16-10-2026
 * @see ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.Cliente
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteResponseDto {

    /**
     * Identificador único universal del cliente dentro del sistema.
     */
    private UUID idCliente;

    /**
     * Documento de identificación nacional del cliente.
     */
    private String dni;

    /**
     * Nombre completo del cliente.
     */
    private String nombreCompleto;

    /**
     * Correo Electrónico de contacto del cliente.
     */
    private String email;

    /**
     * Número telefónico de contacto del cliente.
     */
    private String telefono;

    /**
     * Fecha de registro del cliente en el sistema. Atributo heredado de la clase padre de auditoría
     * {@link EntidadAuditable}
     */
    private LocalDateTime fechaCreacion;
}
