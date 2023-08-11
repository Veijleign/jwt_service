package ru.microservices.jwt_service.core.exception;

import lombok.Getter;

@Getter
public class ExtendedException extends RuntimeException {
    private final ExtendedError error;

    public ExtendedException(ExtendedError error) {
        super();
        this.error = error;
    }


    public static ExtendedException of(ExtendedError error) {
        return new ExtendedException(error);
    }

    public static ExtendedException of(Throwable cause) {
        return cause instanceof ExtendedException
                ? (ExtendedException) cause
                : new ExtendedException(
                ExtendedError.UNKNOWN
        );
    }
}
