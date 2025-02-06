package com.example.controller;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.exception.ResourceNotFoundException;
import com.example.service.AccountService;
import com.example.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

@Controller
public class SocialMediaController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private MessageService messageService;

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleResourceConflict(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    //---- Account Endpoints ----
    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<?> registerUser(@RequestBody Account account) {
        Account createdAccount = accountService.createAccount(account);
        if (createdAccount != null) {
            return ResponseEntity.ok(createdAccount);
        } else {
            return ResponseEntity.status(409).body("Username already exists");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Account accountRequest) {
        try {
            Account loggedInAccount = accountService.login(accountRequest.getUsername(), accountRequest.getPassword());
            return ResponseEntity.ok(loggedInAccount); 
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(401).body("User not found");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    @GetMapping("/accounts")
    @ResponseBody
    public ResponseEntity<List<Account>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @GetMapping("/accounts/{id}")
    @ResponseBody
    public ResponseEntity<Account> getAccountById(@PathVariable Integer id) {
        return ResponseEntity.ok(accountService.retrieveAccountByID(id));
    }

    @PutMapping("/accounts/{id}")
    @ResponseBody
    public ResponseEntity<Account> updateAccount(@PathVariable Integer id, @RequestBody Account account) {
        return ResponseEntity.ok(accountService.updateAccount(id, account));
    }

    @DeleteMapping("/accounts/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteAccount(@PathVariable Integer id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    //---- Message Endpoints ----
    @PostMapping("/messages")
    public ResponseEntity<?> createMessage(@RequestBody Message message) {
        try {
            Message savedMessage = messageService.createMessage(message);
            return ResponseEntity.ok(savedMessage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/messages")
    @ResponseBody
    public ResponseEntity<List<Message>> getAllMessages() {
        return ResponseEntity.ok(messageService.getAllMessages());
    }

    @GetMapping("/messages/{id}")
    @ResponseBody
    public ResponseEntity<Message> getMessageById(@PathVariable Integer id) {
        try {
            Message message = messageService.getMessageById(id);
            return ResponseEntity.ok(message);  
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.ok(null);
        }
    }

    @PatchMapping("/messages/{id}")
    @ResponseBody
    public ResponseEntity<?> updateMessage(@PathVariable Integer id, @RequestBody Map<String, String> request) {
        String messageText = request.get("messageText");

        try {
            messageService.updateMessage(id, messageText);
            return ResponseEntity.ok(1);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body("Message not found.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid message text.");
        }
    }

    @DeleteMapping("/messages/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteMessage(@PathVariable Integer id) {
        int deletedRows = messageService.deleteMessage(id);
        if (deletedRows == 1) {
            return ResponseEntity.ok(1); 
        }
        return ResponseEntity.ok().build(); 
    }

    @GetMapping("/accounts/{id}/messages")
    @ResponseBody
    public ResponseEntity<List<Message>> getAllMessagesByUser(@PathVariable Integer id) {
        List<Message> messages = messageService.getMessagesByUser(id);
        return ResponseEntity.ok(messages);
    }

}
