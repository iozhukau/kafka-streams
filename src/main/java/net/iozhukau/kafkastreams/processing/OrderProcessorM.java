package net.iozhukau.kafkastreams.processing;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Aggregator;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Initializer;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Named;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;

@Component
public class OrderProcessorM {

    private static final Serde<String> STRING_SERDE = Serdes.String();

    private final StreamsBuilder streamsBuilder;

    public OrderProcessorM(StreamsBuilder streamsBuilder) {
        this.streamsBuilder = streamsBuilder;
    }

    /*
       Подсчет прибыли нашей компании
     */
    @PostConstruct
    void buildPipelineM() {

        var lowerPriceStream =
                streamsBuilder
                        .stream("lower-price-sum", Consumed.with(STRING_SERDE, STRING_SERDE));

        var biggerPriceStream =
                streamsBuilder
                        .stream("bigger-price-sum", Consumed.with(STRING_SERDE, STRING_SERDE));


        lowerPriceStream.merge(biggerPriceStream, Named.as("margin-merge"))
                        .groupByKey()
                        .aggregate(getStringInitializer(),
                                   getAggregator(),
                                   Named.as("aggregate-margin-prices"),
                                   Materialized.with(STRING_SERDE, STRING_SERDE))
                        .toStream(Named.as("total-margin-processor"))
                        .to("total-margin-sum");
    }

    private static Initializer<String> getStringInitializer() {
        return () -> "0";
    }

    private static Aggregator<String, String, String> getAggregator() {
        return (aggKey, newValue, aggValue) -> new BigDecimal(aggValue).add(new BigDecimal(newValue))
                                                                       .toString();
    }

}
