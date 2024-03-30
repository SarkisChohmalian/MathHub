package com.example.mathhub;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreatePostFragment extends Fragment {
    private EditText editTextTitle, editTextDescription;
    private Button buttonPost;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_post, container, false);

        editTextTitle = view.findViewById(R.id.editTextTitle);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        buttonPost = view.findViewById(R.id.button_post);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postQuestion();
            }
        });

        return view;
    }

    private void postQuestion() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            return;
        }

        String userId = currentUser.getUid();

        if (title.isEmpty() || title.length() < 2) {
            editTextTitle.setError("Title must be at least 2 characters long");
            return;
        }

        if (description.isEmpty() || description.length() < 5) {
            editTextDescription.setError("Description must be at least 5 characters long");
            return;
        }

        String creatorUserId = currentUser.getUid();

        Post post = new Post(title, description, userId, creatorUserId);

        firestore.collection("posts").add(post)
                .addOnSuccessListener(documentReference -> {
                    editTextTitle.setText("");
                    editTextDescription.setText("");
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Post added successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Failed to add post: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
