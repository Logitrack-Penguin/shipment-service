package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.impl;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.OperacionInvalidaException;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.RecursoNoEncontradoException;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.interfaces.GeocodingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
@Slf4j
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
        String direccionCompleta = String.format("%s %s, %s, Argentina", calle, numero, ciudad);

        log.info("Ejecutando búsqueda de coordenadas via Nominatim.");
        String url = UriComponentsBuilder.fromUriString("https://nominatim.openstreetmap.org/search")
                .queryParam("street", numero + " " + calle)
                .queryParam("city", ciudad)
                .queryParam("country", "Argentina")
                .queryParam("format", "json")
                .queryParam("limit", 1)
                .queryParam("addressdetails", 1)
                .build()
                .encode()
                .toUriString();

        log.info("Buscando coordenadas para: {}", direccionCompleta);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Shipment-service/V1 (contacto: dyevara4@gmail.com");
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<NominatimResponse[]> responseEntity =
                    restTemplate.exchange(url, HttpMethod.GET, entity, NominatimResponse[].class);

            NominatimResponse[] response = responseEntity.getBody();

            if (response == null || response.length == 0) {
                throw new RecursoNoEncontradoException(
                        "Dirección no encontrada: " + direccionCompleta
                );
            }

            double lat = Double.parseDouble(response[0].lat);
            double lon = Double.parseDouble(response[0].lon);

            return new Coordenadas(lat, lon);
        } catch (HttpStatusCodeException e) {
            log.error(
                    "Error HTTP de Nominatim. Estado: {}",
                    e.getStatusCode()
            );

            throw new OperacionInvalidaException(
                    "Error al consultar el servicio de geo codificación."
            );

        } catch (RestClientException | NumberFormatException e) {
            log.error("Error al obtener las coordenadas", e);

            throw new OperacionInvalidaException(
                    "No se pudieron obtener las coordenadas."
            );
        }

    }
}
