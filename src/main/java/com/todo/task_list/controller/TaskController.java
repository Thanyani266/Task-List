package com.todo.task_list.controller;

import com.todo.task_list.entity.Task;
import com.todo.task_list.repository.TaskRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // ---------------- GET /tasks with optional filters ----------------
    // /tasks?date=YYYY-MM-DD&status=PENDING
    @GetMapping
    public List<Task> getTasks(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String status) {

        if (date != null && status != null) {
            LocalDate filterDate = LocalDate.parse(date);
            return taskRepository.findByDateAndStatus(filterDate, status);
        } else if (date != null) {
            LocalDate filterDate = LocalDate.parse(date);
            return taskRepository.findByDate(filterDate);
        } else if (status != null) {
            return taskRepository.findByStatus(status);
        } else {
            return taskRepository.findAll();
        }
    }

    // ---------------- Convenience endpoints ----------------

    // Today's tasks
    @GetMapping("/today")
    public List<Task> getTodayTasks() {
        return taskRepository.findByDate(LocalDate.now());
    }

    // Tomorrow's tasks
    @GetMapping("/tomorrow")
    public List<Task> getTomorrowTasks() {
        return taskRepository.findByDate(LocalDate.now().plusDays(1));
    }

    // ---------------- CRUD endpoints ----------------

    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    @PostMapping
    public Task createTask(@RequestBody Task task) {
        if (task.getDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Task date cannot be in the past");
        }
        return taskRepository.save(task);
    }

    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Long id, @RequestBody Task taskDetails) {
        if (taskDetails.getDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Task date cannot be in the past");
        }

        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found"));
        task.setTitle(taskDetails.getTitle());
        task.setDate(taskDetails.getDate());
        task.setStatus(taskDetails.getStatus());
        return taskRepository.save(task);
    } 


    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskRepository.deleteById(id);
    }
}
