package com.softserve.itacademy.controller;

import com.softserve.itacademy.config.exception.EmailAlreadyExistsException;
import com.softserve.itacademy.config.exception.NullEntityReferenceException;
import com.softserve.itacademy.dto.userDto.CreateUserDto;
import com.softserve.itacademy.dto.userDto.UpdateUserDto;
import com.softserve.itacademy.dto.userDto.UserDto;
import com.softserve.itacademy.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public String create(@ModelAttribute("user") @Valid CreateUserDto createUserDto, BindingResult bindingResult, Model model,
                         HttpSession session) {
        if (bindingResult.hasErrors()) {
            LOGGER.warn("Validation failed for user creation. Errors: {}", bindingResult.getAllErrors());
            return "create-user";
        }

        try {
            UserDto userDto = userService.create(createUserDto);

            session.setAttribute("username", userDto.getFirstName());
            session.setAttribute("user_id", userDto.getId());
            LOGGER.info("User logged in after successful registration: {}", userDto.getEmail());

            return "redirect:/todos-user";
        } catch (EmailAlreadyExistsException e) {
            LOGGER.warn("Failed to create user: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "create-user";
        } catch (NullEntityReferenceException e) {
            LOGGER.error("Null entity reference error: {}", e.getMessage());
            model.addAttribute("errorMessage", "Internal error occurred. Please try again later.");
            return "bad-request";
        } catch (Exception e) {
            LOGGER.error("Unexpected error during user creation: {}", e.getMessage());
            model.addAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
            return "bad-request";
        }
    }


    @GetMapping("/{id}/read")
    public String read(@PathVariable("id") Long id, Model model) {
        UserDto userDto = userService.findByIdThrowing(id);
        model.addAttribute("user", userDto);
        return "user-info";
    }

    @GetMapping("/{id}/update")
    public String update(@PathVariable("id") long id, Model model) {
        UpdateUserDto updateUserDto = userService.findByIdToUpdate(id);

        model.addAttribute("user", updateUserDto);
        return "update-user";
    }

    @PostMapping("/{id}/update")
    public String update(@ModelAttribute("user") @Valid UpdateUserDto updateUserDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            LOGGER.warn("Validation failed for user update. Errors: {}", bindingResult.getAllErrors());
            return "update-user";
        }

        try {
            userService.update(updateUserDto);
            return "redirect:/home";
        } catch (EmailAlreadyExistsException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "update-user";
        }
    }


    @GetMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id) {
        userService.delete(id);
        LOGGER.info("Deleted user with id: {}", id);
        return "redirect:/users/all";
    }

    @GetMapping("/all")
    public String getAll(Model model) {
        List<UserDto> users = userService.findAll();
        model.addAttribute("users", users);
        LOGGER.info("Retrieved users: {}", users.size());
        return "users-list";
    }
}
