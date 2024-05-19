package com.example.mathhub;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private ImageView backButton, settingsButton;
    private TextView emailTextView, usernameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        backButton = findViewById(R.id.back);
        settingsButton = findViewById(R.id.settings);
        emailTextView = findViewById(R.id.email);
        usernameTextView = findViewById(R.id.username);

        backButton.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            finish();
        });
        settingsButton.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, SettingsActivity.class)));

        updateUserInfo();
    }

    private void updateUserInfo() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            emailTextView.setText(user.getEmail());
            usernameTextView.setText(user.getDisplayName());
        }
    }
}
