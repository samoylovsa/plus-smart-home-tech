package ru.yandex.practicum.smarthome.telemetry.collector.converter;

import org.apache.avro.specific.SpecificRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.smarthome.telemetry.collector.model.hub.*;
import ru.yandex.practicum.smarthome.telemetry.collector.model.sensor.*;

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
                .setTimestamp(event.getTimestamp());

        Object payload = createSensorPayload(event);
        builder.setPayload(payload);

        return builder.build();
    }

    private HubEventAvro convertHubEvent(HubEvent event) {
        HubEventAvro.Builder builder = HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp());

        Object payload = createHubPayload(event);
        builder.setPayload(payload);

        return builder.build();
    }

    private Object createHubPayload(HubEvent event) {
        return switch (event) {
            case DeviceAddedEvent deviceEvent -> createDeviceAddedPayload(deviceEvent);
            case DeviceRemovedEvent deviceEvent -> createDeviceRemovedPayload(deviceEvent);
            case ScenarioAddedEvent scenarioEvent -> createScenarioAddedPayload(scenarioEvent);
            case ScenarioRemovedEvent scenarioEvent -> createScenarioRemovedPayload(scenarioEvent);
            default -> throw new IllegalArgumentException(
                    "Unsupported hub event type: " + event.getClass().getName()
            );
        };
    }

    private Object createSensorPayload(SensorEvent event) {
        return switch (event) {
            case ClimateSensorEvent climateEvent -> createClimateSensorPayload(climateEvent);
            case LightSensorEvent lightEvent -> createLightSensorPayload(lightEvent);
            case MotionSensorEvent motionEvent -> createMotionSensorPayload(motionEvent);
            case SwitchSensorEvent switchEvent -> createSwitchSensorPayload(switchEvent);
            case TemperatureSensorEvent tempEvent -> createTemperatureSensorPayload(tempEvent);
            default -> throw new IllegalArgumentException(
                    "Unsupported sensor type: " + event.getClass().getName()
            );
        };
    }

    private DeviceAddedEventAvro createDeviceAddedPayload(DeviceAddedEvent event) {
        return DeviceAddedEventAvro.newBuilder()
                .setId(event.getId())
                .setType(DeviceTypeAvro.valueOf(event.getDeviceType().name()))
                .build();
    }

    private DeviceRemovedEventAvro createDeviceRemovedPayload(DeviceRemovedEvent event) {
        return DeviceRemovedEventAvro.newBuilder()
                .setId(event.getId())
                .build();
    }

    private ScenarioAddedEventAvro createScenarioAddedPayload(ScenarioAddedEvent event) {
        List<ScenarioConditionAvro> conditionAvros = event.getConditions().stream()
                .map(this::convertCondition)
                .collect(Collectors.toList());
        List<DeviceActionAvro> actionAvros = event.getActions().stream()
                .map(this::convertAction)
                .collect(Collectors.toList());

        return ScenarioAddedEventAvro.newBuilder()
                .setName(event.getName())
                .setConditions(conditionAvros)
                .setActions(actionAvros)
                .build();
    }

    private ScenarioRemovedEventAvro createScenarioRemovedPayload(ScenarioRemovedEvent event) {
        return ScenarioRemovedEventAvro.newBuilder()
                .setName(event.getName())
                .build();
    }

    private ClimateSensorAvro createClimateSensorPayload(ClimateSensorEvent event) {
        return ClimateSensorAvro.newBuilder()
                .setTemperatureC(event.getTemperatureC())
                .setHumidity(event.getHumidity())
                .setCo2Level(event.getCo2Level())
                .build();
    }

    private LightSensorAvro createLightSensorPayload(LightSensorEvent event) {
        return LightSensorAvro.newBuilder()
                .setLinkQuality(event.getLinkQuality())
                .setLuminosity(event.getLuminosity())
                .build();
    }

    private MotionSensorAvro createMotionSensorPayload(MotionSensorEvent event) {
        return MotionSensorAvro.newBuilder()
                .setLinkQuality(event.getLinkQuality())
                .setMotion(event.getMotion())
                .setVoltage(event.getVoltage())
                .build();
    }

    private SwitchSensorAvro createSwitchSensorPayload(SwitchSensorEvent event) {
        return SwitchSensorAvro.newBuilder()
                .setState(event.getState())
                .build();
    }

    private TemperatureSensorAvro createTemperatureSensorPayload(TemperatureSensorEvent event) {
        return TemperatureSensorAvro.newBuilder()
                .setTemperatureC(event.getTemperatureC())
                .setTemperatureF(event.getTemperatureF())
                .build();
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
        } else {
            builder.setValue(null);
        }

        return builder.build();
    }

    private DeviceActionAvro convertAction(DeviceAction action) {
        DeviceActionAvro.Builder builder = DeviceActionAvro.newBuilder()
                .setSensorId(action.getSensorId())
                .setType(ActionTypeAvro.valueOf(action.getType().name()));

        if (action.getValue() != null) {
            builder.setValue(action.getValue());
        } else {
            builder.setValue(null);
        }

        return builder.build();
    }
}
