package com.todo.task_list.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private LocalDate date;
    private String status;

    // 🔹 Link each task to a user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    public Task() {}

    public Task(String title, LocalDate date, String status, User user) {
        this.title = title;
        this.date = date;
        this.status = status;
        this.user = user;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    @Transient
    private String dayLabel;

    public String getDayLabel() {
        if (date.equals(LocalDate.now())) {
            return "Today";
        } else if (date.equals(LocalDate.now().plusDays(1))) {
            return "Tomorrow";
        } else {
            return "Other";
        }
    }

    // Expose user_id directly in the JSON response
    public Long getUserId() {
        return user != null ? user.getId() : null;
    }
}



