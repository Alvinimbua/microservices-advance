package com.imbuka.accounts.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.imbuka.accounts.client.CardsFeignClient;
import com.imbuka.accounts.client.LoansFeignClient;
import com.imbuka.accounts.config.AccountsServiceConfig;
import com.imbuka.accounts.model.*;
import com.imbuka.accounts.repository.AccountsRepository;
import com.imbuka.accounts.service.AccountsService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor

public class AccountsController {
    private final AccountsService accountsService;

    private final AccountsServiceConfig accountsServiceConfig;

    private final AccountsRepository accountsRepository;

    private final CardsFeignClient cardsFeignClient;

    private final LoansFeignClient loansFeignClient;

    @PostMapping("api/v1/myAccount")
    public Accounts getAccountDetails(@RequestBody Customer customer) {
        return accountsService.findByCustomerId(customer);
    }

    @GetMapping("/accounts/properties")
    public String getPropertyDetails() throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        Properties properties = new Properties(
                accountsServiceConfig.getMsg(),
                accountsServiceConfig.getBuildVersion(),
                accountsServiceConfig.getMailDetails(),
                accountsServiceConfig.getActiveBranches());
        return ow.writeValueAsString(properties);
    }

    @PostMapping("/api/v1/myCustomerDetails")
   /*
   Implementation for circuit breaker
    @CircuitBreaker(name = "detailsForCustomerSupportApp", fallbackMethod = "myCustomerDetailsFallBack")
    */
    @Retry(name = "retryForCustomerDetails")
    public CustomerDetails myCustomerDetails(@RequestBody Customer customer) {
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
        List<Cards> cards = cardsFeignClient.getCardDetails(customer);
        List<Loans> loans = loansFeignClient.getLoanDetails(customer);

        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setAccounts(accounts);
        customerDetails.setCards(cards);
        customerDetails.setLoans(loans);

        return customerDetails;
    }

    private CustomerDetails myCustomerDetailsFallBack(Customer customer, Throwable throwable) {
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
        List<Loans> loans = loansFeignClient.getLoanDetails(customer);

        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setAccounts(accounts);
        customerDetails.setLoans(loans);

        return customerDetails;
    }

    @GetMapping("/api/v1/sayHello")
    @RateLimiter(name = "sayHello", fallbackMethod = "sayHelloFallback")

    public String sayHello() {
        return "Hello, Welcome to ImbukaBank";
    }

    private String sayHelloFallback(Throwable throwable) {
        return "Hi, Welcome to AlvinBank";
    }



}
