package com.softserve.itacademy.service;

import com.softserve.itacademy.config.exception.BusinessException;
import com.softserve.itacademy.dto.userDto.CreateUserDto;
import com.softserve.itacademy.dto.userDto.UpdateUserDto;
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
import org.springframework.dao.DataAccessException;


import java.util.List;
import java.util.Optional;

import static com.softserve.itacademy.config.exception.ErrorMessage.*;
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
        User user = createUser();
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
    void testCreateUserIsNullThrowsException() {
        BusinessException exception = assertThrows(BusinessException.class, () -> userService.create(null));

        assertEquals("400", exception.getCode());
        assertEquals(NULL_ENTITY_REFERENCE, exception.getMessage());
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    void testCreateUserWithExistingEmailThrowsException() {
        CreateUserDto createUserDto = createCreateUserDto();
        Mockito.when(userRepository.findByEmail(createUserDto.getEmail()))
                .thenReturn(Optional.of(new User()));

        BusinessException exception = assertThrows(BusinessException.class, () -> userService.create(createUserDto));

        assertEquals("409", exception.getCode());
        assertEquals(EMAIL_ALREADY_EXISTS, exception.getMessage());
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
        assertEquals(DATABASE_CONNECTION_ERROR, exception.getMessage());
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
        assertEquals(UNEXPECTED_ERROR, exception.getMessage());
        Mockito.verify(userRepository).save(Mockito.any(User.class));
    }

    @Test
    void testUpdateUserSuccess() {
        UpdateUserDto updateUserDto = createUpdateUserDto();
        User existingUser = createUser();
        User updatedUser = createUser();
        updatedUser.setFirstName(updateUserDto.getFirstName());
        updatedUser.setLastName(updateUserDto.getLastName());
        updatedUser.setEmail(updateUserDto.getEmail());

        UserDto userDto = new UserDto();
        userDto.setId(updatedUser.getId());
        userDto.setFirstName(updatedUser.getFirstName());
        userDto.setLastName(updatedUser.getLastName());
        userDto.setEmail(updatedUser.getEmail());

        Mockito.when(userRepository.findById(updateUserDto.getId())).thenReturn(Optional.of(existingUser));
        Mockito.doNothing().when(userDtoConverter).fillFields(Mockito.any(User.class), Mockito.eq(updateUserDto));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(updatedUser);
        Mockito.when(userDtoConverter.toDto(updatedUser)).thenReturn(userDto);

        UserDto result = userService.update(updateUserDto);

        assertNotNull(result);
        assertEquals(updateUserDto.getEmail(), result.getEmail());
        Mockito.verify(userRepository).save(Mockito.any(User.class));
    }

    @Test
    void testUpdateUserWithNullThrowsException() {
        BusinessException exception = assertThrows(BusinessException.class, () -> userService.update(null));

        assertEquals("400", exception.getCode());
        assertEquals(NULL_ENTITY_REFERENCE, exception.getMessage());

        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    void testUpdateUserNotFoundThrowsException() {
        UpdateUserDto updateUserDto = createUpdateUserDto();
        Mockito.when(userRepository.findById(updateUserDto.getId())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> userService.update(updateUserDto));

        assertEquals("404", exception.getCode());
        assertEquals(USER_NOT_FOUND, exception.getMessage());

        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    void testUpdateUserWithExistingEmailThrowsException() {
        UpdateUserDto updateUserDto = createUpdateUserDto();
        User existingUser = createUser();
        User conflictingUser = createUser();
        conflictingUser.setEmail(updateUserDto.getEmail());

        Mockito.when(userRepository.findById(updateUserDto.getId())).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.findByEmail(updateUserDto.getEmail())).thenReturn(Optional.of(conflictingUser));

        BusinessException exception = assertThrows(BusinessException.class, () -> userService.update(updateUserDto));

        assertEquals("409", exception.getCode());
        assertEquals(EMAIL_ALREADY_EXISTS, exception.getMessage());

        Mockito.verify(userRepository, Mockito.times(1)).findById(updateUserDto.getId());
        Mockito.verify(userRepository, Mockito.times(1)).findByEmail(updateUserDto.getEmail());
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    void testUpdateUserWithDatabaseConnectionErrorThrowsException() {
        UpdateUserDto updateUserDto = createUpdateUserDto();
        User existingUser = createUser();

        Mockito.when(userRepository.findById(updateUserDto.getId())).thenReturn(Optional.of(existingUser));
        Mockito.doNothing().when(userDtoConverter).fillFields(Mockito.any(User.class), Mockito.eq(updateUserDto));
        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenThrow(new DataAccessException("Database connection error") {});

        BusinessException exception = assertThrows(BusinessException.class, () -> userService.update(updateUserDto));

        assertEquals("500", exception.getCode());
        assertEquals(DATABASE_CONNECTION_ERROR, exception.getMessage());
    }

    @Test
    void testUpdateUserWithUnexpectedErrorThrowsException() {
        UpdateUserDto updateUserDto = createUpdateUserDto();
        User existingUser = createUser();

        Mockito.when(userRepository.findById(updateUserDto.getId())).thenReturn(Optional.of(existingUser));
        Mockito.doNothing().when(userDtoConverter).fillFields(Mockito.any(User.class), Mockito.eq(updateUserDto));
        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        BusinessException exception = assertThrows(BusinessException.class, () -> userService.update(updateUserDto));

        assertEquals("400", exception.getCode());
        assertEquals(UNEXPECTED_ERROR, exception.getMessage());
    }

    @Test
    void testReadByIdSuccess() {
        User user = createUser();

        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User result = userService.readById(user.getId());

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals("John", result.getFirstName());
        Mockito.verify(userRepository, Mockito.times(1)).findById(user.getId());
    }

    @Test
    void testReadByIdUserNotFoundThrowsException() {
        long userId = 1L;
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> userService.readById(userId));

        assertEquals("404", exception.getCode());
        assertEquals(USER_NOT_FOUND, exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
    }

    @Test
    void testReadByIdDatabaseConnectionException() {
        long userId = 1L;
        Mockito.when(userRepository.findById(userId)).thenThrow(new DataAccessException("Database connection error") {});

        BusinessException exception = assertThrows(BusinessException.class, () -> userService.readById(userId));

        assertEquals("500", exception.getCode());
        assertEquals(DATABASE_CONNECTION_ERROR, exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
    }

    @Test
    void testReadByIdUnexpectedException() {
        long userId = 1L;
        Mockito.when(userRepository.findById(userId)).thenThrow(new RuntimeException("Unexpected error"));

        BusinessException exception = assertThrows(BusinessException.class, () -> userService.readById(userId));

        assertEquals("400", exception.getCode());
        assertEquals(UNEXPECTED_ERROR, exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
    }

    @Test
    void testDeleteUserSuccess() {
        User user = createUser();

        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.doNothing().when(userRepository).delete(user);

        assertDoesNotThrow(() -> userService.delete(user.getId()));

        Mockito.verify(userRepository, Mockito.times(1)).findById(user.getId());
        Mockito.verify(userRepository, Mockito.times(1)).delete(user);
    }

    @Test
    void testDeleteUserNotFoundThrowsException() {
        long userId = 1L;
        Mockito.doThrow(new BusinessException("404", USER_NOT_FOUND)).when(userRepository).findById(userId);

        BusinessException exception = assertThrows(BusinessException.class, () -> userService.delete(userId));

        assertEquals("404", exception.getCode());
        assertEquals(USER_NOT_FOUND, exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verify(userRepository, Mockito.never()).delete(Mockito.any());
    }

    @Test
    void testDeleteDatabaseConnectionException() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);

        Mockito.doReturn(Optional.of(user)).when(userRepository).findById(userId);
        Mockito.doThrow(new DataAccessException("Database connection error") {}).when(userRepository).delete(user);

        BusinessException exception = assertThrows(BusinessException.class, () -> userService.delete(userId));

        assertEquals("500", exception.getCode());
        assertEquals(DATABASE_CONNECTION_ERROR, exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verify(userRepository, Mockito.times(1)).delete(user);
    }

    @Test
    void testDeleteUnexpectedException() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);

        Mockito.doReturn(Optional.of(user)).when(userRepository).findById(userId);
        Mockito.doThrow(new RuntimeException("Unexpected error")).when(userRepository).delete(user);

        BusinessException exception = assertThrows(BusinessException.class, () -> userService.delete(userId));

        assertEquals("400", exception.getCode());
        assertEquals(UNEXPECTED_ERROR, exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verify(userRepository, Mockito.times(1)).delete(user);
    }

    @Test
    void testFindAllUsersSuccess() {
        User user1 = createUser(1L, "John", "Doe", "john@doe.com");
        User user2 = createUser(2L, "Jane", "Smith", "jane@smith.com");
        List<User> users = List.of(user1, user2);

        UserDto userDto1 = createUserDto(1L, "John", "Doe", "john@doe.com");
        UserDto userDto2 = createUserDto(2L, "Jane", "Smith", "jane@smith.com");

        Mockito.when(userRepository.findAll()).thenReturn(users);
        Mockito.when(userDtoConverter.toDto(user1)).thenReturn(userDto1);
        Mockito.when(userDtoConverter.toDto(user2)).thenReturn(userDto2);

        List<UserDto> result = userService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John", result.get(0).getFirstName());
        assertEquals("Jane", result.get(1).getFirstName());

        Mockito.verify(userRepository, Mockito.times(1)).findAll();
        Mockito.verify(userDtoConverter, Mockito.times(2)).toDto(Mockito.any(User.class));
    }


    @Test
    void testFindAllUsersDatabaseConnectionError() {
        Mockito.when(userRepository.findAll()).thenThrow(new DataAccessException("Database connection error") {});

        BusinessException exception = assertThrows(BusinessException.class, () -> userService.findAll());
        assertEquals("500", exception.getCode());
        assertEquals(DATABASE_CONNECTION_ERROR, exception.getMessage());

        Mockito.verify(userRepository, Mockito.times(1)).findAll();
        Mockito.verifyNoInteractions(userDtoConverter);
    }

    @Test
    void testFindAllUsersUnexpectedError() {
        Mockito.when(userRepository.findAll()).thenThrow(new RuntimeException("Unexpected error"));

        BusinessException exception = assertThrows(BusinessException.class, () -> userService.findAll());
        assertEquals("400", exception.getCode());
        assertEquals(UNEXPECTED_ERROR, exception.getMessage());

        Mockito.verify(userRepository, Mockito.times(1)).findAll();
        Mockito.verifyNoInteractions(userDtoConverter);
    }

    private CreateUserDto createCreateUserDto() {
        CreateUserDto dto = new CreateUserDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john@doe.com");
        dto.setPassword("password123");
        return dto;
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@doe.com");
        user.setPassword("{noop}password123");
        user.setRole(UserRole.USER);
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

    private UpdateUserDto createUpdateUserDto() {
        UpdateUserDto dto = new UpdateUserDto();
        dto.setId(1L);
        dto.setFirstName("Jane");
        dto.setLastName("Smith");
        dto.setEmail("jane@smith.com");
        return dto;
    }

    private User createUser(Long id, String firstName, String lastName, String email) {
        User user = new User();
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setRole(UserRole.USER);
        return user;
    }

    private UserDto createUserDto(Long id, String firstName, String lastName, String email) {
        UserDto userDto = new UserDto();
        userDto.setId(id);
        userDto.setFirstName(firstName);
        userDto.setLastName(lastName);
        userDto.setEmail(email);
        return userDto;
    }
}
