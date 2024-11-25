package com.softserve.itacademy.component.home;

import com.softserve.itacademy.controller.TaskController;
import com.softserve.itacademy.controller.advice.TaskControllerAdvice;
import com.softserve.itacademy.controller.advice.UserControllerAdvice;
import com.softserve.itacademy.dto.TaskTransformer;
import com.softserve.itacademy.dto.todoDto.ToDoDto;
import com.softserve.itacademy.dto.todoDto.ToDoDtoConverter;
import com.softserve.itacademy.model.*;
import com.softserve.itacademy.service.StateService;
import com.softserve.itacademy.service.TaskService;
import com.softserve.itacademy.service.ToDoService;
import com.softserve.itacademy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@ExtendWith(SpringExtension.class)
public class TaskControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    @MockBean
    private TaskService taskService;
    @MockBean
    private ToDoService todoService;
    @MockBean
    private StateService stateService;
    @MockBean
    private ToDoDtoConverter toDoDtoConverter;
    @MockBean
    private TaskTransformer taskTransformer;


    private Task mockTask;
    private ToDo mockToDo;
    private State mockState;
    private ToDoDto mockToDoDto;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        mockToDo = new ToDo();
        mockToDo.setId(1L);
        mockToDo.setTitle("Mike's To-Do #1");
        mockToDo.setCollaborators(new ArrayList<>());
        mockToDo.setOwner(new User());

        mockState = new State();
        mockState.setId(1L);
        mockState.setName("New");

        mockTask = new Task();
        mockTask.setId(1L);
        mockTask.setName("Task #1");
        mockTask.setPriority(TaskPriority.HIGH);
        mockTask.setState(mockState);
        mockTask.setTodo(mockToDo);

        mockToDoDto = new ToDoDto();
        mockToDoDto.setId(mockToDo.getId());
        mockToDoDto.setTitle(mockToDo.getTitle());

        when(todoService.readById(1L)).thenReturn(mockToDoDto);
        when(toDoDtoConverter.fromDto(mockToDoDto)).thenReturn(mockToDo);
        when(toDoDtoConverter.toDto(mockToDo)).thenReturn(mockToDoDto);
        when(taskService.readById(1L)).thenReturn(mockTask);
        when(stateService.getByName("New")).thenReturn(mockState);

    }

    @Test
    void testGetTasksForToDo() throws Exception {
        when(taskService.getByTodoId(1L)).thenReturn(List.of(mockTask));
        when(userService.getAll()).thenReturn(List.of(new User()));

        mockMvc.perform(get("/tasks/todos/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("todo-tasks"))
                .andExpect(model().attributeExists("tasks", "todo", "todoCollaborators", "allCollaborators"));
    }

    @Test
    void testCreateTaskForm() throws Exception {
        mockMvc.perform(get("/tasks/create/todos/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("create-task"))
                .andExpect(model().attributeExists("task", "todo", "priorities"));
    }

    @Test
    void testCreateTask() throws Exception {
        mockMvc.perform(post("/tasks/create/todos/1")
                        .param("name", "New Task")
                        .param("priority", "LOW"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks/todos/1?success=true"));
    }

    @Test
    void testUpdateTaskForm() throws Exception {
        mockMvc.perform(get("/tasks/1/update/todos/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("update-task"))
                .andExpect(model().attributeExists("task", "todo", "priorities", "states"));
    }

    @Test
    void testUpdateTask() throws Exception {
        mockMvc.perform(post("/tasks/1/update/todos/1")
                        .param("name", "Updated Task")
                        .param("priority", "HIGH"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks/todos/1"));
    }

    @Test
    void testDeleteTask() throws Exception {
        mockMvc.perform(post("/tasks/1/delete/todos/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks/todos/1"));
    }

}