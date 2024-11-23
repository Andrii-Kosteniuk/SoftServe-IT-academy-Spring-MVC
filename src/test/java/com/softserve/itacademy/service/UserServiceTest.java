package com.softserve.itacademy.service;

import com.softserve.itacademy.dto.userDto.CreateUserDto;
import com.softserve.itacademy.dto.userDto.UserDto;
import com.softserve.itacademy.dto.userDto.UserDtoConverter;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.model.UserRole;
import com.softserve.itacademy.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
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
