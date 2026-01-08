package com.users.service;

import com.users.dto.UserRequest;
import com.users.dto.UserResponse;
import com.users.entity.User;
import com.users.exception.UserAlreadyExistsException;
import com.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Create a new user
     * @param userRequest the user data to create
     * @return the created user response with generated id
     * @throws UserAlreadyExistsException if email already exists
     */
    @Transactional
    public UserResponse createUser(UserRequest userRequest) {
        // Check if user with email already exists
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new UserAlreadyExistsException(
                "User with email " + userRequest.getEmail() + " already exists"
            );
        }

        // Create and save user entity
        User user = new User(userRequest.getName(), userRequest.getEmail());
        User savedUser = userRepository.save(user);

        // Convert to response DTO
        return new UserResponse(
            savedUser.getId(),
            savedUser.getName(),
            savedUser.getEmail()
        );
    }
}

