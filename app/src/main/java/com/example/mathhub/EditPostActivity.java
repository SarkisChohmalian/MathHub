package com.example.mathhub;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class EditPostActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextDescription;
    private Button buttonSave;

    private String postId;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonSave = findViewById(R.id.buttonSave);

        postId = getIntent().getStringExtra("postId");

        firestore = FirebaseFirestore.getInstance();

        // Populate EditText fields with post data
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        editTextTitle.setText(title);
        editTextDescription.setText(description);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });
    }

    private void saveChanges() {
        String newTitle = editTextTitle.getText().toString().trim();
        String newDescription = editTextDescription.getText().toString().trim();

        if (postId != null) {
            firestore.collection("posts").document(postId).update("title", newTitle, "description", newDescription)
                    .addOnSuccessListener(aVoid -> {
                        setResult(Activity.RESULT_OK);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        // Failed to update post
                    });
        }
    }
}
