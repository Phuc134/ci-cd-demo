package org.example.customerservice;

import com.example.customer.proto.Account;
import com.example.customer.proto.AccountServiceGrpc;
import com.google.protobuf.Empty;
import io.grpc.StatusRuntimeException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccountClient {

    private final AccountServiceGrpc.AccountServiceBlockingStub accountStub;

    public AccountClient(AccountServiceGrpc.AccountServiceBlockingStub accountStub) {
        this.accountStub = accountStub;
    }

    public List<Account> getAccountsForCustomer(int customerId) {
        try {

            var allAccounts = accountStub.findAll(Empty.newBuilder().build());

            List<Account> customerAccounts = allAccounts.getAccountsList().stream()
                    .filter(a -> a.getCustomerId() == customerId)
                    .toList();

            System.out.println("✅ Received " + customerAccounts.size() + " accounts from account-service");
            return customerAccounts;

        } catch (StatusRuntimeException e) {
            return new ArrayList<>();
        }
    }
}
