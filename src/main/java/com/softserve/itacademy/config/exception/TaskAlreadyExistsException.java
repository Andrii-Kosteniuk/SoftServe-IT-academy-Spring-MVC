package com.softserve.itacademy.config.exception;

public class TaskAlreadyExistsException extends RuntimeException {
    public TaskAlreadyExistsException() {
    }

    public TaskAlreadyExistsException(String message) {
        super(message);
    }
}
