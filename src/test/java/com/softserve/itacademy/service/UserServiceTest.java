package com.softserve.itacademy.service;

import com.softserve.itacademy.config.exception.BusinessException;
import com.softserve.itacademy.dto.userDto.CreateUserDto;
import com.softserve.itacademy.dto.userDto.UserDto;
import com.softserve.itacademy.dto.userDto.UserDtoConverter;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDtoConverter userDtoConverter;

    @InjectMocks
    private UserService userService;

    @Test
    void testCreateUserSuccess() {
        CreateUserDto createUserDto = createCreateUserDto();
        User user = createUser(createUserDto);
        User savedUser = createSavedUser();
        UserDto userDto = createExpectedUserDto();

        Mockito.when(userRepository.findByEmail(createUserDto.getEmail())).thenReturn(Optional.empty());
        Mockito.doNothing().when(userDtoConverter).fillFields(Mockito.any(User.class), Mockito.eq(createUserDto));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(savedUser);
        Mockito.when(userDtoConverter.toDto(savedUser)).thenReturn(userDto);

        UserDto result = userService.create(createUserDto);

        assertNotNull(result);
        assertEquals("john@doe.com", result.getEmail());
        assertEquals(1L, result.getId());
        Mockito.verify(userRepository).save(Mockito.any(User.class));
    }

    @Test
    void testCreateUserIsNull() {
        BusinessException exception = assertThrows(BusinessException.class, () -> userService.create(null));

        assertEquals("400", exception.getCode());
        assertEquals("User cannot be 'null'", exception.getMessage());
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    void testCreateUserWithExistingEmail() {
        CreateUserDto createUserDto = createCreateUserDto();
        Mockito.when(userRepository.findByEmail(createUserDto.getEmail()))
                .thenReturn(Optional.of(new User()));

        BusinessException exception = assertThrows(BusinessException.class, () -> userService.create(createUserDto));

        assertEquals("409", exception.getCode());
        assertEquals("User with this email already exists", exception.getMessage());
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    void testCreateUserWithDatabaseConnectionErrorThrowsException() {
        CreateUserDto createUserDto = createCreateUserDto();

        Mockito.when(userRepository.findByEmail(createUserDto.getEmail())).thenReturn(Optional.empty());
        Mockito.doNothing().when(userDtoConverter).fillFields(Mockito.any(User.class), Mockito.eq(createUserDto));
        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenThrow(new DataAccessException("Database connection error") {});

        BusinessException exception = assertThrows(BusinessException.class, () -> userService.create(createUserDto));

        assertEquals("500", exception.getCode());
        assertEquals("An internal server error occurred. Please try again later", exception.getMessage());
        Mockito.verify(userRepository).save(Mockito.any(User.class));
    }

    @Test
    void testCreateUserWithUnexpectedErrorThrowsException() {
        CreateUserDto createUserDto = createCreateUserDto();

        Mockito.when(userRepository.findByEmail(createUserDto.getEmail())).thenReturn(Optional.empty());
        Mockito.doNothing().when(userDtoConverter).fillFields(Mockito.any(User.class), Mockito.eq(createUserDto));
        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        BusinessException exception = assertThrows(BusinessException.class, () -> userService.create(createUserDto));

        assertEquals("400", exception.getCode());
        assertEquals("An unexpected error occurred. Please try again later", exception.getMessage());
        Mockito.verify(userRepository).save(Mockito.any(User.class));
    }

    private CreateUserDto createCreateUserDto() {
        CreateUserDto dto = new CreateUserDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john@doe.com");
        dto.setPassword("password123");
        return dto;
    }

    private User createUser(CreateUserDto createUserDto) {
        User user = new User();
        user.setEmail(createUserDto.getEmail());
        user.setPassword("{noop}" + createUserDto.getPassword());
        return user;
    }

    private User createSavedUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("john@doe.com");
        return user;
    }

    private UserDto createExpectedUserDto() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setEmail("john@doe.com");
        return userDto;
    }
}
