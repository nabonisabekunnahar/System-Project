package com.example.adminapp.faculty;

public class TeacherItem extends ListItem {
    private TeacherData teacherData;

    public TeacherItem(TeacherData teacherData) {
        this.teacherData = teacherData;
    }

    public TeacherData getTeacherData() {
        return teacherData;
    }

    @Override
    public int getType() {
        return TYPE_TEACHER;
    }
}
