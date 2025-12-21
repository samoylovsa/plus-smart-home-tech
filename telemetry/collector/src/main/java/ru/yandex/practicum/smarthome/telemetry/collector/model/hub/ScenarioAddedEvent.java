package ru.yandex.practicum.smarthome.telemetry.collector.model.hub;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class ScenarioAddedEvent extends HubEvent {

    @NotBlank(message = "Название сценария не может быть пустым")
    private String name;

    @NotEmpty(message = "Сценарий должен содержать хотя бы одно условие")
    @Valid
    private List<ScenarioCondition> conditions;

    @NotEmpty(message = "Сценарий должен содержать хотя бы одно действие")
    @Valid
    private List<DeviceAction> actions;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}
