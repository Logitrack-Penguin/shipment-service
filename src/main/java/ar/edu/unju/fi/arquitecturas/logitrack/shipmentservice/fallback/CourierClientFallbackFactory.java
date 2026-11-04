package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.fallback;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.response.CourierResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.CourierNoEncontradoException;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.CourierServiceException;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.client.CourierFeignClient;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class CourierClientFallbackFactory implements FallbackFactory<CourierFeignClient> {

    @Override
    public CourierFeignClient create(Throwable cause) {
        return new CourierFeignClient() {

            @Override
            public CourierResponseDto obtenerPorDni(String dniRepartidor) {
                log.error("Fallback obtenerRepartidorPorDni({}): {}", dniRepartidor, cause.toString());
                return CourierResponseDto.builder()
                        .dni(dniRepartidor)
                        .estadoRepartidor("DESCONOCIDO")
                        .fallback(true)
                        .mensaje(mensaje(cause, dniRepartidor))
                        .build();
            }

            @Override
            public boolean estaDisponible(String dniRepartidor) {
                log.error("Fallback estaDisponible({}): {}", dniRepartidor, cause.toString());
                return false;
            }
        };
    }

    private String mensaje(Throwable cause, String dni) {
        return switch (cause) {
            case CourierNoEncontradoException e -> "El repartidor con DNI " + dni + " no existe";
            case CourierServiceException e      -> "courier-service respondió con error";
            case CallNotPermittedException e    -> "courier-service fuera de servicio temporalmente";
            default                             -> "courier-service no responde";
        };
    }
}
