package com.todo.task_list.controller;

import com.todo.task_list.entity.Task;
import com.todo.task_list.entity.User;
import com.todo.task_list.repository.TaskRepository;
import com.todo.task_list.repository.UserRepository;
import com.todo.task_list.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    // Get all tasks for the logged-in user
    @GetMapping
    public List<Task> getUserTasks(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return taskRepository.findByUser(user);
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
}
