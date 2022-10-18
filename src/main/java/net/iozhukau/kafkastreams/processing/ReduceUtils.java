package net.iozhukau.kafkastreams.processing;

import lombok.experimental.UtilityClass;
import org.apache.kafka.streams.kstream.Reducer;

import java.math.BigDecimal;

@UtilityClass
public class ReduceUtils {

    public static Reducer<String> reducePrices() {
        return (a, b) -> {
            BigDecimal oldValue = new BigDecimal(a);
            BigDecimal newValue = new BigDecimal(b);
            return oldValue.add(newValue).toString();
        };
    }
}
