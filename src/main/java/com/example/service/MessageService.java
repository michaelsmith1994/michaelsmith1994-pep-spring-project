package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Message;
import com.example.exception.ResourceNotFoundException;
import com.example.repository.AccountRepository;
import com.example.repository.MessageRepository;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final AccountRepository accountRepository;


    @Autowired
    public MessageService(MessageRepository messageRepository, AccountRepository accountRepository) {
        this.messageRepository = messageRepository;
        this.accountRepository = accountRepository;
    }

    public Message createMessage(Message message) {
        if (message.getMessageText() == null || message.getMessageText().trim().isEmpty()) {
            throw new IllegalArgumentException("Message text cannot be empty.");
        }
        if (message.getMessageText().length() > 255) {
            throw new IllegalArgumentException("Message text must be under 255 characters.");
        }
                
        // Ensure user exists before saving the message
        if (!accountRepository.existsById(message.getPostedBy())) {
            throw new IllegalArgumentException("User does not exist.");
        }
    

        return messageRepository.save(message);
    }

    // Retrieve a message by ID
    public Message getMessageById(Integer messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message with ID " + messageId + " not found."));
    }

    // Retrieve all messages posted by a specific user
    public List<Message> getMessagesByUser(Integer postedBy) {
        return messageRepository.findByPostedBy(postedBy);
    }

    // Retrieve all messages
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    // Update a message
    public Message updateMessage(Integer messageId, String newMessageText) {
        Message existingMessage = getMessageById(messageId);  // Throws MessageNotFoundException if not found
        if (newMessageText == null || newMessageText.trim().isEmpty() || newMessageText.length() > 255) {
            throw new IllegalArgumentException("Invalid message text.");
        }
        existingMessage.setMessageText(newMessageText);
        return messageRepository.save(existingMessage);
    }

    // Delete a message
    public int deleteMessage(Integer messageId) {
        return messageRepository.deleteByPostedBy(messageId);
    }
}
