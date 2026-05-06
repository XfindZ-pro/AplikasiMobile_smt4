package com.aplikasiprojeksmt4.models;

public class User {
    private int id;
    private String nama;
    private String email;

    // Constructor, Getter, dan Setter (Inilah OOP!)
    public User(int id, String nama, String email) {
        this.id = id;
        this.nama = nama;
        this.email = email;
    }

    public String getNama() { return nama; }
    public String getEmail() { return email; }
}
