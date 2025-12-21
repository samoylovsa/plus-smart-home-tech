package ru.yandex.practicum.smarthome.telemetry.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.smarthome.telemetry.collector.model.sensor.SensorEvent;

@Slf4j
@Component("sensorEventProcessor")
@RequiredArgsConstructor
public class SensorEventProcessor implements EventProcessor {

    private final AvroConverter avroConverter;
    private final KafkaProducerService kafkaProducerService;

    @Override
    public void process(Object event) {
        SensorEvent sensorEvent = castToSensorEvent(event);
        SpecificRecord avroRecord = avroConverter.convertToAvro(sensorEvent);
        kafkaProducerService.sendSensorEvent(sensorEvent.getId(), avroRecord);
        log.debug("Sensor event {} processed and sent to Kafka", sensorEvent.getId());
    }

    private boolean supports(Class<?> eventType) {
        return SensorEvent.class.isAssignableFrom(eventType);
    }

    private SensorEvent castToSensorEvent(Object event) {
        if (!supports(event.getClass())) {
            throw new IllegalArgumentException(
                    String.format("SensorEventProcessor supports only SensorEvent, but got %s",
                            event.getClass().getSimpleName())
            );
        }
        return (SensorEvent) event;
    }
}
