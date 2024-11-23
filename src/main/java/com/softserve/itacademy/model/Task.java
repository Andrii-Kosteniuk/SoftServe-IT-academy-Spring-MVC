package com.softserve.itacademy.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "tasks")
@Getter
@Setter
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_seq")
    @SequenceGenerator(name = "task_seq", sequenceName = "task_sequence", allocationSize = 1)
    private long id;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "The 'name' cannot be empty")
    private String name;

    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "todo_id")
    private ToDo todo;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private State state;

    public Task() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && priority == task.priority && Objects.equals(todo, task.todo) && Objects.equals(state, task.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, priority, todo, state);
    }

    @Override
    public String toString() {
        return "Task { " +
                "id = " + id +
                ", name = '" + name + '\'' +
                ", priority = " + priority +
                ", todo = " + todo +
                ", state = " + state +
                " }";
    }
}
