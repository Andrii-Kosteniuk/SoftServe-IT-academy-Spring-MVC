package com.softserve.itacademy.config.handlers;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionsHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public String entityNotFoundExceptionHandler(EntityNotFoundException entityNotFoundException, Model model, HttpServletResponse httpServletResponse) {

        httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        model.addAttribute("code", 404);
        model.addAttribute("message", entityNotFoundException.getMessage());

        return "error";
    }

}
