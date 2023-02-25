package ru.freelance.exchange.models;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Set;


public class Users {
    private int id;

    @NotEmpty(message = "Поле должно быть заполнено.")
    private String firstName;

    @NotEmpty(message = "Поле должно быть заполнено.")
    private String secondName;

    private float cash;

    @NotEmpty(message = "Поле должно быть заполнено.")
    private String email;

    @NotEmpty(message = "Поле должно быть заполнено.")
    private String password;

    private String repeatPassword;

    private String profilePicture;

    private byte access;

    private String role;

    private boolean status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public float getCash() {
        return cash;
    }

    public void setCash(float cash) {
        this.cash = cash;
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

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public byte getAccess() {
        return access;
    }

    public void setAccess(byte access) {
        this.access = access;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isPasswordsEqual() {
        return password.equals(repeatPassword);
    }
}
