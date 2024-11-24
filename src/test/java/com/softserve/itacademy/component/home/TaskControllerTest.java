//package com.softserve.itacademy.component.home;
//
//import com.softserve.itacademy.controller.TaskController;
//import com.softserve.itacademy.controller.advice.TaskControllerAdvice;
//import com.softserve.itacademy.controller.advice.UserControllerAdvice;
//import com.softserve.itacademy.dto.TaskDto;
//import com.softserve.itacademy.dto.TaskTransformer;
//import com.softserve.itacademy.dto.todoDto.ToDoDtoConverter;
//import com.softserve.itacademy.model.State;
//import com.softserve.itacademy.model.Task;
//import com.softserve.itacademy.model.TaskPriority;
//import com.softserve.itacademy.model.ToDo;
//import com.softserve.itacademy.service.StateService;
//import com.softserve.itacademy.service.TaskService;
//import com.softserve.itacademy.service.ToDoService;
//import com.softserve.itacademy.service.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.util.Collections;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.doNothing;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(TaskController.class)
//public class TaskControllerTest {
//
//    @MockBean
//    private UserService userService;
//    private MockMvc mockMvc;
//    @Autowired
//    private TaskControllerAdvice taskControllerAdvice;
//    @Autowired
//    private UserControllerAdvice userControllerAdvice;
//    @MockBean
//    private TaskService taskService;
//    @MockBean
//    private ToDoService todoService;
//    @MockBean
//    private StateService stateService;
//    @MockBean
//    private ToDoDtoConverter toDoDtoConverter;
//    @MockBean
//    private TaskTransformer taskTransformer;
//
//
//    private Task mockTask;
//    private ToDo mockToDo;
//    private State mockState;
//
//    @BeforeEach
//    void setup() {
//        MockitoAnnotations.openMocks(this);
//        mockMvc = MockMvcBuilders.standaloneSetup(new TaskController(
//                taskService, todoService, stateService, taskTransformer, userService, toDoDtoConverter)).build();
//
//        toDoDtoConverter.toDto(mockToDo);
//        mockToDo = new ToDo();
//        mockToDo.setId(1L);
//        mockToDo.setTitle("Mike's To-Do #1");
//
//        mockState = new State();
//        mockState.setId(1L);
//        mockState.setName("New");
//
//        mockTask = new Task();
//        mockTask.setId(1L);
//        mockTask.setName("Task #1");
//        mockTask.setPriority(TaskPriority.HIGH);
//        mockTask.setState(mockState);
//        mockTask.setTodo(mockToDo);
//
//
//    }
//
//    @Test
//    void testGetTasksForToDo() throws Exception {
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/tasks/todos/1"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("todo-tasks"))
//                .andExpect(model().attributeExists("tasks", "todo", "todoCollaborators", "allCollaborators"));
//    }
//
//    @Test
//    void testCreateTaskForm() throws Exception {
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/tasks/create/todos/1"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("create-task"))
//                .andExpect(model().attributeExists("task", "todo", "priorities"));
//    }
//
//    @Test
//    void testCreateTask() throws Exception {
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/tasks/create/todos/1")
//                        .param("name", "New Task")
//                        .param("priority", "LOW"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/tasks/todos/1"));
//    }
//
//    @Test
//    void testUpdateTaskForm() throws Exception {
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/tasks/1/update/todos/1"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("update-task"))
//                .andExpect(model().attributeExists("task", "todo", "priorities", "states"));
//    }
//
//    @Test
//    void testUpdateTask() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.post("/tasks/1/update/todos/1")
//                        .param("name", "Updated Task")
//                        .param("priority", "HIGH"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/tasks/todos/1"));
//    }
//
//    @Test
//    void testDeleteTask() throws Exception {
//        doNothing().when(taskService).delete(1L);
//        mockMvc.perform(MockMvcRequestBuilders.post("/tasks/1/delete/todos/1"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/tasks/todos/1"));
//    }
//
//    @Test
//    void testAddCollaborator() throws Exception {
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/tasks/todos/1/add/collaborator")
//                        .param("collaborator_id", "2"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/tasks/todos/1"));
//    }
//
//    @Test
//    void testDeleteCollaborator() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/tasks/delete/todos/1/collaborator/2"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/tasks/todos/1"));
//    }
//}