package com.example.mathhub;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CROP_IMAGE_REQUEST = 2;
    private ShapeableImageView profilePicture;
    private ImageView backButton, settingsButton;
    private TextView emailTextView, usernameTextView;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        profilePicture = findViewById(R.id.profile_picture);
        backButton = findViewById(R.id.back);
        settingsButton = findViewById(R.id.settings);
        emailTextView = findViewById(R.id.email);
        usernameTextView = findViewById(R.id.username);

        profilePicture.setOnClickListener(v -> openFileChooser());
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

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            cropImage(imageUri);
        } else if (requestCode == CROP_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap bitmap = extras.getParcelable("data");
                profilePicture.setImageBitmap(bitmap);
                uploadImageToFirebase(bitmap);
            }
        }
    }

    private void cropImage(Uri imageUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 256);
        intent.putExtra("outputY", 256);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_IMAGE_REQUEST);
    }

    private void uploadImageToFirebase(Bitmap bitmap) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profile_pictures").child(userId + ".jpg");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = storageReference.putBytes(data);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                // Image uploaded successfully
                storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Profile picture download URL retrieved
                    String downloadUrl = uri.toString();
                    updateUserProfilePicture(downloadUrl);
                });
            }).addOnFailureListener(e -> {
                // Handle unsuccessful uploads
            });
        }
    }

    private void updateUserProfilePicture(String imageUrl) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
            userRef.child("profilePictureUrl").setValue(imageUrl).addOnSuccessListener(aVoid -> {
                // Profile picture URL saved to the user's account successfully
            }).addOnFailureListener(e -> {
                // Handle failure
            });
        }
    }
}
