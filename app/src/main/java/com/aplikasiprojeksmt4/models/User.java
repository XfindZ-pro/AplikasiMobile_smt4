package com.aplikasiprojeksmt4.models;

public class User {
    private String id;
    private String nama;
    private String email;
    private String fotoUrl;

    public User() {
        // Required for Firestore
    }

    public User(String id, String nama, String email, String fotoUrl) {
        this.id = id;
        this.nama = nama;
        this.email = email;
        this.fotoUrl = fotoUrl;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
}
