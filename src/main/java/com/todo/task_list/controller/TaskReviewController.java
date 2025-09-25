package com.todo.task_list.controller;

import com.todo.task_list.entity.Task;
import com.todo.task_list.repository.TaskRepository;
import com.todo.task_list.dto.TaskActionRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskReviewController {

    private final TaskRepository taskRepository;

    public TaskReviewController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // 1. Fetch yesterday’s incomplete tasks
    @GetMapping("/review")
    public List<Task> getYesterdayIncompleteTasks() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        return taskRepository.findByDateAndStatusNot(yesterday, "DONE");
    }

    // 2. Handle actions on tasks
    @PatchMapping("/{id}/action")
    public ResponseEntity<?> handleTaskAction(
            @PathVariable Long id,
            @RequestBody TaskActionRequest request) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        switch (request.getAction()) {
            case "MOVE_TO_TODAY" -> task.setDate(LocalDate.now());
            case "RESCHEDULE" -> {
                if (request.getNewDate() == null) {
                    return ResponseEntity.badRequest().body("newDate is required for RESCHEDULE");
                }
                task.setDate(request.getNewDate());
            }
            case "DELETE" -> {
                taskRepository.delete(task);
                return ResponseEntity.noContent().build();
            }
            default -> {
                return ResponseEntity.badRequest().body("Invalid action");
            }
        }

        taskRepository.save(task);
        return ResponseEntity.ok(task);
    }
}

