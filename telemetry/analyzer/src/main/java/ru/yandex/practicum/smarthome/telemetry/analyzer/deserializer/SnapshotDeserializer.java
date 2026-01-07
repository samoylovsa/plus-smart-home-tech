package ru.yandex.practicum.smarthome.telemetry.analyzer.deserializer;

import ru.yandex.practicum.deserializer.BaseAvroDeserializer;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public class SnapshotDeserializer extends BaseAvroDeserializer<SensorsSnapshotAvro> {

    public SnapshotDeserializer() {
        super(SensorsSnapshotAvro.getClassSchema());
    }
}
