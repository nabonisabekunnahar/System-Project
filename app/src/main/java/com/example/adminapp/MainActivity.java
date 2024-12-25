package com.example.adminapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;


import com.example.adminapp.CtTracking.UpdateCTInfo;
import com.example.adminapp.eBook.UploadPdfActivity;
import com.example.adminapp.faculty.UpdateFaculty;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private CardView uploadNotice,addGalareyImage,addEbook,faculty,ctTracking;
    private Toolbar toolbar;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize Firebase Authentication and Firestore
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Fetch and display user information
        fetchAndSetUserInfo();

        uploadNotice=findViewById(R.id.addNotice);
        addGalareyImage=findViewById(R.id.addGalleryImage);
        addEbook=findViewById(R.id.addEbook);
        faculty=findViewById(R.id.faculty);
        ctTracking=findViewById(R.id.ctTracking);

        uploadNotice.setOnClickListener(this);
        addEbook.setOnClickListener(this);
        faculty.setOnClickListener(this);
        ctTracking.setOnClickListener(this);
    }

    private void fetchAndSetUserInfo() {
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid(); // Get the authenticated user's UID

            DocumentReference userRef = firestore.collection("Admin").document(userId);
            userRef.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String department = documentSnapshot.getString("department");
                            String year = documentSnapshot.getString("year");

                            if (department != null && year != null) {
                                // Set the toolbar title
                                String title = "CR Dept of " + department + ", " + year + " Year";
                                if (getSupportActionBar() != null) {
                                    getSupportActionBar().setTitle(title);
                                }
                            } else {
                                Toast.makeText(this, "Admin info is incomplete.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Admin document does not exist.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch user info: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "No authenticated user found.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addNotice) {
            Intent intent = new Intent(MainActivity.this, UploadNotice.class);
            startActivity(intent);
        }
        else if(v.getId() == R.id.addGalleryImage){
            Intent intent = new Intent(MainActivity.this, UploadImage.class);
            startActivity(intent);
        }
        else if(v.getId() == R.id.addEbook){
            Intent intent = new Intent(MainActivity.this, UploadPdfActivity.class);
            startActivity(intent);
        }
        else if(v.getId() == R.id.faculty){
            Intent intent = new Intent(MainActivity.this, UpdateFaculty.class);
            startActivity(intent);
        }else if(v.getId() == R.id.ctTracking){
            Intent intent = new Intent(MainActivity.this, UpdateCTInfo.class);
            startActivity(intent);
        }
    }
}