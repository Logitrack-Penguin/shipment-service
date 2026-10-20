package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.controller;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.request.ClienteRequestDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.response.ClienteResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.interfaces.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para la Gestión de Clientes
 *
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 20-10-2026
 */
@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    /**
     * Endpoint para registrar un nuevo cliente en el sistema.
     *
     * @param request Datos validados del cliente a registrar.
     * @return {@link ResponseEntity} con el {@link ClienteResponseDto} creado y código HTTP 201 (Created).
     */
    @PostMapping
    public ResponseEntity<ClienteResponseDto> crearCliente(@Valid @RequestBody ClienteRequestDto request) {
        ClienteResponseDto nuevoCliente = clienteService.crearCliente(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
    }

    /**
     * Endpoint para consultar un cliente por su identificador único (UUID).
     *
     * @param id Identificador único del cliente.
     * @return {@link ResponseEntity} con los datos del cliente y código HTTP 200 (OK).
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDto> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(clienteService.buscarPorId(id));
    }

    /**
     * Endpoint para listar la totalidad de los clientes registrados en la plataforma.
     *
     * @return {@link ResponseEntity} con la lista de clientes y código HTTP 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<ClienteResponseDto>> listarClientes() {
        return ResponseEntity.ok(clienteService.listarClientes());
    }

    /**
     * Endpoint para actualizar la información de un cliente existente.
     *
     * @param id Identificador único del cliente a modificar.
     * @param request Nuevos datos del cliente.
     * @return {@link ResponseEntity} con el cliente actualizado y código HTTP 200 (OK).
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDto> actualizarCliente(@PathVariable UUID id, @Valid @RequestBody ClienteRequestDto request) {
        return ResponseEntity.ok(clienteService.actualizarCliente(id, request));
    }

    /**
     * Endpoint para eliminar un cliente del sistema por su identificador único.
     *
     * @param id Identificador único del cliente a eliminar.
     * @return {@link ResponseEntity} con código HTTP 204 (No Content) tras la eliminación exitosa.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable UUID id) {
        clienteService.eliminarCliente(id);
        return ResponseEntity.noContent().build();
    }
}
