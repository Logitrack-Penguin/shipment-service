package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.response;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.enums.EstadoEnvio;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.Dimensiones;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.Direccion;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.EntidadAuditable;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnvioCourierResponseDto {
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
     * {@link EntidadAuditable}
     */
    private LocalDateTime fechaCreacionEnvio;

    private String mensajeAsignacion;

    /**
     * Identificador único universal del repartidor dentro del sistema.
     */
    private UUID idRepartidor;

    /**
     * Documento de identificación nacional del repartidor.
     */
    private String dniRepartidor;

    /**
     * Nombre completo del repartidor.
     */
    private String nombreCompletoRepartidor;

    /**
     * Número telefónico de contacto del repartidor.
     */
    private String telefonoRepartidor;

    /**
     * Tipo de vehículo de trabajo del repartidor.
     */
    private String tipoVehiculoRepartidor;
}
