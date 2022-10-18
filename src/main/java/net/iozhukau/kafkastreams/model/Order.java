package net.iozhukau.kafkastreams.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record Order(
        String id,
        Product product,
        String purchaseTime
) {
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static record Product(
            String productName,
            String productPrice
    ) {
    }
}
