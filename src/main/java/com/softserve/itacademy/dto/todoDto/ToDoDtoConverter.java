package com.softserve.itacademy.dto.todoDto;

import com.softserve.itacademy.model.ToDo;
import org.springframework.stereotype.Component;

@Component
public class ToDoDtoConverter {

    public ToDoDto toDto(ToDo toDo) {
        return ToDoDto
                .builder()
                .id(toDo.getId())
                .title(toDo.getTitle())
                .createdAt(toDo.getCreatedAt())
                .owner(toDo.getOwner())
                .tasks(toDo.getTasks())
                .collaborators(toDo.getCollaborators())
                .build();
    }

    public ToDo fromDto(ToDoDto toDoDto) {
        ToDo toDo = new ToDo();
        toDo.setId(toDoDto.getId());
        toDo.setTitle(toDoDto.getTitle());
        toDo.setOwner(toDoDto.getOwner());
        toDo.setCreatedAt(toDoDto.getCreatedAt());
        toDo.setTasks(toDoDto.getTasks());
        toDo.setCollaborators(toDoDto.getCollaborators());
        return toDo;
    }
}
