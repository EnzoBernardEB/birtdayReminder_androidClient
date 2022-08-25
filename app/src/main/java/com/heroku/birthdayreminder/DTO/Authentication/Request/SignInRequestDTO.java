package com.heroku.birthdayreminder.DTO.Authentication.Request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SignInRequestDTO {

    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("password")
    @Expose
    private String password;

    public SignInRequestDTO() {
    }

    /**
     *
     * @param password
     * @param username
     */
    public SignInRequestDTO(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}