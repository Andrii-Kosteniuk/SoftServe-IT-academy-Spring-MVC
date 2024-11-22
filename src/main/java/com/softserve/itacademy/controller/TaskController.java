package com.softserve.itacademy.controller;

import com.softserve.itacademy.dto.TaskTransformer;
import com.softserve.itacademy.model.*;
import com.softserve.itacademy.service.StateService;
import com.softserve.itacademy.service.TaskService;
import com.softserve.itacademy.service.ToDoService;
import com.softserve.itacademy.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Controller
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final ToDoService todoService;
    private final StateService stateService;
    private final TaskTransformer taskTransformer;
    private final UserService userService;


    @GetMapping("/create/todos/{todo_id}")
    public String create(@PathVariable("todo_id") long todo_id, Model model) {

        ToDo toDo = todoService.readById(todo_id);
        TaskPriority[] priorities = TaskPriority.values();

        model.addAttribute("task", new Task());
        model.addAttribute("todo", toDo);
        model.addAttribute("priorities", priorities);
        return "create-task";
    }

    @PostMapping("/create/todos/{todo_id}")
    public String create(@PathVariable("todo_id") long todo_id,
                         @Valid @ModelAttribute("task") Task task,
                         BindingResult result,
                         RedirectAttributes redirectAttributes,
                         Model model) {

        ToDo toDo = todoService.readById(todo_id);
        task.setTodo(toDo);
        task.setState(stateService.getByName("New"));
        TaskPriority[] priorities = TaskPriority.values();

        if (result.hasErrors()) {
            model.addAttribute("task", task);
            model.addAttribute("todo", toDo);
            model.addAttribute("priorities", priorities);
            return "create-task";
        }
//        if (taskService.getAll().contains(task)) {
//            redirectAttributes.addFlashAttribute("successCreateTaskMessage",
//                    "Task " + task.getName() + " was created");
//        } else {
//            redirectAttributes.addFlashAttribute("errorCreateTaskMessage",
//                    "Task + " + task.getName() + " already exists");
//        }
        taskService.create(taskTransformer.convertToDto(task));
        return "redirect:/tasks/todos/" + todo_id;
    }

    @GetMapping("/{task_id}/update/todos/{todo_id}")
    public String taskUpdateForm(@PathVariable("task_id") long task_id,
                                 @PathVariable("todo_id") long todo_id,
                                 Model model) {

        ToDo toDo = todoService.readById(todo_id);
        List<State> states = stateService.getAll();
        TaskPriority[] priorities = TaskPriority.values();

        model.addAttribute("task", taskService.readById(task_id));
        model.addAttribute("todo", toDo);
        model.addAttribute("states", states);
        model.addAttribute("priorities", priorities);
        return "update-task";
    }

    @PostMapping("/{task_id}/update/todos/{todo_id}")
    public String update(@PathVariable("task_id") long task_id,
                         @PathVariable("todo_id") long todo_id,
                         @Valid @ModelAttribute("task") Task task,
                         BindingResult bindingResult,
                         Model model) {
        ToDo toDo = todoService.readById(todo_id);
        List<State> states = stateService.getAll();
        TaskPriority[] priorities = TaskPriority.values();
        task.setId(task_id);
        task.setTodo(toDo);
        if (bindingResult.hasErrors()) {
            model.addAttribute("task", task);
            model.addAttribute("todo", toDo);
            model.addAttribute("states", states);
            model.addAttribute("priorities", priorities);
            return "update-task";
        }

        taskService.update(task);
        return "redirect:/tasks/todos/" + todo_id;
    }

    @PostMapping("/{task_id}/delete/todos/{todo_id}")
    public String delete(@PathVariable("task_id") long task_id,
                         @PathVariable("todo_id") long todo_id,
                         RedirectAttributes redirectAttributes) {

        String name = taskService.readById(task_id).getName();
        taskService.delete(task_id);

        redirectAttributes.addFlashAttribute("successDeleteTaskMessage", "Task " + name + " was successfully deleted!");
        redirectAttributes.addFlashAttribute("name", name);

        return "redirect:/tasks/todos/" + todo_id;
    }

    @GetMapping("/delete/todos/{todo_id}/collaborator/{collaborator_id}")
    public String deleteCollaborator(
            @PathVariable("todo_id") long todo_id,
            @PathVariable("collaborator_id") long collaborator_id,
            Model model,
            RedirectAttributes redirectAttributes) {

        ToDo toDo = todoService.readById(todo_id);
        List<User> collaborators = toDo.getCollaborators();
        collaborators.removeIf(user -> user.getId() == collaborator_id);
        todoService.update(toDo);
        User collaborator = userService.readById(collaborator_id);
        String name = collaborator.getFirstName() + " " + collaborator.getLastName();

        redirectAttributes.addFlashAttribute("successDeleteCollaboratorMessage", "Collaborator " + name + " was successfully deleted!");
        redirectAttributes.addFlashAttribute("collaborator", name);

        model.addAttribute("todo", toDo);
        model.addAttribute("collaborators", collaborators);
        return "redirect:/tasks/todos/" + todo_id;
    }

    @PostMapping("/todos/{todo_id}/add/collaborator")
    public String addCollaborator(
            @PathVariable("todo_id") long todo_id,
            @RequestParam("collaboratorId") long collaborator_id,
            RedirectAttributes redirectAttributes) {

        ToDo toDo = todoService.readById(todo_id);
        List<User> collaborators = toDo.getCollaborators();

        User user = userService.readById(collaborator_id);
        String name = user.getFirstName() + " " + user.getLastName();

        if (! collaborators.contains(user)) {
            collaborators.add(user);
            todoService.update(toDo);
            redirectAttributes.addFlashAttribute("successAddCollaboratorMessage",
                    "Collaborator " + name + " was added to current to-do list");
        } else {
            redirectAttributes.addFlashAttribute("errorCollaboratorMessage",
                    "Collaborator already present in to-do list");
        }

        return "redirect:/tasks/todos/" + todo_id;
    }

    @GetMapping("/todos/{todo_id}")
    public String taskListPage(@PathVariable("todo_id") long todo_id, Model model) {
        ToDo toDo = todoService.readById(todo_id);
        List<Task> tasks = taskService.getByTodoId(todo_id);
        List<User> todoCollaborators = toDo.getCollaborators();
        List<User> allCollaborators = userService.getAll();

        model.addAttribute("todo", toDo);
        model.addAttribute("tasks", tasks);
        model.addAttribute("todoCollaborators", todoCollaborators);
        model.addAttribute("allCollaborators", allCollaborators);

        return "todo-tasks";
    }

}
