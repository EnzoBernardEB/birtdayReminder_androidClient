package com.heroku.birthdayreminder.DTO.Birthdates;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BirthdateDTO {

    @SerializedName("id")
    @Expose
    private String id;
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
    private Object userId;


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
    public BirthdateDTO(String id, String date, String firstname, String lastname, Object userId) {
        super();
        this.id = id;
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

    public Object getUserId() {
        return userId;
    }

    public void setUserId(Object userId) {
        this.userId = userId;
    }

}