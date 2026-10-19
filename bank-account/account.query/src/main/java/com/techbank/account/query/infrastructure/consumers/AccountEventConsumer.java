package com.techbank.account.query.infrastructure.consumers;

import com.techbank.account.query.infrastructure.handlers.EventHandler;
import com.techbank.cqrs.core.events.BaseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;

@Service
public class AccountEventConsumer implements EventConsumer {
    private static final Logger logger = LoggerFactory.getLogger(AccountEventConsumer.class);

    @Autowired
    private EventHandler eventHandler;

    /**
     * Un unico listener sobre un unico topic. El JsonDeserializer reconstruye el tipo concreto
     * del evento (AccountOpenedEvent, FundsDepositedEvent, ...) a partir del header __TypeId__
     * que agrega el JsonSerializer del productor, y aqui se despacha por reflexion al metodo
     * on(...) correspondiente del EventHandler.
     */
    @KafkaListener(topics = "${spring.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    @Override
    public void consume(@Payload BaseEvent event,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                        @Header(KafkaHeaders.OFFSET) long offset,
                        Acknowledgment ack) {
        logger.info("CONSUMED  partition={} offset={} type={} id={} version={}",
                partition, offset, event.getClass().getSimpleName(), event.getId(), event.getVersion());
        try {
            var eventHandlerMethod = eventHandler.getClass().getDeclaredMethod("on", event.getClass());
            eventHandlerMethod.setAccessible(true);
            eventHandlerMethod.invoke(eventHandler, event);
            ack.acknowledge();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Error while consuming event " + event.getClass().getSimpleName(), e);
        }
    }
}
