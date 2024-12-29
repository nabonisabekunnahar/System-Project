package com.example.adminapp;

public class NoticeData {
    public String key, title, body, image, date, time, visibility, adminUid;

    public NoticeData() {
    }

    public NoticeData(String key, String title, String body, String image, String date, String time,
                      String visibility,String adminUid) {
        this.key = key;
        this.title = title;
        this.body = body;
        this.image = image;
        this.date = date;
        this.time = time;
        this.visibility = visibility;
        this.adminUid = adminUid;
    }

    public String getAdminUid() {
        return adminUid;
    }

    public void setAdminUid(String adminUid) {
        this.adminUid = adminUid;
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
