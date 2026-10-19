package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.interfaces;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.request.RepartidorRequestDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.response.RepartidorResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.enums.EstadoRepartidor;

import java.util.List;
import java.util.UUID;

/**
 * Interfaz de la capa de servicio que define el contrato de negocio para la gestión
 * de la entidad {@link ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.Repartidor}.
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 19-10-2026
 * @see RepartidorRequestDto
 * @see RepartidorResponseDto
 */
public interface RepartidorService {

    /**
     * Registra un nuevo repartidor en el sistema validando restricciones de unicidad.
     *
     * @param requestDto Payload con los datos del nuevo repartidor.
     * @return DTO {@link RepartidorResponseDto} con la información del repartidor guardado.
     */
    RepartidorResponseDto registrarRepartidor(RepartidorRequestDto requestDto);

    /**
     * Obtiene los datos detallados de un repartidor a partir de su identificador único.
     *
     * @param idRepartidor Identificador UUID del repartidor.
     * @return DTO {@link RepartidorResponseDto} asociado al ID consultado.
     */
    RepartidorResponseDto obtenerPorId(UUID idRepartidor);

    /**
     * Consulta la lista de repartidores registrados, opcionalmente filtrada por estado operativo.
     *
     * @param estado Estado operativo opcional por el cual filtrar (e.g., ACTIVO, INACTIVO).
     * @return Lista de {@link RepartidorResponseDto}.
     */
    List<RepartidorResponseDto> listarRepartidores(EstadoRepartidor estado);
}