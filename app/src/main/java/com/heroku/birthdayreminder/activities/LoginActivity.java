package com.heroku.birthdayreminder.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.heroku.birthdayreminder.DTO.Authentication.Request.SignInRequestDTO;
import com.heroku.birthdayreminder.DTO.Authentication.Response.SignInResponseDTO;
import com.heroku.birthdayreminder.container.BirthdayReminderApplication;
import com.heroku.birthdayreminder.databinding.ActivityLoginBinding;
import com.heroku.birthdayreminder.models.ApiCallback;
import com.heroku.birthdayreminder.services.BirthdatesHttpService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements ApiCallback {
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
        setContentView(view);


        this.context = this;
        birthdatesHttpService = ((BirthdayReminderApplication) getApplication()).getBirthdatesHttpService();
        sharedPreferences = ((BirthdayReminderApplication) getApplication()).getSharedPreferencesApp();


        activityLoginBinding.loginButton.setOnClickListener(v -> {
            this.attemptLogin();
        });
        setOnClickRegisterListener();
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
                    SignInResponseDTO signInResponseDTO = response.body();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("access_token", signInResponseDTO.getToken());
                    editor.putString("refresh_token", signInResponseDTO.getRefreshToken());
                    editor.apply();
                    startActivity(new Intent(context, MainActivity.class));
                    showProgress(false);
                }
            }

            @Override
            public void onFailure(Call<SignInResponseDTO> call, Throwable t) {
                Log.d("TAG", "onFailure: " + t.getMessage());
                Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
            }
        });

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

    @Override
    public void fail(final String json) {
        activityLoginBinding.loading.setVisibility(View.INVISIBLE);

    }

    @Override
    public void success(final String json) {


    }
}