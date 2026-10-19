package com.techbank.account.cmd.infrastructure;

import com.techbank.cqrs.core.events.BaseEvent;
import com.techbank.cqrs.core.producers.EventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AccountEventProducer implements EventProducer {
    private static final Logger logger = LoggerFactory.getLogger(AccountEventProducer.class);

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void produce(String topic, BaseEvent event) {
        // La key del mensaje es el ID del agregado: Kafka garantiza el orden por particion,
        // y todos los eventos con la misma key van siempre a la misma particion.
        this.kafkaTemplate.send(topic, event.getId(), event);
        logger.info("PRODUCED  topic={} key={} type={} version={}",
                topic, event.getId(), event.getClass().getSimpleName(), event.getVersion());
    }
}
