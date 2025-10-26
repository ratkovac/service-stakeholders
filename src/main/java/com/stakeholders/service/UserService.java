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
        return userRepository.findAll();
    }
    
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public User blockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        if (user.getRole() == UserRole.ROLE_ADMIN) {
            throw new RuntimeException("Cannot block administrator accounts");
        }
        
        user.setBlocked(true);
        return userRepository.save(user);
    }
    
    public User unblockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        user.setBlocked(false);
        return userRepository.save(user);
    }
    
    public List<User> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }
    
    public List<User> getBlockedUsers() {
        return userRepository.findByBlocked(true);
    }
    
    public List<User> getActiveUsers() {
        return userRepository.findByBlocked(false);
    }
}

