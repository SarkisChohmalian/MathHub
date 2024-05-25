package com.example.mathhub;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class DiophantineActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diophantine);

        ImageView backButton = findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateBack();
            }
        });

        ImageButton learnMoreButton = findViewById(R.id.learnmoree); // Update ID here
        learnMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWikipediaPage();
            }
        });
    }

    private void navigateBack() {
        finish();
    }

    private void openWikipediaPage() {
        String wikipediaUrl = "https://en.wikipedia.org/wiki/Diophantine_equation#Linear_Diophantine_equations";

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(wikipediaUrl));
        startActivity(intent);
    }
}
