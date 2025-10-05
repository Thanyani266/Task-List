package com.todo.task_list.repository;

import com.todo.task_list.entity.Task;
import com.todo.task_list.entity.User;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByDateAndStatusNot(LocalDate date, String status);
    // Get all tasks for a specific date
    List<Task> findByUserAndDate(User user, LocalDate date);
    // Get all tasks with a specific status
    List<Task> findByUserAndStatus(User user, String status);
    // Get tasks by date and status
    List<Task> findByUserAndDateAndStatus(User user, LocalDate date, String status);
    // Delete tasks by date and status
    @Modifying
    @Transactional
    int deleteByDateAndStatus(LocalDate date, String status);
    List<Task> findByUser(User user);

}
