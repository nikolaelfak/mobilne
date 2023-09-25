package com.example.app1;

public class User {
    private String userId;
    private String username;
    private int points;
    private String fullName;

    public User() {

    }

    public User(String userId, String username, int points) {
        this.userId = userId;
        this.username = username;
        this.points = points;
    }

    public User(String userId, String username, String fullName, int points) { // Dodajte fullName u konstruktor
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.points = points;
    }

    // Getter za fullName
    public String getFullName() {
        return fullName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}

