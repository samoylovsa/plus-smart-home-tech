package ru.yandex.practicum.smarthome.telemetry.collector.handler.sensor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.smarthome.telemetry.collector.converter.AvroConverter;
import ru.yandex.practicum.smarthome.telemetry.collector.converter.ProtoToModelConverter;
import ru.yandex.practicum.smarthome.telemetry.collector.model.sensor.MotionSensorEvent;
import ru.yandex.practicum.smarthome.telemetry.collector.service.KafkaProducerService;

@Slf4j
@Service
@RequiredArgsConstructor
public class MotionSensorEventHandler implements SensorEventHandler {

    private final KafkaProducerService kafkaProducerService;
    private final ProtoToModelConverter protoConverter;
    private final AvroConverter avroConverter;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR;
    }

    @Override
    public void handle(SensorEventProto event) {
        try {
            log.info("Началась обработка события датчика движения: sensorId={}", event.getId());
            MotionSensorEvent motionSensorEvent = protoConverter.convertMotionSensor(event);
            log.debug("Преобразовано в Java объект: {}", motionSensorEvent);
            SpecificRecord avroRecord = avroConverter.convertToAvro(motionSensorEvent);
            log.debug("Преобразовано в Avro запись");
            kafkaProducerService.sendSensorEvent(motionSensorEvent.getHubId(), avroRecord);
            log.info("Завершилась обработка события датчика движения: hubId={}, sensorId={}",
                    event.getHubId(), motionSensorEvent.getId());
        } catch (Exception e) {
            log.error("Ошибка обработки события датчика движения: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process motion sensor event", e);
        }
    }
}
