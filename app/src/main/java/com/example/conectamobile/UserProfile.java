package com.example.conectamobile;

public class UserProfile {
    private String name;
    private String email;
    private String password;
    private String photoUrl;

    public UserProfile() {
        // Constructor vacío requerido por Firebase
    }

    public UserProfile(String name, String email, String password, String photoUrl) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
