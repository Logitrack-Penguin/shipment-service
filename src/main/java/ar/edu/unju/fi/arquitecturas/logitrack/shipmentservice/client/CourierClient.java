package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.client;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.response.CourierResponseDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import java.util.Optional;

/**
 * Componente encargado de realizar las peticiones HTTP hacia el microservicio de repartidores (courier-service). Manejaremos posibles
 * errores como el 404 (si el repartidor no existe).
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 27-10-2026
 */
@Component
public class CourierClient {

    private final RestClient restClient;

    public CourierClient() {
        this.restClient = RestClient.create("http://localhost:8081");
    }

    public Optional<CourierResponseDto> obtenerRepartidor(String dniRepartidor) {
        try {
            CourierResponseDto courier = restClient.get()
                    .uri("/repartidores/{dniRepartidor}", dniRepartidor)
                    .retrieve()
                    .body(CourierResponseDto.class);

            return Optional.ofNullable(courier);
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }
    }

    public boolean estaDisponible(String dniRepartidor) {
        try {
            Boolean disponibilidad = restClient.get()
                    .uri("/repartidores/{dniRepartidor}/estaDisponible", dniRepartidor)
                    .retrieve()
                    .body(Boolean.class);
            return Boolean.TRUE.equals(disponibilidad);
        } catch (HttpClientErrorException.NotFound e) {
            return Boolean.FALSE;
        }
    }
}
