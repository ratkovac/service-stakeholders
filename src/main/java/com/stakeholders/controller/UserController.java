package com.stakeholders.controller;

import com.stakeholders.model.User;
import com.stakeholders.model.UserRole;
import com.stakeholders.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    
    private final UserService userService;
    
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    private boolean isAdmin(String username) {
        if (username == null) return false;
        return userService.getUserByUsername(username)
            .map(user -> user.getRole().equals(UserRole.ROLE_ADMIN))
            .orElse(false);
    }
    
    @GetMapping
    public ResponseEntity<?> getAllUsers(@RequestHeader(value = "X-Username", required = false) String username) {
        if (username == null || !isAdmin(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Samo administratori mogu pristupiti ovoj funkciji");
        }
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        Optional<User> user = userService.getUserByUsername(username);
        return user.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}/block")
    public ResponseEntity<?> blockUser(@PathVariable Long id, 
                                      @RequestHeader(value = "X-Username", required = false) String username) {
        if (username == null || !isAdmin(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Samo administratori mogu pristupiti ovoj funkciji");
        }
        try {
            User user = userService.blockUser(id);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}/unblock")
    public ResponseEntity<?> unblockUser(@PathVariable Long id, 
                                         @RequestHeader(value = "X-Username", required = false) String username) {
        if (username == null || !isAdmin(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Samo administratori mogu pristupiti ovoj funkciji");
        }
        try {
            User user = userService.unblockUser(id);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable UserRole role) {
        List<User> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/blocked")
    public ResponseEntity<List<User>> getBlockedUsers() {
        List<User> users = userService.getBlockedUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<User>> getActiveUsers() {
        List<User> users = userService.getActiveUsers();
        return ResponseEntity.ok(users);
    }
}

