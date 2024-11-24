package com.softserve.itacademy.component.home;

import com.softserve.itacademy.controller.ToDoController;
import com.softserve.itacademy.dto.userDto.UserDto;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.ToDoService;
import com.softserve.itacademy.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ToDoController.class)
public class ToDoControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ToDoService todoService;

    @MockBean
    private UserService userService;

    private static final long USER_ID = 4;

    private static final long INVALID_USER_ID = -1;

    @Test
    void todoShouldWorkForAllTodos() throws Exception {
        User mockUser = new User();
        mockUser.setId(USER_ID);
        when(userService.readById(USER_ID)).thenReturn(mockUser);
        Mockito.doNothing().when(todoService).changeDataFormat(anyList());

        mvc.perform(get("/todos/all/users/{owner_id}", USER_ID)
                        .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("todo-lists"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void createToDoForm_IntegrationTest() throws Exception {
        Mockito.when(userService.findById(anyLong())).thenReturn(Optional.of(new UserDto()));

        mvc.perform(get("/todos/create/users/{owner_id}", USER_ID)
                        .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("create-todo"))
                .andExpect(model().attributeExists("owner_id", "todo"));
    }

//    @Test
//    void createToDoForm_ShouldReturn404_WhenOwnerNotFound() throws Exception {
//        Mockito.when(userService.findById(INVALID_USER_ID)).thenReturn(Optional.of(new UserDto()));
//
//        mvc.perform(get("/todos/create/users/{owner_id}", INVALID_USER_ID)
//                        .contentType(MediaType.TEXT_HTML))
//                .andExpect(view().name("error-page"));
////                .andExpect(model().attribute("error", "Owner wasn't found by id: -1"));
//    }
//
//    @Test
//    void getToDoById_IntegrationTest() throws Exception {
//        mvc.perform(get("/todos/{todo_id}/update/users/{owner_id}", USER_ID)
//                .contentType(MediaType.TEXT_HTML))
//                .andExpect(status().isOk())
//                .andExpect(view().name("/todos/all/users/%s".formatted(USER_ID)))
//                .andExpect(model().attributeExists("todo"));
//    }
}
