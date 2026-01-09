package ru.yandex.practicum.smarthome.telemetry.aggregator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    private KafkaConsumer<String, SensorEventAvro> consumer;

    @Value("${kafka.bootstrap.servers:localhost:9092}")
    private String bootstrapServers;

    @Value("${kafka.topics.sensors:telemetry.sensors.v1}")
    private String sensorTopic;

    @Value("${kafka.consumer.group-id:smart-home-aggregator}")
    private String groupId;

    private final SnapshotAggregatorService snapshotAggregatorService;
    private final AggregatorKafkaProducerService aggregatorKafkaProducerService;

    @PostConstruct
    public void init() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "ru.yandex.practicum.smarthome.telemetry.aggregator.deserializer.SensorEventDeserializer");
        consumer = new KafkaConsumer<>(props);
    }

    @PreDestroy
    public void shutdown() {
        if (consumer != null) {
            consumer.wakeup();
        }
    }

    public void start() {
        try {
            log.info("Starting Aggregator service...");
            consumer.subscribe(Collections.singletonList(sensorTopic));
            while (true) {
                ConsumerRecords<String, SensorEventAvro> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, SensorEventAvro> record : records) {
                    processEvent(record.value());
                }
                if (!records.isEmpty()) {
                    consumer.commitSync();
                }
            }
        } catch (WakeupException ignored) {
            log.info("AggregationStarter woken up, shutting down...");
        } catch (Exception e) {
            log.error("Error during sensor events processing", e);
        } finally {
            try {
                aggregatorKafkaProducerService.flush();
                consumer.commitSync();
            } finally {
                log.info("Closing consumer");
                consumer.close();
                log.info("Aggregator service stopped");
                aggregatorKafkaProducerService.close();
                log.info("Aggregator service stopped");
            }
        }
    }

    private void processEvent(SensorEventAvro event) {
        try {
            log.debug("Processing sensor event: id={}, hubId={}, timestamp={}",
                    event.getId(), event.getHubId(), event.getTimestamp());
            Optional<SensorsSnapshotAvro> updatedSnapshot = snapshotAggregatorService.updateState(event);
            updatedSnapshot.ifPresent(aggregatorKafkaProducerService::sendSnapshot);
        } catch (Exception e) {
            log.error("Error processing sensor event: {}", event, e);
        }
    }
}