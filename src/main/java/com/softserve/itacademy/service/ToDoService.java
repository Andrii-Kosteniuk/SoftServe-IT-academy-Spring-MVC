package com.softserve.itacademy.service;

import com.softserve.itacademy.config.exception.NullEntityReferenceException;
import com.softserve.itacademy.dto.todoDto.ToDoDto;
import com.softserve.itacademy.dto.todoDto.ToDoDtoConverter;
import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.repository.ToDoRepository;
import com.softserve.itacademy.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ToDoService {
    private final static String STATIC_TIME_FORMAT = "yyyy-MM-dd HH:mm";

    private final ToDoRepository todoRepository;
    private final ToDoDtoConverter toDoDtoConverter;
    private final UserService userService;

    public ToDoDto create(String title, String owner_id) {
        if (title != null) {

            User user = userService.readById(Long.parseLong(owner_id));
            ToDo savedTodo = todoRepository.save(new ToDo(title, LocalDateTime.now(), user));

            return toDoDtoConverter.toDto(savedTodo);
        }
        throw new NullEntityReferenceException("Title cannot be 'null'");
    }

    public ToDoDto readById(long id) {
        ToDo toDo = todoRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("ToDo with id " + id + " not found"));

        return toDoDtoConverter.toDto(toDo);
    }

    public ToDo update(ToDoDto toDoDto) {
        if (toDoDto != null) {
            ToDoDto existingToDoDto = readById(toDoDto.getId());
            existingToDoDto.setTitle(toDoDto.getTitle());
            return todoRepository.save(toDoDtoConverter.fromDto(existingToDoDto));
        }
        throw new NullEntityReferenceException("ToDo cannot be 'null'");
    }

    public void delete(long id) {
        ToDoDto toDoDto = readById(id);
        todoRepository.delete(toDoDtoConverter.fromDto(toDoDto));
    }

    public List<ToDo> getAll() {
        return todoRepository.findAll();
    }

    public List<ToDoDto> getByUserId(long userId) {
        ToDoDtoConverter toDoDtoConverter = new ToDoDtoConverter();
        return Optional.ofNullable(todoRepository.getByUserId(userId))
                .orElse(Collections.emptyList())
                .stream()
                .map(toDoDtoConverter::toDto)
                .toList();
    }

    public void changeDataFormat(List<ToDo> toDos) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(STATIC_TIME_FORMAT);

        toDos.forEach(toDo -> {
            LocalDateTime createdAt = toDo.getCreatedAt();
            String formattedDate = createdAt.format(dateTimeFormatter);
            toDo.setCreatedAt(LocalDateTime.parse(formattedDate, dateTimeFormatter));
        });
    }

}
