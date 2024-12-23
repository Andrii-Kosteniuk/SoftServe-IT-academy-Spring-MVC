package com.softserve.itacademy.controller;

import com.softserve.itacademy.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class LoginController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
    private final UserService userService;

    @GetMapping("/login")
    public String login(HttpSession session, Model model) {
        if (session.getAttribute("user_id") != null) {
            LOGGER.info("User with ID {} is already logged in. Redirecting to home page.", session.getAttribute("user_id"));
            return "redirect:/";
        }
        LOGGER.info("Rendering login page.");
        return "login";
    }

    @PostMapping("/login")
    public String loginPost(@RequestParam("username") String email,
                            @RequestParam("password") String password,
                            HttpSession session) {
        LOGGER.info("Attempting login for email: {}", email);

        var userOpt = userService.findByUsername(email);
        if (userOpt.isEmpty()) {
            LOGGER.warn("Login failed: User with email {} not found.", email);
            return "redirect:/login?error=true";
        }

        var user = userOpt.get();
        if (user.getPassword().equals("{noop}" + password)) {
            session.setAttribute("username", user.getFirstName());
            session.setAttribute("user_id", user.getId());

            LOGGER.info("Login successful for user with email {} and ID {}. Redirecting to home page.", email, user.getId());
            return "redirect:/";
        } else {
            LOGGER.warn("Login failed: Incorrect password for email {}", email);
            return "redirect:/login?error=true";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        if (session != null) {
            LOGGER.info("Invalidating session for user with ID: {}", session.getAttribute("user_id"));
            session.invalidate();
        }

        LOGGER.info("User logged out successfully. Redirecting to login page.");
        return "redirect:/login?logout=true";
    }
}
