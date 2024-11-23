package com.softserve.itacademy.service;

import com.softserve.itacademy.config.exception.DatabaseConnectionException;
import com.softserve.itacademy.config.exception.EmailAlreadyExistsException;
import com.softserve.itacademy.config.exception.NullEntityReferenceException;
import com.softserve.itacademy.dto.userDto.CreateUserDto;
import com.softserve.itacademy.dto.userDto.UpdateUserDto;
import com.softserve.itacademy.dto.userDto.UserDto;
import com.softserve.itacademy.dto.userDto.UserDtoConverter;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.model.UserRole;
import com.softserve.itacademy.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final UserDtoConverter userDtoConverter;

    public UserDto create(CreateUserDto newUser) {
        if (newUser == null) {
            LOGGER.error("Attempted to create a user, but the provided CreateUserDto is null");
            throw new NullEntityReferenceException("User cannot be 'null'");
        }

        LOGGER.info("Starting user creation process for email: {}", newUser.getEmail());

        try {
            userRepository.findByEmail(newUser.getEmail()).ifPresent(existingUser -> {
                LOGGER.warn("User with email {} already exists. Aborting creation.", newUser.getEmail());
                throw new EmailAlreadyExistsException("User with email " + newUser.getEmail() + " already exists");
            });

            LOGGER.debug("No existing user found with email: {}. Proceeding with creation.", newUser.getEmail());
            User user = new User();
            userDtoConverter.fillFields(user, newUser);
            user.setPassword("{noop}" + user.getPassword());

            User savedUser = userRepository.save(user);
            LOGGER.info("User successfully created with ID: {} and email: {}", savedUser.getId(), savedUser.getEmail());
            return userDtoConverter.toDto(savedUser);
        } catch (DataAccessException e) {
            LOGGER.error("Database error while creating user with email {}: {}", newUser.getEmail(), e.getMessage(), e);
            throw new DatabaseConnectionException("Failed to connect to the database during user creation", e);
        }
    }

    public User readById(long id) {
        try {
            LOGGER.info("Attempting to find user with ID: {}", id);
            return userRepository.findById(id).orElseThrow(() -> {
                LOGGER.error("User with ID {} not found", id);
                return new EntityNotFoundException("User with ID " + id + " not found");
            });
        } catch (DataAccessException e) {
            LOGGER.error("Database access error occurred while finding user with ID: {}", id, e);
            throw new DatabaseConnectionException("Database access error occurred", e);
        }
    }

    public UserDto update(UpdateUserDto updateUserDto) {
        if (updateUserDto == null) {
            LOGGER.error("Attempted to update a user, but the provided UpdateUserDto is null");
            throw new NullEntityReferenceException("User cannot be 'null'");
        }

        try {
            User user = userRepository.findById(updateUserDto.getId())
                    .orElseThrow(() -> {
                        LOGGER.error("User not found with ID: {}", updateUserDto.getId());
                        return new EntityNotFoundException("User with ID " + updateUserDto.getId() + " not found");
                    });

            if (!user.getEmail().equals(updateUserDto.getEmail())) {
                userRepository.findByEmail(updateUserDto.getEmail()).ifPresent(existingUser -> {
                    LOGGER.warn("User with email {} already exists. Aborting update.", updateUserDto.getEmail());
                    throw new EmailAlreadyExistsException("User with email " + updateUserDto.getEmail() + " already exists");
                });
            }

            if (user.getRole() == UserRole.ADMIN) {
                user.setRole(updateUserDto.getRole());
            }

            userDtoConverter.fillFields(user, updateUserDto);
            userRepository.save(user);
            LOGGER.info("User successfully updated with ID: {} and email: {}", user.getId(), user.getEmail());
            return userDtoConverter.toDto(user);

        } catch (DataAccessException e) {
            LOGGER.error("Database access error occurred while updating user with ID: {}", updateUserDto.getId(), e);
            throw new DatabaseConnectionException("Database access error occurred.", e);
        }
    }

    public void delete(long id) {
        User user = readById(id);
        userRepository.delete(user);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByEmail(username);
    }

    public Optional<UserDto> findById(long id) {
        return userRepository.findById(id).map(userDtoConverter::toDto);
    }

    public UserDto findByIdThrowing(long id) {
        return userRepository.findById(id).map(userDtoConverter::toDto).orElseThrow(EntityNotFoundException::new);
    }

    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(userDtoConverter::toDto).toList();
    }

    public UpdateUserDto findByIdToUpdate(long id) {
        User user = readById(id);
        return userDtoConverter.toUpdateUserDto(user);
    }
}
