package ru.yandex.practicum.smarthome.telemetry.aggregator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SnapshotAggregatorService {

    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        String hubId = event.getHubId();
        String sensorId = event.getId();
        Instant eventTimestamp = event.getTimestamp();
        SensorsSnapshotAvro snapshot = snapshots.computeIfAbsent(hubId,
                id -> createEmptySnapshot(id, eventTimestamp));
        Map<String, SensorStateAvro> sensorsState = new HashMap<>(snapshot.getSensorsState());
        SensorStateAvro oldState = sensorsState.get(sensorId);
        if (shouldUpdate(oldState, event, eventTimestamp)) {
            SensorStateAvro newState = createSensorState(event, eventTimestamp);
            sensorsState.put(sensorId, newState);
            SensorsSnapshotAvro updatedSnapshot = SensorsSnapshotAvro.newBuilder(snapshot)
                    .setTimestamp(eventTimestamp)
                    .setSensorsState(sensorsState)
                    .build();
            snapshots.put(hubId, updatedSnapshot);
            log.debug("Snapshot updated for hub {}: sensor {}", hubId, sensorId);
            return Optional.of(updatedSnapshot);
        }
        log.debug("No update needed for hub {}: sensor {}", hubId, sensorId);
        return Optional.empty();
    }

    private boolean shouldUpdate(SensorStateAvro oldState, SensorEventAvro event, Instant eventTimestamp) {
        if (oldState == null) {
            return true;
        }
        if (eventTimestamp.isBefore(oldState.getTimestamp())) {
            return false;
        }
        Object oldData = oldState.getData();
        Object newData = event.getPayload();
        return !isDataEqual(oldData, newData);
    }

    private boolean isDataEqual(Object oldData, Object newData) {
        if (oldData == null && newData == null) return true;
        if (oldData == null || newData == null) return false;
        return oldData.equals(newData);
    }

    private SensorStateAvro createSensorState(SensorEventAvro event, Instant timestamp) {
        return SensorStateAvro.newBuilder()
                .setTimestamp(timestamp)
                .setData(event.getPayload())
                .build();
    }

    private SensorsSnapshotAvro createEmptySnapshot(String hubId, Instant timestamp) {
        return SensorsSnapshotAvro.newBuilder()
                .setHubId(hubId)
                .setTimestamp(timestamp)
                .setSensorsState(new HashMap<>())
                .build();
    }
}
