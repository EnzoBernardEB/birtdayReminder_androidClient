package com.heroku.birthdayreminder.DTO.Authentication.Response;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TokenRefreshResponseDTO {

    @SerializedName("accessToken")
    @Expose
    private String accessToken;
    @SerializedName("refreshToken")
    @Expose
    private String refreshToken;
    @SerializedName("tokenType")
    @Expose
    private String tokenType;


    public TokenRefreshResponseDTO() {
    }

    /**
     *
     * @param accessToken
     * @param tokenType
     * @param refreshToken
     */
    public TokenRefreshResponseDTO(String accessToken, String refreshToken, String tokenType) {
        super();
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

}