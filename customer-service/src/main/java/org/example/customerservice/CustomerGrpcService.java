package org.example.customerservice;

import com.example.customer.proto.*;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

import java.util.List;

@GrpcService
public class CustomerGrpcService extends CustomerServiceGrpc.CustomerServiceImplBase {

    private final CustomerRepository customerRepository;
    private final AccountClient accountClient;

    public CustomerGrpcService(CustomerRepository customerRepository, AccountClient accountClient) {
        this.customerRepository = customerRepository;
        this.accountClient = accountClient;
    }

    @Override
    public void findAll(Empty request, StreamObserver<CustomerList> responseObserver) {

        CustomerList list = CustomerList.newBuilder()
                .addAllCustomers(customerRepository.findAll())
                .build();

        responseObserver.onNext(list);
        responseObserver.onCompleted();

    }

    @Override
    public void findById(CustomerId request, StreamObserver<Customer> responseObserver) {

        Customer customer = customerRepository.findById(request.getId());

        if (customer != null) {
            List<Account> accounts = accountClient.getAccountsForCustomer(customer.getId());

            Customer customerWithAccounts = Customer.newBuilder(customer)
                    .addAllAccounts(accounts)
                    .build();

            responseObserver.onNext(customerWithAccounts);
            responseObserver.onCompleted();

        } else {
            responseObserver.onError(new RuntimeException("Customer not found"));
        }
    }
}
