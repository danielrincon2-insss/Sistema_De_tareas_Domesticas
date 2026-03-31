package com.domesticas.application;

import com.domesticas.domain.model.User;
import com.domesticas.application.ports.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String email, String name, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("User with email already exists");
        }
        User user = new User(email, name, password);
        return userRepository.save(user);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.findById(id).ifPresent(userRepository::delete);
    }
}