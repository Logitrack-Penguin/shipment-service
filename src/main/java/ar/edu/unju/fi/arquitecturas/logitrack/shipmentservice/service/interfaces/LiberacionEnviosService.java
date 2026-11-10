package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.interfaces;

/**
 * Contrato para liberar los envíos de un repartidor que se dio de baja en courier-service (LT4-34).
 *
 * @author Elías Maciel Villalva
 * @version 1.0.0
 * @since 10-11-2026
 */
public interface LiberacionEnviosService {

    /**
     * Devuelve a {@code PENDIENTE_ASIGNACION} (y sin repartidor) los envíos de ese DNI que estaban en
     * {@code REPARTIDOR_ASIGNADO}, y registra cada cambio en el historial. Los envíos {@code EN_RUTA}
     * no se tocan (el paquete ya salió): solo se registra una advertencia en el log.
     * <p>
     * Es idempotente: si se llama dos veces con el mismo DNI, la segunda no encuentra envíos para
     * liberar y no hace nada. Por eso no hace falta guardar qué mensajes ya se procesaron.
     * </p>
     *
     * @param dniRepartidor DNI del repartidor dado de baja.
     * @return Cantidad de envíos liberados.
     */
    int liberarEnviosDeRepartidor(String dniRepartidor);
}
