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
import java.util.List;
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

    public ToDo readById(long id) {
        return todoRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("ToDo with id " + id + " not found"));
    }

    public ToDo update(ToDo todo) {
        if (todo != null) {
            readById(todo.getId());
            return todoRepository.save(todo);
        }
        throw new NullEntityReferenceException("ToDo cannot be 'null'");
    }

    public void delete(long id) {
        ToDo todo = readById(id);
        todoRepository.delete(todo);
    }

    public List<ToDo> getAll() {
        return todoRepository.findAll();
    }

    public List<ToDo> getByUserId(long userId) {
        return todoRepository.getByUserId(userId);
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
