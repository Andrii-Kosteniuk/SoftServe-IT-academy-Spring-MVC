package com.softserve.itacademy.controller;

import com.softserve.itacademy.config.exception.BusinessException;
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
        } catch (BusinessException e) {
            String status = handleBusinessException(e, model);
            return status.equals("conflict") ? "create-user" : status;
        }
    }


    @GetMapping("/{id}/read")
    public String read(@PathVariable("id") Long id, Model model) {
        try {
            UserDto userDto = userService.findByIdThrowing(id);
            model.addAttribute("user", userDto);
            return "user-info";
        } catch (BusinessException e) {
            return handleBusinessException(e, model);
        }
    }

    @GetMapping("/{id}/update")
    public String update(@PathVariable("id") long id, Model model) {
        try {
            UpdateUserDto updateUserDto = userService.findByIdToUpdate(id);

            model.addAttribute("user", updateUserDto);
            return "update-user";
        } catch (BusinessException e) {
            return handleBusinessException(e, model);
        }
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable("id") long id, @ModelAttribute("user") @Valid UpdateUserDto updateUserDto,
                         BindingResult bindingResult, Model model, HttpSession session) {
        if (bindingResult.hasErrors()) {
            LOGGER.warn("Validation failed for user update. Errors: {}", bindingResult.getAllErrors());
            return "update-user";
        }

        try {
            userService.update(updateUserDto);

            Long currentUserId = (Long) session.getAttribute("user_id");
            if (currentUserId != null && currentUserId.equals(id)) {
                session.setAttribute("username", updateUserDto.getFirstName());
            }

            return "redirect:/home";
        } catch (BusinessException e) {
            String status = handleBusinessException(e, model);
            return status.equals("conflict") ? "update-user" : status;
        }
    }


    @GetMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, Model model) {
        try {
            userService.delete(id);
            LOGGER.info("Deleted user with id: {}", id);
            return "redirect:/users/all";
        } catch (BusinessException e) {
            return handleBusinessException(e, model);
        }
     }

    @GetMapping("/all")
    public String getAll(Model model) {
        try {
            List<UserDto> users = userService.findAll();
            model.addAttribute("users", users);
            LOGGER.info("Retrieved users: {}", users.size());
            return "users-list";
        } catch (BusinessException e) {
            return handleBusinessException(e, model);
        }
    }

    private String handleBusinessException(BusinessException e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        model.addAttribute("code", e.getCode());
        return switch (e.getCode()) {
            case "404" -> "not-found";
            case "500" -> "error";
            case "409" -> "conflict";
            default -> "bad-request";
        };
    }
}
