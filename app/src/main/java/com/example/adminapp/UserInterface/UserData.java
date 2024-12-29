package com.example.adminapp.UserInterface;

public class UserData {
    private String name;
    private String email;
    private String roll;
    private String faculty;
    private String department;
    private String year;
    private String userId;
    private String fcmToken;
    private String userImageUrl;

    // Empty constructor (required for Firestore)
    public UserData() {}

    // Constructor
    public UserData(String name, String email, String roll, String faculty, String department, String year, String userId, String fcmToken, String profileImageUrl) {
        this.name = name;
        this.email = email;
        this.roll = roll;
        this.faculty = faculty;
        this.department = department;
        this.year = year;
        this.userId = userId;
        this.fcmToken = fcmToken;
        this.userImageUrl = userImageUrl;
    }
    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRoll() {
        return roll;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String profileImageUrl) {
        this.userImageUrl = userImageUrl;
    }
}

