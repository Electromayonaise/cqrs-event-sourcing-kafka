package com.techbank.account.query.infrastructure.consumers;

import com.techbank.cqrs.core.events.BaseEvent;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

/**
 * Guarantee Event Consume Order:
 * en lugar de un metodo (y un topic) por tipo de evento, un solo metodo consume
 * todos los eventos del topic unico, en el mismo orden en que fueron producidos.
 */
public interface EventConsumer {
    void consume(@Payload BaseEvent event,
                 @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                 @Header(KafkaHeaders.OFFSET) long offset,
                 Acknowledgment ack);
}
