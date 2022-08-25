package com.heroku.birthdayreminder.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


import com.heroku.birthdayreminder.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

public class User {

    public Long id;
    public String username;
    public String email;

    public ArrayList<Birthdate> birthdays;

    public String stringJson;

    //{"id":1,"username":"peter","email":"peter.bardu@gmail.com", "birthdays": [
    //        {
    //            "date": "1988-02-02",
    //            "firstName": "Peter",
    //            "lastName": "Bardu"
    //        }
    //    ]
    // }
    public User(String json) throws JSONException, ParseException {

        stringJson = json;

        JSONObject jsonObject = new JSONObject(json);
        id = jsonObject.getLong("id");
        username = jsonObject.getString("username");
        email = jsonObject.getString("email");
        birthdays = new ArrayList<>();

        JSONArray jsonArray = jsonObject.getJSONArray("birthdays");
        for (int i = 0; i < jsonArray.length(); i++) {
            birthdays.add(new Birthdate(jsonObject.getJSONArray("birthdays").getJSONObject(i).toString()));
        }
    }

    public void addBirthday(SharedPreferences sharedPreferences, Birthdate birthday) {
        birthdays.add(birthday);

        try {

            JSONObject jsonObject = new JSONObject(stringJson);
            jsonObject.getJSONArray("birthdays").put(birthday.toJson());
            stringJson = jsonObject.toString();

            Log.d("lol", "addBirthday: " + stringJson);

            Util.setUser(sharedPreferences, stringJson);
        } catch (JSONException e) {

        }
    }

}