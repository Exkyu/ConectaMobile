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

public class MainMenuActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Button addContactButton = findViewById(R.id.addContactButton);
        Button chatButton = findViewById(R.id.chatButton);
        Button backButton = findViewById(R.id.backButton);
        Button editUserButton = findViewById(R.id.editUserButton);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference("users");



        backButton.setOnClickListener(v ->{
            Intent intent = new Intent(MainMenuActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // Ir a la actividad de Añadir Contacto
        addContactButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, ContactsActivity.class);
            startActivity(intent);
        });

        // Ir a la lista de contactos para chatear
        chatButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, ContactsListActivity.class);
            startActivity(intent);
        });

        editUserButton.setOnClickListener(v -> {
            FirebaseUser user = auth.getCurrentUser();
            if (user != null) {
                Intent intent = new Intent(MainMenuActivity.this, ProfileActivity.class);
                intent.putExtra("userId", user.getUid());
                startActivity(intent);
            } else {
                Toast.makeText(MainMenuActivity.this, "Matate esta wea no funca", Toast.LENGTH_SHORT).show();
            }
        });
    }
}