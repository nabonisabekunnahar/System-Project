package com.example.adminapp.UserInterface.CreatePost;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.adminapp.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private final Context context;
    private final List<CommentData> commentList;

    public CommentAdapter(Context context, List<CommentData> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment_layout, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        CommentData comment = commentList.get(position);

        // Safely set user name or fallback to "Anonymous"
        holder.commentUserNameTextView.setText(comment.getUserName() != null ? comment.getUserName() : "Anonymous");

        // Safely set comment content or fallback to a default message
        holder.commentContentTextView.setText(comment.getContent() != null ? comment.getContent() : "No content available.");

        // Format and display timestamp
        if (comment.getTimestamp() > 0) {
            holder.commentTimeTextView.setText(android.text.format.DateFormat.format("MMM dd, yyyy HH:mm", comment.getTimestamp()));
        } else {
            holder.commentTimeTextView.setText("N/A");
        }

        // Fetch user profile image URL based on userId from Firestore
        if (comment.getUserId() != null) {
            FirebaseFirestore.getInstance()
                    .collection("User")
                    .document(comment.getUserId()) // Use userId from the comment
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists() && documentSnapshot.contains("userImageUrl")) {
                                String userImageUrl = documentSnapshot.getString("userImageUrl");
                                loadUserProfileImage(holder, userImageUrl);
                            } else {
                                // Fallback if no image is found
                                loadUserProfileImage(holder, null);
                            }
                        } else {
                            // Handle failure (optional)
                            loadUserProfileImage(holder, null);
                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        return commentList != null ? commentList.size() : 0;
    }

    public void updateComments(List<CommentData> newComments) {
        if (commentList != null) {
            commentList.clear();
            commentList.addAll(newComments);
            notifyDataSetChanged();
        }
    }

    private void loadUserProfileImage(CommentViewHolder holder, String userImageUrl) {
        // Load user profile image using Glide
        if (userImageUrl != null) {
            Glide.with(context)
                    .load(userImageUrl) // Use the actual image URL
                    .placeholder(R.drawable.placeholder_image) // Fallback placeholder
                    .into(holder.commentUserProfileImageView);
        } else {
            holder.commentUserProfileImageView.setImageResource(R.drawable.placeholder_image); // Set placeholder if no image URL
        }
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentUserNameTextView, commentContentTextView, commentTimeTextView;
        ImageView commentUserProfileImageView;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentUserNameTextView = itemView.findViewById(R.id.commentUserNameTextView);
            commentContentTextView = itemView.findViewById(R.id.commentContentTextView);
            commentTimeTextView = itemView.findViewById(R.id.commentTimeTextView);
            commentUserProfileImageView = itemView.findViewById(R.id.commentUserProfileImageView);
        }
    }
}
