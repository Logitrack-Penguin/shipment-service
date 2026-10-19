package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.interfaces;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.request.EnvioRequestDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.response.EnvioResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.enums.EstadoEnvio;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.OperacionInvalidaException;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.RecursoNoEncontradoException;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.Envio;

import java.util.List;
import java.util.UUID;

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
     * Registra un nuevo envío en el sistema calculando su tarifa e inicializando su estado a PENDIENTE.
     *
     * @param request Payload de entrada con los datos del paquete y las direcciones.
     * @return DTO con la información del envío registrado.
     */
    EnvioResponseDto registrarEnvio(EnvioRequestDto request);

    /**
     * Recupera un envío a partir de su identificador único (UUID).
     *
     * @param idEnvio Identificador único del envío.
     * @return DTO de respuesta del envío encontrado.
     * @throws RecursoNoEncontradoException Si no existe un envío con el ID especificado.
     */
    EnvioResponseDto obtenerPorId(UUID idEnvio);

    /**
     * Actualiza el estado del envío según el código de seguimiento ingresado.
     * <p>
     * En caso del cambio EN_RUTA &rarr; ENTREGADO se requiere del código de recepción para validar que el destinatario es el correcto.
     * </p>
     *
     * @param codigoSeguimiento Código de seguimiento del envío.
     * @param codigoRecepcion Código de recepción del envío. Se requiere solo al pasar a ENTREGADO.
     * @return DTO de respuesta con el estado actualizado.
     * @throws RecursoNoEncontradoException Si no se encuentra el envío.
     * @throws OperacionInvalidaException Si la transición de estado no cumple las reglas de negocio.
     */
    EnvioResponseDto cambiarEstado(String codigoSeguimiento, String codigoRecepcion);

    /**
     * Localiza un envío a partir de su código de seguimiento público.
     *
     * @param codigoSeguimiento Código de seguimiento único del envío.
     * @return DTO de respuesta del envío.
     * @throws RecursoNoEncontradoException Si no existe el código de seguimiento.
     */
    EnvioResponseDto obtenerEnvioPorCodigoSeguimiento(String codigoSeguimiento);

    /**
     * Lista los envíos registrados filtrados opcionalmente por su estado actual.
     *
     * @param estado Estado por el cual filtrar (opcional). Si es null, retorna todos los envíos.
     * @return Lista de DTOs de envíos que cumplen el criterio.
     */
    List<EnvioResponseDto> listarPorEstado(EstadoEnvio estado);

    /**
     * Cancela un envío registrado garantizando que solo se lleve a cabo si se encuentra en estado PENDIENTE.
     *
     * @param idEnvio Identificador único del envío a cancelar.
     * @return DTO de respuesta con el envío en estado CANCELADO.
     * @throws RecursoNoEncontradoException Si el envío no existe.
     * @throws OperacionInvalidaException Si el envío no está en estado PENDIENTE.
     */
    EnvioResponseDto cancelarEnvio(UUID idEnvio);

    /**
     * Asigna un repartidor activo a un envío registrado que se encuentre en estado PENDIENTE.
     *
     * @param idEnvio Identificador único del envío.
     * @param idRepartidor Identificador único del repartidor activo.
     * @return DTO de respuesta con la información del envío actualizado.
     * @throws RecursoNoEncontradoException Si el envío o el repartidor no existen.
     * @throws OperacionInvalidaException Si el envío no está PENDIENTE o el repartidor no está ACTIVO.
     */
    EnvioResponseDto asignarRepartidor(UUID idEnvio, UUID idRepartidor);
}