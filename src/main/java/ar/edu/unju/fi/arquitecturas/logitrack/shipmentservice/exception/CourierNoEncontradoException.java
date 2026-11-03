package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception;

public class CourierNoEncontradoException extends RuntimeException {
    public CourierNoEncontradoException(String message) {
        super(message);
    }
}
