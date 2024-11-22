package com.softserve.itacademy.controller;

import com.softserve.itacademy.dto.userDto.UserDto;
import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.service.ToDoService;
import com.softserve.itacademy.model.Task;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.TaskService;
import com.softserve.itacademy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.h2.engine.Mode;
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
    public String createToDoForm(@PathVariable String owner_id) {
        return "create-todo";
    }

    @PostMapping("/create/users/{owner_id}")
    public String createToDo(@PathVariable String owner_id, @RequestParam("Title") String title) {

        ToDo toDo = new ToDo();

        toDo.setTitle(title);
        toDo.setOwner(userService.readById(Long.parseLong(owner_id)));
        toDo.setCreatedAt(LocalDateTime.now());

        todoService.create(toDo);

        return "redirect:/todos/all/users/%s".formatted(owner_id);
    }

    @GetMapping("/{todo_id}/update/users/{owner_id}")
    public String getToDoById(/*add needed parameters*/) {
        // TODO
        return "update-todo";
    }

    //
//    @PostMapping("/{todo_id}/update/users/{owner_id}")
//    public String update(/*add needed parameters*/) {
//        //TODO
//        return "test";
//    }
//
    @PostMapping("/{todoId}/remove/users/{userId}")
    public String removeTodo(@PathVariable Long todoId, @PathVariable Long userId) {
        todoService.delete(todoId);
        return "redirect:/todos/all/users/%s".formatted(userId);
    }

    @GetMapping("/all/users/{user_id}")
    public String getAll(@PathVariable String user_id, Model model) {

        User user = userService.readById(Long.parseLong(user_id));
        todoService.changeDataFormat(user.getMyTodos());
        model.addAttribute("user", user);

        return "todo-lists";
    }

//    @GetMapping("/{id}/add")
//    public String addCollaborator(/*add needed parameters*/) {
//        //TODO
//        return "test";
//    }
//
//    @GetMapping("/{id}/remove")
//    public String removeCollaborator(/*add needed parameters*/) {
//        //TODO
//        return "test";
//    }
}
