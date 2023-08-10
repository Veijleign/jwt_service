package ru.microservices.jwt_service.config.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcGlobalInterceptor;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.microservices.jwt_service.config.internal.InstanceConfig;
import ru.microservices.proto.AuthServiceGrpc;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GrpcExceptionHandler implements ServerInterceptor {
    private final InstanceConfig instanceConfig;
    private final ObjectMapper objectMapper;

    private record ErrorResponse(
            String errorCode,
            String instanceKey,
            String instanceId,
            Map<String, String> data,
            Map<String, String> stacktrace
    ) {
    }

    private void preInterceptor(ServerCall<?, ?> call) {
        LoggerFactory.getLogger(call.getMethodDescriptor().getServiceName())
                .info("GRPC CALL: '" + call.getMethodDescriptor().getBareMethodName() + "'");
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata requestHeaders,
            ServerCallHandler<ReqT, RespT> next
    ) {
        preInterceptor(call);

        ServerCall.Listener<ReqT> delegate = next.startCall(
                call,
                requestHeaders
        );

        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<>(delegate) {
            @Override
            public void onHalfClose() {
                try {
                    super.onHalfClose();
                } catch (Exception e) {
                    ExtendedException typedException = ExtendedException.of(e);

                    try {
                        final String responseString = objectMapper.writeValueAsString(
                                buildErrorResponse(
                                        typedException,
                                        e
                                )
                        );
                        call.close(
                                typedException.getError().getGrpcStatus()
                                        .withCause(typedException)
                                        .withDescription(responseString),
                                new Metadata()
                        );

                        log.error("GRPC ERROR: " + responseString);
                    } catch (JsonProcessingException ex) {
                        call.close(
                                Status.CANCELLED
                                        .withCause(ex)
                                        .withDescription(ex.getMessage()),
                                new Metadata()
                        );
                        log.error("GRPC ERROR: " + ex.getMessage());
                    }
                }
            }
        };
    }

    private ErrorResponse buildErrorResponse(
            ExtendedException exception,
            Throwable source
    ) {
        final var stacktrace = source.getStackTrace();
        final Map<String, String> stacktraceMap = new LinkedHashMap<>();

        for (StackTraceElement e : stacktrace) {
            if (!stacktraceMap.containsKey(e.getFileName() + "#" + e.getMethodName())) {
                stacktraceMap.put(e.getFileName() + "#" + e.getMethodName(), "at line " + e.getLineNumber());
            }
        }
        return new ErrorResponse(
                exception.getError()
                        .name(),
                instanceConfig.getKey(),
                instanceConfig.getId(),
                exception.getData(),
                stacktraceMap
        );
    }


}
