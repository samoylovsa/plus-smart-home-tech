package ru.yandex.practicum.smarthome.telemetry.aggregator.deserializer;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.deserializer.BaseAvroDeserializer;

public class SensorEventDeserializer extends BaseAvroDeserializer<SensorEventAvro> {

    public SensorEventDeserializer() {
        super(SensorEventAvro.getClassSchema());
    }
}
