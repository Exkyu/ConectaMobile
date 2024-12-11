package com.example.conectamobile;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttServicio {

    private final MqttClient client;
    private final MqttConnectOptions connectOptions;

    public MqttServicio(String brokerUrl) {
        String clientId = "dbf40fc4b0a3413c86160847055a20c7_" + System.currentTimeMillis(); // ClientID único
        try {
            MemoryPersistence persistence = new MemoryPersistence();
            client = new MqttClient(brokerUrl, clientId, persistence);
            connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);
        } catch (MqttException e) {
            throw new RuntimeException("Error al crear el cliente MQTT", e);
        }
    }

    public void setCallback(MqttCallback callback) {
        client.setCallback(callback);
    }

    public void connect() {
        try {
            if (!client.isConnected()) {
                client.connect(connectOptions);
                System.out.println("Conexión exitosa al broker MQTT");
            }
        } catch (MqttException e) {
            e.printStackTrace();
            System.err.println("Error al conectar al broker MQTT: " + e.getMessage());
        }
    }

    public void disconnect() {
        try {
            if (client.isConnected()) {
                client.disconnect();
                System.out.println("Desconexión exitosa del broker MQTT");
            }
        } catch (MqttException e) {
            e.printStackTrace();
            System.err.println("Error al desconectar del broker MQTT: " + e.getMessage());
        }
    }

    public void publish(String topic, String message) {
        try {
            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            mqttMessage.setQos(1); // QoS nivel 1
            client.publish(topic, mqttMessage);
            System.out.println("Mensaje publicado en el tópico: " + topic);
        } catch (MqttException e) {
            e.printStackTrace();
            System.err.println("Error al publicar el mensaje: " + e.getMessage());
        }
    }

    public void subscribe(String topic) {
        try {
            client.subscribe(topic, 1); // Suscribirse con QoS nivel 1
            System.out.println("Suscripción exitosa al tópico: " + topic);
        } catch (MqttException e) {
            e.printStackTrace();
            System.err.println("Error al suscribirse al tópico: " + e.getMessage());
        }
    }

    public boolean isConnected() {
        return client.isConnected();
    }
}
