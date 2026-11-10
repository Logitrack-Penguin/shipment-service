package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.messaging;

import java.time.Instant;
import java.util.UUID;

/**
 * Mensaje que courier-service publica cuando un repartidor se desactiva o se elimina.
 * <p>
 * Es un record PROPIO de shipment con los mismos campos que el JSON: shipment no importa clases de
 * courier. Mientras el JSON no cambie ({@code version} 1), un cambio interno en courier no rompe nada.
 * </p>
 *
 * @param eventId    Identificador único del mensaje.
 * @param eventType  {@code repartidor.desactivado} o {@code repartidor.eliminado}.
 * @param version    Versión del formato.
 * @param occurredAt Momento en que courier publicó el evento.
 * @param source     Servicio de origen.
 * @param payload    Datos del repartidor.
 * @author Elías Maciel Villalva
 * @version 1.0.0
 * @since 10-11-2026
 */
public record EventoRepartidor(UUID eventId, String eventType, int version, Instant occurredAt,
                               String source, Payload payload) {

    /**
     * Datos del repartidor afectado.
     *
     * @param dni            DNI del repartidor.
     * @param estadoAnterior Estado previo (ACTIVO o INACTIVO).
     * @param estadoNuevo    Estado nuevo; {@code null} si fue eliminado.
     */
    public record Payload(String dni, String estadoAnterior, String estadoNuevo) {
    }
}
