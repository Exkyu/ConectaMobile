package com.example.conectamobile;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    private static final String MQTT_SERVER_URI = "tcp://broker.hivemq.com:1883"; // Servidor público MQTT
    private static final String TOPIC = "chat/messages";

    private ListView chatListView;
    private EditText messageEditText;
    private Button sendButton;
    private ArrayList<String> messagesList;
    private ChatAdapter chatAdapter;

    private DatabaseReference database;
    private MqttAndroidClient mqttClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatListView = findViewById(R.id.chatListView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        Button backButton = findViewById(R.id.backButton);
        messagesList = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, messagesList);
        chatListView.setAdapter(chatAdapter);

        database = FirebaseDatabase.getInstance().getReference("messages");

        setupMQTT();
        loadMessages();

        sendButton.setOnClickListener(view -> sendMessage());
        backButton.setOnClickListener(v -> finish());
    }

    private void setupMQTT() {
        mqttClient = new MqttAndroidClient(getApplicationContext(), MQTT_SERVER_URI, MqttClient.generateClientId());

        try {
            mqttClient.connect().setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "Conexión exitosa a MQTT");
                    try {
                        mqttClient.subscribe(TOPIC, 1); // Suscribirse al tópico
                    } catch (MqttException e) {
                        Log.e(TAG, "Error al suscribirse al tópico", e);
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e(TAG, "Error al conectar con MQTT", exception);
                }
            });

            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.e(TAG, "MQTT connection lost", cause);
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    String newMessage = new String(message.getPayload());
                    messagesList.add(newMessage); // Agregar mensaje a la lista
                    chatAdapter.notifyDataSetChanged(); // Actualizar el adaptador
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.d(TAG, "Mensaje entregado correctamente");
                }
            });
        } catch (MqttException e) {
            Log.e(TAG, "Error al conectar al broker MQTT", e);
        }
    }

    private void loadMessages() {
        database.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesList.clear();
                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                    String message = messageSnapshot.getValue(String.class);
                    messagesList.add(message);
                }
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading messages", error.toException());
            }
        });
    }

    private void sendMessage() {
        String message = messageEditText.getText().toString().trim();
        if (!message.isEmpty()) {
            // Guardar mensaje en Firebase
            database.push().setValue(message);

            // Publicar mensaje en MQTT
            try {
                mqttClient.publish(TOPIC, new MqttMessage(message.getBytes()));
            } catch (MqttException e) {
                Log.e(TAG, "Error publishing message", e);
            }

            messageEditText.setText("");
        } else {
            Toast.makeText(this, "El mensaje está vacío", Toast.LENGTH_SHORT).show();
        }
    }
}