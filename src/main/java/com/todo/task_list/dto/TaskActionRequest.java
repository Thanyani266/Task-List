package com.todo.task_list.dto;

import java.time.LocalDate;

public class TaskActionRequest {
    private String action;   // MOVE_TO_TODAY, RESCHEDULE, UNSCHEDULE, DELETE
    private LocalDate newDate; // used only for RESCHEDULE

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public LocalDate getNewDate() { return newDate; }
    public void setNewDate(LocalDate newDate) { this.newDate = newDate; }
}
