package com.example.adminapp.CtTracking;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.adminapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddCTInfo extends AppCompatActivity {

    private EditText inputCtNumber, inputCtDate, inputCourse, inputTeacherName, inputSyllabus;
    private Spinner inputStatus;
    private Button btnSubmitCT;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private DatabaseReference databaseReference;

    private String faculty, department, year, adminUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ctinfo);

        // Initialize views
        inputCtNumber = findViewById(R.id.input_ct_number);
        inputCtDate = findViewById(R.id.input_ct_date);
        inputCourse = findViewById(R.id.input_course);
        inputTeacherName = findViewById(R.id.input_teacher_name);
        inputSyllabus = findViewById(R.id.input_syllabus);
        inputStatus = findViewById(R.id.input_status);
        btnSubmitCT = findViewById(R.id.btn_submit_ct);

        // Firebase initialization
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("CT_Info");

        // Populate spinner
        setupSpinner();

        // Fetch faculty, department, year, and admin UID from Firestore
        fetchAdminDetails();

        // Submit button listener
        btnSubmitCT.setOnClickListener(v -> addCtInfo());
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Pending", "Done"} // Spinner options
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inputStatus.setAdapter(adapter);
    }

    private void fetchAdminDetails() {
        // Fetch admin details from Firestore
        firestore.collection("Admin")
                .document(auth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Retrieve admin details
                        faculty = documentSnapshot.getString("faculty");
                        department = documentSnapshot.getString("department");
                        year = documentSnapshot.getString("year");
                        adminUid = auth.getCurrentUser().getUid();

                        // Check if any required field is null
                        if (TextUtils.isEmpty(faculty) || TextUtils.isEmpty(department) || TextUtils.isEmpty(year)) {
                            Toast.makeText(this, "Admin details are incomplete. Please check Firestore.", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("AdminDetails", "Faculty: " + faculty + ", Department: " + department + ", Year: " + year);
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

    private void addCtInfo() {
        // Get input values
        String ctNumber = inputCtNumber.getText().toString().trim();
        String ctDate = inputCtDate.getText().toString().trim();
        String course = inputCourse.getText().toString().trim();
        String teacherName = inputTeacherName.getText().toString().trim();
        String syllabus = inputSyllabus.getText().toString().trim();
        String status = inputStatus.getSelectedItem().toString();

        // Validate inputs
        if (TextUtils.isEmpty(ctNumber) || TextUtils.isEmpty(ctDate) || TextUtils.isEmpty(course) ||
                TextUtils.isEmpty(teacherName) || TextUtils.isEmpty(syllabus)) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(faculty) || TextUtils.isEmpty(department) || TextUtils.isEmpty(year)) {
            Toast.makeText(this, "Admin details not loaded. Try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate unique key for the CT entry
        String ctKey = databaseReference.child(faculty).child(department).child(year).push().getKey();

        // Create CTData object
        CTData ctData = new CTData(
                ctKey,
                ctNumber,
                ctDate,
                course,
                teacherName,
                syllabus,
                status,
                faculty,
                department,
                year,
                adminUid
        );

        // Save data to Firebase
        if (ctKey != null) {
            databaseReference.child(faculty).child(department).child(year).child(ctKey)
                    .setValue(ctData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "CT info added successfully.", Toast.LENGTH_SHORT).show();
                            clearInputs();
                        } else {
                            Toast.makeText(this, "Failed to add CT info.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void clearInputs() {
        inputCtNumber.setText("");
        inputCtDate.setText("");
        inputCourse.setText("");
        inputTeacherName.setText("");
        inputSyllabus.setText("");
        inputStatus.setSelection(0); // Reset spinner to "Pending"
    }
}
