package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.config;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.CourierNoEncontradoException;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.CourierServiceException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CourierErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();
        log.warn("courier-service respondió {} en {}", status, methodKey);

        return switch (status) {
            case 404 -> new CourierNoEncontradoException(
                    "El repartidor solicitado no existe en courier-service");
            case 500, 502, 503, 504 -> new CourierServiceException(
                    "courier-service respondió con error " + status, status);
            default ->  defaultDecoder.decode(methodKey,response);
        };
    }
}
