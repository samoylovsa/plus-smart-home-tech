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
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.smarthome.telemetry.analyzer.deserializer.HubEventDeserializer;
import ru.yandex.practicum.smarthome.telemetry.analyzer.service.ScenarioService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {

    private KafkaConsumer<String, HubEventAvro> consumer;

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.topics.hubs}")
    private String hubTopic;

    @Value("${kafka.consumer.hub-group-id}")
    private String groupId;

    private final ScenarioService scenarioService;

    @PostConstruct
    public void init() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, HubEventDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        consumer = new KafkaConsumer<>(props);
    }

    @PreDestroy
    public void shutdown() {
        if (consumer != null) {
            consumer.wakeup();
        }
    }

    @Override
    public void run() {
        try {
            log.info("Starting HubEventProcessor...");
            consumer.subscribe(Collections.singletonList(hubTopic));
            while (true) {
                ConsumerRecords<String, HubEventAvro> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    processHubEvent(record.value());
                }
            }
        } catch (WakeupException ignored) {
            log.info("HubEventProcessor woken up, shutting down...");
        } catch (Exception e) {
            log.error("Error in HubEventProcessor", e);
        } finally {
            log.info("Closing HubEventProcessor consumer...");
            consumer.close();
        }
    }

    private void processHubEvent(HubEventAvro event) {
        try {
            String hubId = event.getHubId();
            Object payload = event.getPayload();
            if (payload instanceof DeviceAddedEventAvro deviceAdded) {
                scenarioService.handleDeviceAdded(hubId, deviceAdded);
                log.debug("Processed device added: {} for hub: {}", deviceAdded.getId(), hubId);
            } else if (payload instanceof DeviceRemovedEventAvro deviceRemoved) {
                scenarioService.handleDeviceRemoved(hubId, deviceRemoved);
                log.debug("Processed device removed: {} for hub: {}", deviceRemoved.getId(), hubId);
            } else if (payload instanceof ScenarioAddedEventAvro scenarioAdded) {
                scenarioService.handleScenarioAdded(hubId, scenarioAdded);
                log.debug("Processed scenario added: {} for hub: {}", scenarioAdded.getName(), hubId);
            } else if (payload instanceof ScenarioRemovedEventAvro scenarioRemoved) {
                scenarioService.handleScenarioRemoved(hubId, scenarioRemoved);
                log.debug("Processed scenario removed: {} for hub: {}", scenarioRemoved.getName(), hubId);
            }
        } catch (Exception e) {
            log.error("Error processing hub event: {}", e.getMessage(), e);
        }
    }
}