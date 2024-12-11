package com.example.conectamobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// Actividad para manejar el inicio, registro y cierre de sesión de usuarios
public class LoginActivity extends AppCompatActivity {

    // Elementos de la interfaz
    private EditText emailEditText, passwordEditText; // Campos de entrada para correo y contraseña
    private FirebaseAuth auth; // Instancia de FirebaseAuth para la autenticación

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_login);

        // Inicialización de los elementos de la interfaz
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton); // Botón de inicio de sesión
        Button logoutButton = findViewById(R.id.logoutButton); // Botón de cierre de sesión
        Button registerButton = findViewById(R.id.registerButton); // Botón de registro

        // Inicialización de FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // Configurar los listeners para los botones
        loginButton.setOnClickListener(v -> loginUser()); // Maneja el inicio de sesión
        registerButton.setOnClickListener(v -> registerUser()); // Maneja el registro de usuario
        logoutButton.setOnClickListener(v -> logoutUser()); // Maneja el cierre de sesión
    }

    // Método para iniciar sesión con Firebase
    private void loginUser() {
        String email = emailEditText.getText().toString().trim(); // Obtener correo ingresado
        String password = passwordEditText.getText().toString().trim(); // Obtener contraseña ingresada

        // Validar que ambos campos estén completos
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa correo y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        // Intentar iniciar sesión con FirebaseAuth
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Si el inicio de sesión es exitoso, mostrar mensaje y redirigir al menú principal
                Toast.makeText(LoginActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
                startActivity(intent);
                finish(); // Finalizar esta actividad para evitar regresar a la pantalla de login
            } else {
                // Si hay un error, mostrar el mensaje correspondiente
                Toast.makeText(LoginActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para registrar un nuevo usuario con Firebase
    private void registerUser() {
        String email = emailEditText.getText().toString().trim(); // Obtener correo ingresado
        String password = passwordEditText.getText().toString().trim(); // Obtener contraseña ingresada

        // Validar que ambos campos estén completos
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar que la contraseña cumpla con la longitud mínima
        if (password.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        // Intentar registrar el usuario con FirebaseAuth
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Si el registro es exitoso, mostrar mensaje
                Toast.makeText(LoginActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
            } else {
                // Si hay un error, mostrar el mensaje correspondiente
                Toast.makeText(LoginActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para cerrar sesión
    private void logoutUser() {
        auth.signOut(); // Cerrar la sesión del usuario en Firebase
        Toast.makeText(this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show();

        // Limpiar los campos de correo y contraseña para evitar datos residuales
        emailEditText.setText("");
        passwordEditText.setText("");
    }
}
