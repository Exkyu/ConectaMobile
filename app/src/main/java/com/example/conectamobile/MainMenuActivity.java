package com.example.conectamobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// Actividad principal del menú que ofrece opciones al usuario autenticado
public class MainMenuActivity extends AppCompatActivity {

    private FirebaseAuth auth; // Instancia para la autenticación de usuarios
    private DatabaseReference database; // Referencia a la base de datos de Firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // Inicialización de los botones de la interfaz de usuario
        Button addContactButton = findViewById(R.id.addContactButton); // Botón para añadir contacto
        Button chatButton = findViewById(R.id.chatButton); // Botón para ir a la lista de contactos y chatear
        Button backButton = findViewById(R.id.backButton); // Botón para regresar al login
        Button editUserButton = findViewById(R.id.editUserButton); // Botón para editar el perfil del usuario

        // Inicialización de FirebaseAuth y DatabaseReference
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference("users");

        // Configuración del botón "Volver" que regresa al LoginActivity
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // Configuración del botón "Añadir Contacto" que abre la actividad para gestionar contactos
        addContactButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, ContactsActivity.class);
            startActivity(intent);
        });

        // Configuración del botón "Chatear" que abre la actividad con la lista de contactos
        chatButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, ContactsListActivity.class);
            startActivity(intent);
        });

        // Configuración del botón "Editar Usuario" que permite modificar los datos del perfil del usuario
        editUserButton.setOnClickListener(v -> {
            FirebaseUser user = auth.getCurrentUser(); // Obtener el usuario autenticado actual
            if (user != null) {
                Intent intent = new Intent(MainMenuActivity.this, ProfileActivity.class);
                intent.putExtra("userId", user.getUid()); // Pasar el ID del usuario al perfil
                startActivity(intent);
            } else {
                // Mostrar un mensaje en caso de que no haya usuario autenticado
                Toast.makeText(MainMenuActivity.this, "No se pudo acceder al perfil. Por favor, inicia sesión.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
