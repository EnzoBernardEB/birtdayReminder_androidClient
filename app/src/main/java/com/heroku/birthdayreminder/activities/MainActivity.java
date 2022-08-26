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
    private Context context;

    private BirthdayAdapter birthdayAdapter;
    private User user;
    private ArrayList<Birthdate> birthdates;

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
            birthdatesHttpService.getUserBirthdates(Util.getUserUUID(sharedPreferences)).enqueue(new Callback<List<BirthdateDTO>>() {
                @Override
                public void onResponse(Call<List<BirthdateDTO>> call, Response<List<BirthdateDTO>> response) {
                    if(response.code() == 401){
                        startActivity(new Intent(context, LoginActivity.class));
                        finish();
                        return;
                    }

                    Gson gson = new Gson();
                    Util.setBirthdates(sharedPreferences,gson.toJson(response));
                }

                @Override
                public void onFailure(Call<List<BirthdateDTO>> call, Throwable t) {
                    Log.d("TAG", "onFailure: "+t.getMessage());
                }
            });
        } else {

        }

        try {
            this.user = Util.getUser(sharedPreferences);
            this.birthdates = this.user.birthdays;
        } catch (Exception e) {
            Log.d("TAG", "onCreate: erroir"+ e.getMessage());
        }
        ArrayList<ListItem> listItems = Util.createListItems(user.birthdays,context);
        final RecyclerView recyclerView = activityMainBinding.coordinatorRoot.findViewById(R.id.recycler_view_home);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        birthdayAdapter = new BirthdayAdapter(context, listItems);
        recyclerView.setAdapter(birthdayAdapter);

        activityMainBinding.fab.setOnClickListener(v -> showDialogAddNewBirthday());

    }

    private void showDialogAddNewBirthday() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_add_birthdate, null);
        final EditText editTextFirstName = view.findViewById(R.id.edit_text_text_first_name);
        final EditText editTextLastName = view.findViewById(R.id.edit_text_text_last_name);
        final DatePicker editTextDate = view.findViewById(R.id.datePicker);



        builder.setTitle("Nouvel anniversaire ?");
        builder.setView(view);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // TODO : récupérer les valeurs et appeler la méthode addNewBirthday

            }
        });

        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();
    }
    private void addNewBirthday(String dateStr, String firstname, String lastname) {
        try {
            if (dateStr == null || dateStr.isEmpty()) {
                throw new Exception("Date incorrecte");
            }

            Date date = Util.initDateFromEditText(dateStr);

            if (firstname == null || firstname.isEmpty()) {
                throw new Exception("Prénom incorrecte");
            }

            if (lastname == null || lastname.isEmpty()) {
                throw new Exception("Nom incorrecte");
            }

            Birthdate birthday = new Birthdate(date, firstname, lastname);

            // TODO : Appeler la méthode qui ajoute cet anniversaire à la liste des anniversaires de cet utilisateur (comprendre ce que fait la méthode)

            birthdayAdapter.setListItems(Util.createListItems(user.birthdays,context));

            // Appel API POST /users/id/birthdays
            Map<String, String> map = new HashMap<>();
            map.put("firstname", birthday.firstname);
            map.put("lastname", birthday.lastname);
            map.put("date", Util.printDate(birthday.date));

            String[] id = {user.id.toString()};


        } catch (ParseException e) {
            Toast.makeText(MainActivity.this, "Date incorrecte", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}