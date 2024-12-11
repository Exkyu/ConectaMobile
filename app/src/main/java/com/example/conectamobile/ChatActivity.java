package com.example.conectamobile;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    private static final String MQTT_BROKER_URL = "tcp://broker.hivemq.com:1883";
    private static final String TOPIC_BASE = "chat/";

    private ListView chatListView;
    private EditText messageEditText;
    private TextView chatTextView;
    private Button sendButton;
    private ArrayList<Message> messagesList;
    private MessageAdapter messageAdapter;

    private DatabaseReference database;
    private DatabaseReference messagesDatabase;
    private MqttServicio mqttServicio;
    private String topic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatListView = findViewById(R.id.chatListView);
        messageEditText = findViewById(R.id.messageEditText);
        chatTextView = findViewById(R.id.chatTextView);
        sendButton = findViewById(R.id.sendButton);
        Button backButton = findViewById(R.id.backButtonC);

        messagesList = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messagesList);
        chatListView.setAdapter(messageAdapter);

        String contactId = getIntent().getStringExtra("contactId");

        if (contactId == null) {
            Toast.makeText(this, "No se seleccionó un contacto válido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        chatTextView.setText("Cargando...");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            database = FirebaseDatabase.getInstance().getReference("contacts").child(userId).child(contactId);
            messagesDatabase = FirebaseDatabase.getInstance().getReference("messages").child(userId).child(contactId);

            database.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String contactEmail = snapshot.child("email").getValue(String.class);
                        String contactName = snapshot.child("name").getValue(String.class);

                        if (contactEmail != null && contactName != null) {
                            topic = generateTopic(user.getEmail(), contactEmail);

                            chatTextView.setText("Chat con " + contactName + " (" + topic + ")");

                            try {
                                mqttServicio = new MqttServicio(MQTT_BROKER_URL);
                                setupMQTT();
                                loadMessages();
                            } catch (RuntimeException e) {
                                Log.e(TAG, "Error al inicializar MqttServicio", e);
                                Toast.makeText(ChatActivity.this, "Error al inicializar el servicio MQTT", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            sendButton.setOnClickListener(view -> sendMessage());
                            backButton.setOnClickListener(v -> finish());
                        } else {
                            Toast.makeText(ChatActivity.this, "Datos del contacto no válidos", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(ChatActivity.this, "El contacto no existe en la base de datos", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Error al recuperar datos del contacto", error.toException());
                }
            });
        } else {
            Toast.makeText(this, "No se pudo obtener la información del usuario", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private String generateTopic(String email1, String email2) {
        ArrayList<String> emails = new ArrayList<>();
        emails.add(normalizeEmail(email1));
        emails.add(normalizeEmail(email2));
        Collections.sort(emails); // Ordenar alfabéticamente
        return TOPIC_BASE + emails.get(0) + "_" + emails.get(1);
    }

    private String normalizeEmail(String email) {
        return email.replace("@", "_").replace(".", "_");
    }

    private void setupMQTT() {
        mqttServicio.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.e(TAG, "MQTT conexión perdida", cause);
                runOnUiThread(() -> Toast.makeText(ChatActivity.this, "Conexión MQTT perdida", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                String newMessage = new String(message.getPayload());
                Log.d(TAG, "Mensaje recibido en tópico: " + topic + ", mensaje: " + newMessage);

                saveMessageToFirebase(newMessage, "received");

                runOnUiThread(() -> {
                    messagesList.add(new Message(newMessage, "received"));
                    messageAdapter.notifyDataSetChanged();
                });
            }

            @Override
            public void deliveryComplete(org.eclipse.paho.client.mqttv3.IMqttDeliveryToken token) {
                Log.d(TAG, "Mensaje entregado correctamente");
            }
        });

        mqttServicio.connect();
        Log.d(TAG, "Suscribiéndose al tópico: " + topic);
        mqttServicio.subscribe(topic);
    }

    private void loadMessages() {
        messagesDatabase.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesList.clear();
                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                    String message = messageSnapshot.child("text").getValue(String.class);
                    String type = messageSnapshot.child("type").getValue(String.class);
                    messagesList.add(new Message(message, type));
                }
                runOnUiThread(() -> messageAdapter.notifyDataSetChanged());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error cargando mensajes", error.toException());
            }
        });
    }

    private void sendMessage() {
        String message = messageEditText.getText().toString().trim();
        if (!message.isEmpty()) {
            saveMessageToFirebase(message, "sent");

            if (mqttServicio.isConnected()) {
                Log.d(TAG, "Publicando mensaje en tópico: " + topic + ", mensaje: " + message);
                mqttServicio.publish(topic, message);
            } else {
                Toast.makeText(this, "No hay conexión MQTT", Toast.LENGTH_SHORT).show();
            }

            messageEditText.setText("");
        } else {
            Toast.makeText(this, "El mensaje está vacío", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveMessageToFirebase(String message, String type) {
        String messageId = messagesDatabase.push().getKey();
        if (messageId != null) {
            Map<String, String> messageData = new HashMap<>();
            messageData.put("text", message);
            messageData.put("type", type);
            messagesDatabase.child(messageId).setValue(messageData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Mensaje guardado en Firebase: " + message);
                        } else {
                            Log.e(TAG, "Error al guardar el mensaje en Firebase");
                        }
                    });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mqttServicio != null) {
            mqttServicio.disconnect();
            Log.d(TAG, "Desconexión de MQTT exitosa");
        } else {
            Log.w(TAG, "mqttServicio es null. No se realizó la desconexión.");
        }
    }
}
