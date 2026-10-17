package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.EstadoRepartidor;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Objeto de Transferencia de Datos correspondiente a los repartidores para la recepción y validación de la información a
 * la hora de registrar un nuevo repartidor o actualizar uno ya existente.
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 16-10-2026
 * @see ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.Repartidor
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepartidorResponseDto {

    /**
     * Identificador único universal del repartidor dentro del sistema.
     */
    private UUID idRepartidor;

    /**
     * Documento de identificación nacional del repartidor.
     */
    private String dni;

    /**
     * Nombre completo del repartidor.
     */
    private String nombreCompleto;

    /**
     * Número telefónico de contacto del repartidor.
     */
    private String telefono;

    /**
     * Tipo de vehículo de trabajo del repartidor.
     */
    private String tipoVehiculo;

    /**
     * Estado actual del repartidor.
     * @see EstadoRepartidor
     */
    private EstadoRepartidor estadoRepartidor;

    /**
     * Fecha de registro del repartidor en el sistema. Atributo heredado de la clase padre de auditoría
     * {@link ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.audit.EntidadAuditable}
     */
    private LocalDateTime fechaCreacion;
}
