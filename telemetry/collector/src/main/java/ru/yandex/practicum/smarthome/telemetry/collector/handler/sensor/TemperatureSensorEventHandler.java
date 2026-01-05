package ru.yandex.practicum.smarthome.telemetry.collector.handler.sensor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.smarthome.telemetry.collector.converter.AvroConverter;
import ru.yandex.practicum.smarthome.telemetry.collector.converter.ProtoToModelConverter;
import ru.yandex.practicum.smarthome.telemetry.collector.model.sensor.TemperatureSensorEvent;
import ru.yandex.practicum.smarthome.telemetry.collector.service.CollectorKafkaProducerService;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemperatureSensorEventHandler implements SensorEventHandler {

    private final CollectorKafkaProducerService collectorKafkaProducerService;
    private final ProtoToModelConverter protoConverter;
    private final AvroConverter avroConverter;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR;
    }

    @Override
    public void handle(SensorEventProto event) {
        try {
            log.info("Началась обработка события датчика температуры: sensorId={}", event.getId());
            TemperatureSensorEvent temperatureSensorEvent = protoConverter.convertTemperatureSensor(event);
            log.debug("Преобразовано в Java объект: {}", temperatureSensorEvent);
            SpecificRecord avroRecord = avroConverter.convertToAvro(temperatureSensorEvent);
            log.debug("Преобразовано в Avro запись");
            collectorKafkaProducerService.sendSensorEvent(temperatureSensorEvent.getHubId(), avroRecord);
            log.info("Завершилась обработка события датчика температуры: hubId={}, sensorId={}",
                    event.getHubId(), temperatureSensorEvent.getId());
        } catch (Exception e) {
            log.error("Ошибка обработки события датчика температуры: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process temperature sensor event", e);
        }
    }
}
