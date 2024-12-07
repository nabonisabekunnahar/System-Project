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
import android.widget.Spinner;
import android.widget.Toast;

import com.example.adminapp.R;
import com.example.adminapp.UploadNotice;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddTeachers extends AppCompatActivity {

    private ImageView addTeacherImage;
    private EditText addTeacherName,addTeacherEmail,addTeacherPhone,addTeacherCourse;
    private Spinner addTeacherCategory;
    private Button addTeacherBtn;
    private final int REQ = 1;
    private Bitmap bitmap = null;
    private String name,email,phone,course,downloadUrl = "";
    private ProgressDialog pd;
    private StorageReference storageReference;
    private DatabaseReference reference,dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teachers);

        addTeacherImage=findViewById(R.id.addTeacherImage);
        addTeacherName=findViewById(R.id.addTeacherName);
        addTeacherEmail=findViewById(R.id.addTeacherEmail);
        addTeacherPhone=findViewById(R.id.addTeacherPhone);
        addTeacherCourse=findViewById(R.id.addTeacherCourse);
        //addTeacherCategory=findViewById(R.id.addTeacherCategory);
        addTeacherBtn=findViewById(R.id.addTeacherBtn);

        pd=new ProgressDialog(this);

        reference = FirebaseDatabase.getInstance().getReference().child("teacher");
        storageReference = FirebaseStorage.getInstance().getReference();

        addTeacherImage.setOnClickListener((View.OnClickListener) (view) -> {
            openGalarey();
        });

        addTeacherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidation();
            }
        });

    }

    private void checkValidation() {

        name = addTeacherName.getText().toString();
        email = addTeacherEmail.getText().toString();
        phone = addTeacherPhone.getText().toString();
        course = addTeacherCourse.getText().toString();

        if (name.isEmpty()) {
            addTeacherName.setError("Empty");
            addTeacherName.requestFocus();
        } else if (email.isEmpty()) {
            addTeacherEmail.setError("Empty");
            addTeacherEmail.requestFocus();
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            addTeacherEmail.setError("Invalid Email");
            addTeacherEmail.requestFocus();
        } else if (phone.isEmpty()) {
            addTeacherPhone.setError("Empty");
            addTeacherPhone.requestFocus();
        } else if (!phone.matches("\\d{11}")) { // Example: Checking for a 10-digit phone number
            addTeacherPhone.setError("Invalid Phone Number");
            addTeacherPhone.requestFocus();
        } else if (course.isEmpty()) {
            addTeacherCourse.setError("Empty");
            addTeacherCourse.requestFocus();
        } else {
            uploadImage();
        }
    }

    private void insertData() {
        pd.setMessage("Uploading data...");
        pd.show();

        // Get the authenticated admin's data from Firestore
        FirebaseFirestore.getInstance().collection("Admin")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String faculty = documentSnapshot.getString("faculty");
                        String department = documentSnapshot.getString("department");
                        String batch = documentSnapshot.getString("year"); // Assuming "year" represents batch info

                        if (faculty == null || department == null || batch == null) {
                            pd.dismiss();
                            Toast.makeText(this, "Failed to fetch admin's faculty, department, or batch", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Generate a unique key for the teacher
                        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference()
                                .child("teacher")
                                .child(faculty)
                                .child(department)
                                .child(batch);
                        String key = dbRef.push().getKey();

                        // Create a TeacherData object
                        TeacherData teacherData = new TeacherData(name, email, phone, course, downloadUrl, key);

                        // Push the TeacherData object to the database
                        dbRef.child(key).setValue(teacherData)
                                .addOnSuccessListener(aVoid -> {
                                    pd.dismiss();
                                    Toast.makeText(AddTeachers.this, "Teacher added successfully", Toast.LENGTH_SHORT).show();
                                    // Clear fields or reset UI if necessary
                                    addTeacherName.setText("");
                                    addTeacherEmail.setText("");
                                    addTeacherPhone.setText("");
                                    addTeacherCourse.setText("");
                                    //addTeacherImage.setImageResource(R.drawable.placeholder_image); // Reset to default image
                                    bitmap = null;
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


    private void uploadImage() {
        if (bitmap == null) {
            insertData(); // Skip image upload if no image is selected
            return;
        }

        pd.setMessage("Uploading...");
        pd.show();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] finalImg = baos.toByteArray();

        final String fileName = System.currentTimeMillis() + ".jpg";
        final StorageReference filePath = storageReference.child(fileName);

        Log.d("UploadImage", "Uploading image to: " + filePath.getPath());

        UploadTask uploadTask = filePath.putBytes(finalImg);
        uploadTask.addOnCompleteListener(AddTeachers.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downloadUrl = uri.toString();
                            Log.d("UploadImage", "Image uploaded successfully, URL: " + downloadUrl);
                            insertData(); // Proceed with data upload after image upload
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Log.e("UploadImage", "Failed to get download URL: " + e.getMessage());
                            Toast.makeText(AddTeachers.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    pd.dismiss();
                    Log.e("UploadImage", "Upload failed: " + task.getException().getMessage());
                    Toast.makeText(AddTeachers.this, "Upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void insertImage(){
        
    }

    private void openGalarey(){
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
            } catch (IOException e) {
                e.printStackTrace();
            }
            addTeacherImage.setImageBitmap(bitmap);
        }
    }
}