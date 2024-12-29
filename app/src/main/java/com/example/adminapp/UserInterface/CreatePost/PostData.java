package com.example.adminapp.UserInterface.CreatePost;

import java.util.ArrayList;
import java.util.List;

public class PostData {
    private String postId;
    private String userId;
    private String content;
    private List<String> images;
    private long timestamp;
    private String userName;
    private String faculty;
    private String department;
    private List<String> likes = new ArrayList<>();
    private List<String> dislikes = new ArrayList<>();
    private int comments;
    private String userImageUrl;  // Added user profile image URL field

    public PostData() {
        // Default constructor required for Firestore
    }

    public PostData(String postId, String userId, String content, List<String> images, long timestamp,
                    String userName, String faculty, String department, List<String> likes,
                    List<String> dislikes, int comments, String userImageUrl) {
        this.postId = postId;
        this.userId = userId;
        this.content = content;
        this.images = images;
        this.timestamp = timestamp;
        this.userName = userName;
        this.faculty = faculty;
        this.department = department;
        this.likes = likes;
        this.dislikes = dislikes;
        this.comments = comments;
        this.userImageUrl = userImageUrl;  // Initialize userImageUrl
    }

    // Getters and setters
    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public List<String> getLikes() {
        return likes != null ? likes : new ArrayList<>();
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    public List<String> getDislikes() {
        return dislikes != null ? dislikes : new ArrayList<>();
    }

    public void setDislikes(List<String> dislikes) {
        this.dislikes = dislikes;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int commentsCount) {
        this.comments = commentsCount;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }
}
