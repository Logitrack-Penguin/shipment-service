package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.messaging;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.interfaces.LiberacionEnviosService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de {@link CourierEventsListener} y del conversor JSON de {@link RabbitConfig} (LT4-34).
 *
 * @author Elías Maciel Villalva
 * @version 1.0.0
 * @since 10-11-2026
 */
@ExtendWith(MockitoExtension.class)
class CourierEventsListenerTest {

    @Mock
    private LiberacionEnviosService liberacionEnviosService;

    @InjectMocks
    private CourierEventsListener listener;

    private static EventoRepartidor evento(String dni) {
        return new EventoRepartidor(UUID.randomUUID(), "repartidor.desactivado", 1, Instant.now(),
                "courier-service", new EventoRepartidor.Payload(dni, "ACTIVO", "INACTIVO"));
    }

    @Test
    @DisplayName("Libera los envíos del DNI que trae el evento")
    void onEvento_LiberaEnviosDelDni() {
        listener.onEvento(evento("28111222"));

        verify(liberacionEnviosService).liberarEnviosDeRepartidor("28111222");
    }

    @Test
    @DisplayName("Un evento sin DNI se rechaza sin reintentar (va a la DLQ)")
    void onEvento_SinDni_SeRechaza() {
        assertThrows(AmqpRejectAndDontRequeueException.class, () -> listener.onEvento(evento(null)));
        assertThrows(AmqpRejectAndDontRequeueException.class, () -> listener.onEvento(evento(" ")));

        verifyNoInteractions(liberacionEnviosService);
    }

    @Test
    @DisplayName("El conversor lee el JSON de courier aunque la cabecera __TypeId__ nombre una clase de courier")
    void conversor_IgnoraTypeIdDeCourier() {
        String json = """
                {"eventId":"6f1c2a9e-0000-4000-8000-000000000001","eventType":"repartidor.eliminado",
                 "version":1,"occurredAt":"2026-11-10T14:03:11Z","source":"courier-service",
                 "payload":{"dni":"28111222","estadoAnterior":"ACTIVO","estadoNuevo":null}}
                """;
        MessageProperties props = new MessageProperties();
        props.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        props.setHeader("__TypeId__",
                "ar.edu.unju.fi.arquitecturas.logitrack.courierservice.messaging.EventoIntegracion");
        props.setInferredArgumentType(EventoRepartidor.class);

        Object convertido = new RabbitConfig().jsonMessageConverter()
                .fromMessage(new Message(json.getBytes(StandardCharsets.UTF_8), props));

        EventoRepartidor evento = assertInstanceOf(EventoRepartidor.class, convertido);
        assertEquals("repartidor.eliminado", evento.eventType());
        assertEquals("28111222", evento.payload().dni());
        assertNull(evento.payload().estadoNuevo());
    }
}
