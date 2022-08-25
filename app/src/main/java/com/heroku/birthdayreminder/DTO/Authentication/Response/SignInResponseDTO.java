package com.heroku.birthdayreminder.DTO.Authentication.Response;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.heroku.birthdayreminder.DTO.Birthdates.BirthdateDTO;
import com.heroku.birthdayreminder.models.Birthdate;

public class SignInResponseDTO {

    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("refreshToken")
    @Expose
    private String refreshToken;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("roles")
    @Expose
    private List<String> roles = null;
    @SerializedName("birthdates")
    @Expose
    private List<BirthdateDTO> birthdates = null;

    public SignInResponseDTO() {
    }

    /**
     *
     * @param roles
     * @param id
     * @param type
     * @param email
     * @param token
     * @param refreshToken
     * @param username
     */
    public SignInResponseDTO(String token, String type, String refreshToken, String id, String username, String email, List<String> roles,List<BirthdateDTO> birthdates
    ) {
        super();
        this.token = token;
        this.type = type;
        this.refreshToken = refreshToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.birthdates = birthdates;

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public List<BirthdateDTO> getBirthdates() {
        return birthdates;
    }

    public void setBirthdates(List<BirthdateDTO> birthdates) {
        this.birthdates = birthdates;
    }
}