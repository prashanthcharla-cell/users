package com.users.service;

import com.users.dto.UserRequest;
import com.users.dto.UserResponse;
import com.users.entity.User;
import com.users.exception.UserAlreadyExistsException;
import com.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private UserRequest userRequest;
    private User savedUser;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequest("John Doe", "john.doe@example.com");
        savedUser = new User("John Doe", "john.doe@example.com");
        savedUser.setId(1L);
    }

    @Test
    void shouldCreateUserSuccessfully() {
        // Given
        when(userRepository.existsByEmail(userRequest.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        UserResponse response = userService.createUser(userRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("John Doe");
        assertThat(response.getEmail()).isEqualTo("john.doe@example.com");

        verify(userRepository, times(1)).existsByEmail(userRequest.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        when(userRepository.existsByEmail(userRequest.getEmail())).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> userService.createUser(userRequest))
            .isInstanceOf(UserAlreadyExistsException.class)
            .hasMessageContaining("User with email john.doe@example.com already exists");

        verify(userRepository, times(1)).existsByEmail(userRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldCreateUserWithDifferentEmail() {
        // Given
        UserRequest anotherRequest = new UserRequest("Jane Doe", "jane.doe@example.com");
        User anotherUser = new User("Jane Doe", "jane.doe@example.com");
        anotherUser.setId(2L);

        when(userRepository.existsByEmail(anotherRequest.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(anotherUser);

        // When
        UserResponse response = userService.createUser(anotherRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getName()).isEqualTo("Jane Doe");
        assertThat(response.getEmail()).isEqualTo("jane.doe@example.com");
    }
}

