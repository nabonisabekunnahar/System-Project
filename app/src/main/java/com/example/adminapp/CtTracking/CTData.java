package com.example.adminapp.CtTracking;

public class CTData {
    private String ctKey;
    private String ctNumber;
    private String ctDate;
    private String course;
    private String teacherName;
    private String syllabus;
    private String status;
    private String faculty;
    private String department;
    private String year;
    private String adminUid;

    // Default constructor for Firebase
    public CTData() {
    }

    public CTData(String ctKey, String ctNumber, String ctDate, String course, String teacherName,
                  String syllabus, String status, String faculty, String department, String year, String adminUid) {
        this.ctKey = ctKey;
        this.ctNumber = ctNumber;
        this.ctDate = ctDate;
        this.course = course;
        this.teacherName = teacherName;
        this.syllabus = syllabus;
        this.status = status;
        this.faculty = faculty;
        this.department = department;
        this.year = year;
        this.adminUid = adminUid;
    }

    public String getCtKey() {
        return ctKey;
    }

    public void setCtKey(String ctKey) {
        this.ctKey = ctKey;
    }

    public String getCtNumber() {
        return ctNumber;
    }

    public void setCtNumber(String ctNumber) {
        this.ctNumber = ctNumber;
    }

    public String getCtDate() {
        return ctDate;
    }

    public void setCtDate(String ctDate) {
        this.ctDate = ctDate;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getSyllabus() {
        return syllabus;
    }

    public void setSyllabus(String syllabus) {
        this.syllabus = syllabus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getAdminUid() {
        return adminUid;
    }

    public void setAdminUid(String adminUid) {
        this.adminUid = adminUid;
    }
}
