package com.example.conectamobile;

// Clase Contact que representa un contacto en la aplicación
public class Contact {

    // Atributos de la clase
    private String name;  // Nombre del contacto
    private String email; // Correo electrónico del contacto
    private String phone; // Teléfono del contacto

    // Constructor vacío necesario para Firebase
    public Contact() {
        // Firebase requiere un constructor vacío para deserializar objetos
    }

    // Constructor con parámetros para inicializar un contacto
    public Contact(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    // Métodos Getter y Setter para cada atributo

    // Devuelve el nombre del contacto
    public String getName() {
        return name;
    }

    // Establece el nombre del contacto
    public void setName(String name) {
        this.name = name;
    }

    // Devuelve el correo electrónico del contacto
    public String getEmail() {
        return email;
    }

    // Establece el correo electrónico del contacto
    public void setEmail(String email) {
        this.email = email;
    }

    // Devuelve el teléfono del contacto
    public String getPhone() {
        return phone;
    }

    // Establece el teléfono del contacto
    public void setPhone(String phone) {
        this.phone = phone;
    }
}
