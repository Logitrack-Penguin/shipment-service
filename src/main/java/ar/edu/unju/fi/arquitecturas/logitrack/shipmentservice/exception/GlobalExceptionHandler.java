package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Interceptor global de excepciones para la capa de presentación REST.
 * <p>
 * Centraliza la captura de fallas de validación (Bean Validation JSR-380),
 * excepciones de negocio y conflictos de integridad relacional,
 * traduciéndolas en contratos JSON semánticos y legibles para la herramienta Postman.
 * </p>
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 17-10-2026
 * @see OperacionInvalidaException
 * @see OperacionNoPermitidaException
 * @see RecursoDuplicadoException
 * @see RecursoNoEncontradoException
 */

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> manejarExcepcionGenerica(Exception ex) {
        log.error("Error interno no controlado en el servidor: ", ex);
        return construirRespuesta(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error interno en el servidor.");
    }

    @ExceptionHandler(OperacionInvalidaException.class)
    public ResponseEntity<Map<String, Object>> manejarOperacionInvalida(OperacionInvalidaException ex) {
        log.warn("Operación funcional rechazada [400 BAD REQUEST]: {}", ex.getMessage());
        return construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(OperacionNoPermitidaException.class)
    public ResponseEntity<Map<String, Object>> manejarOperacionNoPermitida(OperacionNoPermitidaException ex) {
        log.warn("Operación funcional rechazada [403 FORBIDDEN]: {}", ex.getMessage());
        return construirRespuesta(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(RecursoDuplicadoException.class)
    public ResponseEntity<Map<String, Object>> manejarRecursoDuplicado(RecursoDuplicadoException ex) {
        log.warn("Conflicto de unicidad detectado [409 CONFLICT]: {}", ex.getMessage());
        return construirRespuesta(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> manejarRecursoNoEncontrado(RecursoNoEncontradoException ex) {
        log.warn("Excepción de dominio capturada [404 NOT FOUND]: {}", ex.getMessage());
        return construirRespuesta(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Asignar un repartidor que no está ACTIVO es un conflicto con el estado actual del recurso, no un error del servidor.
     */
    @ExceptionHandler(CourierNoDisponibleException.class)
    public ResponseEntity<Map<String, Object>> manejarCourierNoDisponible(CourierNoDisponibleException ex) {
        log.warn("Repartidor no disponible [409 CONFLICT]: {}", ex.getMessage());
        return construirRespuesta(HttpStatus.CONFLICT, ex.getMessage());
    }

    /**
     * Un parámetro de ruta o de consulta con tipo inválido (UUID mal formado, estado inexistente) es un error del cliente.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> manejarTipoInvalido(MethodArgumentTypeMismatchException ex) {
        log.warn("Parámetro con tipo inválido [400 BAD REQUEST]: {}", ex.getName());
        return construirRespuesta(HttpStatus.BAD_REQUEST, "Valor inválido para el parámetro '" + ex.getName() + "'.");
    }

    @ExceptionHandler(CourierNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(CourierNoEncontradoException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler({CourierServiceException.class, FeignException.class})
    public ResponseEntity<ErrorResponse> handleCourierDown(Exception ex, HttpServletRequest req) {
        return build(HttpStatus.SERVICE_UNAVAILABLE,
                "Servicio de repartidores no disponible", req);
    }

    /**
     * Captura fallas de Bean Validation en payloads recibidos con @Valid en los controladores.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidaciones(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage())
        );

        log.warn("Validación JSR-380 fallida en payload de entrada: {}", errores);

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validación fallida");
        body.put("detalles", errores);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    private ResponseEntity<Map<String, Object>> construirRespuesta(HttpStatus status, String mensaje) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("mensaje", mensaje);
        return ResponseEntity.status(status).body(body);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String msg, HttpServletRequest req) {
        return ResponseEntity.status(status).body(
                new ErrorResponse(LocalDateTime.now(), status.value(),
                        status.getReasonPhrase(), msg, req.getRequestURI()));
    }

    public record ErrorResponse(LocalDateTime timestamp, int status,
                                String error, String message, String path) {}
}
