package ru.yandex.practicum.smarthome.telemetry.collector.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.smarthome.telemetry.collector.converter.AvroConverter;
import ru.yandex.practicum.smarthome.telemetry.collector.converter.ProtoToModelConverter;
import ru.yandex.practicum.smarthome.telemetry.collector.model.hub.ScenarioRemovedEvent;
import ru.yandex.practicum.smarthome.telemetry.collector.service.KafkaProducerService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioRemovedEventHandler implements HubEventHandler {

    private final KafkaProducerService kafkaProducerService;
    private final ProtoToModelConverter protoConverter;
    private final AvroConverter avroConverter;

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_REMOVED;
    }

    @Override
    public void handle(HubEventProto event) {
        try {
            log.info("Началась обработка события удаления сценария: hubId={}", event.getHubId());
            ScenarioRemovedEvent scenarioRemovedEvent = protoConverter.convertScenarioRemoved(event);
            log.debug("Преобразовано в Java объект: {}", scenarioRemovedEvent);
            SpecificRecord avroRecord = avroConverter.convertToAvro(scenarioRemovedEvent);
            log.debug("Преобразовано в Avro запись");
            kafkaProducerService.sendHubEvent(scenarioRemovedEvent.getHubId(), avroRecord);
            log.info("Завершилась обработка события удаления сценария: hubId={}, scenario name={}",
                    event.getHubId(), scenarioRemovedEvent.getName());
        } catch (Exception e) {
            log.error("Ошибка обработки события удаления сценария: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process scenario removed event", e);
        }
    }
}
