package ru.yandex.practicum.smarthome.telemetry.collector.handler.sensor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.smarthome.telemetry.collector.converter.AvroConverter;
import ru.yandex.practicum.smarthome.telemetry.collector.converter.ProtoToModelConverter;
import ru.yandex.practicum.smarthome.telemetry.collector.model.sensor.LightSensorEvent;
import ru.yandex.practicum.smarthome.telemetry.collector.service.KafkaProducerService;

@Slf4j
@Service
@RequiredArgsConstructor
public class LightSensorEventHandler implements SensorEventHandler {

    private final KafkaProducerService kafkaProducerService;
    private final ProtoToModelConverter protoConverter;
    private final AvroConverter avroConverter;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR;
    }

    @Override
    public void handle(SensorEventProto event) {
        try {
            log.info("Началась обработка события датчика света: sensorId={}", event.getId());
            LightSensorEvent lightSensorEvent = protoConverter.convertLightSensor(event);
            log.debug("Преобразовано в Java объект: {}", lightSensorEvent);
            SpecificRecord avroRecord = avroConverter.convertToAvro(lightSensorEvent);
            log.debug("Преобразовано в Avro запись");
            kafkaProducerService.sendSensorEvent(lightSensorEvent.getHubId(), avroRecord);
            log.info("Завершилась обработка события датчика света: hubId={}, sensorId={}",
                    event.getHubId(), lightSensorEvent.getId());
        } catch (Exception e) {
            log.error("Ошибка обработки события датчика света: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process light sensor event", e);
        }
    }
}
