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

public class ContactsListActivity extends AppCompatActivity {

    private ListView contactsListView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> contactNames;
    private ArrayList<String> contactIds;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);

        contactsListView = findViewById(R.id.contactsListView);

        Button deleteContactButton = findViewById(R.id.deleteContactButton);
        Button backButton = findViewById(R.id.backButtonList);
        backButton.setOnClickListener(v ->{
            Intent intent = new Intent(ContactsListActivity.this, MainMenuActivity.class);
            startActivity(intent);
        });

        contactNames = new ArrayList<>();
        contactIds = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contactNames);
        contactsListView.setAdapter(adapter);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance().getReference("contacts").child(userId);

        loadContacts();

        contactsListView.setOnItemClickListener((parent, view, position, id) -> {
            String contactId = contactIds.get(position);
            String contactName = contactNames.get(position);

            // Redirigir al chat con el contacto seleccionado
            Intent intent = new Intent(ContactsListActivity.this, ChatActivity.class);
            intent.putExtra("contactId", contactId); // Enviar ID del contacto
            intent.putExtra("contactName", contactName); // Enviar nombre del contacto
            startActivity(intent);
        });

        deleteContactButton.setOnClickListener(v -> {
            int position = contactsListView.getCheckedItemPosition();
            if (position != -1) {
                String contactIdToDelete = contactIds.get(position);
                deleteContact(contactIdToDelete);
            } else {
                Toast.makeText(this, "Selecciona un contacto para eliminar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadContacts() {
        database.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contactNames.clear();
                contactIds.clear();
                for (DataSnapshot contactSnapshot : snapshot.getChildren()) {
                    String contactId = contactSnapshot.getKey();
                    String contactName = contactSnapshot.child("name").getValue(String.class);

                    if (contactId != null && contactName != null) {
                        contactIds.add(contactId);
                        contactNames.add(contactName);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ContactsListActivity", "Error loading contacts", error.toException());
                Toast.makeText(ContactsListActivity.this, "Error al cargar los contactos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteContact(String contactId) {
        database.child(contactId).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Contacto eliminado correctamente", Toast.LENGTH_SHORT).show();
                        loadContacts();
                    } else {
                        Toast.makeText(this, "Error al eliminar el contacto", Toast.LENGTH_SHORT).show();
                        Log.e("ContactsListActivity", "Error deleting contact");
                    }
                });
    }
}