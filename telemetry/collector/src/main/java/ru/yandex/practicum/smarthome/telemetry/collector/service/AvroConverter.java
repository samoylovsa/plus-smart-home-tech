package ru.yandex.practicum.smarthome.telemetry.collector.service;

import org.apache.avro.specific.SpecificRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.smarthome.telemetry.collector.model.hub.*;
import ru.yandex.practicum.smarthome.telemetry.collector.model.sensor.*;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AvroConverter {

    public SpecificRecord convertToAvro(Object event) {
        if (event instanceof SensorEvent) {
            return convertSensorEvent((SensorEvent) event);
        } else if (event instanceof HubEvent) {
            return convertHubEvent((HubEvent) event);
        }
        throw new IllegalArgumentException("Unsupported event type: " + event.getClass());
    }

    private SensorEventAvro convertSensorEvent(SensorEvent event) {
        SensorEventAvro.Builder builder = SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(Instant.ofEpochMilli(event.getTimestamp().toEpochMilli()));
        Object payload = createSensorPayload(event);
        builder.setPayload(payload);
        return builder.build();
    }

    private HubEventAvro convertHubEvent(HubEvent event) {
        HubEventAvro.Builder builder = HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(Instant.ofEpochMilli(event.getTimestamp().toEpochMilli()));
        Object payload = createHubPayload(event);
        builder.setPayload(payload);
        return builder.build();
    }

    private Object createSensorPayload(SensorEvent event) {
        if (event instanceof ClimateSensorEvent climateEvent) {
            return ClimateSensorAvro.newBuilder()
                    .setTemperatureC(climateEvent.getTemperatureC())
                    .setHumidity(climateEvent.getHumidity())
                    .setCo2Level(climateEvent.getCo2Level())
                    .build();
        }

        if (event instanceof LightSensorEvent lightEvent) {
            return LightSensorAvro.newBuilder()
                    .setLinkQuality(lightEvent.getLinkQuality())
                    .setLuminosity(lightEvent.getLuminosity())
                    .build();
        }

        if (event instanceof MotionSensorEvent motionEvent) {
            return MotionSensorAvro.newBuilder()
                    .setLinkQuality(motionEvent.getLinkQuality())
                    .setMotion(motionEvent.getMotion())
                    .setVoltage(motionEvent.getVoltage())
                    .build();
        }

        if (event instanceof SwitchSensorEvent switchEvent) {
            return SwitchSensorAvro.newBuilder()
                    .setState(switchEvent.getState())
                    .build();
        }

        if (event instanceof TemperatureSensorEvent tempEvent) {
            return TemperatureSensorAvro.newBuilder()
                    .setTemperatureC(tempEvent.getTemperatureC())
                    .setTemperatureF(tempEvent.getTemperatureF())
                    .build();
        }

        throw new IllegalArgumentException("Unsupported sensor type: " + event.getClass());
    }

    private Object createHubPayload(HubEvent event) {
        if (event instanceof DeviceAddedEvent deviceEvent) {
            return DeviceAddedEventAvro.newBuilder()
                    .setId(deviceEvent.getId())
                    .setType(DeviceTypeAvro.valueOf(deviceEvent.getDeviceType().name()))
                    .build();
        }

        if (event instanceof DeviceRemovedEvent deviceEvent) {
            return DeviceRemovedEventAvro.newBuilder()
                    .setId(deviceEvent.getId())
                    .build();
        }

        if (event instanceof ScenarioAddedEvent scenarioEvent) {
            List<ScenarioConditionAvro> conditionAvros = scenarioEvent.getConditions().stream()
                    .map(this::convertCondition)
                    .collect(Collectors.toList());
            List<DeviceActionAvro> actionAvros = scenarioEvent.getActions().stream()
                    .map(this::convertAction)
                    .collect(Collectors.toList());
            return ScenarioAddedEventAvro.newBuilder()
                    .setName(scenarioEvent.getName())
                    .setConditions(conditionAvros)
                    .setActions(actionAvros)
                    .build();
        }

        if (event instanceof ScenarioRemovedEvent scenarioEvent) {
            return ScenarioRemovedEventAvro.newBuilder()
                    .setName(scenarioEvent.getName())
                    .build();
        }

        throw new IllegalArgumentException("Unsupported hub event type: " + event.getClass());
    }

    private ScenarioConditionAvro convertCondition(ScenarioCondition condition) {
        ScenarioConditionAvro.Builder builder = ScenarioConditionAvro.newBuilder()
                .setSensorId(condition.getSensorId())
                .setType(ConditionTypeAvro.valueOf(condition.getType().name()))
                .setOperation(ConditionOperationAvro.valueOf(condition.getOperation().name()));
        if (condition.getValue() != null) {
            if (condition.getType() == ConditionType.SWITCH || condition.getType() == ConditionType.MOTION) {
                boolean boolValue = condition.getValue() != 0;
                builder.setValue(boolValue);
            } else {
                builder.setValue(condition.getValue());
            }
        }
        return builder.build();
    }

    private DeviceActionAvro convertAction(DeviceAction action) {
        DeviceActionAvro.Builder builder = DeviceActionAvro.newBuilder()
                .setSensorId(action.getSensorId())
                .setType(ActionTypeAvro.valueOf(action.getType().name()));
        if (action.getValue() != null) {
            builder.setValue(action.getValue());
        }
        return builder.build();
    }
}
