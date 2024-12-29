package com.example.adminapp.UserInterface;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adminapp.R;
import com.example.adminapp.UserInterface.CreatePost.CreatePostActivity;
import com.example.adminapp.UserInterface.CreatePost.PostAdapter;
import com.example.adminapp.UserInterface.CreatePost.PostData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserMainActivity extends AppCompatActivity {

    private FloatingActionButton fab;

    private RecyclerView feedRecyclerView;
    private PostAdapter postAdapter;
    private List<PostData> postList;
    private FirebaseFirestore firestore;
    private String currentUserId;
    private String currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initialize RecyclerView and Adapter
        fab=findViewById(R.id.createPostButton);
        feedRecyclerView = findViewById(R.id.feedRecyclerView);
        postList = new ArrayList<>();
        feedRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        fab.setOnClickListener(v -> startActivity(new Intent(UserMainActivity.this, CreatePostActivity.class)));

        // Fetch current user's name
        fetchCurrentUserName();
    }

    private void fetchCurrentUserName() {
        firestore.collection("User").document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        currentUserName = documentSnapshot.getString("name");
                        if (currentUserName != null) {
                            // Initialize PostAdapter after fetching user name
                            postAdapter = new PostAdapter(this, postList, currentUserName);
                            feedRecyclerView.setAdapter(postAdapter);

                            // Load posts after initializing adapter
                            loadPosts();
                        } else {
                            // Handle case where userName is null
                            Log.e("UserMainActivity", "currentUserName is null.");
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("UserMainActivity", "Error fetching user name: " + e.getMessage()));
    }


    private void loadPosts() {
        firestore.collection("Posts")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        postList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            PostData post = document.toObject(PostData.class);
                            postList.add(post);
                        }
                        postAdapter.notifyDataSetChanged();
                    } else {
                        // Handle failure
                    }
                });
    }
}
