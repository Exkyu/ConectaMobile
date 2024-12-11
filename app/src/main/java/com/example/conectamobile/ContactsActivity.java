package com.example.conectamobile;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ContactsActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, phoneEditText;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        Button saveButton = findViewById(R.id.saveButton);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            database = FirebaseDatabase.getInstance().getReference("contacts").child(userId);

            saveButton.setOnClickListener(v -> saveContact());
        } else {
            Toast.makeText(this, "Por favor, inicia sesión para continuar.", Toast.LENGTH_SHORT).show();
            finish(); // Finaliza la actividad si no hay usuario autenticado
        }
    }

    private void saveContact() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        String contactId = database.push().getKey();
        Contact contact = new Contact(name, email, phone);
        database.child(contactId).setValue(contact).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Contacto añadido exitosamente", Toast.LENGTH_SHORT).show();
                finish(); // Cierra esta actividad
            } else {
                Toast.makeText(this, "Error al guardar el contacto", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
