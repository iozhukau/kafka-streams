package net.iozhukau.kafkastreams.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.iozhukau.kafkastreams.model.Order;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class OrderGenerator {

    private final Faker faker = new Faker();

    private final KafkaTemplate<String, String> ordersKafkaTemplate;
    private final ObjectMapper objectMapper;

    public OrderGenerator(KafkaTemplate<String, String> ordersKafkaTemplate,
                          ObjectMapper objectMapper) {
        this.ordersKafkaTemplate = ordersKafkaTemplate;
        this.objectMapper = objectMapper;
    }

    private final List<Order> oracle = List.of(
            Order.builder()
                 .id(faker.idNumber().valid())
                 .product(Order.Product.builder()
                                       .productName(faker.commerce().productName())
                                       .productPrice("10")
                                       .build())
                 .purchaseTime(faker.date().past(10, TimeUnit.MINUTES).toInstant().toString())
                 .build(),
            Order.builder()
                 .id(faker.idNumber().valid())
                 .product(Order.Product.builder()
                                       .productName(faker.commerce().productName())
                                       .productPrice("20")
                                       .build())
                 .purchaseTime(faker.date().past(10, TimeUnit.MINUTES).toInstant().toString())
                 .build(),
            Order.builder()
                 .id(faker.idNumber().valid())
                 .product(Order.Product.builder()
                                       .productName(faker.commerce().productName())
                                       .productPrice("30")
                                       .build())
                 .purchaseTime(faker.date().past(10, TimeUnit.MINUTES).toInstant().toString())
                 .build(),
            Order.builder()
                 .id(faker.idNumber().valid())
                 .product(Order.Product.builder()
                                       .productName(faker.commerce().productName())
                                       .productPrice("40")
                                       .build())
                 .purchaseTime(faker.date().past(10, TimeUnit.MINUTES).toInstant().toString())
                 .build(),
            Order.builder()
                 .id(faker.idNumber().valid())
                 .product(Order.Product.builder()
                                       .productName(faker.commerce().productName())
                                       .productPrice("50")
                                       .build())
                 .purchaseTime(faker.date().past(10, TimeUnit.MINUTES).toInstant().toString())
                 .build(),
            Order.builder()
                 .id(faker.idNumber().valid())
                 .product(Order.Product.builder()
                                       .productName(faker.commerce().productName())
                                       .productPrice("50")
                                       .build())
                 .purchaseTime(faker.date().past(10, TimeUnit.MINUTES).toInstant().toString())
                 .build(),
            Order.builder()
                 .id(faker.idNumber().valid())
                 .product(Order.Product.builder()
                                       .productName(faker.commerce().productName())
                                       .productPrice("80")
                                       .build())
                 .purchaseTime(faker.date().past(10, TimeUnit.MINUTES).toInstant().toString())
                 .build(),
            Order.builder()
                 .id(faker.idNumber().valid())
                 .product(Order.Product.builder()
                                       .productName(faker.commerce().productName())
                                       .productPrice("100")
                                       .build())
                 .purchaseTime(faker.date().future(5, TimeUnit.DAYS).toInstant().toString())
                 .build(),
            Order.builder()
                 .id(faker.idNumber().valid())
                 .product(Order.Product.builder()
                                       .productName(faker.commerce().productName())
                                       .productPrice("100")
                                       .build())
                 .purchaseTime(faker.date().future(5, TimeUnit.DAYS).toInstant().toString())
                 .build(),
            Order.builder()
                 .id(faker.idNumber().valid())
                 .product(Order.Product.builder()
                                       .productName(faker.commerce().productName())
                                       .productPrice("100")
                                       .build())
                 .purchaseTime(faker.date().future(5, TimeUnit.DAYS).toInstant().toString())
                 .build(),
            Order.builder()
                 .id(faker.idNumber().valid())
                 .product(Order.Product.builder()
                                       .productName(faker.commerce().productName())
                                       .productPrice("100")
                                       .build())
                 .purchaseTime(faker.date().future(5, TimeUnit.DAYS).toInstant().toString())
                 .build()
    );

    @SneakyThrows
    public void generate(Order order) {
        log.info("{} | New order price", order.product().productPrice());

        String data = objectMapper.writeValueAsString(order);
        ordersKafkaTemplate.send("orders", order.id(), data);
    }

    @SneakyThrows
    @EventListener(classes = ApplicationReadyEvent.class)
    public void onStartApplication() {
        log.info("Order Generator run");

        for (Order order : oracle) {
            generate(order);
            TimeUnit.MILLISECONDS.sleep(1000);
        }
    }
}
