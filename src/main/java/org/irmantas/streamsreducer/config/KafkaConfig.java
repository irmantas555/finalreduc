package org.irmantas.streamsreducer.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.irmantas.streamsreducer.mappers.MessageSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

import java.util.HashMap;
import java.util.Map;
@Configuration
public class KafkaConfig {



    private String activeServer = "192.168.1.67:9092";

    @Autowired
    ApplicationContext context;

    @Bean
    public KafkaSender<String, String> myKafkaSender() {
        return KafkaSender.create(senderOptions());
    }

    @Bean
    public SenderOptions<String, String> senderOptions() {
        String bootstrapervers = activeServer;
        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapervers);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, MessageSerializer.class);

        SenderOptions<String, String> senderOption = SenderOptions.<String, String>create(producerProps)
                .maxInFlight(1024);
        return senderOption;
    }
}
