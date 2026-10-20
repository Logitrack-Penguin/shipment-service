package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.controller;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.request.RepartidorRequestDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.response.RepartidorResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.enums.EstadoRepartidor;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.interfaces.RepartidorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST encargado de exponer las operaciones CRUD y de consulta
 * para la gestión de repartidores en el sistema.
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 19-10-2026
 * @see RepartidorService
 */
@RestController
@RequestMapping("/api/v1/repartidores")
@RequiredArgsConstructor
public class RepartidorController {

    private final RepartidorService repartidorService;

    /**
     * Endpoint para registrar un nuevo repartidor en el sistema.
     *
     * @param requestDto Payload con los datos requeridos.
     * @return {@link ResponseEntity} con HTTP 201 (CREATED) y el DTO creado.
     */
    @PostMapping
    public ResponseEntity<RepartidorResponseDto> registrarRepartidor(@Valid @RequestBody RepartidorRequestDto requestDto) {
        RepartidorResponseDto creado = repartidorService.registrarRepartidor(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /**
     * Endpoint para consultar el detalle de un repartidor mediante su ID.
     *
     * @param idRepartidor UUID del repartidor a consultar.
     * @return {@link ResponseEntity} con HTTP 200 (OK) y el DTO del repartidor.
     */
    @GetMapping("/{idRepartidor}")
    public ResponseEntity<RepartidorResponseDto> obtenerPorId(@PathVariable UUID idRepartidor) {
        RepartidorResponseDto repartidor = repartidorService.obtenerPorId(idRepartidor);
        return ResponseEntity.ok(repartidor);
    }

    /**
     * Endpoint para listar repartidores, permitiendo filtrar por su estado operativo.
     *
     * @param estado Parámetro opcional para filtrar por {@link EstadoRepartidor}.
     * @return {@link ResponseEntity} con HTTP 200 (OK) y la lista de repartidores.
     */
    @GetMapping
    public ResponseEntity<List<RepartidorResponseDto>> listarRepartidores(@RequestParam(required = false) EstadoRepartidor estado) {
        List<RepartidorResponseDto> repartidores = repartidorService.listarRepartidores(estado);
        return ResponseEntity.ok(repartidores);
    }
}