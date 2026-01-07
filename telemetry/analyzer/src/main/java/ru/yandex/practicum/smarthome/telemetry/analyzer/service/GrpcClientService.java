package ru.yandex.practicum.smarthome.telemetry.analyzer.service;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.smarthome.telemetry.analyzer.entity.ActionType;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@Service
@RequiredArgsConstructor
public class GrpcClientService {

    private ManagedChannel channel;
    private HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    @PostConstruct
    public void init() {
        channel = ManagedChannelBuilder.forAddress("localhost", 59090)
                .usePlaintext()
                .build();
        hubRouterClient = HubRouterControllerGrpc.newBlockingStub(channel);
        log.info("gRPC client initialized for Hub Router on port 59090");
    }

    @PreDestroy
    public void shutdown() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
        }
    }

    public void sendDeviceAction(String hubId, String scenarioName, String sensorId,
                                 ActionType actionType, Integer value, long timestamp) {
        try {
            DeviceActionProto.Builder actionBuilder = DeviceActionProto.newBuilder()
                    .setSensorId(sensorId)
                    .setType(convertActionType(actionType));
            if (value != null) {
                actionBuilder.setValue(value);
            }
            DeviceActionRequest request = DeviceActionRequest.newBuilder()
                    .setHubId(hubId)
                    .setScenarioName(scenarioName)
                    .setAction(actionBuilder.build())
                    .setTimestamp(com.google.protobuf.Timestamp.newBuilder()
                            .setSeconds(timestamp / 1000)
                            .setNanos((int) ((timestamp % 1000) * 1_000_000))
                            .build())
                    .build();

            hubRouterClient.handleDeviceAction(request);
            log.debug("Sent action to hub {} for scenario {}", hubId, scenarioName);
        } catch (StatusRuntimeException e) {
            log.error("Failed to send action to hub {}: gRPC error - {}", hubId, e.getStatus(), e);
        } catch (Exception e) {
            log.error("Failed to send action to hub {}: {}", hubId, e.getMessage(), e);
        }
    }

    private ActionTypeProto convertActionType(ActionType actionType) {
        return switch (actionType) {
            case ACTIVATE -> ActionTypeProto.ACTIVATE;
            case DEACTIVATE -> ActionTypeProto.DEACTIVATE;
            case INVERSE -> ActionTypeProto.INVERSE;
            case SET_VALUE -> ActionTypeProto.SET_VALUE;
        };
    }
}
