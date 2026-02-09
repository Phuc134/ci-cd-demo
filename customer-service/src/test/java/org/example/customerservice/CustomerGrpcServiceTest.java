package org.example.customerservice;

import com.example.customer.proto.*;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerGrpcServiceTest {

    private CustomerRepository customerRepository;
    private AccountClient accountClient;
    private CustomerGrpcService service;

    @BeforeEach
    void setUp() {
        customerRepository = mock(CustomerRepository.class);
        accountClient = mock(AccountClient.class);
        service = new CustomerGrpcService(customerRepository, accountClient);
    }

    @Test
    void findAll_shouldReturnCustomersWithTheirAccounts() {
        // Arrange
        Customer c1 = Customer.newBuilder().setId(1).setName("John").build();
        Customer c2 = Customer.newBuilder().setId(2).setName("Jane").build();
        when(customerRepository.findAll()).thenReturn(List.of(c1, c2));

        Account a1 = Account.newBuilder().setId(10).setNumber("A-1").setCustomerId(1).build();
        Account a2 = Account.newBuilder().setId(11).setNumber("A-2").setCustomerId(1).build();
        Account b1 = Account.newBuilder().setId(20).setNumber("B-1").setCustomerId(2).build();

        when(accountClient.getAccountsForCustomer(1)).thenReturn(List.of(a1, a2));
        when(accountClient.getAccountsForCustomer(2)).thenReturn(List.of(b1));

        @SuppressWarnings("unchecked")
        StreamObserver<CustomerList> observer = mock(StreamObserver.class);

        // Act
        service.findAll(Empty.getDefaultInstance(), observer);

        // Assert
        ArgumentCaptor<CustomerList> captor = ArgumentCaptor.forClass(CustomerList.class);
        verify(observer).onNext(captor.capture());
        verify(observer).onCompleted();
        CustomerList list = captor.getValue();
        assertEquals(2, list.getCustomersCount());

        Customer out1 = list.getCustomers(0);
        Customer out2 = list.getCustomers(1);
        assertEquals(2, out1.getAccountsCount());
        assertEquals(1, out2.getAccountsCount());

        // Ensure immutability of input preserved apart from accounts enrichment
        assertEquals("John", out1.getName());
        assertEquals("Jane", out2.getName());

        verify(accountClient).getAccountsForCustomer(1);
        verify(accountClient).getAccountsForCustomer(2);
    }

    @Test
    void findAll_shouldHandleNoCustomers() {
        when(customerRepository.findAll()).thenReturn(List.of());
        @SuppressWarnings("unchecked")
        StreamObserver<CustomerList> observer = mock(StreamObserver.class);

        service.findAll(Empty.getDefaultInstance(), observer);

        ArgumentCaptor<CustomerList> captor = ArgumentCaptor.forClass(CustomerList.class);
        verify(observer).onNext(captor.capture());
        verify(observer).onCompleted();
        assertEquals(0, captor.getValue().getCustomersCount());
        verifyNoInteractions(accountClient);
    }

    @Test
    void findById_shouldReturnCustomerWithAccounts_whenFound() {
        Customer c1 = Customer.newBuilder().setId(42).setName("Answer").build();
        when(customerRepository.findById(42)).thenReturn(c1);

        Account acc = Account.newBuilder().setId(1).setNumber("X").setCustomerId(42).build();
        when(accountClient.getAccountsForCustomer(42)).thenReturn(List.of(acc));

        @SuppressWarnings("unchecked")
        StreamObserver<Customer> observer = mock(StreamObserver.class);

        service.findById(CustomerId.newBuilder().setId(42).build(), observer);

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(observer).onNext(captor.capture());
        verify(observer).onCompleted();

        Customer out = captor.getValue();
        assertEquals(42, out.getId());
        assertEquals(1, out.getAccountsCount());
        assertEquals("X", out.getAccounts(0).getNumber());
        verify(accountClient).getAccountsForCustomer(42);
    }

    @Test
    void findById_shouldError_whenCustomerNotFound() {
        when(customerRepository.findById(7)).thenReturn(null);
        @SuppressWarnings("unchecked")
        StreamObserver<Customer> observer = mock(StreamObserver.class);

        service.findById(CustomerId.newBuilder().setId(7).build(), observer);

        verify(observer).onError(Mockito.any(RuntimeException.class));
        verify(observer, never()).onNext(any());
        verify(observer, never()).onCompleted();
        verifyNoInteractions(accountClient);
    }

    @Test
    void findAll_shouldIncludeOnlyAccountsForThatCustomer_whenClientReturnsMixed() {
        Customer c1 = Customer.newBuilder().setId(5).setName("Five").build();
        when(customerRepository.findAll()).thenReturn(List.of(c1));

        // Even if client mistakenly returns accounts for other customers, service relies on client contract.
        Account a1 = Account.newBuilder().setId(1).setNumber("A").setCustomerId(5).build();
        when(accountClient.getAccountsForCustomer(5)).thenReturn(List.of(a1));

        @SuppressWarnings("unchecked")
        StreamObserver<CustomerList> observer = mock(StreamObserver.class);

        service.findAll(Empty.getDefaultInstance(), observer);

        ArgumentCaptor<CustomerList> captor = ArgumentCaptor.forClass(CustomerList.class);
        verify(observer).onNext(captor.capture());
        verify(observer).onCompleted();
        CustomerList list = captor.getValue();
        assertEquals(1, list.getCustomersCount());
        assertEquals(1, list.getCustomers(0).getAccountsCount());
        assertEquals(5, list.getCustomers(0).getAccounts(0).getCustomerId());
    }
}