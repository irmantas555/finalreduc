package org.irmantas.streamsreducer.services;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.logging.log4j.message.Message;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.irmantas.streamsreducer.mappers.MessageDeserializer;
import org.irmantas.streamsreducer.mappers.MessageSerializer;
import org.irmantas.streamsreducer.model.InMessage;
import org.irmantas.streamsreducer.model.Liker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class TetsService {

    private String activeServer = "192.168.1.67:9092";
//    private String activeServer = "localhost:9092";
    private String likesTopic = "likesbytes";

    @Autowired
    ReactiveElasticsearchClient client;

    @Autowired
    KafkaSender kafkaSender;

    @Autowired
    private ElasticsearchOperations operations;


    @Autowired
    ReactiveElasticsearchTemplate template;

    public void runIt() {
        Flux.interval(Duration.ofMillis(2000))
                .doOnNext(interval -> System.out.println("Time passed: " + (interval * 2) + "seconds"))
                .blockLast();
    }

    @PostConstruct
    public void getRecords() {
        ReceiverOptions<String, InMessage> options = ReceiverOptions.<String, InMessage>create(getConsumerProps())
                .subscription(Arrays.asList(likesTopic))
                .commitInterval(Duration.ofMillis(5000))
                .commitBatchSize(10);
        KafkaReceiver.create(options)
                .receive()
                .doOnNext(record -> processRecord(record))
                .doOnNext(record -> record.receiverOffset().acknowledge())
                .blockLast();
    }

    private void processRecord(ReceiverRecord<String, InMessage> record) {
//        System.out.println("Offest: " + record.receiverOffset() + " key: " + record.key() + " value: " + record.value());
        String event = record.key();
        InMessage message = record.value();
        long receiverId = message.getReceiverID();
        long sender = message.getSenderID();
        long timestamp = record.timestamp();
        message.setTimestamp(timestamp);
        String[] index = {event};
        Mono<InMessage> event1 = template.get(String.valueOf(sender), InMessage.class, IndexCoordinates.of("event"));
        client.indices()
                .existsIndex(getIndexRequest -> {
                    getIndexRequest.humanReadable(true);
                    getIndexRequest.indices(index);
                })
                .switchIfEmpty(client.indices().createIndex(createIndexRequest -> {
                    createIndexRequest.index(event);
                }))
                .flatMap(likesTopic -> {
                    Criteria criteria = new Criteria("_id").matches(String.valueOf(sender));
                    CriteriaQuery query3 = new CriteriaQuery(criteria);
                    NativeSearchQuery query2 = new NativeSearchQueryBuilder()
                            .withIds(Arrays.asList(String.valueOf(sender)))
                            .build();
                    return template.search(query3, InMessage.class, IndexCoordinates.of(event))
                            .count();
                })
//                .doOnNext(l -> System.out.println("Count: " + l))
                .filter(l -> l == 0)
                .then(template.save(message, IndexCoordinates.of(event)))
//                .doOnNext(liker -> System.out.println("Issaugojom likeri: " + liker))
                .flatMap(liker -> template.count(new StringQuery("{\"match_all\": {}}"), Liker.class, IndexCoordinates.of(event)))
//                .doOnNext(longg -> System.out.println("Laikeriu kiekis: " + longg))
                .doOnNext(longg -> {
                    NativeSearchQuery query1 = new NativeSearchQueryBuilder()
                            .withQuery(QueryBuilders.matchAllQuery())
                            .withSort(SortBuilders.fieldSort("timestamp").order(SortOrder.DESC))
                            .build();
                    query1.setMaxResults(10);
                    template.search(query1, InMessage.class, IndexCoordinates.of(event))
                            .doOnNext(likerSearchHit -> System.out.println(likerSearchHit))
                            .reduce(new InMessage(), (aggregatedMessage, likerSearchHit) -> {
                                if (null == aggregatedMessage.getSenderID()) {
                                    aggregatedMessage = new InMessage(likerSearchHit.getContent());
//                                    System.out.println("agregated: " + aggregatedMessage);
                                } else {
                                    aggregatedMessage.setSenderNick(aggregatedMessage.getSenderNick() + "_" + likerSearchHit.getContent().getSenderNick());
                                }
                                return aggregatedMessage;
                            })
//                            .doOnNext(s -> System.out.println("Got string: " + s))
                            .map(inMessage -> {
                                inMessage.setSenderID(longg);
                                SenderRecord record1 = SenderRecord.create("aggregated-likes", null, null, String.valueOf(inMessage.getReceiverID()), inMessage, inMessage.getReceiverID());
                                Disposable subscribe = kafkaSender.send(Mono.just(record1)).subscribe();
                                return subscribe;
                            }).subscribe();
                })
                .subscribe();
    }

    public Map<String, Object> getConsumerProps() {
        String bootstrapervers = activeServer;
        String consumerGroup = "final-pro-processor-group";
        Map<String, Object> receiveProps = new HashMap<>();
        receiveProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapervers);
        receiveProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        receiveProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageDeserializer.class);
        receiveProps.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
        receiveProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        return receiveProps;
    }
}
