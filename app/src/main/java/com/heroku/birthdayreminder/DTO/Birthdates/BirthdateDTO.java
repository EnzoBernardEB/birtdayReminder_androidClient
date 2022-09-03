package com.heroku.birthdayreminder.DTO.Birthdates;
import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

public class BirthdateDTO {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("date")
    @Expose
    private LocalDate date;
    @SerializedName("firstname")
    @Expose
    private String firstname;
    @SerializedName("lastname")
    @Expose
    private String lastname;
    @SerializedName("userId")
    @Expose
    private UUID userId;


    public BirthdateDTO() {
    }

    /**
     *
     * @param date
     * @param firstname
     * @param id
     * @param userId
     * @param lastname
     */
    public BirthdateDTO(String id, LocalDate date, String firstname, String lastname, UUID userId) {
        super();
        this.id = id;
        this.date = date;
        this.firstname = firstname;
        this.lastname = lastname;
        this.userId = userId;
    }

    public BirthdateDTO(LocalDate date, String firstname, String lastname, UUID userId) {
        this.date = date;
        this.firstname = firstname;
        this.lastname = lastname;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
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

    public Object getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    @NonNull
    @Override
    public String toString() {
        return "Birthdate of "+getFirstname()+" "+getLastname()+" : "+getDate().toString();
    }
}