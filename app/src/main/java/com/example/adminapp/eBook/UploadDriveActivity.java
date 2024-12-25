package com.example.adminapp.eBook;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.adminapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UploadDriveActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText edtName, edtDriveLink;
    private Button btnSaveLink;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private DatabaseReference databaseReference;

    private String faculty, department, batch, adminUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_drive);

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        edtName = findViewById(R.id.edtName);
        edtDriveLink = findViewById(R.id.edtDriveLink);
        btnSaveLink = findViewById(R.id.btnSaveLink);

        // Set up toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Drive Link");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Firebase initialization
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("drive");

        // Get current user's UID
        adminUid = firebaseAuth.getCurrentUser().getUid();

        // Fetch faculty, department, and batch from Firestore
        fetchUserDetails();

        // Save data on button click
        btnSaveLink.setOnClickListener(v -> saveDriveLink());
    }

    private void fetchUserDetails() {
        firestore.collection("Admin").document(adminUid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        faculty = documentSnapshot.getString("faculty");
                        department = documentSnapshot.getString("department");
                        batch = documentSnapshot.getString("year");

                        // Log admin details for debugging
                        Log.d("AdminDetails", "Faculty: " + faculty + ", Department: " + department + ", Batch: " + batch);

                        // Validate that the details are fetched properly
                        if (TextUtils.isEmpty(faculty) || TextUtils.isEmpty(department) || TextUtils.isEmpty(batch)) {
                            Toast.makeText(this, "User details incomplete, please check Firestore.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Admin details not found in Firestore!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch user details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("FirestoreError", e.getMessage());
                    finish();
                });
    }

    private void saveDriveLink() {
        // Get input values
        String name = edtName.getText().toString().trim();
        String link = edtDriveLink.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(name)) {
            edtName.setError("Drive name is required");
            return;
        }

        if (TextUtils.isEmpty(link)) {
            edtDriveLink.setError("Drive link is required");
            return;
        }

        if (TextUtils.isEmpty(faculty) || TextUtils.isEmpty(department) || TextUtils.isEmpty(batch)) {
            Toast.makeText(this, "User details not loaded. Try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a unique key for the drive
        String driveKey = databaseReference.child(faculty).child(department).child(batch).push().getKey();
        if (driveKey == null) {
            Toast.makeText(this, "Error generating drive key", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create DriveLink object using the existing class
        DriveLink driveLink = new DriveLink(name, link, adminUid);

        // Save the data to Firebase Database
        databaseReference.child(faculty)
                .child(department)
                .child(batch)
                .child(driveKey)
                .setValue(driveLink)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Drive link uploaded successfully.", Toast.LENGTH_SHORT).show();
                        edtName.setText("");  // Clear the name field
                        edtDriveLink.setText("");  // Clear the link field
                    } else {
                        Toast.makeText(this, "Failed to upload drive link: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to upload drive link: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("FirebaseError", e.getMessage());
                });
    }
}
