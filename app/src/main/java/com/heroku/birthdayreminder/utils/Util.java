package com.heroku.birthdayreminder.utils;

import static java.util.stream.Collectors.toCollection;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.heroku.birthdayreminder.DTO.Authentication.Request.TokenRefreshRequestDTO;
import com.heroku.birthdayreminder.DTO.Authentication.Response.TokenRefreshResponseDTO;
import com.heroku.birthdayreminder.R;
import com.heroku.birthdayreminder.activities.LoginActivity;
import com.heroku.birthdayreminder.activities.MainActivity;
import com.heroku.birthdayreminder.activities.SplashScreenActivity;
import com.heroku.birthdayreminder.adapter.BirthdayItem;
import com.heroku.birthdayreminder.adapter.ListItem;
import com.heroku.birthdayreminder.adapter.MonthItem;
import com.heroku.birthdayreminder.container.BirthdayReminderApplication;
import com.heroku.birthdayreminder.models.Birthdate;
import com.heroku.birthdayreminder.models.User;
import com.heroku.birthdayreminder.services.BirthdatesHttpService;

import org.json.JSONException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Util {

    public static final String USER_APP = "user";
    public static final String BIRTHDATES = "birthdates";
    public static final String USER_ID = "userId";
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("dd/MM/yyyy");
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

    public static User getUser(SharedPreferences sharedPreferences, Gson gson) {

        String jsonUser = sharedPreferences.getString(USER_APP, null);
        Type listTypeUser = new TypeToken<User>() {
        }.getType();
        User result = gson.fromJson(jsonUser, listTypeUser);
        return result;
    }

    public static String getRefreshToken(SharedPreferences sharedPreferences) {
        String refreshToken = sharedPreferences.getString(REFRESH_TOKEN, null);

        return refreshToken;
    }

    public static String getAccessToken(SharedPreferences sharedPreferences) {
        String accesToken = sharedPreferences.getString(ACCESS_TOKEN, null);

        return accesToken;
    }

    public static UUID getUserUUID(SharedPreferences sharedPreferences) {
        String uuid = sharedPreferences.getString(USER_ID, null);
        if (uuid != null)
            return UUID.fromString(uuid);
        return null;
    }


    public static LocalDate initDateFromDB(String str) throws ParseException {
        return LocalDate.parse(str);
    }
    public static LocalDate convertToLocalDate(Date dateToConvert) {
        return  Instant.ofEpochMilli(dateToConvert.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
    public static Date convertToDate(LocalDate dateToConvert) {
        return java.util.Date.from(dateToConvert.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    public static String printDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
    }

    public static int calculateAge(LocalDate birthDate, LocalDate currentDate) {
        if ((birthDate != null) && (currentDate != null)) {
            return Period.between(birthDate, currentDate).getYears();
        } else {
            return 0;
        }
    }

    public static String printNumberPretty(int number) {
        if(number >=10)
            return String.valueOf(number);
        return "0"+String.valueOf(number);
    }

    public static void silentAuthentication(BirthdatesHttpService birthdatesHttpService,SharedPreferences sharedPreferences, Context context) {
        String refreshToken = Util.getRefreshToken(sharedPreferences);
        TokenRefreshRequestDTO tokenRefreshRequestDTO = new TokenRefreshRequestDTO(refreshToken);

        birthdatesHttpService.refreshToken(tokenRefreshRequestDTO).enqueue(new Callback<TokenRefreshResponseDTO>() {
            @Override
            public void onResponse(Call<TokenRefreshResponseDTO> call, Response<TokenRefreshResponseDTO> response) {
                if(response.code() == 403) {
                    Log.d("TAG", "onResponse: REFRESH TOKEN UNVALID SILENT AUTH  "+refreshToken);
                    Intent intent  = new Intent(context, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    Toast.makeText(context,context.getString(R.string.not_authenticated_anymore), Toast.LENGTH_LONG).show();
                    return;
                }
                Log.d("TAG", "onResponse: GETTING NEW TOKEN" + response.body().getAccessToken());
                Log.d("TAG", "onResponse: GETTING NEW TOKEN" + response.body().getRefreshToken());
                Util.saveTokens(response,sharedPreferences);
            }

            @Override
            public void onFailure(Call<TokenRefreshResponseDTO> call, Throwable t) {
                Log.d("TAG", "onFailure: "+t.getMessage());
            }
        });
    }
    public static void navigateToMain(Context context) {
        Boolean isSilentAuthenticated = true;
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("isSilentAuthenticated", isSilentAuthenticated);
        context.startActivity(intent);
    }

    public static void saveTokens(Response<TokenRefreshResponseDTO> response, SharedPreferences sharedPreferences) {
        TokenRefreshResponseDTO tokenRefreshResponseDTO = response.body();
        Util.setAccessToken(sharedPreferences,tokenRefreshResponseDTO.getAccessToken());
        Util.setRefreshToken(sharedPreferences,tokenRefreshResponseDTO.getRefreshToken());
        Log.d("TAG", "WRITTE NEW refresh "+tokenRefreshResponseDTO.getRefreshToken());
        Log.d("TAG", "WRITTE NEW   new token"+tokenRefreshResponseDTO.getAccessToken());
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

    public static ArrayList<ListItem> createListItems(ArrayList<Birthdate> birthdays, Context context) {

        ArrayList<ListItem> result = new ArrayList<>();
        String[] months = Util.getMonths(context);

        ArrayList<MonthItem> monthItems = Util.convertMonthItem(months);
        ArrayList<BirthdayItem> birthdayItems = Util.convertBirthdateItem(birthdays);
        ArrayList<BirthdayItem> sortedBirthdates = new ArrayList<>();

        result.addAll(monthItems);
        if(birthdays.isEmpty())
            return result;
        sortedBirthdates.add(birthdayItems.get(0));
        Comparator comparator = new Birthdate.CustomComparator();

        for (int i = 0; i < monthItems.size(); i++) {
            if (birthdayItems.size() > 0) {
                int finalI = i;
                ArrayList<BirthdayItem> birthdatesOfMonth = Util.getBirthdatesOfSpecificMonth( birthdayItems, monthItems,i);
                if (birthdatesOfMonth.size() == 0)
                    continue;

                int index = Util.getIndexMonth(result,birthdatesOfMonth);
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

    private static int getIndexMonth(ArrayList<ListItem> result, ArrayList<BirthdayItem> birthdatesOfMonth) {
        Optional<ListItem> month = result
                .stream()
                .filter(listItem -> listItem.getType() == 0 &&
                        ((MonthItem) listItem).number == ((BirthdayItem) birthdatesOfMonth.get(0)).birthday.date.getMonthValue()).findFirst();

        return result.indexOf(month.get());
    }
    private static ArrayList<BirthdayItem> getBirthdatesOfSpecificMonth(ArrayList<BirthdayItem> birthdayItems,ArrayList<MonthItem> monthItems, int index) {
        ArrayList<BirthdayItem> birthdatesOfMonth = birthdayItems
                .stream()
                .filter(birthdayItem -> birthdayItem.birthday.date.getMonthValue() == monthItems.get(index).number)
                .collect(toCollection(ArrayList::new));

        return birthdatesOfMonth;
    }

    private static ArrayList<MonthItem> convertMonthItem(String[] months) {
        ArrayList<MonthItem> monthItems = new ArrayList<>();
        int monthNumber = 1;
        for (String month : months) {
            monthItems.add(new MonthItem(monthNumber++, month));
        }

        return monthItems;
    }

    private static String[] getMonths(Context context) {
        String[] months = {
                context.getString(R.string.January),
                context.getString(R.string.February),
                context.getString(R.string.March),
                context.getString(R.string.April),
                context.getString(R.string.May),
                context.getString(R.string.June),
                context.getString(R.string.July),
                context.getString(R.string.August),
                context.getString(R.string.September),
                context.getString(R.string.October),
                context.getString(R.string.November),
                context.getString(R.string.December),
        };

        return months;
    }
}