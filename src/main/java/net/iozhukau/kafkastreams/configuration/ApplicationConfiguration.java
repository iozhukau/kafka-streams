package net.iozhukau.kafkastreams.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ApplicationConfiguration {

    public static final String BOOTSTRAP_SERVER_URL = "localhost:29092";
    public static final String STREAMS_APP_NAME = "order-streams-app";

    @Configuration
    public static class KafkaTopicsApplicationConfiguration {

        @Bean
        public KafkaAdmin admin() {
            Map<String, Object> configs = Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER_URL);
            return new KafkaAdmin(configs);
        }

        @Bean
        public NewTopic ordersTopic() {
            return TopicBuilder.name("orders")
                               .partitions(5)// IMPORTANT
                               .build();

        }

        @Bean
        public NewTopic biggerPriceSumTopic() {
            return TopicBuilder.name("bigger-price-sum")
                               .partitions(5)// IMPORTANT
                               .build();

        }

        @Bean
        public NewTopic lowerPriceSumTopic() {
            return TopicBuilder.name("lower-price-sum")
                               .partitions(5)// IMPORTANT
                               .compact()// IMPORTANT
                               .build();

        }

        @Bean
        public NewTopic totalMarginSumTopic() {
            return TopicBuilder.name("total-margin-sum")
                               .partitions(5)// IMPORTANT
                               .config(TopicConfig.RETENTION_MS_CONFIG, "1000")
                               .config(TopicConfig.MIN_CLEANABLE_DIRTY_RATIO_CONFIG, "0.01")
                               .config(TopicConfig.SEGMENT_MS_CONFIG, "100")
                               .config(TopicConfig.DELETE_RETENTION_MS_CONFIG, "100")
                               .config(TopicConfig.MIN_COMPACTION_LAG_MS_CONFIG, "100")
                               .config(TopicConfig.MAX_COMPACTION_LAG_MS_CONFIG, "1000")
                               .compact()// IMPORTANT
                               .build();

        }
    }

    @Configuration
    public static class KafkaProducersApplicationConfiguration {
        @Bean
        public KafkaTemplate<String, String> ordersKafkaTemplate(ProducerFactory<String, String> producerFactory) {
            return new KafkaTemplate<String, String>(producerFactory);
        }

        @Bean
        public ProducerFactory<String, String> producerFactory() {
            Map<String, Object> config = Map.of(
                    ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, ApplicationConfiguration.BOOTSTRAP_SERVER_URL,
                    ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                    ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class
            );

            return new DefaultKafkaProducerFactory<String, String>(config);
        }
    }

    @Configuration
    public static class KafkaStreamsApplicationConfiguration {

        @Bean
        KafkaStreamsConfiguration defaultKafkaStreamsConfig() {
            Map<String, Object> props = new HashMap<String, Object>();

            props.put(StreamsConfig.APPLICATION_ID_CONFIG, ApplicationConfiguration.STREAMS_APP_NAME);
            props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, ApplicationConfiguration.BOOTSTRAP_SERVER_URL);

            // Интересная конфигурация SERializer и DEserializer
            props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
            props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

            return new KafkaStreamsConfiguration(props);
        }
    }

    @Configuration
    public static class KafkaConsumersApplicationConfiguration {

        @Bean
        public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>> kafkaListenerContainerFactory(
                ConsumerFactory<Integer, String> consumerFactory) {
            var factory = new ConcurrentKafkaListenerContainerFactory<Integer, String>();
            factory.setConsumerFactory(consumerFactory);
            factory.setConcurrency(1);
            factory.getContainerProperties().setPollTimeout(3000);
            return factory;
        }

        @Bean
        public ConsumerFactory<Integer, String> consumerFactory() {
            Map<String, Object> config = Map.of(
                    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, ApplicationConfiguration.BOOTSTRAP_SERVER_URL,
                    ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                    ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class
            );

            return new DefaultKafkaConsumerFactory<>(config);
        }
    }

    @Configuration
    public static class BeenApplicationConfiguration {

        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }

    }

}
