package com.codeoftheweb.salvo;

public class Player {

    private int id;
    private String firstName;
    private String lastName;
    private String username;

    public Player() {
    }

    public Player(String first, String last) {
        firstName = first;
        lastName = last;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String toString() {
        return firstName + " " + lastName;
    }
}
