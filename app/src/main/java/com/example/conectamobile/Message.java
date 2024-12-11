package com.example.conectamobile;

public class Message {
    private String text;
    private String type; // "sent" o "received"

    public Message(String text, String type) {
        this.text = text;
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public String getType() {
        return type;
    }
}
