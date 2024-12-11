package com.example.conectamobile;

// Clase que representa el perfil de un usuario
public class UserProfile {

    private String name; // Nombre del usuario
    private String email; // Correo electrónico del usuario
    private String password; // Contraseña del usuario (aunque esto no es una buena práctica almacenarla directamente)
    private String photoUrl; // URL de la foto de perfil del usuario

    // Constructor vacío requerido por Firebase para la deserialización de objetos
    public UserProfile() {
        // Constructor vacío necesario para Firebase
    }

    // Constructor que inicializa todos los atributos del perfil
    public UserProfile(String name, String email, String password, String photoUrl) {
        this.name = name;        // Asigna el nombre del usuario
        this.email = email;      // Asigna el correo electrónico
        this.password = password; // Asigna la contraseña
        this.photoUrl = photoUrl; // Asigna la URL de la foto de perfil
    }

    // Getter para el nombre del usuario
    public String getName() {
        return name;
    }

    // Setter para el nombre del usuario
    public void setName(String name) {
        this.name = name;
    }

    // Getter para el correo electrónico del usuario
    public String getEmail() {
        return email;
    }

    // Setter para el correo electrónico del usuario
    public void setEmail(String email) {
        this.email = email;
    }

    // Getter para la contraseña del usuario
    public String getPassword() {
        return password;
    }

    // Setter para la contraseña del usuario
    public void setPassword(String password) {
        this.password = password;
    }

    // Getter para la URL de la foto de perfil del usuario
    public String getPhotoUrl() {
        return photoUrl;
    }

    // Setter para la URL de la foto de perfil del usuario
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
