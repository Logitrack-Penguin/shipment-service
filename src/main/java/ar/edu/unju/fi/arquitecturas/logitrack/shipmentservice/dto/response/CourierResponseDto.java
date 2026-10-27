package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.response;

import lombok.*;
import java.util.UUID;

/**
 * Objeto de Transferencia de Datos correspondiente a los repartidores obtenido tras la consulta a courier-service.
 *
 * @author Logitrack-Penguin
 * @version 1.1.0
 * @since 27-10-2026
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourierResponseDto {

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
     * Booleano que representa la disponibilidad de un repartidor para realizar una entrega.
     */
    private boolean estaDisponible;

}
