package com.heroku.birthdayreminder.activities;

import static com.heroku.birthdayreminder.utils.Util.USER_APP;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.heroku.birthdayreminder.DTO.Birthdates.BirthdateDTO;
import com.heroku.birthdayreminder.R;
import com.heroku.birthdayreminder.adapter.BirthdayAdapter;
import com.heroku.birthdayreminder.adapter.ListItem;
import com.heroku.birthdayreminder.container.BirthdayReminderApplication;
import com.heroku.birthdayreminder.utils.Util;
import com.heroku.birthdayreminder.databinding.ActivityMainBinding;
import com.heroku.birthdayreminder.models.Birthdate;
import com.heroku.birthdayreminder.models.User;
import com.heroku.birthdayreminder.services.BirthdatesHttpService;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding activityMainBinding;
    private BirthdatesHttpService birthdatesHttpService;
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private Context context;

    private BirthdayAdapter birthdayAdapter;
    private User user;
    private ArrayList<Birthdate> birthdates = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = this.activityMainBinding.getRoot();
        setContentView(view);

        this.context = this;
        this.birthdatesHttpService = ((BirthdayReminderApplication) getApplication()).getBirthdatesHttpService();
        this.sharedPreferences = ((BirthdayReminderApplication) getApplication()).getSharedPreferencesApp();
        this.gson = ((BirthdayReminderApplication) getApplication()).getGsonWithLocalDateSerializer();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            birthdatesHttpService.getUserBirthdates(Util.getUserUUID(sharedPreferences)).enqueue(new Callback<List<BirthdateDTO>>() {
                @Override
                public void onResponse(Call<List<BirthdateDTO>> call, Response<List<BirthdateDTO>> response) {
                    if(response.code() == 401){
                        startActivity(new Intent(context, LoginActivity.class));
                        finish();
                        return;
                    }

                    Gson gson = new Gson();
                    Util.setBirthdates(sharedPreferences,gson.toJson(response.body()));
                }

                @Override
                public void onFailure(Call<List<BirthdateDTO>> call, Throwable t) {
                    Log.d("TAG", "onFailure: "+t.getMessage());
                }
            });
        } else {

        }
        this.user = Util.getUser(sharedPreferences,this.gson);
        this.birthdates = this.user.birthdays;

        ArrayList<ListItem> listItems = new ArrayList<>();
        if(user != null) {
             listItems = Util.createListItems(user.birthdays,context);
        }

        final RecyclerView recyclerView = activityMainBinding.coordinatorRoot.findViewById(R.id.recycler_view_home);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        birthdayAdapter = new BirthdayAdapter(context, listItems);
        recyclerView.setAdapter(birthdayAdapter);
        activityMainBinding.fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(context, BirthdateActionActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        birthdatesHttpService.getUserBirthdates(Util.getUserUUID(sharedPreferences)).enqueue(new Callback<List<BirthdateDTO>>() {
            @Override
            public void onResponse(Call<List<BirthdateDTO>> call, Response<List<BirthdateDTO>> response) {
                if(response.code() == 401){
                    startActivity(new Intent(context, LoginActivity.class));
                    finish();
                    return;
                }
                Gson gson = ((BirthdayReminderApplication)getApplication()).getGsonWithLocalDateSerializer();
                String jsonBirthdates = gson.toJson(response.body());
                Util.setBirthdates(sharedPreferences,jsonBirthdates);
                ArrayList<Birthdate> birthdates = gson.fromJson(jsonBirthdates, new TypeToken<List<Birthdate>>(){}.getType());
                ArrayList<ListItem> listItems = Util.createListItems(birthdates,context);
                birthdayAdapter.setListItems(listItems);
                birthdayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<BirthdateDTO>> call, Throwable t) {

            }
        });
    }
}