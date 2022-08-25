package com.heroku.birthdayreminder.DTO.Authentication.Request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TokenRefreshRequestDTO {

    @SerializedName("refreshToken")
    @Expose
    private String refreshToken;

    public TokenRefreshRequestDTO() {
    }

    /**
     *
     * @param refreshToken
     */
    public TokenRefreshRequestDTO(String refreshToken) {
        super();
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}
