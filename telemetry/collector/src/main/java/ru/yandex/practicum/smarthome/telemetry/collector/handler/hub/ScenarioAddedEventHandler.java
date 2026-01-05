package ru.yandex.practicum.smarthome.telemetry.collector.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.smarthome.telemetry.collector.converter.AvroConverter;
import ru.yandex.practicum.smarthome.telemetry.collector.converter.ProtoToModelConverter;
import ru.yandex.practicum.smarthome.telemetry.collector.model.hub.ScenarioAddedEvent;
import ru.yandex.practicum.smarthome.telemetry.collector.service.CollectorKafkaProducerService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioAddedEventHandler implements HubEventHandler {

    private final CollectorKafkaProducerService collectorKafkaProducerService;
    private final ProtoToModelConverter protoConverter;
    private final AvroConverter avroConverter;

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    public void handle(HubEventProto event) {
        try {
            log.info("Началась обработка события добавления сценария: hubId={}", event.getHubId());
            ScenarioAddedEvent scenarioAddedEvent = protoConverter.convertScenarioAdded(event);
            log.debug("Преобразовано в Java объект: {}", scenarioAddedEvent);
            SpecificRecord avroRecord = avroConverter.convertToAvro(scenarioAddedEvent);
            log.debug("Преобразовано в Avro запись");
            collectorKafkaProducerService.sendHubEvent(scenarioAddedEvent.getHubId(), avroRecord);
            log.info("Завершилась обработка события добавления сценария: hubId={}, scenario name={}",
                    event.getHubId(), scenarioAddedEvent.getName());
        } catch (Exception e) {
            log.error("Ошибка обработки события добавления сценария: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process scenario added event", e);
        }
    }
}