package com.example.socialmedia.service;

import com.example.socialmedia.model.User;
import com.example.socialmedia.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Save a new user to the database
    public void saveUser(User user) {
        if (emailExists(user.getEmail())) {
            throw new RuntimeException("User already exists with this email");
        }
        userRepository.save(user);
    }

    // Check if the email already exists in the database
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean existsById(Long id) {
        return userRepository.findById(id).isPresent();
    }

    public boolean doesUserExist(Long userID) {
        return userRepository.existsById(userID);
    }
    
    // Authenticate user by checking email and password
    public boolean authenticateUser(String email, String password) {
        return userRepository.findByEmail(email)
                .map(user -> user.getPassword().equals(password))
                .orElse(false);
    }

    // Get a user by ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Delete a user by ID
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
