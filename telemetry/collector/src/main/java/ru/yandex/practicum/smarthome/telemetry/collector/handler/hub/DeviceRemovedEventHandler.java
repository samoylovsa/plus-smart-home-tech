package ru.yandex.practicum.smarthome.telemetry.collector.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.smarthome.telemetry.collector.converter.AvroConverter;
import ru.yandex.practicum.smarthome.telemetry.collector.converter.ProtoToModelConverter;
import ru.yandex.practicum.smarthome.telemetry.collector.model.hub.DeviceRemovedEvent;
import ru.yandex.practicum.smarthome.telemetry.collector.service.KafkaProducerService;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceRemovedEventHandler implements HubEventHandler {

    private final KafkaProducerService kafkaProducerService;
    private final ProtoToModelConverter protoConverter;
    private final AvroConverter avroConverter;

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_REMOVED;
    }

    @Override
    public void handle(HubEventProto event) {
        try {
            log.info("Началась обработка события удаления устройства: hubId={}", event.getHubId());
            DeviceRemovedEvent deviceRemovedEvent = protoConverter.convertDeviceRemoved(event);
            log.debug("Преобразовано в Java объект: {}", deviceRemovedEvent);
            SpecificRecord avroRecord = avroConverter.convertToAvro(deviceRemovedEvent);
            log.debug("Преобразовано в Avro запись");
            kafkaProducerService.sendHubEvent(deviceRemovedEvent.getHubId(), avroRecord);
            log.info("Завершилась обработка события удаления устройства: hubId={}, deviceId={}",
                    event.getHubId(), deviceRemovedEvent.getId());
        } catch (Exception e) {
            log.error("Ошибка обработки события удаления устройства: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process device removed event", e);
        }
    }
}
