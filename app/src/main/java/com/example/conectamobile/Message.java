package com.example.conectamobile;

// Clase Message que representa un mensaje en la aplicación de chat
public class Message {

    // Atributos de la clase
    private String text; // Contenido del mensaje
    private String type; // Tipo de mensaje: "sent" (enviado) o "received" (recibido)

    // Constructor que inicializa el contenido y tipo del mensaje
    public Message(String text, String type) {
        this.text = text; // Texto del mensaje
        this.type = type; // Tipo de mensaje
    }

    // Getter para obtener el texto del mensaje
    public String getText() {
        return text;
    }

    // Getter para obtener el tipo de mensaje
    public String getType() {
        return type;
    }
}
