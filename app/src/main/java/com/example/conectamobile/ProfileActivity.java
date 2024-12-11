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

// Actividad para editar y mostrar el perfil del usuario
public class ProfileActivity extends AppCompatActivity {

    // Constantes
    private static final int PICK_IMAGE_REQUEST = 1; // Código para seleccionar imagen
    private static final String TAG = "ProfileActivity"; // Etiqueta para los logs

    // Declaración de variables
    private FirebaseAuth auth; // Para autenticación de usuarios
    private DatabaseReference database; // Para acceder a la base de datos de Firebase
    private StorageReference storage; // Para almacenar las imágenes de perfil

    private FirebaseUser currentUser; // Usuario actual autenticado
    private ImageView profileImageView; // Vista de la imagen de perfil
    private EditText nameEditText, emailEditText; // Campos de texto para nombre y correo
    private Button changePhotoButton, saveButton; // Botones para cambiar foto y guardar datos

    private EditText passwordEditText; // Campo de texto para contraseña
    private Uri imageUri; // URI de la imagen seleccionada

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Inicialización de objetos de Firebase y vistas
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference("users");
        storage = FirebaseStorage.getInstance().getReference("profile_images");

        profileImageView = findViewById(R.id.profileImageView);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        changePhotoButton = findViewById(R.id.changePhotoButton);
        saveButton = findViewById(R.id.saveButton);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button backButton = findViewById(R.id.backButtonP);

        // Mostrar el correo del usuario actual
        emailEditText.setText(currentUser.getEmail());

        // Cargar el perfil del usuario
        loadUserProfile();

        // Configurar botones con sus respectivos eventos
        changePhotoButton.setOnClickListener(v -> openImagePicker());
        saveButton.setOnClickListener(v -> saveUserProfile());
        backButton.setOnClickListener(v -> finish()); // Regresar a la actividad anterior
        saveButton.setOnClickListener(v -> saveUserData());
    }

    // Metodo para cargar el perfil del usuario desde la base de datos
    private void loadUserProfile() {
        // Obtener los datos del usuario desde Firebase
        database.child(currentUser.getUid()).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue(String.class);
                String photoUrl = snapshot.child("photoUrl").getValue(String.class);

                // Si existen los datos, mostrarlos en los campos correspondientes
                if (name != null) {
                    nameEditText.setText(name);
                }
                if (photoUrl != null) {
                    Glide.with(ProfileActivity.this).load(photoUrl).into(profileImageView); // Cargar imagen con Glide
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error al cargar perfil", error.toException()); // Manejo de errores en la carga de datos
            }
        });
    }

    // Metodo para guardar los datos del perfil
    private void saveUserData() {
        String name = nameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validar que los campos no estén vacíos
        if (name.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            // Guardar el nombre y la contraseña en la base de datos
            database.child(user.getUid()).child("name").setValue(name);
            database.child(user.getUid()).child("password").setValue(password);
            Toast.makeText(this, "Datos actualizados", Toast.LENGTH_SHORT).show();
        }
    }

    // Metodo para abrir el selector de imagenes
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST); // Iniciar la actividad para seleccionar imagen
    }

    // Metodo que se llama cuando se selecciona una imagen
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData(); // Obtener la URI de la imagen seleccionada
            profileImageView.setImageURI(imageUri); // Mostrar la imagen seleccionada en el ImageView
        }
    }

    // Metodo para guardar el perfil del usuario (nombre y foto de perfil)
    private void saveUserProfile() {
        String name = nameEditText.getText().toString();

        // Validar que el nombre no esté vacío
        if (name.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa un nombre", Toast.LENGTH_SHORT).show();
            return;
        }

        // Guardar el nombre en la base de datos
        database.child(currentUser.getUid()).child("name").setValue(name);

        // Si se ha seleccionado una imagen, subirla a Firebase Storage y guardar la URL
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
