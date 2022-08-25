package com.heroku.birthdayreminder.DTO.Authentication.Request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SignUpRequestDTO {

    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("password")
    @Expose
    private String password;

    public SignUpRequestDTO() {
    }

    /**
     *
     * @param password
     * @param email
     * @param username
     */
    public SignUpRequestDTO(String username, String email, String password) {
        super();
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

}