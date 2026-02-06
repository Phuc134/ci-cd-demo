package org.example.customerservice;

import com.example.customer.proto.AccountServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class GrpcClientConfig {
    @Bean
    AccountServiceGrpc.AccountServiceBlockingStub accountServiceStub(GrpcChannelFactory channels) {
        return AccountServiceGrpc.newBlockingStub(channels.createChannel("account-service"));
    }}
