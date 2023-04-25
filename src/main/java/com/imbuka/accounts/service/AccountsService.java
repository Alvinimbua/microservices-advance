package com.imbuka.accounts.service;

import com.imbuka.accounts.model.Accounts;
import com.imbuka.accounts.model.Customer;
import com.imbuka.accounts.repository.AccountsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountsService {
    private final AccountsRepository accountsRepository;

    public Accounts findByCustomerId(Customer customer) {
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
        return accounts;

    }
}
