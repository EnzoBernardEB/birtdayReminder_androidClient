package com.heroku.birthdayreminder.DTO.User;




        import java.util.ArrayList;
        import java.util.List;
        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;
        import com.heroku.birthdayreminder.DTO.Birthdates.BirthdateDTO;

public class UserInformationsDTO {

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
    private ArrayList<RoleDTO> roles = null;
    @SerializedName("birthdays")
    @Expose
    private ArrayList<BirthdateDTO> birthdays = null;

    public UserInformationsDTO() {
    }

    /**
     *
     * @param roles
     * @param birthdays
     * @param id
     * @param email
     * @param username
     */
    public UserInformationsDTO(String id, String username, String email, ArrayList<RoleDTO> roles, ArrayList<BirthdateDTO> birthdays) {
        super();
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.birthdays = birthdays;
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

    public ArrayList<RoleDTO> getRoles() {
        return roles;
    }

    public void setRoles(ArrayList<RoleDTO> roles) {
        this.roles = roles;
    }

    public ArrayList<BirthdateDTO> getBirthdays() {
        return birthdays;
    }

    public void setBirthdays(ArrayList<BirthdateDTO> birthdays) {
        this.birthdays = birthdays;
    }

}