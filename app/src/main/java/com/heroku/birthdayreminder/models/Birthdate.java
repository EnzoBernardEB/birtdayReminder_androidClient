package com.heroku.birthdayreminder.models;

import com.heroku.birthdayreminder.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Date;
import java.util.UUID;


public class Birthdate implements Serializable {
    public UUID id;
    public LocalDate date;
    public String firstname;
    public String lastname;

    //    {
    //            "date": "1988-02-02",
    //            "firstName": "Peter",
    //            "lastName": "Bardu"
    //        }
    public Birthdate(String json) throws JSONException, ParseException {
        JSONObject jsonObject = new JSONObject(json);

        date = Util.initDateFromDB(jsonObject.getString("date"));
        firstname = jsonObject.getString("firstName");
        lastname = jsonObject.getString("lastName");
    }

    public Birthdate(LocalDate date, String firstname, String lastname) {
        this.date = date;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public Birthdate(UUID id, LocalDate date, String firstname, String lastname) {
        this.id = id;
        this.date = date;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("date", Util.printDate(date));
            json.put("firstname", firstname);
            json.put("lastname", lastname);
        } catch (JSONException e) {
        }
        return json;
    }


    public static class CustomComparator implements Comparator<Birthdate> {
        @Override
        public int compare(Birthdate birthday1, Birthdate birthday2) {

            if (birthday1.date.getMonthValue() > birthday2.date.getMonthValue())
                return 1;
            else if (birthday1.date.getMonth() == birthday2.date.getMonth()) {
                if (birthday1.date.getDayOfMonth() > birthday2.date.getDayOfMonth())
                    return 1;
                else if (birthday1.date.getDayOfMonth() == birthday2.date.getDayOfMonth())
                    return 0;
                else return -1;
            } else
                return -1;
        }
    }
}