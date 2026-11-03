package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.config;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

public class CourierFeignConfig {
    @Bean
    public ErrorDecoder courierErrorDecoder() { return new CourierErrorDecoder(); }
}
