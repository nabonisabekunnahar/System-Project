package com.example.adminapp.faculty;

public abstract class ListItem {
    public static final int TYPE_COURSE = 0;
    public static final int TYPE_TEACHER = 1;

    abstract public int getType();
}
