package org.example.customerservice;

import com.example.customer.proto.Account;
import com.example.customer.proto.AccountList;
import com.example.customer.proto.AccountServiceGrpc;
import com.google.protobuf.Empty;
import io.grpc.StatusRuntimeException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AccountClient {
    private final AccountServiceGrpc.AccountServiceBlockingStub accountStub;

    public AccountClient(AccountServiceGrpc.AccountServiceBlockingStub accountStub) {
        this.accountStub = accountStub;
    }

    public List<Account> getAllAccounts() {
        try {
            AccountList accounts = accountStub.findAll(Empty.newBuilder().build());
            return accounts.getAccountsList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public List<Account> getAccountsForCustomer(int customerId) {
        try {
            var allAccounts = accountStub.findAll(Empty.newBuilder().build());
            return allAccounts.getAccountsList().stream()
                    .filter(a -> a.getCustomerId() == customerId)
                    .toList();
        } catch (StatusRuntimeException e) {
            return new ArrayList<>();
        }
    }
}
