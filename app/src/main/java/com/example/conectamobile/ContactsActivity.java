package com.example.conectamobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// Actividad para añadir contactos a la base de datos Firebase
public class ContactsActivity extends AppCompatActivity {

    // Declaración de los campos de entrada y referencia a Firebase
    private EditText nameEditText, emailEditText, phoneEditText;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        // Inicialización de los elementos de la interfaz de usuario
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        Button saveButton = findViewById(R.id.saveButton); // Botón para guardar el contacto
        Button backButton = findViewById(R.id.backButtonCon); // Botón para regresar al menú principal

        // Configuración del botón "Volver"
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ContactsActivity.this, MainMenuActivity.class);
            startActivity(intent);
        });

        // Verificar si hay un usuario autenticado
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Obtener el ID del usuario actual y configurar la referencia de Firebase
            String userId = currentUser.getUid();
            database = FirebaseDatabase.getInstance().getReference("contacts").child(userId);

            // Configurar el botón "Guardar"
            saveButton.setOnClickListener(v -> saveContact());
        } else {
            // Si no hay usuario autenticado, mostrar mensaje y cerrar la actividad
            Toast.makeText(this, "Por favor, inicia sesión para continuar.", Toast.LENGTH_SHORT).show();
            finish(); // Finaliza la actividad para evitar continuar sin un usuario válido
        }
    }

    // Método para guardar un contacto en Firebase
    private void saveContact() {
        // Obtener los valores de los campos de entrada
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();

        // Validar que todos los campos estén llenos
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generar un ID único para el contacto
        String contactId = database.push().getKey();

        // Crear una instancia del contacto
        Contact contact = new Contact(name, email, phone);

        // Guardar el contacto en Firebase bajo el ID generado
        database.child(contactId).setValue(contact).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Mostrar mensaje de éxito y cerrar la actividad
                Toast.makeText(this, "Contacto añadido exitosamente", Toast.LENGTH_SHORT).show();
                finish(); // Regresa automáticamente a la actividad anterior
            } else {
                // Mostrar mensaje de error
                Toast.makeText(this, "Error al guardar el contacto", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
