package com.heroku.birthdayreminder.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.heroku.birthdayreminder.R;
import com.heroku.birthdayreminder.adapter.BirthdayAdapter;
import com.heroku.birthdayreminder.adapter.ListItem;
import com.heroku.birthdayreminder.container.BirthdayReminderApplication;
import com.heroku.birthdayreminder.databinding.ActivityLoginBinding;
import com.heroku.birthdayreminder.databinding.ActivityMainBinding;
import com.heroku.birthdayreminder.models.User;
import com.heroku.birthdayreminder.services.BirthdatesHttpService;
import com.heroku.birthdayreminder.utils.Util;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding activityMainBinding;
    private BirthdatesHttpService birthdatesHttpService;
    private SharedPreferences sharedPreferences;
    private Context context;

    private BirthdayAdapter birthdayAdapter;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = this.activityMainBinding.getRoot();
        setContentView(view);

        this.context = this;
        this.birthdatesHttpService = ((BirthdayReminderApplication) getApplication()).getBirthdatesHttpService();
        this.sharedPreferences = ((BirthdayReminderApplication) getApplication()).getSharedPreferencesApp();
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            Boolean isAuthenticate = bundle.getBoolean("isAuthenticate");
            Log.d("TAG", "Silent auth: "+isAuthenticate);
        }

        ArrayList<ListItem> listItems = Util.createListItems(user.birthdays);


    }
}