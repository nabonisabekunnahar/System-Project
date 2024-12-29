package com.example.adminapp.UserInterface;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.adminapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserLogin extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Get references to UI elements
        emailEditText = findViewById(R.id.user_email);
        passwordEditText = findViewById(R.id.user_password);
        loginButton = findViewById(R.id.user_login_button);

        // Set up login button click listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(UserLogin.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Perform login with email and password
                loginUser(email, password);
            }
        });
    }

    private void loginUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Login successful
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            // Proceed to the next activity (e.g., User Dashboard)
                            Intent intent = new Intent(UserLogin.this, UserMainActivity.class); // Replace with your target activity
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        // If login fails, display a message
                        Toast.makeText(UserLogin.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
