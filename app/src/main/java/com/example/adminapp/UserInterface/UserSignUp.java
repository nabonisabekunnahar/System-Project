package com.example.adminapp.UserInterface;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.adminapp.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserSignUp extends AppCompatActivity {

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
        setContentView(R.layout.activity_user_sign_up);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize Views
        edName = findViewById(R.id.user_name);
        edEmail = findViewById(R.id.user_email);
        edPassword = findViewById(R.id.user_password);
        edRoll = findViewById(R.id.user_roll);
        facultySpinner = findViewById(R.id.user_faculty);
        departmentSpinner = findViewById(R.id.user_department);
        yearSpinner = findViewById(R.id.user_year);
        signUpButton = findViewById(R.id.user_signup_button);
        loginRedirectText = findViewById(R.id.user_login_redirect);

        // Setup Spinners
        setupSpinners();

        // Redirect to Login
        loginRedirectText.setOnClickListener(v -> {
            Intent intent = new Intent(UserSignUp.this, UserLogin.class);
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
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                String selectedFaculty = faculties.get(position);
                if (position == 0) {
                    List<String> defaultDepartmentList = new ArrayList<>();
                    defaultDepartmentList.add("Select Department");
                    departmentSpinner.setAdapter(new ArrayAdapter<>(UserSignUp.this, android.R.layout.simple_spinner_item, defaultDepartmentList));
                    return;
                }
                List<String> departments = facultyToDepartments.get(selectedFaculty);
                if (departments != null) {
                    ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(UserSignUp.this, android.R.layout.simple_spinner_item, departments);
                    departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    departmentSpinner.setAdapter(departmentAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action required
            }
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

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(roll) || faculty.equals("Select Faculty") ||
                department.equals("Select Department") || year.equals("Select Year")) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        checkDuplicateUser(email, roll, name, password, faculty, department, year);
    }

    private void checkDuplicateUser(String email, String roll, String name, String password, String faculty, String department, String year) {
        CollectionReference usersRef = firestore.collection("User");

        Query query = usersRef.whereEqualTo("email", email).whereEqualTo("roll", roll);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                Toast.makeText(UserSignUp.this, "Email or Roll number already exists!", Toast.LENGTH_SHORT).show();
            } else {
                createUserInFirebase(email, password, name, roll, faculty, department, year);
            }
        });
    }

    private void createUserInFirebase(String email, String password, String name, String roll, String faculty, String department, String year) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && firebaseAuth.getCurrentUser() != null) {
                        userId = firebaseAuth.getCurrentUser().getUid();
                        saveUserToFirestore(name, email, roll, faculty, department, year, userId);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(UserSignUp.this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveUserToFirestore(String name, String email, String roll, String faculty, String department, String year, String userId) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String fcmToken = task.getResult();

                        // Pass null for userImageUrl since the user hasn't uploaded an image yet
                        UserData user = new UserData(name, email, roll, faculty, department, year, userId, fcmToken, null);

                        firestore.collection("User").document(userId)
                                .set(user)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(UserSignUp.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(UserSignUp.this, UserMainActivity.class));
                                })
                                .addOnFailureListener(e -> Log.e("Firestore Error", "Error saving user data", e));
                    } else {
                        Toast.makeText(UserSignUp.this, "Failed to retrieve FCM token", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
