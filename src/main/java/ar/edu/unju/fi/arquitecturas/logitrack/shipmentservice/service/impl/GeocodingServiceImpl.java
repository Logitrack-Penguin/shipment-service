package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.impl;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.OperacionInvalidaException;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.RecursoNoEncontradoException;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.interfaces.GeocodingService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Implementación del servicio de geo codificación que utiliza la API pública de Nominatim
 * (OpenStreetMap) para convertir direcciones postales en coordenadas geográficas (latitud y longitud).
 *
 * <p>Esta clase utiliza {@link RestTemplate} para realizar las peticiones HTTP y maneja
 * la respuesta mapeándola a una clase interna de transferencia de datos.</p>
 *
 * @see GeocodingService
 * @see Coordenadas
 */
@Service
public class GeocodingServiceImpl implements GeocodingService {

    /** Cliente HTTP utilizado para realizar las peticiones a la API de Nominatim. */
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Clase interna de apoyo para deserializar la respuesta JSON obtenida
     * desde el servicio de Nominatim.
     */
    private static class NominatimResponse {
        /** Latitud obtenida en formato de cadena. */
        public String lat;

        /** Longitud obtenida en formato de cadena. */
        public String lon;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Coordenadas obtenerCoordenadas(String calle, String numero, String ciudad) {
        String direccionCompleta = String.format("%s %s, %s", calle, numero, ciudad);

        String url = UriComponentsBuilder.fromUriString("https://nominatim.openstreetmap.org/search")
                .queryParam("q", direccionCompleta)
                .queryParam("format", "json")
                .queryParam("limit", 1)
                .toUriString();

        try {
            NominatimResponse[] response = restTemplate.getForObject(url, NominatimResponse[].class);

            if (response != null && response.length > 0) {
                double lat = Double.parseDouble(response[0].lat);
                double lon = Double.parseDouble(response[0].lon);
                return new Coordenadas(lat, lon);
            }
        } catch (Exception e) {
            throw new OperacionInvalidaException("No se pudieron obtener las coordenadas para la dirección: " + direccionCompleta);
        }

        throw new RecursoNoEncontradoException("Dirección no encontrada: " + direccionCompleta);
    }
}
