package net.iozhukau.kafkastreams.processing;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import net.iozhukau.kafkastreams.model.Order;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Component
public class OrderProcessorB {

    public static final BigDecimal BIG_PRICE = new BigDecimal(50);
    private static final Serde<String> STRING_SERDE = Serdes.String();

    private final StreamsBuilder streamsBuilder;
    private final ObjectMapper objectMapper;

    public OrderProcessorB(StreamsBuilder streamsBuilder,
                           ObjectMapper objectMapper) {
        this.streamsBuilder = streamsBuilder;
        this.objectMapper = objectMapper;
    }

    /*
       Подсчет коммисии для поставщиков.
       Если товар стоит меньше 50 у.е., то мы получаем 10% прибыли от цены товара.
     */
    @PostConstruct
    void buildPipelineB() {
        streamsBuilder
                .stream("orders", Consumed.with(STRING_SERDE, STRING_SERDE))
                // заменяем ключ
                .selectKey(getPurchaseDate(), Named.as("get-purchase-date-for-lower-price"))
                // получаем ценны продуктов
                .mapValues(getProductPriceInOrder(), Named.as("get-product-price-in-order-for-lower-price"))
                // отсеиваем маленькие цены
                .filter(this::filterLowerPrices, Named.as("filter-low-prices"))
                //
                .mapValues(getMarginForLowPrice(), Named.as("get-margin-for-low-price"))
                .to("lower-price-sum");

        streamsBuilder.build();
    }

    private KeyValueMapper<String, String, String> getPurchaseDate() {
        return (key, value) -> getPurchaseDate(value);
    }

    @SneakyThrows
    private String getPurchaseDate(String value) {
        Order order = objectMapper.readValue(value, Order.class);
        Instant date = order.purchaseTime() != null ? Instant.parse(order.purchaseTime()) : Instant.now();
        LocalDate localDate = LocalDate.ofInstant(date, ZoneId.systemDefault());
        return localDate.toString();
    }

    private ValueMapper<String, String> getProductPriceInOrder() {
        return this::getProductPriceInOrder;
    }

    @SneakyThrows
    private String getProductPriceInOrder(String value) {
        Order order = objectMapper.readValue(value, Order.class);
        return order.product().productPrice();
    }

    private boolean filterLowerPrices(String key, String value) {
        BigDecimal price = new BigDecimal(value);
        // if price to less than constanta, then return true
        return price.compareTo(BIG_PRICE) < 0;
    }

    private ValueMapper<String, String> getMarginForLowPrice() {
        return this::getMarginForLowPrice;
    }

    private String getMarginForLowPrice(String value) {
        return MarginUtils.getMargin(value, 10);
    }
}
