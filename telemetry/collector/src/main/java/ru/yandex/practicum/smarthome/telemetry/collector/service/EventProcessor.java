package ru.yandex.practicum.smarthome.telemetry.collector.service;

public interface EventProcessor {

    void process(Object event);
}
