package org.example.accountservice;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.example.accountservice.proto.*;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
public class AccountGrpcService extends AccountServiceGrpc.AccountServiceImplBase {

    private final AccountRepository repository;

    public AccountGrpcService(AccountRepository accountRepository) {
        this.repository = accountRepository;
    }

    @Override
    public void findAll(Empty request, StreamObserver<AccountList> responseObserver) {
        AccountList list = AccountList.newBuilder()
                .addAllAccounts(repository.findAll())
                .build();
        responseObserver.onNext(list);
        responseObserver.onCompleted();
    }

    @Override
    public void findById(AccountId request, StreamObserver<Account> responseObserver) {
        Account account = repository.findById(request.getId());
        if (account != null) {
            responseObserver.onNext(account);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(new RuntimeException("account not found"));
        }
    }

    @Override
    public void create(CreateAccountRequest request, StreamObserver<Account> responseObserver) {
        Account account = repository.create(request.getNumber(), request.getCustomerId());
        responseObserver.onNext(account);
        responseObserver.onCompleted();
    }

}
