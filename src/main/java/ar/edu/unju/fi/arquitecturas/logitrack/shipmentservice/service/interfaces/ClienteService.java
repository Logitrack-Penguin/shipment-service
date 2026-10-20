package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.interfaces;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.request.ClienteRequestDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.response.ClienteResponseDto;

import java.util.List;
import java.util.UUID;

/**
 * <h1>Interfaz de Servicio para la Gestión de Clientes</h1>
 *
 * <p>Define el contrato de operaciones de negocio disponibles para la entidad {@link ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.Cliente}
 * dentro del microservicio <code>shipment-service</code> de la plataforma LogiTrack.</p>
 *
 * <p>Esta interfaz desacopla la capa de presentación (Controladores REST) de la lógica transaccional,
 * garantizando el uso estricto de DTOs tanto para los datos de entrada (Request) como para las respuestas (Response).</p>
 *
 * @author LogiTrack-Penguin
 * @version 1.0
 * @since 2026-10-20
 * @see  ClienteRequestDto
 * @see ClienteResponseDto
 * @see ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.Cliente
 *
 */
public interface ClienteService {

    /**
     * Registra un nuevo cliente en el sistema tras validar las reglas de negocio y restricciones de unicidad.
     *
     * @param requestDto Objeto de transferencia que contiene los datos necesarios para la creación del cliente.
     * @return {@link ClienteResponseDto} con la información detallada del cliente recién creado y su identificador unívoco (UUID).
     * @throws ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.RecursoDuplicadoException si ya existe un cliente registrado con el mismo correo electrónico.
     */
    ClienteResponseDto crearCliente(ClienteRequestDto requestDto);

    /**
     * Busca y recupera la información de un cliente específico a partir de su identificador único.
     *
     * @param id Identificador único universal {@link UUID} del cliente a buscar.
     * @return {@link ClienteResponseDto} con los datos del cliente encontrado.
     * @throws ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.RecursoNoEncontradoException si no se localiza ningún registro asociado al ID proporcionado.
     */
    ClienteResponseDto buscarPorId(UUID id);

    /**
     * Obtiene un listado completo de todos los clientes registrados en la plataforma LogiTrack.
     *
     * @return {@link List} de {@link ClienteResponseDto} que contiene la información de todos los clientes almacenados.
     *         Si no existen registros, retorna una lista vacía.
     */
    List<ClienteResponseDto> listarClientes();

    /**
     * Actualiza la información de un cliente existente identificado por su UUID.
     *
     * @param id Identificador único universal {@link UUID} del cliente que se desea actualizar.
     * @param requestDto Objeto de transferencia con los nuevos datos a modificar.
     * @return {@link ClienteResponseDto} con la información del cliente actualizada.
     * @throws ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.RecursoNoEncontradoException si el cliente a actualizar no existe en el sistema.
     */
    ClienteResponseDto actualizarCliente(UUID id, ClienteRequestDto requestDto);

    /**
     * Elimina de forma permanente un cliente del sistema utilizando su identificador único.
     *
     * @param id Identificador único universal {@link UUID} del cliente a eliminar.
     * @throws ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.RecursoNoEncontradoException si el cliente no es encontrado para su baja.
     */
    void eliminarCliente(UUID id);
}