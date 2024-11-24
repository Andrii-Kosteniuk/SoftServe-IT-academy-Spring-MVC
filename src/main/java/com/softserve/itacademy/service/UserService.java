package com.softserve.itacademy.service;

import com.softserve.itacademy.config.exception.BusinessException;
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

import static com.softserve.itacademy.config.exception.ErrorMessage.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final UserDtoConverter userDtoConverter;

    public UserDto create(CreateUserDto newUser) {
        if (newUser == null) {
            LOGGER.error("Attempted to create a user, but the provided CreateUserDto is null");
            throw new BusinessException("400", NULL_ENTITY_REFERENCE);
        }

        try {
            LOGGER.info("Starting user creation process for email: {}", newUser.getEmail());

            userRepository.findByEmail(newUser.getEmail()).ifPresent(existingUser -> {
                LOGGER.warn("User with email {} already exists. Aborting creation.", newUser.getEmail());
                throw new BusinessException("409", EMAIL_ALREADY_EXISTS);
            });

            LOGGER.debug("No existing user found with email: {}. Proceeding with creation.", newUser.getEmail());
            User user = new User();
            userDtoConverter.fillFields(user, newUser);
            user.setPassword("{noop}" + user.getPassword());

            User savedUser = userRepository.save(user);
            LOGGER.info("User successfully created with ID: {} and email: {}", savedUser.getId(), savedUser.getEmail());
            return userDtoConverter.toDto(savedUser);
        } catch (BusinessException e) {
            throw e;
        } catch (DataAccessException e) {
            LOGGER.error("Database connection error while creating user with email: {}", newUser.getEmail(), e);
            throw new BusinessException("500", DATABASE_CONNECTION_ERROR);
        } catch (Exception e) {
            LOGGER.error("Unexpected error while creating user with email: {}", newUser.getEmail(), e);
            throw new BusinessException("400", UNEXPECTED_ERROR);
        }
    }

    public User readById(long id) {
        try {
            LOGGER.info("Attempting to find user with ID: {}", id);
            return userRepository.findById(id).orElseThrow(() -> {
                LOGGER.error("User with ID {} not found", id);
                return new BusinessException("404", USER_NOT_FOUND);
            });
        } catch (BusinessException e) {
            throw e;
        }catch (DataAccessException e) {
            LOGGER.error("Database connection error while finding user with ID: {}", id, e);
            throw new BusinessException("500", DATABASE_CONNECTION_ERROR);
        } catch (Exception e) {
            LOGGER.error("Unexpected error while finding user with ID: {}", id, e);
            throw new BusinessException("400", UNEXPECTED_ERROR);
        }
    }

    public UserDto update(UpdateUserDto updateUserDto) {
        if (updateUserDto == null) {
            LOGGER.error("Attempted to update a user, but the provided UpdateUserDto is null");
            throw new BusinessException("400", NULL_ENTITY_REFERENCE);
        }

        try {
            LOGGER.info("Starting update process for user with ID: {}", updateUserDto.getId());

            User user = userRepository.findById(updateUserDto.getId())
                    .orElseThrow(() -> {
                        LOGGER.error("User not found with ID: {}", updateUserDto.getId());
                        return new BusinessException("404", USER_NOT_FOUND);
                    });

            if (!user.getEmail().equals(updateUserDto.getEmail())) {
                userRepository.findByEmail(updateUserDto.getEmail()).ifPresent(existingUser -> {
                    LOGGER.warn("User with email {} already exists. Aborting update.", updateUserDto.getEmail());
                    throw new BusinessException("409", EMAIL_ALREADY_EXISTS);
                });
            }

            if (user.getRole() == UserRole.ADMIN) {
                user.setRole(updateUserDto.getRole());
            }

            userDtoConverter.fillFields(user, updateUserDto);
            User updatedUser = userRepository.save(user);

            LOGGER.info("User successfully updated with ID: {} and email: {}", updatedUser.getId(), updatedUser.getEmail());
            return userDtoConverter.toDto(updatedUser);

        } catch (BusinessException e) {
            throw e;
        } catch (DataAccessException e) {
            LOGGER.error("Database connection error while updating user with ID: {}", updateUserDto.getId(), e);
            throw new BusinessException("500", DATABASE_CONNECTION_ERROR);
        } catch (Exception e) {
            LOGGER.error("Unexpected error while updating user with ID: {}", updateUserDto.getId(), e);
            throw new BusinessException("400", UNEXPECTED_ERROR);
        }
    }

    public void delete(long id) {
        try {
            User user = readById(id);
            userRepository.delete(user);
        } catch (BusinessException e) {
            throw e;
        } catch (DataAccessException e) {
            LOGGER.error("Database connection error while deleting user with ID {}: {}", id, e.getMessage());
            throw new BusinessException("500", DATABASE_CONNECTION_ERROR);
        } catch (Exception e) {
            LOGGER.error("Unexpected error during user deletion: {}", e.getMessage());
            throw new BusinessException("400", UNEXPECTED_ERROR);
        }
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
        try {
            LOGGER.info("Attempting to find user with ID: {}", id);
            return userRepository.findById(id).map(userDtoConverter::toDto).orElseThrow(() -> {
                LOGGER.error("User with ID {} not found", id);
                return new BusinessException("404", USER_NOT_FOUND);
            });
        } catch (BusinessException e) {
            throw e;
        }catch (DataAccessException e) {
            LOGGER.error("Database connection error while finding user with ID: {}", id, e);
            throw new BusinessException("500", DATABASE_CONNECTION_ERROR);
        } catch (Exception e) {
            LOGGER.error("Unexpected error while finding user with ID: {}", id, e);
            throw new BusinessException("400", UNEXPECTED_ERROR);
        }
    }

    public List<UserDto> findAll() {
        try {
            return userRepository.findAll().stream()
                    .map(userDtoConverter::toDto)
                    .toList();
        } catch (DataAccessException e) {
            LOGGER.error("Database connection error during fetching all users: {}", e.getMessage(), e);
            throw new BusinessException("500", DATABASE_CONNECTION_ERROR);
        } catch (Exception e) {
            LOGGER.error("Unexpected error during fetching all users: {}", e.getMessage(), e);
            throw new BusinessException("400", UNEXPECTED_ERROR);
        }
    }

    public UpdateUserDto findByIdToUpdate(long id) {
        User user = readById(id);
        return userDtoConverter.toUpdateUserDto(user);
    }
}
