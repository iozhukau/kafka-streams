package net.iozhukau.kafkastreams.processing;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;

@UtilityClass
public class MarginUtils {

    public static String getMargin(String value, Integer rate) {
        var factor = new BigDecimal(rate).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
        return new BigDecimal(value).multiply(factor)
                                    .setScale(2, RoundingMode.HALF_DOWN)
                                    .toString();
    }
}
