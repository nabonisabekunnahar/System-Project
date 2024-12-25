package com.example.adminapp.faculty;

public class CourseItem extends ListItem {
    private String courseName;

    public CourseItem(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseName() {
        return courseName;
    }

    @Override
    public int getType() {
        return TYPE_COURSE;
    }
}
