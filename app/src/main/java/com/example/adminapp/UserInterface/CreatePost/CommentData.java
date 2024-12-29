package com.example.adminapp.UserInterface.CreatePost;

public class CommentData {
    private String commentId;
    private String postId;  // New field to store the post ID
    private String userId;
    private String userName;
    private String content;
    private long timestamp;
    private String userImageUrl;

    public CommentData() {
        // Default constructor required for Firebase
    }

    public CommentData(String commentId, String postId, String userId, String userName, String content, long timestamp, String userImageUrl) {
        this.commentId = commentId;
        this.postId = postId;
        this.userId = userId;
        this.userName = userName;
        this.content = content;
        this.timestamp = timestamp;
        this.userImageUrl = userImageUrl;  // Initialize the userImageUrl
    }

    // Getters and setters
    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

}
