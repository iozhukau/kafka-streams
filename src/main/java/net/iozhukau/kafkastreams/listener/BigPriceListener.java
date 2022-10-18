package net.iozhukau.kafkastreams.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BigPriceListener {

    @KafkaListener(id = "big-price-consumer", topics = "bigger-price-sum")
    void listen(ConsumerRecord<String, String> record) {
        log.info("{} | New margin for bigger prices", record.value());
    }
}
