package com.example.kek.labs.Models;

public class User {
    private String email;
    private String name;
    private String surname;
    private String phone;

    public User(String email, String name, String surname, String phone) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
    }


    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPhone() {
        return phone;
    }
}
