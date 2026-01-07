package ru.yandex.practicum.smarthome.telemetry.analyzer.processor;

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
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.smarthome.telemetry.analyzer.deserializer.SnapshotDeserializer;
import ru.yandex.practicum.smarthome.telemetry.analyzer.service.ScenarioActionService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor {

    private KafkaConsumer<String, SensorsSnapshotAvro> consumer;

    @Value("${kafka.bootstrap.servers:localhost:9092}")
    private String bootstrapServers;

    @Value("${kafka.topics.snapshots:telemetry.snapshots.v1}")
    private String snapshotTopic;

    @Value("${kafka.consumer.snapshot-group-id:smart-home-analyzer-snapshot}")
    private String groupId;

    private final ScenarioActionService scenarioActionService;

    @PostConstruct
    public void init() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SnapshotDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
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
            log.info("Starting SnapshotProcessor...");
            consumer.subscribe(Collections.singletonList(snapshotTopic));
            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    processSnapshot(record.value());
                }
                if (!records.isEmpty()) {
                    consumer.commitSync();
                }
            }
        } catch (WakeupException ignored) {
            log.info("SnapshotProcessor woken up, shutting down...");
        } catch (Exception e) {
            log.error("Error in SnapshotProcessor", e);
        } finally {
            try {
                consumer.commitSync();
            } finally {
                log.info("Closing SnapshotProcessor consumer...");
                consumer.close();
            }
        }
    }

    private void processSnapshot(SensorsSnapshotAvro snapshot) {
        try {
            log.debug("Processing snapshot for hub: {}", snapshot.getHubId());
            scenarioActionService.processSnapshot(snapshot);
        } catch (Exception e) {
            log.error("Error processing snapshot: {}", e.getMessage(), e);
        }
    }
}
