package com.example.adminapp.faculty;

public class TeacherData {
    private String name;
    private String email;
    private String phone;
    private String course;
    private String imageUrl;
    private String key;
    private String faculty;
    private String department;
    private String year;
    private String adminUid; // New field

    // Constructor
    public TeacherData(String name, String email, String phone, String course, String imageUrl, String key, String faculty, String department, String year, String adminUid) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.course = course;
        this.imageUrl = imageUrl;
        this.key = key;
        this.faculty = faculty;
        this.department = department;
        this.year = year;
        this.adminUid = adminUid; // Assign value
    }

    // Default constructor for Firebase
    public TeacherData() {}

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getFaculty() { return faculty; }
    public void setFaculty(String faculty) { this.faculty = faculty; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public String getAdminUid() { return adminUid; }
    public void setAdminUid(String adminUid) { this.adminUid = adminUid; }
}
