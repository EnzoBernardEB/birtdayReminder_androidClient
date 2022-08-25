package com.heroku.birthdayreminder.DTO.Birthdates;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddBirthdateDTO {

    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("firstname")
    @Expose
    private String firstname;
    @SerializedName("lastname")
    @Expose
    private String lastname;
    @SerializedName("userId")
    @Expose
    private String userId;

    public AddBirthdateDTO() {
    }

    /**
     *
     * @param date
     * @param firstname
     * @param userId
     * @param lastname
     */
    public AddBirthdateDTO(String date, String firstname, String lastname, String userId) {
        super();
        this.date = date;
        this.firstname = firstname;
        this.lastname = lastname;
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}