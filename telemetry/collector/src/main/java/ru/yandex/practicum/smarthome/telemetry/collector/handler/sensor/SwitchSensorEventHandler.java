package ru.yandex.practicum.smarthome.telemetry.collector.handler.sensor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.smarthome.telemetry.collector.converter.AvroConverter;
import ru.yandex.practicum.smarthome.telemetry.collector.converter.ProtoToModelConverter;
import ru.yandex.practicum.smarthome.telemetry.collector.model.sensor.SwitchSensorEvent;
import ru.yandex.practicum.smarthome.telemetry.collector.service.CollectorKafkaProducerService;

@Slf4j
@Service
@RequiredArgsConstructor
public class SwitchSensorEventHandler implements SensorEventHandler {

    private final CollectorKafkaProducerService collectorKafkaProducerService;
    private final ProtoToModelConverter protoConverter;
    private final AvroConverter avroConverter;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR;
    }

    @Override
    public void handle(SensorEventProto event) {
        try {
            log.info("Началась обработка события датчика переключателя: sensorId={}", event.getId());
            SwitchSensorEvent switchSensorEvent = protoConverter.convertSwitchSensor(event);
            log.debug("Преобразовано в Java объект: {}", switchSensorEvent);
            SpecificRecord avroRecord = avroConverter.convertToAvro(switchSensorEvent);
            log.debug("Преобразовано в Avro запись");
            collectorKafkaProducerService.sendSensorEvent(switchSensorEvent.getHubId(), avroRecord);
            log.info("Завершилась обработка события датчика переключателя: hubId={}, sensorId={}",
                    event.getHubId(), switchSensorEvent.getId());
        } catch (Exception e) {
            log.error("Ошибка обработки события датчика переключателя: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process switch sensor event", e);
        }
    }
}
