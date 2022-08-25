package com.heroku.birthdayreminder.DTO.Authentication.Response;
        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;

public class SignUpResponseDTO {

    @SerializedName("message")
    @Expose
    private String message;


    public SignUpResponseDTO() {
    }

    public SignUpResponseDTO(String message) {
        super();
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}