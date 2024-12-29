package com.example.adminapp.UserInterface.CreatePost;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adminapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreatePostActivity extends AppCompatActivity {

    private EditText postContentEditText;
    private Button addImageButton, postButton;
    private RecyclerView selectedImagesRecyclerView;
    private SelectedImagesAdapter imagesAdapter;

    private List<Uri> selectedImages = new ArrayList<>();
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private ProgressDialog progressDialog;

    private String userName, faculty, department, userImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // Initialize Views
        postContentEditText = findViewById(R.id.postContentEditText);
        addImageButton = findViewById(R.id.addImageButton);
        postButton = findViewById(R.id.postButton);
        selectedImagesRecyclerView = findViewById(R.id.selectedImagesRecyclerView);
        progressDialog = new ProgressDialog(this);

        // Set up RecyclerView for selected images
        selectedImagesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imagesAdapter = new SelectedImagesAdapter(this, selectedImages);
        selectedImagesRecyclerView.setAdapter(imagesAdapter);

        // Fetch user data
        fetchUserData();

        // Add Image Button Click Listener
        addImageButton.setOnClickListener(v -> pickImages());

        // Post Button Click Listener
        postButton.setOnClickListener(v -> uploadPost());
    }

    private void pickImages() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Images"), 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    selectedImages.add(imageUri);
                }
            } else if (data.getData() != null) {
                selectedImages.add(data.getData());
            }
            imagesAdapter.notifyDataSetChanged();
            selectedImagesRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void uploadPost() {
        String postContent = postContentEditText.getText().toString().trim();

        if (postContent.isEmpty() && selectedImages.isEmpty()) {
            Toast.makeText(this, "Please add some content or images.", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Uploading post...");
        progressDialog.show();

        List<String> uploadedImageUrls = new ArrayList<>();
        if (!selectedImages.isEmpty()) {
            for (Uri imageUri : selectedImages) {
                uploadImage(imageUri, uploadedImageUrls, postContent);
            }
        } else {
            savePostToFirestore(postContent, uploadedImageUrls);
        }
    }

    private void uploadImage(Uri imageUri, List<String> uploadedImageUrls, String postContent) {
        String uniqueFileName = UUID.randomUUID().toString();
        StorageReference storageReference = storage.getReference().child("post_images/" + uniqueFileName);

        storageReference.putFile(imageUri).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    uploadedImageUrls.add(uri.toString());
                    if (uploadedImageUrls.size() == selectedImages.size()) {
                        savePostToFirestore(postContent, uploadedImageUrls);
                    }
                });
            } else {
                progressDialog.dismiss();
                Toast.makeText(this, "Failed to upload images.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void savePostToFirestore(String content, List<String> imageUrls) {
        String userId = auth.getCurrentUser().getUid();
        String postId = firestore.collection("Posts").document().getId();

        PostData post = new PostData(
                postId, userId, content, imageUrls, System.currentTimeMillis(),
                userName, faculty, department, new ArrayList<>(), new ArrayList<>(), 0, userImageUrl
        );

        firestore.collection("Posts").document(postId).set(post).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(this, "Post uploaded successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to upload post.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUserData() {
        String userId = auth.getCurrentUser().getUid();
        firestore.collection("User").whereEqualTo("userId", userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    userName = document.getString("name");
                    faculty = document.getString("faculty");
                    department = document.getString("department");
                    userImageUrl = document.getString("userImageUrl");  // Fetching the user image URL
                }
            }
        });
    }
}
