package com.example.conectamobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

// Actividad para mostrar y gestionar la lista de contactos del usuario
public class ContactsListActivity extends AppCompatActivity {

    private ListView contactsListView; // ListView para mostrar los nombres de los contactos
    private ArrayAdapter<String> adapter; // Adaptador para la lista de contactos
    private ArrayList<String> contactNames; // Lista de nombres de los contactos
    private ArrayList<String> contactIds; // Lista de IDs de los contactos
    private DatabaseReference database; // Referencia a Firebase Realtime Database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);

        // Inicialización de la interfaz de usuario
        contactsListView = findViewById(R.id.contactsListView);

        Button deleteContactButton = findViewById(R.id.deleteContactButton); // Botón para eliminar contactos
        Button backButton = findViewById(R.id.backButtonList); // Botón para volver al menú principal

        // Configurar el botón "Volver"
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ContactsListActivity.this, MainMenuActivity.class);
            startActivity(intent);
        });

        // Inicialización de las listas y el adaptador
        contactNames = new ArrayList<>();
        contactIds = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contactNames);
        contactsListView.setAdapter(adapter);

        // Obtener el ID del usuario autenticado
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance().getReference("contacts").child(userId);

        // Cargar los contactos desde Firebase
        loadContacts();

        // Configurar el evento de clic en un elemento de la lista
        contactsListView.setOnItemClickListener((parent, view, position, id) -> {
            String contactId = contactIds.get(position); // Obtener el ID del contacto seleccionado
            String contactName = contactNames.get(position); // Obtener el nombre del contacto seleccionado

            // Redirigir al chat con el contacto seleccionado
            Intent intent = new Intent(ContactsListActivity.this, ChatActivity.class);
            intent.putExtra("contactId", contactId); // Pasar el ID del contacto
            intent.putExtra("contactName", contactName); // Pasar el nombre del contacto
            startActivity(intent);
        });

        // Configurar el evento de clic para eliminar un contacto
        deleteContactButton.setOnClickListener(v -> {
            int position = contactsListView.getCheckedItemPosition(); // Obtener la posición seleccionada
            if (position != -1) {
                String contactIdToDelete = contactIds.get(position); // Obtener el ID del contacto a eliminar
                deleteContact(contactIdToDelete); // Llamar al método para eliminar el contacto
            } else {
                Toast.makeText(this, "Selecciona un contacto para eliminar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para cargar los contactos desde Firebase
    private void loadContacts() {
        database.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Limpiar las listas antes de cargarlas de nuevo
                contactNames.clear();
                contactIds.clear();
                for (DataSnapshot contactSnapshot : snapshot.getChildren()) {
                    String contactId = contactSnapshot.getKey(); // Obtener el ID del contacto
                    String contactName = contactSnapshot.child("name").getValue(String.class); // Obtener el nombre del contacto

                    if (contactId != null && contactName != null) {
                        contactIds.add(contactId);
                        contactNames.add(contactName);
                    }
                }
                adapter.notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ContactsListActivity", "Error loading contacts", error.toException());
                Toast.makeText(ContactsListActivity.this, "Error al cargar los contactos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para eliminar un contacto de Firebase
    private void deleteContact(String contactId) {
        database.child(contactId).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Mensaje de éxito y recargar la lista de contactos
                        Toast.makeText(this, "Contacto eliminado correctamente", Toast.LENGTH_SHORT).show();
                        loadContacts();
                    } else {
                        // Mensaje de error al eliminar el contacto
                        Toast.makeText(this, "Error al eliminar el contacto", Toast.LENGTH_SHORT).show();
                        Log.e("ContactsListActivity", "Error deleting contact");
                    }
                });
    }
}
