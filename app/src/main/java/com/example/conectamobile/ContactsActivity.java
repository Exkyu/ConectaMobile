package com.example.conectamobile;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity {
    private ListView contactsListView;
    private ArrayList<String> contactsList;
    private ArrayAdapter<String> adapter;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        contactsListView = findViewById(R.id.contactsListView);
        contactsList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contactsList);
        contactsListView.setAdapter(adapter);

        database = FirebaseDatabase.getInstance().getReference("contacts");
        loadContacts();
    }

    private void loadContacts() {
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contactsList.clear();
                for (DataSnapshot contactSnapshot : snapshot.getChildren()) {
                    String contact = contactSnapshot.getValue(String.class);
                    contactsList.add(contact);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ContactsActivity", "Error loading contacts", error.toException());
            }
        });
    }
}