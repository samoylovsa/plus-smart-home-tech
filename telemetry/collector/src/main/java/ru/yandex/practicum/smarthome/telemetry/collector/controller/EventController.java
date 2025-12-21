package ru.yandex.practicum.smarthome.telemetry.collector.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.smarthome.telemetry.collector.model.hub.HubEvent;
import ru.yandex.practicum.smarthome.telemetry.collector.model.sensor.SensorEvent;
import ru.yandex.practicum.smarthome.telemetry.collector.service.EventService;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping("/sensors")
    @ResponseStatus(HttpStatus.OK)
    public void collectSensorEvent(@Valid @RequestBody SensorEvent event) {
        log.info("Received sensor event with ID: {} and type:{}", event.getId(), event.getType());
        eventService.processSensorEvent(event);
    }

    @PostMapping("/hubs")
    @ResponseStatus(HttpStatus.OK)
    public void collectHubEvent(@Valid @RequestBody HubEvent event) {
        log.info("Received hub event from hub with ID: {}", event.getHubId());
        eventService.processHubEvent(event);
    }
}