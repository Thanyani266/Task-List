package com.todo.task_list.controller;

import com.todo.task_list.entity.Task;
import com.todo.task_list.entity.User;
import com.todo.task_list.repository.TaskRepository;
import com.todo.task_list.repository.UserRepository;
import com.todo.task_list.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public List<Task> getTasks(@RequestParam(required = false) String date,
                               @RequestParam(required = false) String status,
                               @RequestHeader("Authorization") String authHeader) {
    
        // Extract username from JWT
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(token);
    
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    
        // Filtering logic
        if (date != null && status != null) {
            LocalDate filterDate = LocalDate.parse(date);
            return taskRepository.findByUserAndDateAndStatus(user, filterDate, status.toUpperCase());
        } else if (date != null) {
            LocalDate filterDate = LocalDate.parse(date);
            return taskRepository.findByUserAndDate(user, filterDate);
        } else if (status != null) {
            return taskRepository.findByUserAndStatus(user, status.toUpperCase());
        } else {
            return taskRepository.findByUser(user);
        }
    }
    
    // Create a new task for the logged-in user
    @PostMapping
    public Task createTask(@RequestBody Task task,
                           @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        task.setUser(user); // associate task with user
        return taskRepository.save(task);
    }

    // Get task by ID (only if it belongs to the logged-in user)
    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable Long id,
                            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return taskRepository.findById(id)
                .filter(t -> t.getUser().equals(user))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found or not yours"));
    }

    // Update a task (only if it belongs to the logged-in user)
    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Long id,
                           @RequestBody Task taskDetails,
                           @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = taskRepository.findById(id)
                .filter(t -> t.getUser().equals(user)) // check ownership
                .orElseThrow(() -> new RuntimeException("Task not found or not yours"));

        task.setTitle(taskDetails.getTitle());
        task.setDate(taskDetails.getDate());
        task.setStatus(taskDetails.getStatus());
        return taskRepository.save(task);
    }

    // Delete a task (only if it belongs to the logged-in user)
    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id,
                           @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = taskRepository.findById(id)
                .filter(t -> t.getUser().equals(user))
                .orElseThrow(() -> new RuntimeException("Task not found or not yours"));

        taskRepository.delete(task);
    }

    // ---------------- Convenience endpoints ----------------

    // Today's tasks (optionally filter by status)
    @GetMapping("/today")
    public List<Task> getTodayTasks(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String status) {
    
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(token);
    
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    
        if (status != null) {
            return taskRepository.findByUserAndDateAndStatus(user, LocalDate.now(), status);
        } else {
            return taskRepository.findByUserAndDate(user, LocalDate.now());
        }
    }
    
    // Tomorrow's tasks (optionally filter by status)
    @GetMapping("/tomorrow")
    public List<Task> getTomorrowTasks(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String status) {
    
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(token);
    
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    
        if (status != null) {
            return taskRepository.findByUserAndDateAndStatus(user, LocalDate.now().plusDays(1), status);
        } else {
            return taskRepository.findByUserAndDate(user, LocalDate.now().plusDays(1));
        }
    }
}
