package com.heroku.birthdayreminder.activities;

import static com.heroku.birthdayreminder.utils.Util.ACCESS_TOKEN;
import static com.heroku.birthdayreminder.utils.Util.BIRTHDATES;
import static com.heroku.birthdayreminder.utils.Util.REFRESH_TOKEN;
import static com.heroku.birthdayreminder.utils.Util.USER_APP;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.heroku.birthdayreminder.DTO.Authentication.Request.SignInRequestDTO;
import com.heroku.birthdayreminder.DTO.Authentication.Response.SignInResponseDTO;
import com.heroku.birthdayreminder.container.BirthdayReminderApplication;
import com.heroku.birthdayreminder.databinding.ActivityLoginBinding;
import com.heroku.birthdayreminder.models.Birthdate;
import com.heroku.birthdayreminder.models.User;
import com.heroku.birthdayreminder.services.BirthdatesHttpService;
import com.heroku.birthdayreminder.utils.Util;

import java.text.ParseException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding activityLoginBinding;
    private Context context;
    private BirthdatesHttpService birthdatesHttpService;
    private SharedPreferences sharedPreferences;
    private String username;
    private String password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLoginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = activityLoginBinding.getRoot();
        this.setContentView(view);


        this.context = this;
        this.birthdatesHttpService = ((BirthdayReminderApplication) getApplication()).getBirthdatesHttpService();
        this.sharedPreferences = ((BirthdayReminderApplication) getApplication()).getSharedPreferencesApp();


        this.activityLoginBinding.loginButton.setOnClickListener(v -> {
            this.attemptLogin();
        });
        this.setOnClickRegisterListener();
    }


    private void setOnClickRegisterListener() {
        activityLoginBinding.registerLink.setOnClickListener(view -> {
            Intent intent = new Intent(context, RegisterActivity.class);
            startActivity(intent);
        });
    }


    private void attemptLogin() {
        View focusView = null;
        this.resetErrorFields();
        this.setRegistrationFields();


        showProgress(true);

        SignInRequestDTO signInRequestDTO = new SignInRequestDTO(this.username, this.password);


        birthdatesHttpService.authentication(signInRequestDTO).enqueue(new Callback<SignInResponseDTO>() {
            @Override
            public void onResponse(Call<SignInResponseDTO> call, Response<SignInResponseDTO> response) {
                if (response.code() == 401) {
                    displayError(response);
                    return;
                }
                if (response.code() == 200) {
                    try {
                        saveUserAndNavigate(response);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    showProgress(false);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<SignInResponseDTO> call, Throwable t) {
                Log.d("TAG", "ERROR LOGIN: " + t.getMessage());
                Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                showProgress(false);
            }
        });

    }

    private void saveUserAndNavigate(Response<SignInResponseDTO> response) throws ParseException {
        SignInResponseDTO signInResponseDTO = response.body();
        Util.setAccessToken(sharedPreferences,signInResponseDTO.getToken());
        Util.setRefreshToken(sharedPreferences,signInResponseDTO.getRefreshToken());
        Util.setUserId(sharedPreferences,signInResponseDTO.getId());
        Gson gson = ((BirthdayReminderApplication)getApplication()).getGsonWithLocalDateSerializer();

        User user = getUserFromResponse(signInResponseDTO);
        String jsonUser = gson.toJson(user);
        String jsonBirthdates = gson.toJson(signInResponseDTO.getBirthdates());

        Util.setBirthdates(sharedPreferences,jsonBirthdates);
        Util.setUser(sharedPreferences,jsonUser);


        startActivity(new Intent(context, MainActivity.class));
    }

    private User getUserFromResponse(SignInResponseDTO signInResponseDTO) throws ParseException {
        ArrayList<Birthdate> birthdates = new ArrayList<>();
        for (int i = 0; i < signInResponseDTO.getBirthdates().size(); i++) {
            Birthdate birthdate = new Birthdate(
                    signInResponseDTO.getBirthdates().get(i).getDate(),
                    signInResponseDTO.getBirthdates().get(i).getFirstname(),
                    signInResponseDTO.getBirthdates().get(i).getLastname());
            birthdates.add(birthdate);
        }
        User user = new User(signInResponseDTO.getUsername(),signInResponseDTO.getEmail(),birthdates);

        return user;
    }

    private void displayError(Response<SignInResponseDTO> response) {
        View focusView = null;
        activityLoginBinding.username.setError("Bad credentials");
        activityLoginBinding.password.setError("Bad credentials");
        focusView = activityLoginBinding.username;
        showProgress(false);
        focusView.requestFocus();
    }

    private void setRegistrationFields() {
        this.username = activityLoginBinding.username.getText().toString();
        this.password = activityLoginBinding.password.getText().toString();
    }

    private void resetErrorFields() {
        activityLoginBinding.username.setError(null);
        activityLoginBinding.password.setError(null);
    }

    private void showProgress(boolean visible) {
        activityLoginBinding.loading.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }
}