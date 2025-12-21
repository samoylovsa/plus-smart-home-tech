package ru.yandex.practicum.smarthome.telemetry.collector.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

@Slf4j
@Service
public class KafkaProducerService {

    private Producer<String, byte[]> producer;

    @Value("${kafka.bootstrap.servers:localhost:9092}")
    private String bootstrapServers;

    @Value("${kafka.topics.sensors:telemetry.sensors.v1}")
    private String sensorTopic;

    @Value("${kafka.topics.hubs:telemetry.hubs.v1}")
    private String hubTopic;

    @PostConstruct
    public void init() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                ByteArraySerializer.class.getName());
        producer = new KafkaProducer<>(props);
        log.info("Kafka Producer initialized");
    }

    @PreDestroy
    public void cleanup() {
        if (producer != null) {
            try {
                producer.flush();
                producer.close();
                log.info("Kafka Producer closed");
            } catch (Exception e) {
                log.warn("Error closing Kafka Producer", e);
            }
        }
    }

    public void sendSensorEvent(String key, SpecificRecord event) {
        byte[] avroBytes = serializeAvro(event);
        sendToKafka(sensorTopic, key, avroBytes);
    }

    public void sendHubEvent(String key, SpecificRecord event) {
        byte[] avroBytes = serializeAvro(event);
        sendToKafka(hubTopic, key, avroBytes);
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

    private void sendToKafka(String topic, String key, byte[] value) {
        ProducerRecord<String, byte[]> record = new ProducerRecord<>(topic, key, value);
        producer.send(record);
        log.debug("Message sent to topic {} with key {}", topic, key);
    }
}