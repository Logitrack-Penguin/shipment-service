package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.controller;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.request.CambioEstadoRequestDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.request.EnvioRequestDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.response.EnvioCourierResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.response.EnvioResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.response.HistoricoEstadoEnvioResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.enums.EstadoEnvio;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.security.UsuarioActual;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.interfaces.EnvioService;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.interfaces.HistoricoEnvioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/envios")
@RequiredArgsConstructor
public class EnvioController {

    private final EnvioService envioService;
    private final HistoricoEnvioService historicoEnvioService;

    /**
     * Registrar un nuevo envío en la plataforma.
     */
    @PostMapping
    public ResponseEntity<EnvioResponseDto> registrarEnvio(@Valid @RequestBody EnvioRequestDto request) {
        EnvioResponseDto nuevoEnvio = envioService.registrarEnvio(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoEnvio);
    }

    /**
     * Consultar envío por su identificador único (UUID).
     * Un REPARTIDOR solo puede consultar los envíos asignados a él (404 con uno ajeno). Las cabeceras
     * {@code X-User-Roles} y {@code X-User-Dni} las agrega el api-gateway; en este controller, todos
     * los endpoints que las leen siguen la misma regla.
     */
    @GetMapping("/{idEnvio}")
    public ResponseEntity<EnvioResponseDto> obtenerPorId(@PathVariable UUID idEnvio,
            @RequestHeader(value = UsuarioActual.HEADER_ROLES, required = false) String roles,
            @RequestHeader(value = UsuarioActual.HEADER_DNI, required = false) String dniUsuario) {
        envioService.verificarAcceso(idEnvio, UsuarioActual.desde(roles, dniUsuario));
        return ResponseEntity.ok(envioService.obtenerPorId(idEnvio));
    }

    /**
     * Consultar envío por su código de seguimiento (Tracking Code).
     * Un REPARTIDOR solo puede consultar los envíos asignados a él (404 con uno ajeno).
     */
    @GetMapping("/tracking/{codigoSeguimiento}")
    public ResponseEntity<EnvioResponseDto> obtenerEnvioPorCodigoSeguimiento(@PathVariable String codigoSeguimiento,
            @RequestHeader(value = UsuarioActual.HEADER_ROLES, required = false) String roles,
            @RequestHeader(value = UsuarioActual.HEADER_DNI, required = false) String dniUsuario) {
        envioService.verificarAccesoPorCodigo(codigoSeguimiento, UsuarioActual.desde(roles, dniUsuario));
        return ResponseEntity.ok(envioService.obtenerEnvioPorCodigoSeguimiento(codigoSeguimiento));
    }

    /**
     * Consultar envíos de manera grupal filtrando por estado actual.
     * Ejemplo de llamada: GET /api/v1/envios?estado=PENDIENTE
     * Un REPARTIDOR solo recibe los envíos asignados a él (lista vacía si su usuario no tiene DNI).
     */
    @GetMapping
    public ResponseEntity<List<EnvioResponseDto>> listarPorEstado(@RequestParam(required = false) EstadoEnvio estado,
            @RequestHeader(value = UsuarioActual.HEADER_ROLES, required = false) String roles,
            @RequestHeader(value = UsuarioActual.HEADER_DNI, required = false) String dniUsuario) {
        UsuarioActual usuario = UsuarioActual.desde(roles, dniUsuario);
        if (usuario.esRepartidorRestringido()) {
            return ResponseEntity.ok(envioService.listarDeRepartidor(usuario.dni(), estado));
        }
        if (estado != null) {
            return ResponseEntity.ok(envioService.listarPorEstado(estado));
        }
        // Si no se pasa el parámetro estado, se puede retornar la lista completa o lanzar excepción
        return ResponseEntity.ok(envioService.listarPorEstado(null));
    }

    /**
     * Avanzar el envío al siguiente estado (REPARTIDOR_ASIGNADO → EN_RUTA → ENTREGADO).
     * Para pasar a ENTREGADO se exige el código de recepción en el body.
     * El api-gateway lo permite a ADMIN y REPARTIDOR (regla PUT/PATCH sobre /envios/{id}/estado).
     * Un REPARTIDOR solo puede cambiar el estado de los envíos asignados a él (404 con uno ajeno).
     */
    @PatchMapping("/{idEnvio}/estado")
    public ResponseEntity<EnvioResponseDto> cambiarEstado(@PathVariable UUID idEnvio,
                                                          @RequestBody(required = false) CambioEstadoRequestDto request,
            @RequestHeader(value = UsuarioActual.HEADER_ROLES, required = false) String roles,
            @RequestHeader(value = UsuarioActual.HEADER_DNI, required = false) String dniUsuario) {
        envioService.verificarAcceso(idEnvio, UsuarioActual.desde(roles, dniUsuario));
        String codigoSeguimiento = envioService.obtenerPorId(idEnvio).getCodigoSeguimiento();
        String codigoRecepcion = request != null ? request.getCodigoRecepcion() : null;
        return ResponseEntity.ok(envioService.cambiarEstado(codigoSeguimiento, codigoRecepcion));
    }

    /**
     * Consultar el historial de cambios de estado de un envío, en orden cronológico.
     * Un REPARTIDOR solo puede consultar el de los envíos asignados a él (404 con uno ajeno).
     */
    @GetMapping("/{idEnvio}/historial")
    public ResponseEntity<List<HistoricoEstadoEnvioResponseDto>> historial(@PathVariable UUID idEnvio,
            @RequestHeader(value = UsuarioActual.HEADER_ROLES, required = false) String roles,
            @RequestHeader(value = UsuarioActual.HEADER_DNI, required = false) String dniUsuario) {
        envioService.verificarAcceso(idEnvio, UsuarioActual.desde(roles, dniUsuario));
        return ResponseEntity.ok(historicoEnvioService.obtenerHistorial(idEnvio));
    }

    /**
     * Cancelar un envío registrado (solo si está en estado PENDIENTE).
     */
    @PatchMapping("/{idEnvio}/cancelar")
    public ResponseEntity<EnvioResponseDto> cancelarEnvio(@PathVariable UUID idEnvio) {
        return ResponseEntity.ok(envioService.cancelarEnvio(idEnvio));
    }

    @PutMapping("/{idEnvio}/asignarRepartidor")
    public ResponseEntity<EnvioCourierResponseDto> asignarRepartidor(@PathVariable UUID idEnvio, @RequestParam String dniRepartidor){
        return ResponseEntity.ok(envioService.asignarRepartidor(idEnvio, dniRepartidor));
    }
}