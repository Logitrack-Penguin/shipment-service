package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.controller;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.request.ClienteRequestDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.response.ClienteResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.RecursoNoEncontradoException;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.security.UsuarioActual;
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
     * @param dniCliente DNI del cliente.
     * @return {@link ResponseEntity} con los datos del cliente y código HTTP 200 (OK).
     */
    @GetMapping("/{dniCliente}")
    public ResponseEntity<ClienteResponseDto> obtenerPorDni(@PathVariable String dniCliente,
            @RequestHeader(value = UsuarioActual.HEADER_ROLES, required = false) String roles,
            @RequestHeader(value = UsuarioActual.HEADER_DNI, required = false) String dniUsuario) {
        // Un CLIENTE solo ve su propia ficha: la de otro da el mismo 404 que un DNI inexistente.
        if (!UsuarioActual.desde(roles, dniUsuario).puedeUsarCliente(dniCliente)) {
            throw new RecursoNoEncontradoException(
                    "Recurso no encontrado: Cliente no registrado con el DNI: " + dniCliente);
        }
        return ResponseEntity.ok(clienteService.buscarPorDni(dniCliente));
    }

    /**
     * Endpoint para listar la totalidad de los clientes registrados en la plataforma, o buscar uno por
     * email. El api-gateway solo lo permite a ADMIN; security-service lo llama directamente (sin pasar
     * por el gateway) para completar el DNI de las cuentas CLIENTE anteriores a LT4-40.
     *
     * @param email Email del cliente a buscar (opcional).
     * @return {@link ResponseEntity} con la lista de clientes (o el cliente con ese email) y código
     *         HTTP 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<ClienteResponseDto>> listarClientes(@RequestParam(required = false) String email) {
        if (email != null && !email.isBlank()) {
            return ResponseEntity.ok(clienteService.buscarPorEmail(email));
        }
        return ResponseEntity.ok(clienteService.listarClientes());
    }

    /**
     * Endpoint para actualizar la información de un cliente existente.
     *
     * @param dniCliente DNI del cliente a modificar.
     * @param request Nuevos datos del cliente.
     * @return {@link ResponseEntity} con el cliente actualizado y código HTTP 200 (OK).
     */
    @PutMapping("/{dniCliente}")
    public ResponseEntity<ClienteResponseDto> actualizarCliente(@PathVariable String dniCliente, @Valid @RequestBody ClienteRequestDto request) {
        return ResponseEntity.ok(clienteService.actualizarCliente(dniCliente, request));
    }

    /**
     * Endpoint para eliminar un cliente del sistema por su identificador único.
     *
     * @param dniCliente Documento de Identificación Nacional del cliente a eliminar.
     * @return {@link ResponseEntity} con código HTTP 204 (No Content) tras la eliminación exitosa.
     */
    @DeleteMapping("/{dniCliente}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable String dniCliente) {
        clienteService.eliminarCliente(dniCliente);
        return ResponseEntity.noContent().build();
    }
}
