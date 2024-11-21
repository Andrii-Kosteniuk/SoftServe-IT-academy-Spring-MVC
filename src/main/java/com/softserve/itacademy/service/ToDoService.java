package com.softserve.itacademy.service;

import com.softserve.itacademy.config.exception.NullEntityReferenceException;
import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.repository.ToDoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ToDoService {
    private final static String STATIC_TIME_FORMAT = "dd.MM.yyyy HH:mm";

    private final ToDoRepository todoRepository;

    public ToDoService(ToDoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public ToDo create(ToDo todo) {
        if (todo != null) {
            return todoRepository.save(todo);
        }
        throw new NullEntityReferenceException("ToDo cannot be 'null'");
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

    public List<ToDo> changeDataFormat(List<ToDo> toDos) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(STATIC_TIME_FORMAT);

        toDos.forEach(toDo -> toDo.setCreatedAt(
                LocalDateTime.parse(toDo.getCreatedAt().format(dateTimeFormatter))
        ));

        return toDos;
    }

}
