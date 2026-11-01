package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.enums.EstadoEnvio;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Historial de los cambios de estado hechos en el sistema con la fecha y hora exacta de cada cambio.
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 17-10-2026
 * @see Envio
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "historico_estado_envios")
public class HistoricoEstadoEnvio {

    /**
     * Identificador único universal de cada histórico.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idHistoricoEstadoEnvio;

    /**
     * Código de seguimiento del envío correspondiente.
     */
    @Column(name = "codigo_seguimiento", nullable = false, updatable = false)
    private String codigoSeguimiento;

    /**
     * Estado del envío previo al cambio.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_envio_anterior", nullable = false)
    private EstadoEnvio estadoEnvioAnterior;

    /**
     * Estado del envío posterior al cambio.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_envio_nuevo", nullable = false)
    private EstadoEnvio estadoEnvioNuevo;

    /**
     * Fecha y hora exacta del cambio de estado en formato yyyy-MM-dd HH:mm:ss.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "fecha_cambio_estado", nullable = false, updatable = false)
    private LocalDateTime fechaCambioEstado;
}
