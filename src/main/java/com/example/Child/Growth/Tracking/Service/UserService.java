package com.example.Child.Growth.Tracking.Service;

import com.example.Child.Growth.Tracking.Model.User;
import com.example.Child.Growth.Tracking.Repository.UserRepository;
import com.example.Child.Growth.Tracking.ulti.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Register user with additional fields like fullname, email, phoneNumber
    public User registerUser(String username, String password, UserRole role, String fullName, String email, String phoneNumber) {
        // Check if username already exists
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already taken");
        }

        // Create new user object
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));  // Encode password before saving
        user.setRole(role != null ? role : UserRole.MEMBER);  // Set role (default to MEMBER if null)
        user.setFullName(fullName);  // Changed from setFullname to setFullName
        user.setEmail(email);  // Set email
        user.setPhoneNumber(phoneNumber);  // Set phone number

        // Save user to database
        return userRepository.save(user);
    }

    // Method to find user by username
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }


    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User updateUser(User updatedUser) {
        User existingUser = userRepository.findById(updatedUser.getId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Update the fields you want to allow changing
        existingUser.setFullName(updatedUser.getFullName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
        existingUser.setRole(updatedUser.getRole());
        
        // Don't update password here unless specifically needed
        // Don't update username unless specifically needed
        
        return userRepository.save(existingUser);
    }
    public User createUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already taken");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(UserRole.MEMBER);
        user.setFullName(user.getFullName());
        user.setEmail(user.getEmail());
        user.setPhoneNumber(user.getPhoneNumber());
        return userRepository.save(user);
    }

    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }
    
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean existsByPhone(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber).isPresent();
    }
}
