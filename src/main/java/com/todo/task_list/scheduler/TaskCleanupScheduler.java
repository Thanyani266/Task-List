package com.todo.task_list.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional; // <-- Add this import

import com.todo.task_list.repository.TaskRepository;

import java.time.LocalDate;

@Component
public class TaskCleanupScheduler {

    private final TaskRepository taskRepository;

    public TaskCleanupScheduler(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // Runs every 10 minutes
    @Scheduled(fixedRate = 10 * 60 * 1000) // 10 minutes in milliseconds
    @Transactional // <-- Add this annotation
    public void deleteCompletedYesterdayTasks() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        int deletedCount = taskRepository.deleteByDateAndStatus(yesterday, "DONE");
        System.out.println("Deleted " + deletedCount + " completed tasks from yesterday.");
    }
}
