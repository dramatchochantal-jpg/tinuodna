package com.example.edugate.models;

public class Student {
    private String id;
    private String name;
    private String status;
    private boolean isPresent;
    private String grade;

    public Student(String id, String name, String status) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.isPresent = false;
        this.grade = "";
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getStatus() { return status; }
    public boolean isPresent() { return isPresent; }
    public void setPresent(boolean present) { isPresent = present; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
}