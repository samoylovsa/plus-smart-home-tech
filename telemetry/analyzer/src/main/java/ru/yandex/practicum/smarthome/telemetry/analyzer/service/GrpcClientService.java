package ru.yandex.practicum.smarthome.telemetry.analyzer.service;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
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

    @Value("${grpc.client.hub-router.enable-keep-alive:true}")
    private boolean enableKeepAlive;

    @Value("${grpc.client.hub-router.keep-alive-without-calls:true}")
    private boolean keepAliveWithoutCalls;

    @Value("${grpc.client.hub-router.negotiation-type:plaintext}")
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
            log.info("gRPC client initialized for Hub Router on {}:{} (negotiation: {}, keepalive: {})",
                    host, port, negotiationType, enableKeepAlive);
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
