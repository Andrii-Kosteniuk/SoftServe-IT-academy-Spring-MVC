package com.softserve.itacademy.service;

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

        userRepository.findByEmail(newUser.getEmail()).ifPresent(existingUser -> {
            LOGGER.warn("User with email {} already exists. Aborting creation.", newUser.getEmail());
            throw new EmailAlreadyExistsException("User with email " + newUser.getEmail() + " already exists");
        });

        LOGGER.debug("No existing user found with email: {}. Proceeding with creation.", newUser.getEmail());
        User user = new User();
        userDtoConverter.fillFields(user, newUser);

        User savedUser = userRepository.save(user);
        LOGGER.info("User successfully created with ID: {} and email: {}", savedUser.getId(), savedUser.getEmail());
        return userDtoConverter.toDto(savedUser);
    }

    public User readById(long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User with id " + id + " not found"));
    }

    public UserDto update(UpdateUserDto updateUserDto) {
        if (updateUserDto == null) {
            LOGGER.error("Attempted to update a user, but the provided UpdateUserDto is null");
            throw new NullEntityReferenceException("User cannot be 'null'");
        }

        userRepository.findByEmail(updateUserDto.getEmail()).ifPresent(existingUser -> {
            LOGGER.warn("User with email {} already exists. Aborting update.", updateUserDto.getEmail());
            throw new EmailAlreadyExistsException("User with email " + updateUserDto.getEmail() + " already exists");
        });

        User user = userRepository.findById(updateUserDto.getId()).orElseThrow(EntityNotFoundException::new);
        if (user.getRole() == UserRole.ADMIN) {
            user.setRole(updateUserDto.getRole());
        }
        userDtoConverter.fillFields(user, updateUserDto);
        userRepository.save(user);
        return userDtoConverter.toDto(user);
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
