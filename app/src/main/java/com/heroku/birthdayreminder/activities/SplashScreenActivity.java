package com.heroku.birthdayreminder.activities;


import static com.heroku.birthdayreminder.utils.Util.ACCESS_TOKEN;
import static com.heroku.birthdayreminder.utils.Util.REFRESH_TOKEN;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.auth0.android.jwt.JWT;
import com.heroku.birthdayreminder.DTO.Authentication.Request.TokenRefreshRequestDTO;
import com.heroku.birthdayreminder.DTO.Authentication.Response.TokenRefreshResponseDTO;
import com.heroku.birthdayreminder.R;
import com.heroku.birthdayreminder.container.BirthdayReminderApplication;
import com.heroku.birthdayreminder.models.User;
import com.heroku.birthdayreminder.services.BirthdatesHttpService;
import com.heroku.birthdayreminder.utils.Util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreenActivity extends AppCompatActivity {
    private String accessToken;
    private String refreshToken;
    private Boolean isAuthenticate;
    private BirthdatesHttpService birthdatesHttpService;
    private SharedPreferences sharedPreferences;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        this.sharedPreferences= ((BirthdayReminderApplication) getApplication()).getSharedPreferencesApp();
        this.birthdatesHttpService = ((BirthdayReminderApplication) getApplication()).getBirthdatesHttpService();

//        checkNetworkAndCreate();
        try {
            accessToken = sharedPreferences.getString(ACCESS_TOKEN,null);
            refreshToken = sharedPreferences.getString(REFRESH_TOKEN,null);
            Log.d("TAG", "IS a TOKEN ?: "+accessToken);

            if(!isTokenValid()){
                TokenRefreshRequestDTO tokenRefreshRequestDTO = new TokenRefreshRequestDTO(refreshToken);
                birthdatesHttpService.refreshToken(tokenRefreshRequestDTO).enqueue(new Callback<TokenRefreshResponseDTO>() {
                    @Override
                    public void onResponse(Call<TokenRefreshResponseDTO> call, Response<TokenRefreshResponseDTO> response) {
                        if(response.code() == 403) {
                            Log.d("TAG", "onResponse: REFRESH TOKEN UNVALID");
                            startActivity(new Intent(context, LoginActivity.class));
                            Toast.makeText(context,getString(R.string.not_authenticated_anymore), Toast.LENGTH_LONG).show();
                            finish();
                        }
                        Log.d("TAG", "onResponse: GETTING NEW TOKEN");
                        saveTokens(response);
                        navigateToMain();
                        finish();
                    }

                    @Override
                    public void onFailure(Call<TokenRefreshResponseDTO> call, Throwable t) {
                        Log.d("TAG", "onFailure: "+t.getMessage());
                    }
                });
            } else {
                Log.d("TAG", "onResponse: TOKEN STILL VALID");
                navigateToMain();
                finish();
            }
        }catch (Exception e) {
            Log.d("TAG", "onResponse: NO ACCESS TOKEN");
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private boolean isTokenValid(){
        JWT jwt = new JWT(accessToken);
        Date expirationDate = jwt.getExpiresAt();
        LocalDate jwtDate = Instant.ofEpochMilli(expirationDate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        boolean JWTIsExpired = LocalDate.now().isAfter(jwtDate);
        Log.d("TAG", "isTokenValid: "+ JWTIsExpired);
        return JWTIsExpired;
    }


    private void navigateToMain() {
        isAuthenticate = true;
        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
        intent.putExtra("isAuthenticate", isAuthenticate);
        startActivity(intent);
    }

    private void saveTokens(Response<TokenRefreshResponseDTO> response) {
        TokenRefreshResponseDTO tokenRefreshResponseDTO = response.body();
        Util.setAccessToken(sharedPreferences,tokenRefreshResponseDTO.getAccessToken());
        Util.setRefreshToken(sharedPreferences,tokenRefreshResponseDTO.getRefreshToken());
        Log.d("TAG", "onResponse: refresh "+tokenRefreshResponseDTO.getRefreshToken());
        Log.d("TAG", "onResponse:  new token"+tokenRefreshResponseDTO.getAccessToken());
    }

//    private void checkNetworkAndCreate() {
//        this.setVisibility(Util.isActiveNetwork(context));
//        if (Util.isActiveNetwork(context)) {
//            Toast.makeText(this, R.string.text_connected, Toast.LENGTH_SHORT).show();
//        } else {
//            activityLoginBinding.textNotConnected.setText(R.string.text_not_connected_activity);
//            Toast.makeText(context, R.string.text_not_connected, Toast.LENGTH_SHORT).show();
//        }
//    }
//    private void setVisibility(boolean isConnected) {
//        if (isConnected) {
//            activityLoginBinding.appConnected.setVisibility(View.VISIBLE);
//            activityLoginBinding.appNotConnected.setVisibility(View.INVISIBLE);
//        } else {
//            activityLoginBinding.appConnected.setVisibility(View.INVISIBLE);
//            activityLoginBinding.appNotConnected.setVisibility(View.VISIBLE);
//        }
//    }
}