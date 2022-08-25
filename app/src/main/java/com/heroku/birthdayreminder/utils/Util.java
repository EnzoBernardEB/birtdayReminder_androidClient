package com.heroku.birthdayreminder.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;


import com.heroku.birthdayreminder.adapter.ListItem;
import com.heroku.birthdayreminder.models.Birthdate;
import com.heroku.birthdayreminder.models.User;


import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Util {

    private static final String USER = "user";
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat FORMAT_INPUT = new SimpleDateFormat("dd/MM/yyyy");

    public static void setUser(SharedPreferences sharedPreferences, String json) {
        sharedPreferences.edit().putString(USER, json).apply();
    }

    public static User getUser(SharedPreferences sharedPreferences) throws JSONException, ParseException {
        return new User(sharedPreferences.getString(USER,null));
    }

    public static Date initDateFromDB(String str) throws ParseException {
        return FORMAT.parse(str);
    }

    public static String printDate(Date date) {
        return FORMAT.format(date);
    }

    public static long getAge(Date date) {
        long diff = System.currentTimeMillis() - date.getTime();
        return diff / 31622400000l;
    }

    public static boolean isUserNameValid(String userName) {
        if (userName == null || TextUtils.isEmpty(userName)) {
            return false;
        }
        return userName.trim().length() > 4;
    }

    public static boolean isPasswordValid(String password) {
        if (password == null || TextUtils.isEmpty(password)) {
            return false;
        }
        return password.trim().length() > 5;
    }

    public static boolean isActiveNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    public static String capitalize(String s) {
        if (s == null) return null;
        if (s.length() == 1) {
            return s.toUpperCase();
        }
        if (s.length() > 1) {
            return s.substring(0, 1).toUpperCase() + s.substring(1);
        }
        return "";
    }


    public static boolean isDateValid(String string) {
        try {
            FORMAT_INPUT.parse(string);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static Date initDateFromEditText(String str) throws ParseException {
        return FORMAT_INPUT.parse(str);
    }

    public static ArrayList<ListItem>   createListItems(ArrayList<Birthdate> birthdays) {

        ArrayList<ListItem> listItems = new ArrayList<>();

        int monthNumber = 0;
        String[] months = {"Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Aout", "Septembre", "Octobre", "Novembre", "Décembre"};

        // TODO : trier la liste en fonction des mois d'anniversaire

        return listItems;
    }
}