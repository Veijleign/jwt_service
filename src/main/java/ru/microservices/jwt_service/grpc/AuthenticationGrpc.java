package ru.microservices.jwt_service.grpc;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;
import ru.microservices.authentication_service.*;
import ru.microservices.jwt_service.domain.mapper.TokenMapper;
import ru.microservices.jwt_service.domain.service.AuthenticationService;
import ru.microservices.jwt_service.util.StreamObserverUtils;

@GRpcService
@Slf4j
@RequiredArgsConstructor
public class AuthenticationGrpc extends AuthenticationServiceGrpc.AuthenticationServiceImplBase {

    private final AuthenticationService authenticationService;
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

        StreamObserverUtils.actionEmpty(
                responseObserver,
                authenticationService::logout
        );

    }

    @Override
    public void refreshToken(TokenRequest request,
                             StreamObserver<TokenResponse> responseObserver) {

        StreamObserverUtils.actionValue(
                responseObserver,
                () -> tokenMapper.toTokenResponse(
                        authenticationService.refreshToken(
                                request.getToken()
                        )
                )
        );
    }

    @Override
    public void validateToken(TokenRequest request,
                              StreamObserver<ValidateTokenResponse> responseObserver) {

        StreamObserverUtils.actionValue(
                responseObserver,
                () -> tokenMapper.toValidTokenResponse(
                        authenticationService.validateToken(
                                request.getToken()
                        )
                )
        );
    }
}
