package ru.yandex.practicum.smarthome.telemetry.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.smarthome.telemetry.collector.model.hub.HubEvent;

@Slf4j
@Component("hubEventProcessor")
@RequiredArgsConstructor
public class HubEventProcessor implements EventProcessor {

    private final AvroConverter avroConverter;
    private final KafkaProducerService kafkaProducerService;

    @Override
    public void process(Object event) {
        HubEvent hubEvent = castToHubEvent(event);
        SpecificRecord avroRecord = avroConverter.convertToAvro(hubEvent);
        kafkaProducerService.sendHubEvent(hubEvent.getHubId(), avroRecord);
        log.debug("Hub event from {} processed and sent to Kafka", hubEvent.getHubId());
    }

    private boolean supports(Class<?> eventType) {
        return HubEvent.class.isAssignableFrom(eventType);
    }

    private HubEvent castToHubEvent(Object event) {
        if (!supports(event.getClass())) {
            throw new IllegalArgumentException(
                    String.format("HubEventProcessor supports only HubEvent, but got %s",
                            event.getClass().getSimpleName())
            );
        }
        return (HubEvent) event;
    }
}