package com.eazybytes.springsection1.controller;
import com.eazybytes.springsection1.model.Customer;
import com.eazybytes.springsection1.model.Accounts;
import com.eazybytes.springsection1.repository.AccountsRepository;
import com.eazybytes.springsection1.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountsRepository accountsRepository;
    private final CustomerRepository customerRepository;

    @GetMapping("/myAccount")
    public Accounts getAccountDetails(@RequestParam String email) {
        Optional<Customer> optionalCustomer = customerRepository.findByEmail(email);
        if(optionalCustomer.isPresent()) {
        Accounts accounts = accountsRepository.findByCustomerId(optionalCustomer.get().getId());
        if (accounts != null) {
            return accounts;
        } else {
            return null;
        }
                 }
        else {
            return null;
        }
    }

}
