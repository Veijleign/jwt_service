package ru.microservices.jwt_service.util;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.experimental.UtilityClass;

import java.util.function.Supplier;

@UtilityClass
public class StreamObserverUtils {
    public <T> void actionValue(StreamObserver<T> observer, Supplier<T> supplier) {
        observer.onNext(supplier.get());
        observer.onCompleted();
    }

    public void actionEmpty(StreamObserver<Empty> observer, Runnable runnable) {
        runnable.run();

        observer.onNext(Empty.getDefaultInstance());
        observer.onCompleted();
    }
}

