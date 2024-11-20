package com.softserve.itacademy.controller;

import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.service.ToDoService;
import com.softserve.itacademy.model.Task;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.TaskService;
import com.softserve.itacademy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/todos")
@RequiredArgsConstructor
public class ToDoController {

    private final ToDoService todoService;
    private final TaskService taskService;
    private final UserService userService;

    @GetMapping("/create/users/{owner_id}")
    public String createToDoForm(/*add needed parameters*/) {
        // TODO
    }

    @PostMapping("/create/users/{owner_id}")
    public String createToDo(/*add needed parameters*/) {
        //TODO
    }

    @GetMapping("/{todo_id}/update/users/{owner_id}")
    public String update(/*add needed parameters*/) {
        // TODO
    }

    @PostMapping("/{todo_id}/update/users/{owner_id}")
    public String update(/*add needed parameters*/) {
        //TODO
    }

    @GetMapping("/{todo_id}/delete/users/{owner_id}")
    public String delete(/*add needed parameters*/) {
        //TODO
    }

    @GetMapping("/all/users/{user_id}")
    public String getAll(/*add needed parameters*/) {
       //TODO
    }

    @GetMapping("/{id}/add")
    public String addCollaborator(/*add needed parameters*/) {
        //TODO
    }

    @GetMapping("/{id}/remove")
    public String removeCollaborator(/*add needed parameters*/) {
        //TODO
    }
}
