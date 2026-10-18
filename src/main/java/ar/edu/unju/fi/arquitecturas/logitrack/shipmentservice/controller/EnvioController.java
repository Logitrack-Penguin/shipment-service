package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.controller;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.request.EnvioRequestDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.response.EnvioResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.enums.EstadoEnvio;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.interfaces.EnvioService;
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
     */
    @GetMapping("/{idEnvio}")
    public ResponseEntity<EnvioResponseDto> obtenerPorId(@PathVariable UUID idEnvio) {
        return ResponseEntity.ok(envioService.obtenerPorId(idEnvio));
    }

    /**
     * Consultar envío por su código de seguimiento (Tracking Code).
     */
    @GetMapping("/tracking/{codigoSeguimiento}")
    public ResponseEntity<EnvioResponseDto> obtenerEnvioPorCodigoSeguimiento(@PathVariable String codigoSeguimiento) {
        return ResponseEntity.ok(envioService.obtenerEnvioPorCodigoSeguimiento(codigoSeguimiento));
    }

    /**
     * Consultar envíos de manera grupal filtrando por estado actual.
     * Ejemplo de llamada: GET /api/v1/envios?estado=PENDIENTE
     */
    @GetMapping
    public ResponseEntity<List<EnvioResponseDto>> listarPorEstado(@RequestParam(required = false) EstadoEnvio estado) {
        if (estado != null) {
            return ResponseEntity.ok(envioService.listarPorEstado(estado));
        }
        // Si no se pasa el parámetro estado, se puede retornar la lista completa o lanzar excepción
        return ResponseEntity.ok(envioService.listarPorEstado(null));
    }

    /**
     * Cancelar un envío registrado (solo si está en estado PENDIENTE).
     */
    @PatchMapping("/{idEnvio}/cancelar")
    public ResponseEntity<EnvioResponseDto> cancelarEnvio(@PathVariable UUID idEnvio) {
        return ResponseEntity.ok(envioService.cancelarEnvio(idEnvio));
    }
}