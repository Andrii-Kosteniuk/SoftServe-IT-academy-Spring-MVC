package com.softserve.itacademy.controller;

import com.softserve.itacademy.dto.todoDto.ToDoDto;
import com.softserve.itacademy.service.ToDoService;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@Controller
@RequestMapping("/todos")
@RequiredArgsConstructor
public class ToDoController {

    private final ToDoService todoService;
    private final UserService userService;

    @GetMapping("/create/users/{owner_id}")
    public String createToDoForm(@PathVariable String owner_id, Model model) {
        model.addAllAttributes(Map.of("owner_id", owner_id, "todo", new ToDoDto()));
        return "create-todo";
    }

    @PostMapping("/create/users/{owner_id}")
    public String createToDo(
            @PathVariable String owner_id,
            @Valid @ModelAttribute("todo") ToDoDto toDoDto,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("owner_id", owner_id);
            return "create-todo";
        }

        todoService.create(toDoDto.getTitle(), owner_id);
        return "redirect:/todos/all/users/%s".formatted(owner_id);
    }


    @GetMapping("/{todo_id}/update/users/{owner_id}")
    public String getToDoById(
            @PathVariable String todo_id,
            @PathVariable String owner_id,
            Model model
    ) {
        ToDoDto toDoDto = todoService.readById(Long.parseLong(todo_id));

        model.addAllAttributes(Map.of("owner_id", owner_id, "todo", toDoDto));
        return "update-todo";
    }

    @PostMapping("/{todo_id}/update/users/{owner_id}")
    public String update(
            @PathVariable String todo_id,
            @PathVariable String owner_id,
            @Valid @ModelAttribute("todo") ToDoDto toDoDto,
            BindingResult bindingResult,
            Model model
    ) {

        ToDoDto existingToDoDto = todoService.readById(Long.parseLong(todo_id));

        if (bindingResult.hasErrors()) {
            toDoDto.setId(existingToDoDto.getId());
            model.addAttribute("todo", toDoDto);
            return "update-todo";
        }

        existingToDoDto.setTitle(toDoDto.getTitle());
        todoService.update(existingToDoDto);

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
}
