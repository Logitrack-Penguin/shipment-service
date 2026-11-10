package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.messaging;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.interfaces.LiberacionEnviosService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Consume los eventos de baja de repartidores que publica courier-service (LT4-34).
 * <p>
 * Si procesar falla (por ejemplo, MySQL no responde), Spring reintenta según
 * {@code spring.rabbitmq.listener.simple.retry} y, si sigue fallando, el mensaje va a la DLQ.
 * Un mensaje sin DNI no se arregla reintentando: se rechaza directo a la DLQ.
 * </p>
 *
 * @author Elías Maciel Villalva
 * @version 1.0.0
 * @since 10-11-2026
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "logitrack.messaging.enabled", havingValue = "true")
public class CourierEventsListener {

    private final LiberacionEnviosService liberacionEnviosService;

    /**
     * Libera los envíos asignados al repartidor del evento.
     *
     * @param evento Mensaje recibido de la cola {@link RabbitConfig#QUEUE}.
     */
    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void onEvento(EventoRepartidor evento) {
        if (evento == null || evento.payload() == null || evento.payload().dni() == null
                || evento.payload().dni().isBlank()) {
            throw new AmqpRejectAndDontRequeueException("Evento de courier sin DNI: " + evento);
        }
        log.info("Evento {} recibido para el DNI {} (eventId={})", evento.eventType(),
                evento.payload().dni(), evento.eventId());
        liberacionEnviosService.liberarEnviosDeRepartidor(evento.payload().dni());
    }
}
