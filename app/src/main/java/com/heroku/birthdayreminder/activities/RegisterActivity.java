package com.heroku.birthdayreminder.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.heroku.birthdayreminder.DTO.Authentication.Request.SignInRequestDTO;
import com.heroku.birthdayreminder.DTO.Authentication.Request.SignUpRequestDTO;
import com.heroku.birthdayreminder.DTO.Authentication.Response.SignInResponseDTO;
import com.heroku.birthdayreminder.DTO.Authentication.Response.SignUpResponseDTO;
import com.heroku.birthdayreminder.DTO.Error.ErrorResponse;
import com.heroku.birthdayreminder.R;
import com.heroku.birthdayreminder.container.BirthdayReminderApplication;
import com.heroku.birthdayreminder.databinding.ActivityRegisterBinding;
import com.heroku.birthdayreminder.services.BirthdatesHttpService;
import com.heroku.birthdayreminder.strategy.registerError.RegisterErrorStrategy;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Optional;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding activityRegisterBinding;
    private Context context;
    private BirthdatesHttpService birthdatesHttpService;
    private String username;
    private String email;
    private String password;
    private String confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRegisterBinding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = activityRegisterBinding.getRoot();
        setContentView(view);

        this.context = this;
        birthdatesHttpService = ((BirthdayReminderApplication) getApplication()).getBirthdatesHttpService();

        setOnClickLoginListener();
        setOnClickRegisterListener();
    }
    private void setOnClickLoginListener() {
        activityRegisterBinding.registerLink.setOnClickListener(view -> {
            Intent intent = new Intent(context,LoginActivity.class);
            startActivity(intent);
        });
    }

    private void setOnClickRegisterListener() {
        activityRegisterBinding.registerButton.setOnClickListener(view -> {
            this.attemptRegister();
        });
    }

    private void attemptRegister() {
        View focusView = null;
        this.resetErrorFields();
        this.setRegistrationFields();

        if(!this.validatePassword()) {
            activityRegisterBinding.password.setError("Passwords doesn't match !");
            focusView = activityRegisterBinding.password;
            focusView.requestFocus();
            return;
        }
        showProgress(true);
        SignUpRequestDTO signUpRequestDTO = new SignUpRequestDTO(this.username,this.email, this.password);
        birthdatesHttpService.register(signUpRequestDTO).enqueue(new Callback<SignUpResponseDTO>() {
            @Override
            public void onResponse(Call<SignUpResponseDTO> call, Response<SignUpResponseDTO> response) {
                if (response.code() == 400) {
                    checkErrorMessageAndDisplayError(response);
                }
                if (response.code() == 200) {
                    SignUpResponseDTO signUpResponseDTO = response.body();
                    startActivity(new Intent(context, LoginActivity.class));
                    showProgress(false);
                    Toast.makeText(context, signUpResponseDTO.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SignUpResponseDTO> call, Throwable t) {
                Log.d("TAG", "onFailure: " + t.getMessage());
                Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkErrorMessageAndDisplayError(Response<SignUpResponseDTO> response) {
        View focusView = null;
        Converter<ResponseBody, ErrorResponse> errorConverter = ((BirthdayReminderApplication) getApplication()).getRetrofit().responseBodyConverter(ErrorResponse.class, new Annotation[0]);
        try {
            ErrorResponse errorResponse = errorConverter.convert(response.errorBody());
            Optional<RegisterErrorStrategy> strategyTarget = ((BirthdayReminderApplication) getApplication()).getCollectionRegisterErrorStrategies().stream().filter(strategie -> strategie.isError(errorResponse.getMessage())).findFirst();
            if(strategyTarget.isPresent())
                focusView = strategyTarget.get().setErrorFocus(context, activityRegisterBinding);
            showProgress(false);
            focusView.requestFocus();
        } catch (IOException e) {
            Log.d("TAG", "onErrorResponse: "+e.getMessage());
        }
    }

    private void setRegistrationFields() {
        this.username = activityRegisterBinding.username.getText().toString();
        this.email = activityRegisterBinding.email.getText().toString();
        this.password = activityRegisterBinding.password.getText().toString();
        this.confirmPassword = activityRegisterBinding.confirmPassword.getText().toString();
    }

    private boolean validatePassword() {
        if(!this.password.equals(this.confirmPassword))
            return false;
        return true;
    }

    private void showProgress(boolean visible) {
        activityRegisterBinding.loading.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    private void resetErrorFields() {
        activityRegisterBinding.username.setError(null);
        activityRegisterBinding.email.setError(null);
        activityRegisterBinding.password.setError(null);
    }


}