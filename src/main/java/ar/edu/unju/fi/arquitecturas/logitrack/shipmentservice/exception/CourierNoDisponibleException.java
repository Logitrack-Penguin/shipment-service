package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception;

import lombok.Getter;


@Getter
public class CourierNoDisponibleException extends RuntimeException {

    private final String dniRepartidor;
    private final String estado;

    public CourierNoDisponibleException(String dniRepartidor, String estado) {

        super("El repartidor co n DNI " + dniRepartidor + " no está disponible (estado actual: " + estado + ")");
        this.dniRepartidor = dniRepartidor;
        this.estado = estado;
    }

}
