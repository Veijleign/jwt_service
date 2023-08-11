package ru.microservices.jwt_service.core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExtendedExceptionBody {
    private String exceptionName;
    private String grpcStatus;
    private HttpStatus httpStatus;
    private String errorMessage;
    private String requestBody;
    private String methodName;
    private LocalDateTime timestamp;
    private StackTraceElement[] stackTrace;


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("EXCEPTION NAME: ").append("[ ").append(exceptionName).append(" ]").append(System.lineSeparator());
        sb.append("GRPC STATUS: ").append("[ ").append(grpcStatus).append(" ]").append(System.lineSeparator());
        sb.append("HTTP STATUS: ").append("[ ").append(httpStatus).append(" ]").append(System.lineSeparator());
        sb.append("ERROR MESSAGE: ").append("[ ").append(errorMessage).append(" ]").append(System.lineSeparator());
        sb.append("REQUEST BODY: ").append(requestBody).append(System.lineSeparator());
        sb.append("METHOD NAME: ").append("[ ").append(methodName).append(" ]").append(System.lineSeparator());
        sb.append("TIMESTAMP: ").append("[ ").append(timestamp).append(" ]").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("STACKTRACE:").append(System.lineSeparator());
        sb.append("[ ").append(System.lineSeparator());
        String formattedStackTrace = Arrays.stream(stackTrace)
                .map(element -> "\t" + element)
                .collect(Collectors.joining(System.lineSeparator()));
        sb.append(formattedStackTrace);
        sb.append(" ]");
        return sb.toString();
    }
}
