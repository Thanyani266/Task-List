package com.todo.task_list.repository;

import com.todo.task_list.entity.Task;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByDateAndStatusNot(LocalDate date, String status);
    // Get all tasks for a specific date
    List<Task> findByDate(LocalDate date);
    // Get all tasks with a specific status
    List<Task> findByStatus(String status);
    // Get tasks by date and status
    List<Task> findByDateAndStatus(LocalDate date, String status);
    // Delete tasks by date and status
    int deleteByDateAndStatus(LocalDate date, String status);

}
