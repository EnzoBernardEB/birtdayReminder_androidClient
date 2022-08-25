package com.heroku.birthdayreminder.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.heroku.birthdayreminder.R;
import com.heroku.birthdayreminder.container.BirthdayReminderApplication;
import com.heroku.birthdayreminder.utils.Util;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = ((BirthdayReminderApplication) getApplication()).getSharedPreferencesApp();
        sharedPreferences.edit().remove("access_token").apply();
//        checkNetworkAndCreate();

        Log.d("TAG", "onCreate: "+ sharedPreferences.getString("access_token",null));
        if (sharedPreferences.getString("access_token",null) != null)
        startActivity(new Intent(this, MainActivity.class));
        else
        startActivity(new Intent(this, LoginActivity.class));
        finish();
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