/*package com.example.adminapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.widget.AdapterView;
import android.widget.Spinner;



public class UploadNotice extends AppCompatActivity {

    private CardView addImage;
    private ImageView noticeImageView;
    private EditText noticeTitle;
    private Spinner visibilitySpinner;
    private Button uploadNoticeBtn;
    private final int REQ = 1;
    private Bitmap bitmap;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private ProgressDialog pd;

    private String visibilityOption = "Batch Only"; // Default selection
    private String downloadUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_notice);

        reference = FirebaseDatabase.getInstance().getReference().child("Notice");
        storageReference = FirebaseStorage.getInstance().getReference().child("Notice");

        pd = new ProgressDialog(this);

        // Initialize UI components
        addImage = findViewById(R.id.addImage);
        noticeImageView = findViewById(R.id.noticeImageView);
        noticeTitle = findViewById(R.id.noticeTitle);
        visibilitySpinner = findViewById(R.id.visibilitySpinner);
        uploadNoticeBtn = findViewById(R.id.uploadNoticeBtn);

        // Set Spinner Listener
        setupSpinner();

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        uploadNoticeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noticeTitle.getText().toString().isEmpty()) {
                    noticeTitle.setError("Empty");
                    noticeTitle.requestFocus();
                } else if (bitmap == null) {
                    uploadData();
                } else {
                    uploadImage();
                }
            }
        });
    }

    private void setupSpinner() {
        visibilitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                visibilityOption = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Default option is already set
            }
        });
    }

    private void uploadData() {
        final String uniqueKey = reference.push().getKey();

        String title = noticeTitle.getText().toString();

        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yy");
        String date = currentDate.format(callForDate.getTime());

        Calendar callForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        String time = currentTime.format(callForTime.getTime());

        NoticeData noticeData = new NoticeData(title, downloadUrl, date, time, uniqueKey, visibilityOption);

        reference.child(uniqueKey).setValue(noticeData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                pd.dismiss();
                Toast.makeText(UploadNotice.this, "Notice uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(UploadNotice.this, "Notice is not uploaded", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImage() {
        if (bitmap == null) {
            Toast.makeText(UploadNotice.this, "Please select an image first", Toast.LENGTH_SHORT).show();
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
        uploadTask.addOnCompleteListener(UploadNotice.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downloadUrl = uri.toString();
                            Log.d("UploadImage", "Image uploaded successfully, URL: " + downloadUrl);
                            uploadData();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Log.e("UploadImage", "Failed to get download URL: " + e.getMessage());
                            Toast.makeText(UploadNotice.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    pd.dismiss();
                    Log.e("UploadImage", "Upload failed: " + task.getException().getMessage());
                    Toast.makeText(UploadNotice.this, "Upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
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
                noticeImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
*/
package com.example.adminapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UploadNotice extends AppCompatActivity {

    private EditText noticeTitle, noticeBody;
    private Spinner visibilitySpinner;
    private Button uploadNoticeBtn;
    private ImageView noticeImageView;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private Bitmap bitmap;
    private String downloadUrl = "";
    private String faculty, department, batch, adminUid;
    private String visibilityOption = "Batch Only"; // Default visibility
    private final int REQ = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_notice);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Notice");
        storageReference = FirebaseStorage.getInstance().getReference("Notice");

        // Initialize Views
        noticeTitle = findViewById(R.id.noticeTitle);
        noticeBody = findViewById(R.id.noticeBody);
        visibilitySpinner = findViewById(R.id.visibilitySpinner);
        uploadNoticeBtn = findViewById(R.id.uploadNoticeBtn);
        noticeImageView = findViewById(R.id.noticeImageView);

        // Populate Spinner
        setupSpinner();

        // Fetch Admin Details
        fetchAdminDetails();

        // Button Listeners
        findViewById(R.id.addImage).setOnClickListener(v -> openGallery());
        uploadNoticeBtn.setOnClickListener(v -> addNotice());
    }

    private void setupSpinner() {
        visibilitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                visibilityOption = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Default option is already set
            }
        });
    }

    private void fetchAdminDetails() {
        firestore.collection("Admin")
                .document(auth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        faculty = documentSnapshot.getString("faculty");
                        department = documentSnapshot.getString("department");
                        batch = documentSnapshot.getString("year");
                        adminUid = auth.getCurrentUser().getUid();

                        if (TextUtils.isEmpty(faculty) || TextUtils.isEmpty(department) || TextUtils.isEmpty(batch)) {
                            Toast.makeText(this, "Admin details are incomplete. Please check Firestore.", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("AdminDetails", "Faculty: " + faculty + ", Department: " + department + ", Batch: " + batch);
                        }
                    } else {
                        Toast.makeText(this, "Admin details not found in Firestore!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch admin details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("FetchAdminDetailsError", e.getMessage());
                });
    }

    private void addNotice() {
        String title = noticeTitle.getText().toString().trim();
        String body = noticeBody.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(title)) {
            noticeTitle.setError("Enter title");
            noticeTitle.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(body)) {
            noticeBody.setError("Enter body");
            noticeBody.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(faculty) || TextUtils.isEmpty(department) || TextUtils.isEmpty(batch)) {
            Toast.makeText(this, "Admin details not loaded. Try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // If no image is selected, upload data directly
        if (bitmap == null) {
            uploadNoticeData();
        } else {
            uploadImage();
        }
    }

    private void uploadNoticeData() {
        String uniqueKey = databaseReference.child(faculty).child(department).child(batch).push().getKey();

        // Get current date and time
        Calendar calendar = Calendar.getInstance();
        String date = new SimpleDateFormat("dd-MM-yy").format(calendar.getTime());
        String time = new SimpleDateFormat("hh:mm a").format(calendar.getTime());

        // Create NoticeData object
        NoticeData noticeData = new NoticeData(
                uniqueKey, noticeTitle.getText().toString(), noticeBody.getText().toString(), downloadUrl,
                date, time, visibilityOption,adminUid
        );

        // Save data to Firebase
        if (uniqueKey != null) {
            databaseReference.child(faculty).child(department).child(batch).child(uniqueKey)
                    .setValue(noticeData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Notice uploaded successfully.", Toast.LENGTH_SHORT).show();
                            clearInputs();
                        } else {
                            Toast.makeText(this, "Failed to upload notice.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void uploadImage() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] finalImg = baos.toByteArray();

        String fileName = System.currentTimeMillis() + ".jpg";
        StorageReference filePath = storageReference.child(fileName);

        filePath.putBytes(finalImg)
                .addOnSuccessListener(taskSnapshot -> filePath.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            downloadUrl = uri.toString();
                            uploadNoticeData();
                        }))
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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
                noticeImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void clearInputs() {
        noticeTitle.setText("");
        noticeBody.setText("");
        visibilitySpinner.setSelection(0);
        bitmap = null;
       // noticeImageView.setImageResource(R.drawable.ic_placeholder); // Replace with your placeholder
    }
}
