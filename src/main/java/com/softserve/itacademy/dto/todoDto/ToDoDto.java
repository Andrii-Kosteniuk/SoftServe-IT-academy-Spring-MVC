package com.softserve.itacademy.dto.todoDto;

import com.softserve.itacademy.model.Task;
import com.softserve.itacademy.model.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ToDoDto {
    private long id;

    private String title;

    private LocalDateTime createdAt;

    private User owner;

    private List<Task> tasks;

    private List<User> collaborators;
}
