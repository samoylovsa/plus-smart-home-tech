package ru.yandex.practicum.smarthome.telemetry.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.smarthome.telemetry.collector.exception.EventProcessingException;
import ru.yandex.practicum.smarthome.telemetry.collector.model.hub.HubEvent;
import ru.yandex.practicum.smarthome.telemetry.collector.model.sensor.SensorEvent;


@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventProcessor sensorEventProcessor;
    private final EventProcessor hubEventProcessor;

    public void processSensorEvent(SensorEvent event) {
        try {
            sensorEventProcessor.process(event);
            log.debug("Sensor event processed successfully: {}", event.getId());
        } catch (Exception e) {
            log.error("Failed to process sensor event: {}", event.getId(), e);
            throw new EventProcessingException("Failed to process sensor event", e);
        }
    }

    public void processHubEvent(HubEvent event) {
        try {
            hubEventProcessor.process(event);
            log.debug("Hub event processed successfully: {}", event.getHubId());
        } catch (Exception e) {
            log.error("Failed to process hub event from hub: {}", event.getHubId(), e);
            throw new EventProcessingException("Failed to process hub event", e);
        }
    }
}
