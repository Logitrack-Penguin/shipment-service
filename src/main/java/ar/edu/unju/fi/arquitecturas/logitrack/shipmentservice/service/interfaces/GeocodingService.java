package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.interfaces;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.OperacionInvalidaException;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.RecursoNoEncontradoException;

/**
 * Contrato de operaciones de negocio para la geo codificación de las direcciones. El Geocoding es el
 * proceso de transformar una descripción de una ubicación (las direcciones de origen y destino) en coordenadas
 * geográficas (latitud y longitud) que un mapa o sistema informático puede ubicar
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 20-10-2026
 * @see GeocodingService
 */
public interface GeocodingService {

    /**
     * Obtiene las coordenadas geográficas (latitud y longitud) a partir de los datos
     * de una dirección utilizando el servicio de geo codificación de Nominatim.
     *
     * @param calle  el nombre de la calle de la dirección.
     * @param numero el número de la dirección o altura.
     * @param ciudad la ciudad o localidad donde se encuentra la dirección.
     * @return un objeto {@link Coordenadas} que contiene la latitud y longitud correspondientes.
     * @throws OperacionInvalidaException si ocurre un error de red, de parseo o de comunicación con el servicio externo.
     * @throws RecursoNoEncontradoException si la dirección proporcionada no arroja ningún resultado en el servicio.
     */
    Coordenadas obtenerCoordenadas(String calle, String numero, String ciudad);

    /**
     * Record con la latitud y longitud de la coordenada.
     *
     * @param latitud
     * @param longitud
     */
    public record Coordenadas(double latitud, double longitud){}
}
