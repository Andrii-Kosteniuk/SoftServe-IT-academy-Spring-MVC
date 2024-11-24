package com.softserve.itacademy.component.home;

import com.softserve.itacademy.config.exception.BusinessException;
import com.softserve.itacademy.controller.UserController;
import com.softserve.itacademy.dto.userDto.CreateUserDto;
import com.softserve.itacademy.dto.userDto.UpdateUserDto;
import com.softserve.itacademy.dto.userDto.UserDto;
import com.softserve.itacademy.model.UserRole;
import com.softserve.itacademy.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static com.softserve.itacademy.config.exception.ErrorMessage.*;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void testGetCreateUserPage() throws Exception {
        mockMvc.perform(get("/users/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("create-user"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void testCreateUserSuccess() throws Exception {
        CreateUserDto userDto = createCreateUserDto();

        UserDto createdUserDto = new UserDto();
        createdUserDto.setId(1L);
        createdUserDto.setFirstName("John");
        createdUserDto.setLastName("Doe");
        createdUserDto.setEmail("john@doe.com");

        Mockito.when(userService.create(Mockito.any(CreateUserDto.class))).thenReturn(createdUserDto);

        mockMvc.perform(post("/users/create")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "john@doe.com")
                        .param("password", "password123")
                        .sessionAttr("user", userDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos-user"));

        Mockito.verify(userService).create(Mockito.any(CreateUserDto.class));
    }

    @Test
    void testCreateUserValidationFails() throws Exception {
        mockMvc.perform(post("/users/create")
                        .param("firstName", "")
                        .param("lastName", "")
                        .param("email", "invalid-email")
                        .param("password", "123"))
                .andExpect(status().isOk())
                .andExpect(view().name("create-user"))
                .andExpect(model().attributeHasFieldErrors("user", "firstName", "lastName", "email", "password"));
    }

    @Test
    void testCreateUserEmailAlreadyExists() throws Exception {
        CreateUserDto dto = createCreateUserDto();

        Mockito.doThrow(new BusinessException("409", EMAIL_ALREADY_EXISTS))
                .when(userService).create(Mockito.any(CreateUserDto.class));

        mockMvc.perform(post("/users/create")
                        .param("firstName", dto.getFirstName())
                        .param("lastName", dto.getLastName())
                        .param("email", dto.getEmail())
                        .param("password", dto.getPassword()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", EMAIL_ALREADY_EXISTS))
                .andExpect(view().name("create-user"));

        Mockito.verify(userService, Mockito.times(1)).create(Mockito.any(CreateUserDto.class));
    }

    @Test
    void testCreateUserNullDto() throws Exception {
        Mockito.when(userService.create(Mockito.any())).thenThrow(new BusinessException("400", NULL_ENTITY_REFERENCE));

        mockMvc.perform(post("/users/create")
                        .flashAttr("user", new CreateUserDto()))
                .andExpect(status().isOk())
                .andExpect(view().name("bad-request"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", NULL_ENTITY_REFERENCE));

        Mockito.verify(userService, Mockito.times(1)).create(Mockito.any());
    }

    @Test
    void testCreateUser_DatabaseConnectionError() throws Exception {
        CreateUserDto dto = createCreateUserDto();

        Mockito.doThrow(new BusinessException("500", DATABASE_CONNECTION_ERROR))
                .when(userService).create(Mockito.any(CreateUserDto.class));

        mockMvc.perform(post("/users/create")
                        .param("firstName", dto.getFirstName())
                        .param("lastName", dto.getLastName())
                        .param("email", dto.getEmail())
                        .param("password", dto.getPassword()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", DATABASE_CONNECTION_ERROR))
                .andExpect(view().name("error"));
    }

    @Test
    void testCreateUser_UnexpectedError() throws Exception {
        CreateUserDto dto = createCreateUserDto();

        Mockito.doThrow(new BusinessException("400", UNEXPECTED_ERROR))
                .when(userService).create(Mockito.any(CreateUserDto.class));

        mockMvc.perform(post("/users/create")
                        .param("firstName", dto.getFirstName())
                        .param("lastName", dto.getLastName())
                        .param("email", dto.getEmail())
                        .param("password", dto.getPassword()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", UNEXPECTED_ERROR))
                .andExpect(view().name("bad-request"));
    }

    @Test
    void testCreateUserAndLoginSuccess() throws Exception {
        CreateUserDto dto = createCreateUserDto();
        UserDto userDto = createUserDto();

        Mockito.when(userService.create(Mockito.any(CreateUserDto.class))).thenReturn(userDto);

        mockMvc.perform(post("/users/create")
                        .param("firstName", dto.getFirstName())
                        .param("lastName", dto.getLastName())
                        .param("email", dto.getEmail())
                        .param("password", dto.getPassword()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos-user"))
                .andExpect(request().sessionAttribute("username", "John"))
                .andExpect(request().sessionAttribute("user_id", 1L));

        Mockito.verify(userService, Mockito.times(1)).create(Mockito.any(CreateUserDto.class));
    }

    @Test
    void testUpdateUserViewSuccess() throws Exception {
        long userId = 1L;

        UpdateUserDto updateUserDto = createUpdateUserDto();

        Mockito.when(userService.findByIdToUpdate(userId)).thenReturn(updateUserDto);

        mockMvc.perform(get("/users/{id}/update", userId))
                .andExpect(status().isOk())
                .andExpect(view().name("update-user"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", updateUserDto))
                .andExpect(content().string(containsString("Update existing User")))
                .andExpect(content().string(containsString("Jane")))
                .andExpect(content().string(containsString("Smith")))
                .andExpect(content().string(containsString("jane@smith.com")))
                .andExpect(content().string(containsString("USER")));
    }

    @Test
    void testUpdateUserSuccess() throws Exception {
        long userId = 1L;

        UpdateUserDto updateUserDto = createUpdateUserDto();
        UserDto updatedUserDto = createUserDto();
        updatedUserDto.setFirstName(updateUserDto.getFirstName());
        updatedUserDto.setLastName(updateUserDto.getLastName());
        updatedUserDto.setEmail(updateUserDto.getEmail());

        Mockito.doReturn(updatedUserDto).when(userService).update(updateUserDto);

        mockMvc.perform(post("/users/{id}/update", userId)
                        .flashAttr("user", updateUserDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        Mockito.verify(userService, Mockito.times(1)).update(updateUserDto);
    }

    @Test
    void testUpdateUserValidationFails() throws Exception {
        long userId = 1L;

        UpdateUserDto updateUserDto = createUpdateUserDto();
        updateUserDto.setFirstName("");

        mockMvc.perform(post("/users/{id}/update", userId)
                        .flashAttr("user", updateUserDto))
                .andExpect(status().isOk())
                .andExpect(view().name("update-user"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeHasFieldErrors("user", "firstName"));

        Mockito.verify(userService, Mockito.never()).update(Mockito.any());
    }

    @Test
    void testUpdateUserNotFound() throws Exception {
        long userId = 1L;

        UpdateUserDto updateUserDto = createUpdateUserDto();

        Mockito.doThrow(new BusinessException("404", USER_NOT_FOUND))
                .when(userService).update(updateUserDto);

        mockMvc.perform(post("/users/{id}/update", userId)
                        .flashAttr("user", updateUserDto))
                .andExpect(status().isOk())
                .andExpect(view().name("not-found"))
                .andExpect(model().attribute("code", "404"))
                .andExpect(model().attribute("errorMessage", USER_NOT_FOUND));

        Mockito.verify(userService, Mockito.times(1)).update(updateUserDto);
    }

    @Test
    void testUpdateUser_EmailConflict() throws Exception {
        long userId = 1L;

        UpdateUserDto updateUserDto = createUpdateUserDto();

        Mockito.doThrow(new BusinessException("409", EMAIL_ALREADY_EXISTS))
                .when(userService).update(updateUserDto);

        mockMvc.perform(post("/users/{id}/update", userId)
                        .flashAttr("user", updateUserDto))
                .andExpect(status().isOk())
                .andExpect(view().name("update-user"))
                .andExpect(model().attribute("errorMessage", EMAIL_ALREADY_EXISTS));

        Mockito.verify(userService, Mockito.times(1)).update(updateUserDto);
    }

    @Test
    void testUpdateUserDatabaseError() throws Exception {
        long userId = 1L;

        UpdateUserDto updateUserDto = createUpdateUserDto();

        Mockito.doThrow(new BusinessException("500", DATABASE_CONNECTION_ERROR))
                .when(userService).update(updateUserDto);

        mockMvc.perform(post("/users/{id}/update", userId)
                        .flashAttr("user", updateUserDto))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("errorMessage", DATABASE_CONNECTION_ERROR));

        Mockito.verify(userService, Mockito.times(1)).update(updateUserDto);
    }

    @Test
    void testUpdateUserUnexpectedError() throws Exception {
        long userId = 1L;

        UpdateUserDto updateUserDto = createUpdateUserDto();

        Mockito.doThrow(new BusinessException("400", UNEXPECTED_ERROR))
                .when(userService).update(updateUserDto);

        mockMvc.perform(post("/users/{id}/update", userId)
                        .flashAttr("user", updateUserDto))
                .andExpect(status().isOk())
                .andExpect(view().name("bad-request"))
                .andExpect(model().attribute("errorMessage", UNEXPECTED_ERROR));

        Mockito.verify(userService, Mockito.times(1)).update(updateUserDto);
    }

    @Test
    void testReadUserSuccess() throws Exception {
        long userId = 1L;
        UserDto userDto = createUserDto();

        Mockito.when(userService.findByIdThrowing(userId)).thenReturn(userDto);

        mockMvc.perform(get("/users/{id}/read", userId))
                .andExpect(status().isOk())
                .andExpect(view().name("user-info"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", userDto))
                .andExpect(content().string(containsString("John")));
        Mockito.verify(userService, Mockito.times(1)).findByIdThrowing(userId);
    }

    @Test
    void testReadUserNotFound() throws Exception {
        long userId = 1L;

        Mockito.when(userService.findByIdThrowing(userId))
                .thenThrow(new BusinessException("404", USER_NOT_FOUND));

        mockMvc.perform(get("/users/{id}/read", userId))
                .andExpect(status().isOk())
                .andExpect(view().name("not-found"))
                .andExpect(model().attributeExists("code"))
                .andExpect(model().attribute("code", "404"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", USER_NOT_FOUND));

        Mockito.verify(userService, Mockito.times(1)).findByIdThrowing(userId);
    }

    @Test
    void testReadUserDatabaseError() throws Exception {
        long userId = 1L;

        Mockito.when(userService.findByIdThrowing(userId))
                .thenThrow(new BusinessException("500", DATABASE_CONNECTION_ERROR));

        mockMvc.perform(get("/users/{id}/read", userId))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("code"))
                .andExpect(model().attribute("code", "500"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", DATABASE_CONNECTION_ERROR));

        Mockito.verify(userService, Mockito.times(1)).findByIdThrowing(userId);
    }

    @Test
    void testReadUserUnexpectedError() throws Exception {
        long userId = 1L;

        Mockito.when(userService.findByIdThrowing(userId))
                .thenThrow(new BusinessException("400", UNEXPECTED_ERROR));

        mockMvc.perform(get("/users/{id}/read", userId))
                .andExpect(status().isOk())
                .andExpect(view().name("bad-request"))
                .andExpect(model().attributeExists("code"))
                .andExpect(model().attribute("code", "400"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", UNEXPECTED_ERROR));

        Mockito.verify(userService, Mockito.times(1)).findByIdThrowing(userId);
    }

    @Test
    void testDeleteUserSuccess() throws Exception {
        long userId = 1L;

        Mockito.doNothing().when(userService).delete(userId);

        mockMvc.perform(get("/users/{id}/delete", userId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/all"));

        Mockito.verify(userService, Mockito.times(1)).delete(userId);
    }

    @Test
    void testDeleteUserNotFound() throws Exception {
        long userId = 1L;

        Mockito.doThrow(new BusinessException("404", USER_NOT_FOUND))
                .when(userService).delete(userId);

        mockMvc.perform(get("/users/{id}/delete", userId))
                .andExpect(status().isOk())
                .andExpect(view().name("not-found"))
                .andExpect(model().attributeExists("code"))
                .andExpect(model().attribute("code", "404"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", USER_NOT_FOUND));

        Mockito.verify(userService, Mockito.times(1)).delete(userId);
    }

    @Test
    void testDeleteUserDatabaseError() throws Exception {
        long userId = 1L;

        Mockito.doThrow(new BusinessException("500", DATABASE_CONNECTION_ERROR))
                .when(userService).delete(userId);

        mockMvc.perform(get("/users/{id}/delete", userId))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("code"))
                .andExpect(model().attribute("code", "500"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", DATABASE_CONNECTION_ERROR));

        Mockito.verify(userService, Mockito.times(1)).delete(userId);
    }

    @Test
    void testDeleteUserUnexpectedError() throws Exception {
        long userId = 1L;

        Mockito.doThrow(new BusinessException("400", "Unexpected error"))
                .when(userService).delete(userId);

        mockMvc.perform(get("/users/{id}/delete", userId))
                .andExpect(status().isOk())
                .andExpect(view().name("bad-request"))
                .andExpect(model().attributeExists("code"))
                .andExpect(model().attribute("code", "400"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Unexpected error"));

        Mockito.verify(userService, Mockito.times(1)).delete(userId);
    }

    @Test
    void testGetAllUsersSuccess() throws Exception {
        List<UserDto> users = List.of(
                new UserDto(1L, "John", "Doe", "john@doe.com", UserRole.USER),
                new UserDto(2L, "Jane", "Smith", "jane@smith.com", UserRole.ADMIN)
        );

        Mockito.when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/users/all"))
                .andExpect(status().isOk())
                .andExpect(view().name("users-list"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attribute("users", users))
                .andExpect(content().string(containsString("John")))
                .andExpect(content().string(containsString("jane@smith.com")));

        Mockito.verify(userService, Mockito.times(1)).findAll();
    }

    @Test
    void testGetAllUsersEmptyList() throws Exception {
        List<UserDto> users = Collections.emptyList();

        Mockito.when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/users/all"))
                .andExpect(status().isOk())
                .andExpect(view().name("users-list"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attribute("users", users))
                .andExpect(content().string(not(containsString("John"))));

        Mockito.verify(userService, Mockito.times(1)).findAll();
    }

    @Test
    void testGetAllUsersDatabaseError() throws Exception {
        Mockito.when(userService.findAll()).thenThrow(new BusinessException("500", DATABASE_CONNECTION_ERROR));

        mockMvc.perform(get("/users/all"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("code"))
                .andExpect(model().attribute("code", "500"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", DATABASE_CONNECTION_ERROR));

        Mockito.verify(userService, Mockito.times(1)).findAll();
    }

    @Test
    void testGetAllUsersUnexpectedError() throws Exception {
        Mockito.when(userService.findAll()).thenThrow(new BusinessException("400", UNEXPECTED_ERROR));

        mockMvc.perform(get("/users/all"))
                .andExpect(status().isOk())
                .andExpect(view().name("bad-request"))
                .andExpect(model().attributeExists("code"))
                .andExpect(model().attribute("code", "400"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", UNEXPECTED_ERROR));

        Mockito.verify(userService, Mockito.times(1)).findAll();
    }

    private CreateUserDto createCreateUserDto() {
        CreateUserDto dto = new CreateUserDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john@doe.com");
        dto.setPassword("password123");
        return dto;
    }

    private UserDto createUserDto() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john@doe.com");
        dto.setRole(UserRole.USER);
        return dto;
    }

    private UpdateUserDto createUpdateUserDto() {
        UpdateUserDto dto = new UpdateUserDto();
        dto.setId(1L);
        dto.setFirstName("Jane");
        dto.setLastName("Smith");
        dto.setEmail("jane@smith.com");
        dto.setRole(UserRole.USER);
        return dto;
    }
}
