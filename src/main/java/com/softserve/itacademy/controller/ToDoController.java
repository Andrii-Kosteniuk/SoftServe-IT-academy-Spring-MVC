package com.softserve.itacademy.controller;

import com.softserve.itacademy.dto.todoDto.ToDoDto;
import com.softserve.itacademy.dto.userDto.UserDto;
import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.service.ToDoService;
import com.softserve.itacademy.model.Task;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.TaskService;
import com.softserve.itacademy.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
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
    public String createToDoForm(@PathVariable String owner_id, Model model) {
        model.addAttribute("owner_id", owner_id);
        return "create-todo";
    }

    @PostMapping("/create/users/{owner_id}")
    public String createToDo(@PathVariable String owner_id, @RequestParam("title") String title) {

       todoService.create(title, owner_id);

        return "redirect:/todos/all/users/%s".formatted(owner_id);
    }

    @GetMapping("/{todo_id}/update/users/{owner_id}")
    public String getToDoById(@PathVariable String todo_id, @PathVariable String owner_id, Model model) {
        ToDo toDo = todoService.readById(Long.parseLong(todo_id));

        model.addAttribute("todo", toDo);
        return "update-todo";
    }

    @PostMapping("/{todo_id}/update/users/{owner_id}")
    public String update(
            @PathVariable String todo_id,
            @PathVariable String owner_id,
            @Valid @ModelAttribute("todo") ToDo toDo,
            BindingResult bindingResult
    ) {

        if (bindingResult.hasErrors()) {
            return "update-todo";
        }

        ToDo existingToDo = todoService.readById(Long.parseLong(todo_id));
        existingToDo.setTitle(toDo.getTitle());
        todoService.update(existingToDo);

        return "redirect:/todos/all/users/%s".formatted(owner_id);
    }

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
