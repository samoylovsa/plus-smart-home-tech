package ru.yandex.practicum.smarthome.telemetry.analyzer.service;

import com.google.protobuf.Timestamp;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.smarthome.telemetry.analyzer.entity.ActionType;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.URI;

@Slf4j
@Service
@RequiredArgsConstructor
public class GrpcClientService {

    private ManagedChannel channel;
    private HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    @Value("${grpc.client.hub-router.address}")
    private String grpcAddress;

    @Value("${grpc.client.hub-router.enableKeepAlive}")
    private boolean enableKeepAlive;

    @Value("${grpc.client.hub-router.keepAliveWithoutCalls}")
    private boolean keepAliveWithoutCalls;

    @Value("${grpc.client.hub-router.negotiationType}")
    private String negotiationType;

    @PostConstruct
    public void init() {
        try {
            URI uri = new URI(grpcAddress);
            String host = uri.getHost();
            int port = uri.getPort();
            ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder
                    .forAddress(host, port);
            if ("plaintext".equalsIgnoreCase(negotiationType)) {
                channelBuilder.usePlaintext();
            }
            if (enableKeepAlive) {
                channelBuilder.keepAliveWithoutCalls(keepAliveWithoutCalls);
            }
            channel = channelBuilder.build();
            hubRouterClient = HubRouterControllerGrpc.newBlockingStub(channel);
            log.info("gRPC client initialized for Hub Router on {}:{}",
                    host, port);
        } catch (Exception e) {
            log.error("Failed to initialize gRPC client", e);
            throw new RuntimeException("Failed to initialize gRPC client", e);
        }
    }

    @PreDestroy
    public void shutdown() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
            log.debug("gRPC channel shutdown completed");
        }
    }

    public void sendDeviceAction(String hubId, String scenarioName, String sensorId,
                                 ActionType actionType, Integer value, long timestamp) {
        try {
            DeviceActionRequest request = buildDeviceActionRequest(
                    hubId,
                    scenarioName,
                    sensorId,
                    actionType,
                    value,
                    timestamp
            );
            hubRouterClient.handleDeviceAction(request);
        } catch (Exception e) {
            log.error("Unexpected error sending action to hub {}: {} - sensor={}, action={}",
                    hubId, e.getMessage(), sensorId, actionType, e);
        }
    }

    private DeviceActionRequest buildDeviceActionRequest(String hubId, String scenarioName,
                                                         String sensorId, ActionType actionType,
                                                         Integer value, long timestamp) {
        DeviceActionProto.Builder actionBuilder = DeviceActionProto.newBuilder()
                .setSensorId(sensorId)
                .setType(convertActionType(actionType));
        if (value != null) {
            actionBuilder.setValue(value);
        }
        Timestamp protoTimestamp = convertTimestamp(timestamp);
        return DeviceActionRequest.newBuilder()
                .setHubId(hubId)
                .setScenarioName(scenarioName)
                .setAction(actionBuilder.build())
                .setTimestamp(protoTimestamp)
                .build();
    }

    private Timestamp convertTimestamp(long timestamp) {
        long seconds = timestamp / 1000;
        int nanos = (int) ((timestamp % 1000) * 1_000_000);
        return Timestamp.newBuilder()
                .setSeconds(seconds)
                .setNanos(nanos)
                .build();
    }

    private ActionTypeProto convertActionType(ActionType actionType) {
        try {
            return switch (actionType) {
                case ACTIVATE -> ActionTypeProto.ACTIVATE;
                case DEACTIVATE -> ActionTypeProto.DEACTIVATE;
                case INVERSE -> ActionTypeProto.INVERSE;
                case SET_VALUE -> ActionTypeProto.SET_VALUE;
            };
        } catch (IllegalArgumentException e) {
            log.error("Unknown ActionType: {}", actionType, e);
            throw new IllegalArgumentException("Unknown ActionType: " + actionType, e);
        }
    }
}
