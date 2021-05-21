package com.example.myapplication.model;

public class Task {
    public int getId() {
        return id;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public String getTaskDate() {
        return taskDate;
    }

    public String getLocation() {
        return location;
    }

    public String getTaskDetail() {
        return taskDetail;
    }

    public String getPartner() {
        return partner;
    }

    private int id;
    private String taskTitle;
    private String taskDate;
    private String location;
    private String taskDetail;
    private String partner;

    public Task(int id, String taskTitle, String taskDate, String location, String taskDetail, String partner) {
        this.id = id;
        this.taskTitle = taskTitle;
        this.taskDate = taskDate;
        this.location = location;
        this.taskDetail = taskDetail;
        this.partner = partner;
    }

}
