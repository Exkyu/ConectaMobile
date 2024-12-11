package com.example.conectamobile;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

// Clase que maneja las operaciones de conexión, publicación, suscripción y desconexión para MQTT
public class MqttServicio {

    private final MqttClient client; // Cliente MQTT
    private final MqttConnectOptions connectOptions; // Opciones de conexión para MQTT

    // Constructor que recibe la URL del broker MQTT
    public MqttServicio(String brokerUrl) {
        // Crear un ClientID único basado en el tiempo actual
        String clientId = "dbf40fc4b0a3413c86160847055a20c7_" + System.currentTimeMillis();
        try {
            MemoryPersistence persistence = new MemoryPersistence(); // Persistencia en memoria
            client = new MqttClient(brokerUrl, clientId, persistence); // Crear el cliente MQTT
            connectOptions = new MqttConnectOptions(); // Inicializar opciones de conexión
            connectOptions.setCleanSession(true); // Establecer sesión limpia
        } catch (MqttException e) {
            // Si hay un error al crear el cliente MQTT, lanzar una excepción
            throw new RuntimeException("Error al crear el cliente MQTT", e);
        }
    }

    // Metodo para configurar el callback del cliente MQTT
    public void setCallback(MqttCallback callback) {
        client.setCallback(callback);
    }

    // Metodo para conectar el cliente MQTT al broker
    public void connect() {
        try {
            // Verificar si el cliente no está conectado
            if (!client.isConnected()) {
                client.connect(connectOptions); // Intentar conectar con las opciones de conexión
                System.out.println("Conexión exitosa al broker MQTT");
            }
        } catch (MqttException e) {
            e.printStackTrace(); // Imprimir la excepción
            System.err.println("Error al conectar al broker MQTT: " + e.getMessage());
        }
    }

    // Metodo para desconectar el cliente MQTT del broker
    public void disconnect() {
        try {
            // Verificar si el cliente está conectado antes de desconectar
            if (client.isConnected()) {
                client.disconnect(); // Intentar desconectar del broker
                System.out.println("Desconexión exitosa del broker MQTT");
            }
        } catch (MqttException e) {
            e.printStackTrace(); // Imprimir la excepción
            System.err.println("Error al desconectar del broker MQTT: " + e.getMessage());
        }
    }

    // Metodo para publicar un mensaje en un tópico MQTT
    public void publish(String topic, String message) {
        try {
            // Crear un mensaje MQTT con el contenido proporcionado
            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            mqttMessage.setQos(1); // Establecer el nivel de QoS a 1 (entrega al menos una vez)
            client.publish(topic, mqttMessage); // Publicar el mensaje en el tópico
            System.out.println("Mensaje publicado en el tópico: " + topic);
        } catch (MqttException e) {
            e.printStackTrace(); // Imprimir la excepción
            System.err.println("Error al publicar el mensaje: " + e.getMessage());
        }
    }

    // Metodo para suscribirse a un tópico MQTT
    public void subscribe(String topic) {
        try {
            client.subscribe(topic, 1); // Suscribirse al tópico con QoS nivel 1
            System.out.println("Suscripción exitosa al tópico: " + topic);
        } catch (MqttException e) {
            e.printStackTrace(); // Imprimir la excepción
            System.err.println("Error al suscribirse al tópico: " + e.getMessage());
        }
    }

    // Metodo para verificar si el cliente está conectado al broker
    public boolean isConnected() {
        return client.isConnected();
    }
}
