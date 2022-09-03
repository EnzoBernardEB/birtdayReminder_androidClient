package com.heroku.birthdayreminder.activities;

import static com.google.gson.internal.bind.TypeAdapters.UUID;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.heroku.birthdayreminder.DTO.Birthdates.BirthdateDTO;
import com.heroku.birthdayreminder.R;
import com.heroku.birthdayreminder.container.BirthdayReminderApplication;
import com.heroku.birthdayreminder.databinding.ActivityBirthdateActionBinding;
import com.heroku.birthdayreminder.services.BirthdatesHttpService;
import com.heroku.birthdayreminder.utils.Util;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BirthdateActionActivity extends AppCompatActivity {

    private ActivityBirthdateActionBinding activityBirthdateActionBinding;
    private SharedPreferences sharedPreferences;
    private BirthdatesHttpService birthdatesHttpService;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        this.birthdatesHttpService = ((BirthdayReminderApplication) getApplication()).getBirthdatesHttpService();
        this.sharedPreferences = ((BirthdayReminderApplication) getApplication()).getSharedPreferencesApp();
        activityBirthdateActionBinding = ActivityBirthdateActionBinding.inflate(getLayoutInflater());
        setContentView(activityBirthdateActionBinding.getRoot());

        this.configDatePicker();
        activityBirthdateActionBinding.buttonBirthdateAction.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                BirthdateDTO birthdateDTOToAdd = createBirthdateDTOToAdd();
                birthdatesHttpService.addBirthdate(birthdateDTOToAdd).enqueue(callBackAddBirthdate());
            }
        });
    }
    private Callback<BirthdateDTO> callBackAddBirthdate() {
        return new Callback<BirthdateDTO>() {
            @Override
            public void onResponse(Call<BirthdateDTO> call, Response<BirthdateDTO> response) {
                if (response.code() == 400) {
                    Toast.makeText(context, "An error has occured. Try again", Toast.LENGTH_SHORT).show();
                } else {
                    BirthdateDTO birthdateDTOAdded = response.body();
                    Toast.makeText(context, "The birthdate has been added.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(context, MainActivity.class));
                }

            }

            @Override
            public void onFailure(Call<BirthdateDTO> call, Throwable t) {
                Log.d("TAG", "onFailure: " + t.getMessage());
            }
        };
    }

    private BirthdateDTO createBirthdateDTOToAdd() {
        LocalDate birthdateSelected = getBirthdateSelected();
        UUID userId = Util.getUserUUID(sharedPreferences);
        String firstname = activityBirthdateActionBinding.editTextTextFirstName.getText().toString();
        String lastname = activityBirthdateActionBinding.editTextTextLastName.getText().toString();
        BirthdateDTO birthdateDTO = new BirthdateDTO(birthdateSelected,firstname,lastname,userId);

        return birthdateDTO;
    }

    private LocalDate getBirthdateSelected() {
        int daySelected = activityBirthdateActionBinding.datePicker.getDayOfMonth();
        int monthSelected = activityBirthdateActionBinding.datePicker.getMonth()+1;
        int yearSelected = activityBirthdateActionBinding.datePicker.getYear();


        LocalDate birthdateToAdd = LocalDate.of(yearSelected,monthSelected,daySelected);

        return birthdateToAdd;
    }

    private void configDatePicker() {
        this.setMaxDateToActualDate();
    }

    private void setMaxDateToActualDate() {
        activityBirthdateActionBinding.datePicker.setMaxDate(Instant.now().toEpochMilli());
    }

}