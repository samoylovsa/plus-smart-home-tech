package ru.yandex.practicum.smarthome.telemetry.collector.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.smarthome.telemetry.collector.model.hub.DeviceAddedEvent;
import ru.yandex.practicum.smarthome.telemetry.collector.converter.AvroConverter;
import ru.yandex.practicum.smarthome.telemetry.collector.service.KafkaProducerService;
import ru.yandex.practicum.smarthome.telemetry.collector.converter.ProtoToModelConverter;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceAddedEventHandler implements HubEventHandler {

    private final KafkaProducerService kafkaProducerService;
    private final ProtoToModelConverter protoConverter;
    private final AvroConverter avroConverter;

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    @Override
    public void handle(HubEventProto event) {
        try {
            log.info("Началась обработка события добавления устройства: hubId={}", event.getHubId());
            DeviceAddedEvent deviceAddedEvent = protoConverter.convertDeviceAdded(event);
            log.debug("Преобразовано в Java объект: {}", deviceAddedEvent);
            SpecificRecord avroRecord = avroConverter.convertToAvro(deviceAddedEvent);
            log.debug("Преобразовано в Avro запись");
            kafkaProducerService.sendHubEvent(deviceAddedEvent.getHubId(), avroRecord);
            log.info("Завершилась обработка события добавления устройства: hubId={}, deviceId={}",
                    event.getHubId(), deviceAddedEvent.getId());
        } catch (Exception e) {
            log.error("Ошибка обработки события добавления устройства: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process device added event", e);
        }
    }
}