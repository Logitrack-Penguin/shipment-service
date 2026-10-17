package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.Dimensiones;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.Direccion;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.EstadoEnvio;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Objeto de Transferencia de Datos correspondiente los envíos registrados en el sistema. Es un objeto plano que
 * define exactamente qué datos se envían al cliente en una API REST.
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
public class EnvioResponseDto {

    /**
     * Identificador único universal del envío dentro del sistema.
     */
    private UUID idEnvio;

    /**
     * Código único para el seguimiento del envío.
     */
    private String codigoSeguimiento;

    /**
     * Peso registrado en kilogramos del paquete a ser enviado.
     */
    private Double peso;

    /**
     * Dimensiones del paquete en centímetros.
     */
    private Dimensiones dimensiones;

    /**
     * Tarifa correspondiente al envío en ARS.
     */
    private Double tarifa;

    /**
     * Datos del remitente del envío.
     */
    private String remitente;

    /**
     * Datos del repartidor asignado a la entrega.
     */
    private String repartidorAsignado;

    /**
     * Estado actual del envío.
     * @see EstadoEnvio
     */
    private EstadoEnvio estadoEnvio;

    /**
     * Fecha estimada para la entrega del envío.
     * @see LocalDate
     */
    private LocalDate fechaEstimadaEntrega;

    /**
     * Código único para la recepción del envío.
     * <p>
     *     Este mismo permite la actualización: EN_RUTA-->ENTREGADO
     * </p>
     */
    private String codigoRecepcion;

    /**
     * Dirección de origen del envío.
     * @see Direccion
     */
    private Direccion direccionOrigen;

    /**
     * Dirección de destino del envío.
     * @see Direccion
     */
    private Direccion direccionDestino;

    /**
     * Fecha de registro del envío en el sistema. Atributo heredado de la clase padre de auditoría
     * {@link ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.audit.EntidadAuditable}
     */
    private LocalDateTime fechaCreacion;
}
