package com.softserve.itacademy.controller;

import com.softserve.itacademy.dto.TaskDto;
import com.softserve.itacademy.model.TaskPriority;
import com.softserve.itacademy.service.TaskService;
import com.softserve.itacademy.dto.TaskTransformer;
import com.softserve.itacademy.model.Task;
import com.softserve.itacademy.service.StateService;
import com.softserve.itacademy.service.ToDoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final ToDoService todoService;
    private final StateService stateService;
    private final TaskTransformer taskTransformer;

    @GetMapping("/create/todos/{todo_id}")
    public String create(/*add needed parameters*/) {
        // TODO
    }

    @PostMapping("/create/todos/{todo_id}")
    public String create(/*add needed parameters*/) {
       // TODO
    }

    @GetMapping("/{task_id}/update/todos/{todo_id}")
    public String taskUpdateForm(/*add needed parameters*/) {
        // TODO
    }

    @PostMapping("/{task_id}/update/todos/{todo_id}")
    public String update(/*add needed parameters*/) {
        // TODO
    }

}
