package com.stakeholders.service;

import com.stakeholders.model.User;
import com.stakeholders.model.UserProfile;
import com.stakeholders.model.UserRole;
import com.stakeholders.repository.UserProfileRepository;
import com.stakeholders.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    @Autowired
    public UserService(UserRepository userRepository, UserProfileRepository userProfileRepository) {

        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
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
        
        if (user.getRole() == UserRole.ROLE_ADMIN) {
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

    public Optional<UserProfile> getUserProfile(Long userId) {
        return userProfileRepository.findByUserId(userId);
    }

    public UserProfile createOrUpdateUserProfile(Long userId, String firstName, String lastName, String profilePictureUrl, String biography, String motto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Optional<UserProfile> existingProfile = userProfileRepository.findByUserId(userId);

        UserProfile profile;
        if (existingProfile.isPresent()) {
            profile = existingProfile.get();
        } else {
            profile = new UserProfile();
            profile.setUser(user);
        }

        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        profile.setProfilePicture(profilePictureUrl);
        profile.setBiography(biography);
        profile.setMotto(motto);

        return userProfileRepository.save(profile);
    }
}

