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
import java.util.Optional;
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
     * <p>Realiza una búsqueda por DNI. En caso de no encontrar
     * coincidencia, dispara una {@link RecursoNoEncontradoException} que será capturada
     * globalmente por el {@link ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.GlobalExceptionHandler}.</p>
     */
    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDto buscarPorDni(String dniCliente) {
        log.debug("Consultando base de datos en modo solo lectura para obtener cliente con DNI: [{}]", dniCliente);

        Cliente cliente = clienteRepository.findByDni(dniCliente)
                .orElseThrow(() -> {
                    log.error("Entidad no encontrada: No se pudo localizar ningún cliente asociado al DNI [{}]", dniCliente);
                    return new RecursoNoEncontradoException("Recurso no encontrado: Cliente no registrado con el DNI: " + dniCliente);
                });

        return ClienteMapper.toResponseDto(cliente);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponseDto> buscarPorEmail(String email) {
        log.debug("Buscando cliente por email");
        return clienteRepository.findByEmailIgnoreCase(email.trim()).stream()
                .map(ClienteMapper::toResponseDto)
                .toList();
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
     * <p><b>Reglas de Actualización:</b> Valida la existencia previa del registro mediante su DNI.
     * Si el cliente existe, actualiza sus propiedades mutables (nombre, email, teléfono) y
     * consolida los cambios en la base de datos.</p>
     */
    @Override
    @Transactional
    public ClienteResponseDto actualizarCliente(String dniCliente, ClienteRequestDto requestDto) {
        log.info("Iniciando actualización de datos para el cliente con DNI: [{}]", dniCliente);

        Cliente clienteExistente = clienteRepository.findByDni(dniCliente)
                .orElseThrow(() -> {
                    log.error("Fallo operativo de actualización: Cliente inexistente con DNI [{}]", dniCliente);
                    return new RecursoNoEncontradoException("No se puede actualizar. Cliente no encontrado con el ID: " + dniCliente);
                });

        clienteExistente.setNombre(requestDto.getNombre());
        clienteExistente.setEmail(requestDto.getEmail());
        clienteExistente.setTelefono(requestDto.getTelefono());

        Cliente clienteActualizado = clienteRepository.save(clienteExistente);
        log.info("Cliente con DNI [{}] actualizado y persistido correctamente.", dniCliente);

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
    public void eliminarCliente(String dniCliente) {
        log.warn("Solicitud de baja/eliminación física recibida para el cliente con DNI: [{}]", dniCliente);

        if (!clienteRepository.existsByDni(dniCliente)) {
            log.error("Fallo operativo de eliminación: El cliente con DNI [{}] no existe en la base de datos.", dniCliente);
            throw new RecursoNoEncontradoException("No se puede eliminar. Cliente no encontrado con el DNI: " + dniCliente);
        }
        Optional<Cliente> clienteAEliminar = clienteRepository.findByDni(dniCliente);
        clienteRepository.deleteById(clienteAEliminar.get().getIdCliente());
        log.info("Cliente con ID [{}] eliminado permanentemente del sistema.", clienteAEliminar.get().getIdCliente());
    }
}