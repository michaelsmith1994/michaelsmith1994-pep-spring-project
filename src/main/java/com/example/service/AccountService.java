package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Account;
import com.example.exception.ResourceNotFoundException;
import com.example.repository.AccountRepository;

@Service
public class AccountService {

    private AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository){this.accountRepository = accountRepository;};

    public Account createAccount(Account account) {
        if (accountRepository.existsByUsername(account.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        return accountRepository.save(account);
    }
    
    public Account login(String username, String password) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!account.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid password");
        }
        
        return account;
    }

    public Account retrieveAccountByID(Integer id) {
        return accountRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Account id: " + id + " not found."));
    }

    public Account retrieveAccountByUser(String username) {
        return accountRepository.findByUsername(username).orElseThrow(()-> new ResourceNotFoundException("Account username: " + username + " not found."));
    }

    public Account updateAccount(Integer id, Account updatedAccount) {
        return accountRepository.findById(id).map(account -> {
            account.setUsername(updatedAccount.getUsername());
            account.setPassword(updatedAccount.getPassword()); 
            return accountRepository.save(account);
        }).orElse(null);
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public void deleteAccount(Integer id) {
        accountRepository.deleteById(id);
    }

}
