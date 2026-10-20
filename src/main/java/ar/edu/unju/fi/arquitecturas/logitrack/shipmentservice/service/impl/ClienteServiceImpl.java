package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.impl;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.request.ClienteRequestDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.response.ClienteResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.RecursoDuplicadoException;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.RecursoNoEncontradoException;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.mapper.ClienteMapper;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.Cliente;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.repository.ClienteRepository;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.interfaces.ClienteService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <h1>Implementación de la Lógica de Negocio para Clientes</h1>
 *
 * <p>Esta clase implementa la interfaz {@link ClienteService} y encapsula todas las reglas
 * de negocio transaccionales asociadas a la entidad {@link Cliente} dentro del microservicio
 * <code>shipment-service</code> de la plataforma LogiTrack.</p>
 *
 * <p>Responsabilidades principales:</p>
 * <ul>
 *   <li>Validación de restricciones de unicidad (ej. correo electrónico único).</li>
 *   <li>Mapeo bidireccional entre entidades de persistencia y objetos de transferencia (DTOs).</li>
 *   <li>Gestión de transacciones explícitas con Spring Framework (@Transactional).</li>
 *   <li>Auditoría operativa mediante registros de eventos (SLF4J).</li>
 * </ul>
 *
 * @author Logitrack-Penguin
 * @version 1.0
 * @since 2026-10-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    /**
     * {@inheritDoc}
     *
     * <p><b>Proceso de Creación:</b></p>
     * <ol>
     *   <li>Verifica si el correo electrónico proporcionado ya se encuentra registrado mediante {@link ClienteRepository#existsByEmail(String)}.</li>
     *   <li>Si existe, interrumpe el flujo y lanza una {@link RecursoDuplicadoException}.</li>
     *   <li>Si no existe, convierte el {@link ClienteRequestDto} en una entidad {@link Cliente} utilizando {@link ClienteMapper}.</li>
     *   <li>Persiste la entidad en la base de datos relacional y retorna la representación de respuesta.</li>
     * </ol>
     */
    @Override
    @Transactional
    @Builder
    public ClienteResponseDto crearCliente(ClienteRequestDto requestDto) {
        log.info("Iniciando proceso de registro para nuevo cliente con correo electrónico: {}", requestDto.getEmail());

        /**
         * 1. Validar si ya existe un cliente con el mismo DNI
         */
        if (clienteRepository.existsByDni(requestDto.getDni())) {
            log.warn("Fallo en validación de unicidad: Ya existe un cliente registrado con el DNI [{}]", requestDto.getDni());
            throw new RecursoDuplicadoException("Conflicto de negocio: Ya existe un cliente registrado con el DNI: " + requestDto.getDni());
        }
        /**
         * 2. Validar si ya existe un cliente con el mismo teléfono.
         */
        if (clienteRepository.existsByTelefono(requestDto.getTelefono())) {
            log.warn("Fallo en validación de unicidad: Ya existe un cliente registrado con el teléfono [{}]", requestDto.getTelefono());
            throw new RecursoDuplicadoException("Conflicto de negocio: Ya existe un cliente registrado con el teléfono: " + requestDto.getTelefono());
        }
        /**
         * 3.Validar si ya existe un cliente con el mismo email
         */
        if (clienteRepository.existsByEmail(requestDto.getEmail())) {
            log.warn("Fallo en validación de unicidad: Ya existe un cliente registrado con el email [{}]", requestDto.getEmail());
            throw new RecursoDuplicadoException("Conflicto de negocio: Ya existe un cliente registrado con el correo electrónico: " + requestDto.getEmail());
        }
        Cliente clienteEntity = Cliente.builder()
                .dni(requestDto.getDni())
                .nombre(requestDto.getNombre())
                .apellido(requestDto.getApellido())
                .email(requestDto.getEmail())
                .telefono(requestDto.getTelefono())
                .build();
        Cliente clienteGuardado = clienteRepository.save(clienteEntity);
        log.info("Cliente registrado exitosamente y persistido con ID unívoco (UUID): [{}]", clienteGuardado.getIdCliente());
        return ClienteMapper.toResponseDto(clienteGuardado);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Realiza una búsqueda por clave subrogada de tipo UUID. En caso de no encontrar
     * coincidencia, dispara una {@link RecursoNoEncontradoException} que será capturada
     * globalmente por el {@link ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.GlobalExceptionHandler}.</p>
     */
    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDto buscarPorId(UUID id) {
        log.debug("Consultando base de datos en modo solo lectura para obtener cliente con ID: [{}]", id);

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Entidad no encontrada: No se pudo localizar ningún cliente asociado al ID [{}]", id);
                    return new RecursoNoEncontradoException("Recurso no encontrado: Cliente no registrado con el ID: " + id);
                });

        return ClienteMapper.toResponseDto(cliente);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Recupera la totalidad de los registros de clientes almacenados, transformándolos
     * masivamente a una lista de {@link ClienteResponseDto} mediante Streams de Java.</p>
     */
    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponseDto> listarClientes() {
        log.debug("Ejecutando consulta masiva para listar todos los clientes registrados en el sistema.");

        List<ClienteResponseDto> clientes = clienteRepository.findAll().stream()
                .map(ClienteMapper::toResponseDto)
                .collect(Collectors.toList());

        log.info("Se recuperaron exitosamente [{}] registros de clientes.", clientes.size());
        return clientes;
    }

    /**
     * {@inheritDoc}
     *
     * <p><b>Reglas de Actualización:</b> Valida la existencia previa del registro mediante su ID.
     * Si el cliente existe, actualiza sus propiedades mutables (nombre, email, teléfono) y
     * consolida los cambios en la base de datos.</p>
     */
    @Override
    @Transactional
    public ClienteResponseDto actualizarCliente(UUID id, ClienteRequestDto requestDto) {
        log.info("Iniciando actualización de datos para el cliente con ID: [{}]", id);

        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Fallo operativo de actualización: Cliente inexistente con ID [{}]", id);
                    return new RecursoNoEncontradoException("No se puede actualizar. Cliente no encontrado con el ID: " + id);
                });

        clienteExistente.setNombre(requestDto.getNombre());
        clienteExistente.setEmail(requestDto.getEmail());
        clienteExistente.setTelefono(requestDto.getTelefono());

        Cliente clienteActualizado = clienteRepository.save(clienteExistente);
        log.info("Cliente con ID [{}] actualizado y persistido correctamente.", id);

        return ClienteMapper.toResponseDto(clienteActualizado);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Verifica la existencia del recurso antes de proceder con la baja física en la
     * base de datos relacional.</p>
     */
    @Override
    @Transactional
    public void eliminarCliente(UUID id) {
        log.warn("Solicitud de baja/eliminación física recibida para el cliente con ID: [{}]", id);

        if (!clienteRepository.existsById(id)) {
            log.error("Fallo operativo de eliminación: El cliente con ID [{}] no existe en la base de datos.", id);
            throw new RecursoNoEncontradoException("No se puede eliminar. Cliente no encontrado con el ID: " + id);
        }

        clienteRepository.deleteById(id);
        log.info("Cliente con ID [{}] eliminado permanentemente del sistema.", id);
    }
}