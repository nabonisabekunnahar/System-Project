package com.example.adminapp.CtTracking;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.adminapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CTInfoChange extends AppCompatActivity {

    private EditText etCtDate, etCtCourse, etTeacherName, etCtSyllabus;
    private Spinner spinnerCtStatus;
    private Button btnUpdate;

    private String ctKey, faculty, department, year;

    private ArrayAdapter<String> statusAdapter; // Declare statusAdapter here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ctinfo_change);

        // Initialize views
        etCtDate = findViewById(R.id.etCtDate);
        etCtCourse = findViewById(R.id.etCtCourse);
        etTeacherName = findViewById(R.id.etTeacherName);
        etCtSyllabus = findViewById(R.id.etCtSyllabus);
        spinnerCtStatus = findViewById(R.id.spinnerCtStatus);
        btnUpdate = findViewById(R.id.btnUpdate);

        // Initialize statusAdapter for the Spinner
        statusAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Pending", "Done"} // Status options
        );
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCtStatus.setAdapter(statusAdapter);

        // Get data from Intent
        ctKey = getIntent().getStringExtra("ctKey");
        faculty = getIntent().getStringExtra("faculty");
        department = getIntent().getStringExtra("department");
        year = getIntent().getStringExtra("year");

        if (ctKey == null || faculty == null || department == null || year == null) {
            Toast.makeText(this, "Invalid CT data. Please try again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Fetch existing data from Firebase
        fetchCTInfo();

        // Handle update button click
        btnUpdate.setOnClickListener(v -> updateCTInfo());
    }

    private void fetchCTInfo() {
        // Reference to the specific CT entry in the database
        DatabaseReference ctRef = FirebaseDatabase.getInstance().getReference()
                .child("CT_Info")
                .child(faculty)
                .child(department)
                .child(year)
                .child(ctKey);

        ctRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    // Populate the fields with existing data
                    etCtDate.setText(snapshot.child("ctDate").getValue(String.class));
                    etCtCourse.setText(snapshot.child("course").getValue(String.class));
                    etTeacherName.setText(snapshot.child("teacherName").getValue(String.class));
                    etCtSyllabus.setText(snapshot.child("syllabus").getValue(String.class));

                    // Set the spinner selection
                    String status = snapshot.child("status").getValue(String.class);
                    if (status != null) {
                        int spinnerPosition = statusAdapter.getPosition(status);
                        spinnerCtStatus.setSelection(spinnerPosition);
                    }
                } else {
                    Toast.makeText(this, "CT data not found.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                Toast.makeText(this, "Failed to fetch CT data.", Toast.LENGTH_SHORT).show();
                Log.e("FetchError", task.getException().getMessage());
                finish();
            }
        });
    }

    private void updateCTInfo() {
        // Get updated values from the UI
        String updatedCtDate = etCtDate.getText().toString().trim();
        String updatedCtCourse = etCtCourse.getText().toString().trim();
        String updatedTeacherName = etTeacherName.getText().toString().trim();
        String updatedCtSyllabus = etCtSyllabus.getText().toString().trim();
        String updatedCtStatus = spinnerCtStatus.getSelectedItem().toString();

        // Validate inputs
        if (updatedCtDate.isEmpty() || updatedCtCourse.isEmpty() || updatedTeacherName.isEmpty() || updatedCtSyllabus.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Reference to the specific CT entry in the database
        DatabaseReference ctRef = FirebaseDatabase.getInstance().getReference()
                .child("CT_Info")
                .child(faculty)
                .child(department)
                .child(year)
                .child(ctKey);

        // Update values in the database
        ctRef.child("ctDate").setValue(updatedCtDate);
        ctRef.child("course").setValue(updatedCtCourse);
        ctRef.child("teacherName").setValue(updatedTeacherName);
        ctRef.child("syllabus").setValue(updatedCtSyllabus);
        ctRef.child("status").setValue(updatedCtStatus)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "CT Information updated successfully.", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity
                    } else {
                        Toast.makeText(this, "Failed to update CT Information.", Toast.LENGTH_SHORT).show();
                        Log.e("UpdateError", task.getException().getMessage());
                    }
                });
    }
}
