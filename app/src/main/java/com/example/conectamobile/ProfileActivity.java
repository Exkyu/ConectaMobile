package com.example.conectamobile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "ProfileActivity";

    private FirebaseAuth auth;
    private DatabaseReference database;
    private StorageReference storage;

    private FirebaseUser currentUser;
    private ImageView profileImageView;
    private EditText nameEditText, emailEditText;
    private Button changePhotoButton, saveButton;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference("users");
        storage = FirebaseStorage.getInstance().getReference("profile_images");

        profileImageView = findViewById(R.id.profileImageView);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        changePhotoButton = findViewById(R.id.changePhotoButton);
        saveButton = findViewById(R.id.saveButton);

        emailEditText.setText(currentUser.getEmail());
        loadUserProfile();

        changePhotoButton.setOnClickListener(v -> openImagePicker());
        saveButton.setOnClickListener(v -> saveUserProfile());
    }

    private void loadUserProfile() {
        database.child(currentUser.getUid()).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue(String.class);
                String photoUrl = snapshot.child("photoUrl").getValue(String.class);

                if (name != null) {
                    nameEditText.setText(name);
                }
                if (photoUrl != null) {
                    Glide.with(ProfileActivity.this).load(photoUrl).into(profileImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading profile", error.toException());
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);
        }
    }

    private void saveUserProfile() {
        String name = nameEditText.getText().toString();

        if (name.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa un nombre", Toast.LENGTH_SHORT).show();
            return;
        }

        database.child(currentUser.getUid()).child("name").setValue(name);

        if (imageUri != null) {
            StorageReference fileReference = storage.child(currentUser.getUid() + ".jpg");
            fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                database.child(currentUser.getUid()).child("photoUrl").setValue(uri.toString());
                                Toast.makeText(ProfileActivity.this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
                            }))
                    .addOnFailureListener(e -> Log.e(TAG, "Error al cambiar la imagen"));
        } else {
            Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
        }
    }
}
