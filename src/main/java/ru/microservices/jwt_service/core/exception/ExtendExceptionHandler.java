package ru.microservices.jwt_service.core.exception;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.recovery.GRpcExceptionHandler;
import org.lognet.springboot.grpc.recovery.GRpcExceptionScope;
import org.lognet.springboot.grpc.recovery.GRpcServiceAdvice;

import java.time.LocalDateTime;

@Slf4j
@GRpcServiceAdvice
public class ExtendExceptionHandler {


    @GRpcExceptionHandler
    public Status handleExtendedException(ExtendedException ex, GRpcExceptionScope scope) {
        return getStatus(ex, scope, ex.getClass().getSimpleName());
    }

    @GRpcExceptionHandler
    public Status handleStatusRuntimeException(StatusRuntimeException ex, GRpcExceptionScope scope) {
        return ex.getStatus()
                .augmentDescription(
                        getStatus(
                                ExtendedException.of(ex),
                                scope,
                                ex.getClass().getSimpleName()
                        ).getDescription()
                );
    }

    @GRpcExceptionHandler
    public Status handleAnyException(Exception ex, GRpcExceptionScope scope) {
        ExtendedException extendedException = ExtendedException.of(ex);


        return getStatus(extendedException, scope, ex.getClass().getSimpleName());
    }

    private Status getStatus(ExtendedException ex, GRpcExceptionScope scope, String simpleName) {
        ExtendedError error = ex.getError();

        ExtendedExceptionBody exceptionBody = new ExtendedExceptionBody(
                simpleName,
                error.getGrpcStatus().getCode().name(),
                error.getHttpStatus(),
                ex.getMessage(),
                scope.getRequest() != null ? " { " + scope.getRequest() + " } " : "{ }",
                scope.getMethodDescriptor().getFullMethodName(),
                LocalDateTime.now(),
                ex.getStackTrace()
        );

        return error.getGrpcStatus()
                .withDescription(exceptionBody.toString());
    }

}
