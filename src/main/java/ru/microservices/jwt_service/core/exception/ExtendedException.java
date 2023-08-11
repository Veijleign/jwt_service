package ru.microservices.jwt_service.core.exception;

import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
public class ExtendedException extends RuntimeException {
    private final ExtendedError error;
    private final Map<String, String> data = new HashMap<>();

    private ExtendedException(
            ExtendedError error,
            Map<String, String> data
    ) {
        this.error = error != null ? error : ExtendedError.UNKNOWN;

        if (data != null && !data.isEmpty()) {
            this.data.putAll(data);
        }
    }

    public static ExtendedException of(ExtendedError error) {
        return new ExtendedException(
                error,
                Collections.emptyMap()
        );
    }

    public static ExtendedException of(
            ExtendedError error,
            Map<String, String> data
    ) {
        return new ExtendedException(
                error,
                data
        );
    }

    public static ExtendedException of(
            Throwable cause
    ) {
        return cause instanceof ExtendedException
                ? (ExtendedException) cause
                : new ExtendedException(
                ExtendedError.UNKNOWN,
                Map.of("message", cause.getMessage().replace("'", "\""))
        );
    }
}
