package com.stakeholders.service;

import com.stakeholders.model.User;
import com.stakeholders.model.UserRole;
import com.stakeholders.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        users.forEach(user -> user.setPassword(null));
        return users;
    }
    
    public Optional<User> getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            user.get().setPassword(null);
        }
        return user;
    }
    
    public Optional<User> getUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            user.get().setPassword(null);
        }
        return user;
    }
    
    public User blockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        if (user.getRole() == UserRole.ADMINISTRATOR) {
            throw new RuntimeException("Cannot block administrator accounts");
        }
        
        user.setBlocked(true);
        User savedUser = userRepository.save(user);
        savedUser.setPassword(null);
        return savedUser;
    }
    
    public User unblockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        user.setBlocked(false);
        User savedUser = userRepository.save(user);
        savedUser.setPassword(null);
        return savedUser;
    }
    
    public List<User> getUsersByRole(UserRole role) {
        List<User> users = userRepository.findByRole(role);
        users.forEach(user -> user.setPassword(null));
        return users;
    }
    
    public List<User> getBlockedUsers() {
        List<User> users = userRepository.findByBlocked(true);
        users.forEach(user -> user.setPassword(null));
        return users;
    }
    
    public List<User> getActiveUsers() {
        List<User> users = userRepository.findByBlocked(false);
        users.forEach(user -> user.setPassword(null));
        return users;
    }
}

