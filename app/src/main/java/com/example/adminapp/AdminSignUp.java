package com.example.adminapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdminSignUp extends AppCompatActivity {

    private EditText edName, edEmail, edPassword, edRoll;
    private Spinner facultySpinner, departmentSpinner, yearSpinner;
    private Button signUpButton;
    private TextView loginRedirectText;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_sign_up);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize Views
        edName = findViewById(R.id.signup_name);
        edEmail = findViewById(R.id.signup_email);
        edPassword = findViewById(R.id.signup_password);
        edRoll = findViewById(R.id.signup_roll);
        facultySpinner = findViewById(R.id.signup_faculty);
        departmentSpinner = findViewById(R.id.signup_department);
        yearSpinner = findViewById(R.id.signup_year);
        signUpButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        // Setup Spinners
        setupSpinners();

        // Redirect to Login
        loginRedirectText.setOnClickListener(v -> {
            Intent intent = new Intent(AdminSignUp.this,AdminLogin.class);
            startActivity(intent);
        });

        // Handle Sign Up
        signUpButton.setOnClickListener(v -> signUpUser());
    }

    private void setupSpinners() {
        // Data for Faculties
        List<String> faculties = new ArrayList<>();
        faculties.add("Select Faculty");
        faculties.add("EEE");
        faculties.add("Civil");
        faculties.add("Mechanical");

        // Data for Departments
        HashMap<String, List<String>> facultyToDepartments = new HashMap<>();
        facultyToDepartments.put("EEE", new ArrayList<String>() {{
            add("Select Department");
            add("CSE");
            add("EEE");
            add("ECE");
            add("TE");
        }});
        facultyToDepartments.put("Civil", new ArrayList<String>() {{
            add("Select Department");
            add("Civil");
            add("Chemical Engineering");
        }});
        facultyToDepartments.put("Mechanical", new ArrayList<String>() {{
            add("Select Department");
            add("Mechanical");
            add("Mechatronics");
        }});

        // Data for Years
        List<String> years = new ArrayList<>();
        years.add("Select Year");
        years.add("1st Year");
        years.add("2nd Year");
        years.add("3rd Year");
        years.add("4th Year");

        // Set up Adapters
        ArrayAdapter<String> facultyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, faculties);
        facultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        facultySpinner.setAdapter(facultyAdapter);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        facultySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFaculty = faculties.get(position);
                if (position == 0) {
                    List<String> defaultDepartmentList = new ArrayList<>();
                    defaultDepartmentList.add("Select Department");
                    departmentSpinner.setAdapter(new ArrayAdapter<>(AdminSignUp.this, android.R.layout.simple_spinner_item, defaultDepartmentList));
                    return;
                }
                List<String> departments = facultyToDepartments.get(selectedFaculty);
                if (departments != null) {
                    departmentSpinner.setAdapter(new ArrayAdapter<>(AdminSignUp.this, android.R.layout.simple_spinner_item, departments));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void signUpUser() {
        String name = edName.getText().toString().trim();
        String email = edEmail.getText().toString().trim();
        String password = edPassword.getText().toString().trim();
        String roll = edRoll.getText().toString().trim();
        String faculty = facultySpinner.getSelectedItem().toString();
        String department = departmentSpinner.getSelectedItem().toString();
        String year = yearSpinner.getSelectedItem().toString();

        // Check if all fields are filled
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(roll) || faculty.equals("Select Faculty") ||
                department.equals("Select Department") || year.equals("Select Year")) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the email matches the required pattern: 7 digits followed by name@stud.kuet.ac.bd
        String emailPattern = "^\\d{7}[a-z]+@stud\\.kuet\\.ac\\.bd$";  // Regex pattern for roll number and name
        if (!email.matches(emailPattern)) {
            Toast.makeText(this, "Please enter a valid KUET email (7-digit roll number followed by your name@stud.kuet.ac.bd)", Toast.LENGTH_SHORT).show();
            return;
        }

        // Proceed with Firebase authentication and user creation
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && firebaseAuth.getCurrentUser() != null) {
                        userId = firebaseAuth.getCurrentUser().getUid();
                        saveUserToFirestore(name, email, roll, faculty, department, year, userId);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(AdminSignUp.this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    /*private void saveUserToFirestore(String name, String email, String roll, String faculty, String department, String year, String userId) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String fcmToken = task.getResult();
                        HashMap<String, Object> userData = new HashMap<>();
                        userData.put("name", name);
                        userData.put("email", email);
                        userData.put("roll", roll);
                        userData.put("faculty", faculty);
                        userData.put("department", department);
                        userData.put("year", year);
                        userData.put("userId", userId);
                        userData.put("fcmToken", fcmToken);

                        DocumentReference userRef = firestore.collection("Admin").document(userId);
                        userRef.set(userData, SetOptions.merge())
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(AdminSignUp.this, "Admin registered successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(AdminSignUp.this, MainActivity.class);
                                    startActivity(intent);
                                })
                                .addOnFailureListener(e -> Log.e("Firestore Error", "Error saving admin data", e));
                    } else {
                        Toast.makeText(AdminSignUp.this, "Failed to retrieve FCM token", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    */

    private void saveUserToFirestore(String name, String email, String roll, String faculty, String department, String year, String userId) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String fcmToken = task.getResult();

                        // Create Admin object
                        AdminData admin = new AdminData(name, email, roll, faculty, department, year, userId, fcmToken);

                        // Save to Firestore
                        firestore.collection("Admin").document(userId)
                                .set(admin, SetOptions.merge())
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(AdminSignUp.this, "Admin registered successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(AdminSignUp.this, MainActivity.class);
                                    startActivity(intent);
                                })
                                .addOnFailureListener(e -> Log.e("Firestore Error", "Error saving admin data", e));
                    } else {
                        Toast.makeText(AdminSignUp.this, "Failed to retrieve FCM token", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
