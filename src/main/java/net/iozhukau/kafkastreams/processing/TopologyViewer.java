package net.iozhukau.kafkastreams.processing;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@ConditionalOnBean({OrderProcessorA.class, OrderProcessorB.class, OrderProcessorM.class})
public class TopologyViewer {

    private final StreamsBuilder streamsBuilder;

    public TopologyViewer(StreamsBuilder streamsBuilder) {
        this.streamsBuilder = streamsBuilder;
    }

    @PostConstruct
    void print(){
        System.out.println("=========================");

        Topology topology = streamsBuilder.build();
        System.out.println(topology.describe());

        System.out.println("=========================");
    }
}
