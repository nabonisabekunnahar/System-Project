package com.example.adminapp;

public class NoticeData {
    String title,image,date,time,key,visibility,body;

    public NoticeData() {
    }

    public NoticeData(String title, String image, String date, String time, String key,String visibility,String body) {
        this.title = title;
        this.image = image;
        this.date = date;
        this.time = time;
        this.key = key;
        this.visibility = visibility;
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
