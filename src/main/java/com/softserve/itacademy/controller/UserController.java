package com.softserve.itacademy.controller;

import com.softserve.itacademy.dto.userDto.CreateUserDto;
import com.softserve.itacademy.dto.userDto.UpdateUserDto;
import com.softserve.itacademy.dto.userDto.UserDto;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.model.UserRole;
import com.softserve.itacademy.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    public String create(/*add needed parameters*/) {
        //TODO
        return "test";
    }

    @GetMapping("/{id}/read")
    public String read(/*add needed parameters*/) {
        //TODO
        return "test";
    }

    @GetMapping("/{id}")
    public String getbyId(/*add needed parameters*/) {
        //TODO
        return "test";
    }

    @PostMapping("/{id}/update")
    public String update(/*add needed parameters*/) {
        //TODO
        return "test";
    }


    @GetMapping("/{id}/delete")
    public String delete(/*add needed parameters*/) {
        // TODO
        return "test";
    }

    @GetMapping("/all")
    public String getAll(/*add needed parameters*/) {
        // TODO
        return "test";
    }
}
