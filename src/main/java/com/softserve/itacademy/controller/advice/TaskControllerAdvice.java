package com.softserve.itacademy.controller.advice;

import com.softserve.itacademy.config.exception.NullEntityReferenceException;
import com.softserve.itacademy.config.exception.TaskAlreadyExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class TaskControllerAdvice {
    @ExceptionHandler({EntityNotFoundException.class})
    public String handleEntityNotFoundException(EntityNotFoundException ex, RedirectAttributes redirectAttributes) {
        HttpStatusCode code = HttpStatusCode.valueOf(404);
        redirectAttributes.addFlashAttribute("code", code);
        redirectAttributes.addFlashAttribute("message", ex.getMessage());
        return "redirect:/error";
    }

    @ExceptionHandler(NullEntityReferenceException.class)
    public String handleNullEntityReferenceException(NullEntityReferenceException ex, RedirectAttributes redirectAttributes) {
        HttpStatusCode code = HttpStatusCode.valueOf(404);
        redirectAttributes.addFlashAttribute("code", code);
        redirectAttributes.addFlashAttribute("message", ex.getMessage());
        return "redirect:/error";
    }

    @ExceptionHandler(TaskAlreadyExistsException.class)
    public String handleTaskAlreadyExistsException(TaskAlreadyExistsException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", ex.getMessage());
        return "redirect:/error";
    }


}
