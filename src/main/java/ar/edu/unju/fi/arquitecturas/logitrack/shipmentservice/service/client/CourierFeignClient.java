package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.client;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.response.CourierResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "repartidores")
public interface CourierFeignClient {

    @RequestMapping(method = RequestMethod.GET, value = "repartidores/{dniRepartidor}", consumes = "application/json")
    public CourierResponseDto obtenerRepartidorPorDni(@PathVariable String dniRepartidor);

    @RequestMapping(method = RequestMethod.GET, value = "/repartidores/{dniRepartidor}/estaDisponible", consumes = "application/json")
    public boolean estaDisponible(@PathVariable String dniRepartidor);
}
