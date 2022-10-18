package net.iozhukau.kafkastreams.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TotalMarginSumListener {

    @KafkaListener(id = "total-margin-consumer", topics = "total-margin-sum")
    void listen(ConsumerRecord<String, String> record) {
        log.info("{} | New total margin sum for:{}", record.value(), record.key());
    }
}
