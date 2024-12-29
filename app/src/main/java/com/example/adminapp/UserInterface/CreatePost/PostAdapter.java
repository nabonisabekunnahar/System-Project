package com.example.adminapp.UserInterface.CreatePost;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.adminapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private final Context context;
    private final List<PostData> postList;
    private final FirebaseFirestore firestore;
    private final String currentUserId;
    private final String currentUserName;

    public PostAdapter(Context context, List<PostData> postList, String currentUserName) {
        this.context = context;
        this.postList = postList;
        this.firestore = FirebaseFirestore.getInstance();
        this.currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.currentUserName = currentUserName;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post_layout, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        PostData post = postList.get(position);

        // Set User Info
        holder.userNameTextView.setText(post.getUserName());
        holder.postTimeTextView.setText(android.text.format.DateFormat.format("MMM dd, yyyy HH:mm", post.getTimestamp()));

        // Fetch and set User profile image URL for the post
        fetchUserProfileImage(post.getUserId(), holder.userImageView);

        // Set Post Content
        if (post.getContent() != null && !post.getContent().isEmpty()) {
            holder.postContentTextView.setVisibility(View.VISIBLE);
            holder.postContentTextView.setText(post.getContent());
        } else {
            holder.postContentTextView.setVisibility(View.GONE);
        }

        // Set Post Image(s)
        if (post.getImages() != null && !post.getImages().isEmpty()) {
            holder.postImageView.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(post.getImages().get(0)) // Show first image
                    .placeholder(R.drawable.placeholder_image) // Placeholder while loading
                    .into(holder.postImageView);
        } else {
            holder.postImageView.setVisibility(View.GONE);
        }

        // Handle Like/Dislike
        updateLikeDislikeState(holder, post);
        holder.likeButtonLayout.setOnClickListener(v -> toggleLike(holder, post));
        holder.dislikeButtonLayout.setOnClickListener(v -> toggleDislike(holder, post));

        // Setup Comments
        setupCommentSection(holder, post);

        // Menu for Edit/Delete
        holder.menuIcon.setOnClickListener(v -> showPopupMenu(v, post));
    }

    private void setupCommentSection(PostViewHolder holder, PostData post) {
        // Show/hide comments when the comment button is clicked
        holder.commentButtonLayout.setOnClickListener(v -> {
            if (holder.commentSectionLayout.getVisibility() == View.VISIBLE) {
                holder.commentSectionLayout.setVisibility(View.GONE);
            } else {
                holder.commentSectionLayout.setVisibility(View.VISIBLE);
                fetchComments(holder, post);
            }
        });

        // Post a comment
        holder.postCommentButton.setOnClickListener(v -> {
            String commentText = holder.addCommentEditText.getText().toString().trim();
            if (!commentText.isEmpty()) {
                addComment(holder, post, commentText);
                holder.addCommentEditText.setText("");
            }
        });
    }

    private void fetchComments(PostViewHolder holder, PostData post) {
        firestore.collection("Comments")
                .whereEqualTo("postId", post.getPostId())
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("PostAdapter", "Error fetching comments: " + error.getMessage());
                        return;
                    }

                    if (value != null) {
                        List<CommentData> comments = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            CommentData comment = doc.toObject(CommentData.class);
                            comments.add(comment);
                        }

                        // Update RecyclerView with fetched comments
                        CommentAdapter commentAdapter = new CommentAdapter(context, comments);
                        holder.commentRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                        holder.commentRecyclerView.setAdapter(commentAdapter);
                        holder.commentSectionLayout.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void addComment(PostViewHolder holder, PostData post, String commentText) {
        // Generate a unique ID for the comment
        String commentId = firestore.collection("Comments").document().getId();

        // Create the new comment object, including the postId
        CommentData newComment = new CommentData(
                commentId,
                post.getPostId(),  // Include postId to associate the comment with the post
                currentUserId,
                currentUserName,
                commentText,
                System.currentTimeMillis(),
                "" // Placeholder for user image URL (to be fetched)
        );

        // Add the comment to the "Comments" collection
        firestore.collection("Comments").document(commentId)
                .set(newComment)
                .addOnSuccessListener(aVoid -> {
                    // Increment the comments count in the corresponding post
                    firestore.collection("Posts").document(post.getPostId())
                            .update("comments", post.getComments() + 1)
                            .addOnSuccessListener(aVoid1 -> {
                                // Update the local post object and notify adapter
                                post.setComments(post.getComments() + 1);
                                notifyItemChanged(holder.getAdapterPosition());
                            })
                            .addOnFailureListener(e -> Log.e("PostAdapter", "Error updating comments count: " + e.getMessage()));
                })
                .addOnFailureListener(e -> Log.e("PostAdapter", "Error adding comment: " + e.getMessage()));
    }

    private void fetchUserProfileImage(String userId, ImageView userImageView) {
        firestore.collection("User").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String userProfileImageUrl = documentSnapshot.getString("userImageUrl");
                        if (userProfileImageUrl != null && !userProfileImageUrl.isEmpty()) {
                            Glide.with(context)
                                    .load(userProfileImageUrl)
                                    .placeholder(R.drawable.ic_user_placeholder) // Default placeholder
                                    .into(userImageView);
                        } else {
                            Glide.with(context)
                                    .load(R.drawable.placeholder_image) // Default image if no profile URL
                                    .into(userImageView);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("PostAdapter", "Error fetching user profile image: " + e.getMessage()));
    }

    private void updateLikeDislikeState(PostViewHolder holder, PostData post) {
        if (post.getLikes().contains(currentUserId)) {
            holder.likeTextView.setTextColor(ContextCompat.getColor(context, R.color.primaryColor));
        } else {
            holder.likeTextView.setTextColor(Color.GRAY);
        }

        if (post.getDislikes().contains(currentUserId)) {
            holder.dislikeTextView.setTextColor(ContextCompat.getColor(context, R.color.primaryColor));
        } else {
            holder.dislikeTextView.setTextColor(Color.GRAY);
        }

        holder.likeTextView.setText(String.valueOf(post.getLikes().size()));
        holder.dislikeTextView.setText(String.valueOf(post.getDislikes().size()));
    }

    private void toggleLike(PostViewHolder holder, PostData post) {
        if (post.getLikes().contains(currentUserId)) {
            post.getLikes().remove(currentUserId);
        } else {
            post.getLikes().add(currentUserId);
            post.getDislikes().remove(currentUserId);
        }

        updateFirestorePost(post);
        updateLikeDislikeState(holder, post);
    }

    private void toggleDislike(PostViewHolder holder, PostData post) {
        if (post.getDislikes().contains(currentUserId)) {
            post.getDislikes().remove(currentUserId);
        } else {
            post.getDislikes().add(currentUserId);
            post.getLikes().remove(currentUserId);
        }

        updateFirestorePost(post);
        updateLikeDislikeState(holder, post);
    }

    private void updateFirestorePost(PostData post) {
        firestore.collection("Posts").document(post.getPostId())
                .update("likes", post.getLikes(), "dislikes", post.getDislikes());
    }

    private void showPopupMenu(View view, PostData post) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.post_menu);
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.editPost) {
                // Edit functionality
                return true;
            } else if (item.getItemId() == R.id.deletePost) {
                deletePost(post);
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    private void deletePost(PostData post) {
        firestore.collection("Posts").document(post.getPostId())
                .delete()
                .addOnSuccessListener(aVoid -> postList.remove(post));
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView, postTimeTextView, postContentTextView;
        ImageView postImageView, menuIcon, userImageView;
        LinearLayout likeButtonLayout, dislikeButtonLayout, commentButtonLayout;
        TextView likeTextView, dislikeTextView, commentTextView;
        LinearLayout commentSectionLayout;
        RecyclerView commentRecyclerView;
        EditText addCommentEditText;
        Button postCommentButton;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            postTimeTextView = itemView.findViewById(R.id.postTimeTextView);
            postContentTextView = itemView.findViewById(R.id.postContentTextView);
            postImageView = itemView.findViewById(R.id.postImageView);
            userImageView = itemView.findViewById(R.id.userProfileImageView); // Add ImageView for user profile image
            menuIcon = itemView.findViewById(R.id.menuIcon);
            likeButtonLayout = itemView.findViewById(R.id.likeButtonLayout);
            dislikeButtonLayout = itemView.findViewById(R.id.dislikeButtonLayout);
            commentButtonLayout = itemView.findViewById(R.id.commentButtonLayout);
            likeTextView = itemView.findViewById(R.id.likeTextView);
            dislikeTextView = itemView.findViewById(R.id.dislikeTextView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
            commentSectionLayout = itemView.findViewById(R.id.commentSectionLayout);
            commentRecyclerView = itemView.findViewById(R.id.commentRecyclerView);
            addCommentEditText = itemView.findViewById(R.id.addCommentEditText);
            postCommentButton = itemView.findViewById(R.id.postCommentButton);
        }
    }
}
