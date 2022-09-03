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
import com.heroku.birthdayreminder.models.Birthdate;
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

        if(getIntent().getExtras() != null) {
            Birthdate birthdate = (Birthdate) getIntent().getSerializableExtra("birthdateToEdit");
            activityBirthdateActionBinding.titleBirthdateAction.setText(R.string.edit_birthdate);
            activityBirthdateActionBinding.editTextTextFirstName.setText(birthdate.firstname);
            activityBirthdateActionBinding.editTextTextLastName.setText(birthdate.lastname);
            activityBirthdateActionBinding.buttonBirthdateAction.setText(R.string.edit_button);
            activityBirthdateActionBinding.datePicker.updateDate(birthdate.date.getYear(),birthdate.date.getDayOfMonth(),birthdate.date.getDayOfMonth());
            activityBirthdateActionBinding.buttonBirthdateAction.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    BirthdateDTO birthdateDTOToEdit = createBirthdateDTOToSave();
                    birthdateDTOToEdit.setId(birthdate.id.toString());
                    if(checkIfInvalidAndDisplayError(birthdateDTOToEdit))
                        return;

                    birthdatesHttpService.editBirthdate(birthdateDTOToEdit).enqueue(callBackAddBirthdate());
                }
            });
        } else {
            this.configDatePicker();
            activityBirthdateActionBinding.buttonBirthdateAction.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    BirthdateDTO birthdateDTOToAdd = createBirthdateDTOToSave();
                    if(checkIfInvalidAndDisplayError(birthdateDTOToAdd))
                        return;
                    birthdatesHttpService.addBirthdate(birthdateDTOToAdd).enqueue(callBackAddBirthdate());
                }
            });
        }


    }

    private Boolean checkIfInvalidAndDisplayError(BirthdateDTO birthdateDTOToEdit) {
        if(birthdateDTOToEdit.getFirstname().length() == 0) {
           activityBirthdateActionBinding.editTextTextFirstName.setError(getString(R.string.error_field_required));
           activityBirthdateActionBinding.editTextTextFirstName.requestFocus();
           return true;
        }
        return false;
    }

    private Callback<BirthdateDTO> callBackAddBirthdate() {
        return new Callback<BirthdateDTO>() {
            @Override
            public void onResponse(Call<BirthdateDTO> call, Response<BirthdateDTO> response) {
                if (response.code() == 400 || response.code() ==500) {
                    Toast.makeText(context, "An error has occured. Try again", Toast.LENGTH_SHORT).show();
                } else {
                    BirthdateDTO birthdateDTOAdded = response.body();
                    Toast.makeText(context, "The birthdate has been saved.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(context, MainActivity.class));
                }

            }

            @Override
            public void onFailure(Call<BirthdateDTO> call, Throwable t) {
                Log.d("TAG", "onFailure: " + t.getMessage());
            }
        };
    }

    private BirthdateDTO createBirthdateDTOToSave() {
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