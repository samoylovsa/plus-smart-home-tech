package ru.yandex.practicum.smarthome.telemetry.collector.handler.sensor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.smarthome.telemetry.collector.converter.AvroConverter;
import ru.yandex.practicum.smarthome.telemetry.collector.converter.ProtoToModelConverter;
import ru.yandex.practicum.smarthome.telemetry.collector.model.sensor.ClimateSensorEvent;
import ru.yandex.practicum.smarthome.telemetry.collector.service.CollectorKafkaProducerService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClimateSensorEventHandler implements SensorEventHandler {

    private final CollectorKafkaProducerService collectorKafkaProducerService;
    private final ProtoToModelConverter protoConverter;
    private final AvroConverter avroConverter;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR;
    }

    @Override
    public void handle(SensorEventProto event) {
        try {
            log.info("Началась обработка события климатического датчика: sensorId={}", event.getId());
            ClimateSensorEvent climateSensorEvent = protoConverter.convertClimateSensor(event);
            log.debug("Преобразовано в Java объект: {}", climateSensorEvent);
            SpecificRecord avroRecord = avroConverter.convertToAvro(climateSensorEvent);
            log.debug("Преобразовано в Avro запись");
            collectorKafkaProducerService.sendSensorEvent(climateSensorEvent.getHubId(), avroRecord);
            log.info("Завершилась обработка события климатического датчика: hubId={}, sensorId={}",
                    event.getHubId(), climateSensorEvent.getId());
        } catch (Exception e) {
            log.error("Ошибка обработки события климатического датчика: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process climate sensor event", e);
        }
    }
}
