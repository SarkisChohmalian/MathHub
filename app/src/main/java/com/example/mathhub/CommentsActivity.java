package com.example.mathhub;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {

    private static final String TAG = "CommentsActivity";

    private EditText commentEditText;
    private Button postCommentButton;
    private RecyclerView commentsRecyclerView;
    private CommentsAdapter commentsAdapter;
    private List<Comment> commentList;
    private FirebaseFirestore firestore;
    private String currentUserId;
    private String postId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        postId = getIntent().getStringExtra("postId");

        firestore = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        }
        commentEditText = findViewById(R.id.commentEditText);
        postCommentButton = findViewById(R.id.postCommentButton);
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);

        ImageView backButton = findViewById(R.id.back);
        backButton.setOnClickListener(v -> onBackPressed());

        commentList = new ArrayList<>();
        commentsAdapter = new CommentsAdapter(commentList);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsRecyclerView.setAdapter(commentsAdapter);

        loadComments();

        postCommentButton.setOnClickListener(v -> postComment());
    }

    private void loadComments() {
        if (postId != null) {
            firestore.collection("comments").document(postId).collection("post_comments")
                    .addSnapshotListener((value, error) -> {
                        if (value != null) {
                            for (DocumentChange documentChange : value.getDocumentChanges()) {
                                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                                    Comment comment = documentChange.getDocument().toObject(Comment.class);
                                    if (comment != null) {
                                        commentList.add(comment);
                                        commentsAdapter.notifyItemInserted(commentList.size() - 1);
                                    }
                                }
                            }
                        }
                    });
        }
    }

    private void postComment() {
        Log.d(TAG, "postComment: called");
        String commentText = commentEditText.getText().toString().trim();

        if (TextUtils.isEmpty(commentText)) {
            Toast.makeText(this, "Please enter a comment", Toast.LENGTH_SHORT).show();
            return;
        }

        Comment comment = new Comment(currentUserId, commentText);

        if (postId != null) {
            firestore.collection("comments").document(postId).collection("post_comments")
                    .add(comment)
                    .addOnSuccessListener(documentReference -> {
                        commentEditText.setText("");
                        Toast.makeText(this, "Comment posted", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to post comment: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Post ID is null", Toast.LENGTH_SHORT).show();
        }
    }
}
