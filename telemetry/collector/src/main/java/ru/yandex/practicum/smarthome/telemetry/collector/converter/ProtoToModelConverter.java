package ru.yandex.practicum.smarthome.telemetry.collector.converter;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.smarthome.telemetry.collector.model.hub.HubEvent;
import ru.yandex.practicum.smarthome.telemetry.collector.model.hub.*;
import ru.yandex.practicum.smarthome.telemetry.collector.model.sensor.SensorEvent;
import ru.yandex.practicum.smarthome.telemetry.collector.model.sensor.*;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProtoToModelConverter {

    public DeviceAddedEvent convertDeviceAdded(HubEventProto hubEventProto) {
        DeviceAddedEventProto deviceAdded = hubEventProto.getDeviceAdded();
        DeviceAddedEvent deviceAddedEvent = new DeviceAddedEvent();
        setCommonHubFields(deviceAddedEvent, hubEventProto);
        deviceAddedEvent.setId(deviceAdded.getId());
        deviceAddedEvent.setDeviceType(DeviceType.valueOf(deviceAdded.getType().name()));
        return deviceAddedEvent;
    }

    public DeviceRemovedEvent convertDeviceRemoved(HubEventProto hubEventProto) {
        DeviceRemovedEventProto deviceRemoved = hubEventProto.getDeviceRemoved();
        DeviceRemovedEvent event = new DeviceRemovedEvent();
        setCommonHubFields(event, hubEventProto);
        event.setId(deviceRemoved.getId());
        return event;
    }

    public ScenarioAddedEvent convertScenarioAdded(HubEventProto hubEventProto) {
        ScenarioAddedEventProto scenarioAdded = hubEventProto.getScenarioAdded();
        ScenarioAddedEvent event = new ScenarioAddedEvent();
        setCommonHubFields(event, hubEventProto);
        event.setName(scenarioAdded.getName());
        List<ScenarioCondition> conditions = scenarioAdded.getConditionList().stream()
                .map(this::convertCondition)
                .collect(Collectors.toList());
        event.setConditions(conditions);
        List<DeviceAction> actions = scenarioAdded.getActionList().stream()
                .map(this::convertAction)
                .collect(Collectors.toList());
        event.setActions(actions);
        return event;
    }

    public ScenarioRemovedEvent convertScenarioRemoved(HubEventProto hubEventProto) {
        ScenarioRemovedEventProto scenarioRemoved = hubEventProto.getScenarioRemoved();
        ScenarioRemovedEvent event = new ScenarioRemovedEvent();
        setCommonHubFields(event, hubEventProto);
        event.setName(scenarioRemoved.getName());
        return event;
    }

    public MotionSensorEvent convertMotionSensor(SensorEventProto sensorEventProto) {
        MotionSensorProto motion = sensorEventProto.getMotionSensor();
        MotionSensorEvent event = new MotionSensorEvent();
        setCommonSensorFields(event, sensorEventProto);
        event.setLinkQuality(motion.getLinkQuality());
        event.setMotion(motion.getMotion());
        event.setVoltage(motion.getVoltage());
        return event;
    }

    public TemperatureSensorEvent convertTemperatureSensor(SensorEventProto sensorEventProto) {
        TemperatureSensorProto temp = sensorEventProto.getTemperatureSensor();
        TemperatureSensorEvent event = new TemperatureSensorEvent();
        setCommonSensorFields(event, sensorEventProto);
        event.setTemperatureC(temp.getTemperatureC());
        event.setTemperatureF(temp.getTemperatureF());
        return event;
    }

    public LightSensorEvent convertLightSensor(SensorEventProto sensorEventProto) {
        LightSensorProto light = sensorEventProto.getLightSensor();
        LightSensorEvent event = new LightSensorEvent();
        setCommonSensorFields(event, sensorEventProto);
        event.setLinkQuality(light.getLinkQuality());
        event.setLuminosity(light.getLuminosity());
        return event;
    }

    public ClimateSensorEvent convertClimateSensor(SensorEventProto sensorEventProto) {
        ClimateSensorProto climate = sensorEventProto.getClimateSensor();
        ClimateSensorEvent event = new ClimateSensorEvent();
        setCommonSensorFields(event, sensorEventProto);
        event.setTemperatureC(climate.getTemperatureC());
        event.setHumidity(climate.getHumidity());
        event.setCo2Level(climate.getCo2Level());
        return event;
    }

    public SwitchSensorEvent convertSwitchSensor(SensorEventProto sensorEventProto) {
        SwitchSensorProto switchProto = sensorEventProto.getSwitchSensor();
        SwitchSensorEvent event = new SwitchSensorEvent();
        setCommonSensorFields(event, sensorEventProto);
        event.setState(switchProto.getState());
        return event;
    }

    private ScenarioCondition convertCondition(ScenarioConditionProto scenarioConditionProto) {
        ScenarioCondition scenarioCondition = new ScenarioCondition();
        scenarioCondition.setSensorId(scenarioConditionProto.getSensorId());
        scenarioCondition.setType(ConditionType.valueOf(scenarioConditionProto.getType().name()));
        scenarioCondition.setOperation(OperationType.valueOf(scenarioConditionProto.getOperation().name()));
        switch (scenarioConditionProto.getValueCase()) {
            case BOOL_VALUE:
                scenarioCondition.setValue(scenarioConditionProto.getBoolValue() ? 1 : 0);
                break;
            case INT_VALUE:
                scenarioCondition.setValue(scenarioConditionProto.getIntValue());
                break;
            case VALUE_NOT_SET:
                scenarioCondition.setValue(null);
                break;
        }
        return scenarioCondition;
    }

    private DeviceAction convertAction(DeviceActionProto deviceActionProto) {
        DeviceAction deviceAction = new DeviceAction();
        deviceAction.setSensorId(deviceActionProto.getSensorId());
        deviceAction.setType(ActionType.valueOf(deviceActionProto.getType().name()));
        if (deviceActionProto.hasValue()) {
            deviceAction.setValue(deviceActionProto.getValue());
        }
        return deviceAction;
    }

    private void setCommonHubFields(HubEvent hubEvent, HubEventProto hubEventProto) {
        hubEvent.setHubId(hubEventProto.getHubId());
        if (hubEventProto.hasTimestamp()) {
            hubEvent.setTimestamp(Instant.ofEpochSecond(
                    hubEventProto.getTimestamp().getSeconds(),
                    hubEventProto.getTimestamp().getNanos()
            ));
        }
    }

    private void setCommonSensorFields(SensorEvent sensorEvent, SensorEventProto sensorEventProto) {
        sensorEvent.setId(sensorEventProto.getId());
        sensorEvent.setHubId(sensorEventProto.getHubId());
        if (sensorEventProto.hasTimestamp()) {
            sensorEvent.setTimestamp(Instant.ofEpochSecond(
                    sensorEventProto.getTimestamp().getSeconds(),
                    sensorEventProto.getTimestamp().getNanos()
            ));
        }
    }
}
