package ru.yandex.practicum.smarthome.telemetry.aggregator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

@Slf4j
@Service
@RequiredArgsConstructor
public class AggregatorKafkaProducerService {

    private KafkaProducer<String, byte[]> producer;

    @Value("${kafka.bootstrap.servers:localhost:9092}")
    private String bootstrapServers;

    @Value("${kafka.topics.snapshots:telemetry.snapshots.v1}")
    private String snapshotTopic;

    @PostConstruct
    public void init() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
        producer = new KafkaProducer<>(props);
        log.info("Aggregator Kafka Producer initialized");
    }

    @PreDestroy
    public void cleanup() {
        if (producer != null) {
            try {
                flush();
                producer.close();
                log.info("Aggregator Kafka Producer closed");
            } catch (Exception e) {
                log.warn("Error closing Aggregator Kafka Producer", e);
            }
        }
    }

    public void sendSnapshot(SensorsSnapshotAvro snapshot) {
        byte[] avroBytes = serializeAvro(snapshot);
        String key = snapshot.getHubId();
        ProducerRecord<String, byte[]> record = new ProducerRecord<>(snapshotTopic, key, avroBytes);
        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                log.error("Failed to send snapshot for hub {}", key, exception);
            } else {
                log.debug("Message sent to topic {} with key {}", snapshotTopic, key);
            }
        });
    }

    private byte[] serializeAvro(SpecificRecord record) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
            DatumWriter<SpecificRecord> writer = new SpecificDatumWriter<>(record.getSchema());
            writer.write(record, encoder);
            encoder.flush();
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize Avro record", e);
        }
    }

    public void flush() {
        if (producer != null) {
            producer.flush();
        }
    }

    public void close() {  // ← ДОБАВИТЬ
        if (producer != null) {
            try {
                producer.close();
            } catch (Exception e) {
                log.warn("Error closing producer", e);
            }
        }
    }
}
