package org.example.customerservice;

import com.example.customer.proto.Customer;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomerRepository {
    private final List<Customer> customers = new ArrayList<>();
    public CustomerRepository() {
        customers.add(Customer.newBuilder()
                .setId(1)
                .setName("John Doe")
                .build());

        customers.add(Customer.newBuilder()
                .setId(2)
                .setName("Jane Smith")
                .build());
    }

    public List<Customer> findAll() {
        return new ArrayList<>(customers);
    }

    public Customer findById(int id) {
        return customers.stream().filter(c -> c.getId() == id).findFirst().orElse(null);
    }
}
