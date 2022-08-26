package com.heroku.birthdayreminder.utils;

import static java.util.stream.Collectors.toCollection;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.heroku.birthdayreminder.R;
import com.heroku.birthdayreminder.adapter.BirthdayItem;
import com.heroku.birthdayreminder.adapter.ListItem;
import com.heroku.birthdayreminder.adapter.MonthItem;
import com.heroku.birthdayreminder.models.Birthdate;
import com.heroku.birthdayreminder.models.User;
import org.json.JSONException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public class Util {

    public static final String USER_APP = "user";
    public static final String BIRTHDATES = "birthdates";
    public static final String USER_ID = "userId";
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat FORMAT_INPUT = new SimpleDateFormat("dd/MM/yyyy");
    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";

    public static void setUser(SharedPreferences sharedPreferences, String json) {
        sharedPreferences.edit().putString(USER_APP, json).apply();
    }

    public static void setAccessToken(SharedPreferences sharedPreferences, String json) {
        sharedPreferences.edit().putString(ACCESS_TOKEN, json).apply();
    }

    public static void setRefreshToken(SharedPreferences sharedPreferences, String json) {
        sharedPreferences.edit().putString(REFRESH_TOKEN, json).apply();
    }

    public static void setBirthdates(SharedPreferences sharedPreferences, String json) {
        sharedPreferences.edit().putString(BIRTHDATES, json).apply();
    }

    public static void setUserId(SharedPreferences sharedPreferences, String json) {
        sharedPreferences.edit().putString(USER_ID, json).apply();
    }

    public static User getUser(SharedPreferences sharedPreferences) throws JSONException, ParseException {
        Gson gson = new Gson();
        String jsonUser = sharedPreferences.getString(USER_APP, null);
        Type listTypeUser = new TypeToken<User>() {
        }.getType();
        User result = gson.fromJson(jsonUser, listTypeUser);
        return result;
    }

    public static UUID getUserUUID(SharedPreferences sharedPreferences) {
        String uuid = sharedPreferences.getString(USER_ID, null);
        if (uuid != null)
            return UUID.fromString(uuid);
        return null;
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

    public static ArrayList<ListItem> createListItems(ArrayList<Birthdate> birthdays) {

        ArrayList<ListItem> result = new ArrayList<>();
        String[] months = Util.getMonths();

        ArrayList<MonthItem> monthItems = Util.convertMonthItem(months);
        ArrayList<BirthdayItem> birthdayItems = Util.convertBirthdateItem(birthdays);
        ArrayList<BirthdayItem> sortedBirthdates = new ArrayList<>();

        result.addAll(monthItems);
        sortedBirthdates.add(birthdayItems.get(0));
        Comparator comparator = new Birthdate.CustomComparator();

        for (int i = 0; i < monthItems.size(); i++) {
            if (birthdayItems.size() > 0) {
                int finalI = i;
                ArrayList<BirthdayItem> birthdatesOfMonth = Util.getBirthdatesOfSpecificMonth( birthdayItems, monthItems,i);
                if (birthdatesOfMonth.size() == 0)
                    continue;

                Optional<ListItem> month = result
                        .stream()
                        .filter(listItem -> listItem.getType() == 0 &&
                                ((MonthItem) listItem).number == ((BirthdayItem) birthdatesOfMonth.get(0)).birthday.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getMonthValue()).findFirst();
                int index = result.indexOf(month.get());


                birthdatesOfMonth.sort((d1, d2) -> comparator.compare(((BirthdayItem) d1).birthday, ((BirthdayItem) d2).birthday));
                result.addAll(index + 1, birthdatesOfMonth);
                birthdayItems.removeAll(birthdatesOfMonth);
            } else {
                break;
            }
        }


        Log.d("TAG", "createListItems: sortedBirthdays" + sortedBirthdates.toString());


        return result;
    }

    private static ArrayList<BirthdayItem> convertBirthdateItem(ArrayList<Birthdate> birthdays) {
        ArrayList<BirthdayItem> birthdayItems = new ArrayList<>();
        for (Birthdate birthdate : birthdays) {
            birthdayItems.add(new BirthdayItem(birthdate));
        }

        return birthdayItems;
    }
    private static ArrayList<BirthdayItem> getBirthdatesOfSpecificMonth(ArrayList<BirthdayItem> birthdayItems,ArrayList<MonthItem> monthItems, int index) {
        birthdayItems
                .stream()
                .filter(listItem -> listItem.getType() == 1 &&
                        ((BirthdayItem) listItem).birthday.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getMonthValue() == monthItems.get(index).number)
                .collect(toCollection(ArrayList::new));

        return birthdayItems;
    }

    private static ArrayList<MonthItem> convertMonthItem(String[] months) {
        ArrayList<MonthItem> monthItems = new ArrayList<>();
        int monthNumber = 1;
        for (String month : months) {
            monthItems.add(new MonthItem(monthNumber++, month));
        }

        return monthItems;
    }

    private static String[] getMonths() {
        String[] months = {
                Resources.getSystem().getString(R.string.January),
                Resources.getSystem().getString(R.string.February),
                Resources.getSystem().getString(R.string.March),
                Resources.getSystem().getString(R.string.April),
                Resources.getSystem().getString(R.string.May),
                Resources.getSystem().getString(R.string.June),
                Resources.getSystem().getString(R.string.July),
                Resources.getSystem().getString(R.string.August),
                Resources.getSystem().getString(R.string.September),
                Resources.getSystem().getString(R.string.October),
                Resources.getSystem().getString(R.string.November),
                Resources.getSystem().getString(R.string.December),
        };

        return months;
    }
}