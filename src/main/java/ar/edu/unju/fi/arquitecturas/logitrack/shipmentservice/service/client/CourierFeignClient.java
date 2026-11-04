package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.client;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.config.CourierFeignConfig;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.response.CourierResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.fallback.CourierClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "courier-service",
        path = "/api/v1/repartidores",
        configuration = CourierFeignConfig.class,
        fallbackFactory = CourierClientFallbackFactory.class)
public interface CourierFeignClient {

    @GetMapping("/{dniRepartidor}")
    CourierResponseDto obtenerPorDni(@PathVariable("dniRepartidor") String dniRepartidor);

    @GetMapping("/{dniRepartidor}/estaDisponible")
    boolean estaDisponible(@PathVariable("dniRepartidor") String dniRepartidor);
}
