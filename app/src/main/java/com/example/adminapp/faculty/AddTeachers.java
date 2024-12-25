package com.example.adminapp.faculty;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.adminapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddTeachers extends AppCompatActivity {

    private ImageView addTeacherImage;
    private EditText addTeacherName, addTeacherEmail, addTeacherPhone, addTeacherCourse;
    private Button addTeacherBtn;
    private final int REQ = 1;
    private Bitmap bitmap = null;
    private String name, email, phone, course, downloadUrl = "";
    private ProgressDialog pd;
    private StorageReference storageReference;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teachers);

        addTeacherImage = findViewById(R.id.addTeacherImage);
        addTeacherName = findViewById(R.id.addTeacherName);
        addTeacherEmail = findViewById(R.id.addTeacherEmail);
        addTeacherPhone = findViewById(R.id.addTeacherPhone);
        addTeacherCourse = findViewById(R.id.addTeacherCourse);
        addTeacherBtn = findViewById(R.id.addTeacherBtn);

        pd = new ProgressDialog(this);

        reference = FirebaseDatabase.getInstance().getReference().child("teacher");
        storageReference = FirebaseStorage.getInstance().getReference();

        addTeacherImage.setOnClickListener(view -> openGallery());
        addTeacherBtn.setOnClickListener(v -> validateAndUpload());
    }

    private void validateAndUpload() {
        name = addTeacherName.getText().toString().trim();
        email = addTeacherEmail.getText().toString().trim();
        phone = addTeacherPhone.getText().toString().trim();
        course = addTeacherCourse.getText().toString().trim();

        if (name.isEmpty()) {
            addTeacherName.setError("Empty");
            addTeacherName.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            addTeacherEmail.setError("Empty");
            addTeacherEmail.requestFocus();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            addTeacherEmail.setError("Invalid Email");
            addTeacherEmail.requestFocus();
            return;
        }
        if (phone.isEmpty()) {
            addTeacherPhone.setError("Empty");
            addTeacherPhone.requestFocus();
            return;
        }
        if (!phone.matches("\\d{11}")) { // Example: Checking for an 11-digit phone number
            addTeacherPhone.setError("Invalid Phone Number");
            addTeacherPhone.requestFocus();
            return;
        }
        if (course.isEmpty()) {
            addTeacherCourse.setError("Empty");
            addTeacherCourse.requestFocus();
            return;
        }

        uploadImage();
    }

    private void uploadImage() {
        if (bitmap == null) {
            insertData(""); // Skip image upload if no image is selected
            return;
        }

        pd.setMessage("Uploading image...");
        pd.show();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] finalImg = baos.toByteArray();

        final String fileName = "TeacherImages/" + System.currentTimeMillis() + ".jpg";
        final StorageReference filePath = storageReference.child(fileName);

        Log.d("UploadImage", "Uploading image to: " + filePath.getPath());

        filePath.putBytes(finalImg)
                .addOnSuccessListener(taskSnapshot -> filePath.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            downloadUrl = uri.toString();
                            Log.d("UploadImage", "Image uploaded successfully, URL: " + downloadUrl);
                            insertData(downloadUrl);
                        })
                        .addOnFailureListener(e -> {
                            pd.dismiss();
                            Log.e("UploadImage", "Failed to get download URL: " + e.getMessage());
                            Toast.makeText(AddTeachers.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                        }))
                .addOnFailureListener(e -> {
                    pd.dismiss();
                    Log.e("UploadImage", "Upload failed: " + e.getMessage());
                    Toast.makeText(AddTeachers.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                });
    }

    private void insertData(String imageUrl) {
        pd.setMessage("Uploading data...");
        pd.show();

        // Retrieve the authenticated admin's data from Firestore
        FirebaseFirestore.getInstance().collection("Admin")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Retrieve admin's faculty, department, and year
                        String faculty = documentSnapshot.getString("faculty");
                        String department = documentSnapshot.getString("department");
                        String batch = documentSnapshot.getString("year");
                        String adminUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        if (faculty == null || department == null || batch == null) {
                            pd.dismiss();
                            Toast.makeText(this, "Admin details are incomplete", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Generate unique key for the teacher
                        DatabaseReference teacherRef = FirebaseDatabase.getInstance().getReference()
                                .child("teacher")
                                .child(faculty)
                                .child(department)
                                .child(batch);
                        String teacherKey = teacherRef.push().getKey();

                        if (teacherKey == null) {
                            pd.dismiss();
                            Toast.makeText(this, "Failed to generate teacher key", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Create a TeacherData object
                        TeacherData teacherData = new TeacherData(
                                name, email, phone, course, imageUrl, teacherKey, faculty, department, batch, adminUid
                        );

                        // Save teacher data under the teacher path
                        teacherRef.child(teacherKey).setValue(teacherData)
                                .addOnSuccessListener(aVoid -> {
                                    pd.dismiss();
                                    Toast.makeText(AddTeachers.this, "Teacher added successfully", Toast.LENGTH_SHORT).show();
                                    // Reset input fields
                                    resetFields();
                                })
                                .addOnFailureListener(e -> {
                                    pd.dismiss();
                                    Log.e("Database Error", "Failed to upload teacher data", e);
                                    Toast.makeText(AddTeachers.this, "Failed to add teacher", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        pd.dismiss();
                        Toast.makeText(this, "Admin data not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    pd.dismiss();
                    Log.e("Firestore Error", "Failed to fetch admin data", e);
                    Toast.makeText(this, "Failed to fetch admin data", Toast.LENGTH_SHORT).show();
                });
    }

    private void openGallery() {
        Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImage, REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                addTeacherImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void resetFields() {
        addTeacherName.setText("");
        addTeacherEmail.setText("");
        addTeacherPhone.setText("");
        addTeacherCourse.setText("");
        addTeacherImage.setImageResource(R.drawable.circle_img); // Replace with your default image
        bitmap = null;
    }
}
