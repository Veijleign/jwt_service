package ru.microservices.jwt_service.external;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;
import ru.microservices.jwt_service.internal.mapper.TokenMapper;
import ru.microservices.jwt_service.internal.service.AuthenticationService;
import ru.microservices.jwt_service.util.StreamObserverUtils;
import ru.microservices.proto.*;

@GRpcService
@Slf4j
@RequiredArgsConstructor
public class JwtServiceGrpc extends AuthServiceGrpc.AuthServiceImplBase {

    private final AuthenticationService authenticationService; // приходит с gateway\
    private final TokenMapper tokenMapper;

    @Override
    public void signIn(SignInRequest request,
                       StreamObserver<TokenResponse> responseObserver) {

        StreamObserverUtils.actionValue(
                responseObserver,
                () -> tokenMapper.toTokenResponse(
                        authenticationService.signIn(
                                request.getUsername(),
                                request.getPassword()
                        )
                )
        );
    }

    @Override
    public void signUp(SignUpRequest request,
                       StreamObserver<Empty> responseObserver) {

        StreamObserverUtils.actionEmpty(
                responseObserver,
                () -> authenticationService.signUp(
                        request.getUsername(),
                        request.getPassword()
                )
        );
    }

    @Override
    public void logout(Empty request,
                       StreamObserver<Empty> responseObserver) {

        authenticationService.logOut();

        Empty empty = Empty.newBuilder().build();

        StreamObserverUtils.actionValue(
                responseObserver,
                () -> empty
        );
    }

    @Override
    public void refreshToken(TokenRequest request,
                             StreamObserver<TokenResponse> responseObserver) {

        StreamObserverUtils.actionValue(
                responseObserver,
                () -> tokenMapper.toTokenResponse(
                        authenticationService.refreshToken(request.getToken())
                )
        );
    }

    @Override
    public void validateToken(TokenRequest request,
                              StreamObserver<ValidateTokenResponse> responseObserver) {

        StreamObserverUtils.actionValue(
                responseObserver,
                () -> tokenMapper.toValidTokenResponse(
                        authenticationService.validateToken(request.getToken())
                )
        );
    }
}
