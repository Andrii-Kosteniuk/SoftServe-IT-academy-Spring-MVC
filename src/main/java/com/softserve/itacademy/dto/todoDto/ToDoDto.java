package com.softserve.itacademy.dto.todoDto;

import com.softserve.itacademy.model.Task;
import com.softserve.itacademy.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToDoDto {
    private long id;

    @NotBlank(message = "The 'title' cannot be empty")
    @NotNull(message = "The 'title' cannot be empty")
    private String title;

    private LocalDateTime createdAt;

    private User owner;

    private List<Task> tasks;

    private List<User> collaborators;
}
