package org.example.accountservice;

import org.example.accountservice.proto.Account;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
@Repository
public class AccountServiceRepository {
    private List<Account> accounts = new ArrayList<>();
    private final AtomicInteger idCounter = new AtomicInteger(0);

    public AccountServiceRepository() {
        accounts.add(Account.newBuilder()
                .setId(idCounter.incrementAndGet())
                .setNumber("ACC001")
                .setCustomerId(1)
                .build());

        accounts.add(Account.newBuilder()
                .setId(idCounter.incrementAndGet())
                .setNumber("ACC002")
                .setCustomerId(1)
                .build());

        accounts.add(Account.newBuilder()
                .setId(idCounter.incrementAndGet())
                .setNumber("ACC003")
                .setCustomerId(2)
                .build());

    }

    public List<Account> findAll() {
        return new ArrayList<>(accounts);
    }

    public Account findById(int id) {
        return accounts.stream()
                .filter(a -> a.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public Account create(String number, int customerId) {
        Account account = Account.newBuilder()
                .setId(idCounter.incrementAndGet())
                .setNumber(number)
                .setCustomerId(customerId)
                .build();
        accounts.add(account);
        return account;
    }
}
