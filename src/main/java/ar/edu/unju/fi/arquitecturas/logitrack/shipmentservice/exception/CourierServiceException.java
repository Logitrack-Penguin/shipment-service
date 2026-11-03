package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception;

import lombok.Getter;

@Getter
public class CourierServiceException extends RuntimeException {
    private final int status;
    public CourierServiceException(String message, int status) {

        super(message);
        this.status = status;
    }
}
