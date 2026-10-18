package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.enums;

/**
 * Clase de tipo Enumerado correspondiente a los posibles estados que puede tener un envío.
 * <ul>
 *     <li>PENDIENTE: El envío sigue en el correo de origen sin ser despachado.</li>
 *     <li>EN_RUTA: El envío fue despachado y se encuentra en camino a cargo del repartidor asignado.</li>
 *     <li>ENTREGADO: El envío ya fue recibido por el destinatario.</li>
 *     <li>CANCELADO: El envío fue cancelado por el cliente antes de ser despachado.</li>
 * </ul>
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 16-10-2026
 * @see Enum
 */
public enum EstadoEnvio {
    PENDIENTE,
    EN_RUTA,
    ENTREGADO,
    CANCELADO
}
