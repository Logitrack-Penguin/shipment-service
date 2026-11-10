package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ del lado consumidor (LT4-34). Solo se activa con
 * {@code logitrack.messaging.enabled=true}.
 * <pre>
 * courier ─► logitrack.courier.events (topic) ─► shipment.courier-events.q ─► CourierEventsListener
 *                                                        │ si falla 4 veces o el JSON es inválido
 *                                                        ▼
 *                                   logitrack.dlx ─► shipment.courier-events.q.dlq (revisión manual)
 * </pre>
 *
 * @author Elías Maciel Villalva
 * @version 1.0.0
 * @since 10-11-2026
 */
@Configuration
@ConditionalOnProperty(name = "logitrack.messaging.enabled", havingValue = "true")
public class RabbitConfig {

    /** Exchange donde courier publica (misma definición que en courier-service). */
    public static final String EXCHANGE = "logitrack.courier.events";
    /** Cola propia de shipment para los eventos de courier. */
    public static final String QUEUE = "shipment.courier-events.q";
    /** Exchange de mensajes fallidos (dead letter exchange). */
    public static final String DLX = "logitrack.dlx";
    /** Cola donde terminan los mensajes que no se pudieron procesar (dead letter queue). */
    public static final String DLQ = QUEUE + ".dlq";
    /** Routing key del evento de desactivación. */
    public static final String RK_DESACTIVADO = "repartidor.desactivado";
    /** Routing key del evento de eliminación. */
    public static final String RK_ELIMINADO = "repartidor.eliminado";

    @Bean
    public TopicExchange courierEventsExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX, true, false);
    }

    /**
     * Cola durable. Si un mensaje se rechaza, RabbitMQ lo reenvía al DLX con la routing key de la DLQ.
     *
     * @return Cola de eventos de courier.
     */
    @Bean
    public Queue courierEventsQueue() {
        return QueueBuilder.durable(QUEUE)
                .withArgument("x-dead-letter-exchange", DLX)
                .withArgument("x-dead-letter-routing-key", DLQ)
                .build();
    }

    @Bean
    public Queue courierEventsDlq() {
        return QueueBuilder.durable(DLQ).build();
    }

    @Bean
    public Binding desactivadoBinding() {
        return BindingBuilder.bind(courierEventsQueue()).to(courierEventsExchange()).with(RK_DESACTIVADO);
    }

    @Bean
    public Binding eliminadoBinding() {
        return BindingBuilder.bind(courierEventsQueue()).to(courierEventsExchange()).with(RK_ELIMINADO);
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(courierEventsDlq()).to(deadLetterExchange()).with(DLQ);
    }

    /**
     * Conversor JSON (Jackson 3). Ignora la cabecera {@code __TypeId__} que pone courier (trae el nombre
     * de una clase de courier que acá no existe) y convierte según el tipo del parámetro del listener.
     *
     * @return Conversor JSON.
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();
        converter.setAlwaysConvertToInferredType(true);
        return converter;
    }
}
