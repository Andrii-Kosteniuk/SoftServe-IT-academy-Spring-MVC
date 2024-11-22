package com.softserve.itacademy.controller;

import com.softserve.itacademy.dto.userDto.CreateUserDto;
import com.softserve.itacademy.dto.userDto.UserDto;
import com.softserve.itacademy.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("user", new CreateUserDto());
        return "create-user";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute("user") @Valid CreateUserDto createUserDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            LOGGER.warn("Validation failed for user creation. Errors: {}", bindingResult.getAllErrors());
            return "create-user";
        }

        UserDto userDto = userService.create(createUserDto);
        return "redirect:/todos-user";
    }


//    @GetMapping("/{id}/read")
//    public String read(/*add needed parameters*/) {
//        //TODO
//    }
//
//    @GetMapping("/{id}/update")
//    public String update(/*add needed parameters*/) {
//        //TODO
//    }
//
//    @PostMapping("/{id}/update")
//    public String update(/*add needed parameters*/) {
//        //TODO
//    }
//
//
//    @GetMapping("/{id}/delete")
//    public String delete(/*add needed parameters*/) {
//        // TODO
//    }
//
//    @GetMapping("/all")
//    public String getAll(/*add needed parameters*/) {
//        // TODO
//    }
}
