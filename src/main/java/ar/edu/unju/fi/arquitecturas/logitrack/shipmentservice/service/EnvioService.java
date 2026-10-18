package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.Envio;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.EnvioRequestDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.EnvioResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.RecursoNoEncontradoException;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.OperacionInvalidaException;

/**
 * Contrato de operaciones de negocio para la gestión integral de la entidad {@link Envio}.
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 17-10-2026
 * @see Envio
 * @see EnvioRequestDto
 * @see EnvioResponseDto
 */

public interface EnvioService {

    /**
     * Actualiza el estado del envío según el código de seguimiento ingresado.
     * En caso del cambio EN_RUTA --> ENTREGADO se requiere del código de recepción para validar que el destinatario es
     * el correcto.
     *
     * @param codigoSeguimiento Código de seguimiento del envío.
     * @param codigoRecepcion Código de recepción del envío. Se requiere solo en caso de querer cambiar el estado a ENTREGADO.
     * @return {@link EnvioResponseDto} Data Transfer Object de respuesta correspondiente a Envío.
     * @throws RecursoNoEncontradoException Lanzada en caso de no encontrar el envío con el código de seguimiento ingresado.
     * @throws OperacionInvalidaException Lanzada si al querer pasar al estado ENTREGADO no se ingresa el código de recepción o el ingresado es incorrecto.
     */
    EnvioResponseDto cambiarEstado(String codigoSeguimiento, String codigoRecepcion);

    /**
     * Localiza un envío a partir de su código de seguimiento.
     *
     * @param codigoSeguimiento Código de seguimiento del envío.
     * @return {@link EnvioResponseDto} Data transfer object de respuesta correspondiente al envío.
     * @throws RecursoNoEncontradoException En caso de que el envío no exista en la BDD.
     */
    EnvioResponseDto obtenerEnvioPorCodigoSeguimiento(String codigoSeguimiento);

}
