package com.example.adminapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.airbnb.lottie.LottieAnimationView;
import com.example.adminapp.UserInterface.UserSignUp;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Find views
        LottieAnimationView splashAnimation = findViewById(R.id.splashAnimation);
        Button adminButton = findViewById(R.id.adminButton);
        Button studentButton = findViewById(R.id.studentButton);

        splashAnimation.playAnimation();

        // Admin/CR Button Click Listener
        adminButton.setOnClickListener(view -> {
            // Navigate to Admin/CR Activity
            Intent intent = new Intent(SplashScreen.this, AdminSignUp .class);
            startActivity(intent);
            finish(); // Close splash screen
        });

        // Student Button Click Listener
        studentButton.setOnClickListener(view -> {
            // Navigate to Admin/CR Activity
            Intent intent = new Intent(SplashScreen.this, UserSignUp.class);
            startActivity(intent);
            finish(); // Close splash screen
        });

    }
}