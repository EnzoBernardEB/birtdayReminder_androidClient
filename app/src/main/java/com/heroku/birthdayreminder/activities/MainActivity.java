package com.heroku.birthdayreminder.activities;

import static com.heroku.birthdayreminder.utils.Util.USER_APP;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
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
import java.util.UUID;

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
    private ArrayList<ListItem> listItems = new ArrayList<>();
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
        }
        this.user = Util.getUser(sharedPreferences,this.gson);
        this.birthdates = Util.getBirthdates(sharedPreferences,this.gson);
        if(user != null) {
             this.listItems = Util.createListItems(this.birthdates,context);
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

        ItemTouchHelper.SimpleCallback touchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            private final ColorDrawable background = new ColorDrawable(getResources().getColor(R.color.colorPrimaryBackground));

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                if(viewHolder instanceof BirthdayAdapter.MonthViewHolder)
                    return 0;
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT && viewHolder instanceof BirthdayAdapter.BirthDayViewHolder) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Are you sure to delete?");
                    builder.setPositiveButton("REMOVE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            UUID id = ((BirthdayAdapter.BirthDayViewHolder)viewHolder).birthdateId;
                            birthdatesHttpService.deleteBirthdate(id).enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    Toast.makeText(context,"The birhtdate has been deleted", Toast.LENGTH_SHORT).show();
                                    refreshBirthdatesList();
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Toast.makeText(context,"An error has occured. Try again", Toast.LENGTH_SHORT).show();
                                }
                            });
                            return;
                        }
                    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {  //not removing items if cancel is done
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("TAG", "onClick: cancel");
                            return;
                        }
                    }).show();
                } else if (direction == ItemTouchHelper.RIGHT && viewHolder instanceof BirthdayAdapter.BirthDayViewHolder){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Do you want to edit this birthdate ?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Birthdate birthdateToEdit = ((BirthdayAdapter.BirthDayViewHolder) viewHolder).birthdate;
                            Intent intent = new Intent(context,BirthdateActionActivity.class);
                            intent.putExtra("birthdateToEdit", birthdateToEdit);
                            startActivity(intent);
                            finish();
                        }
                    }).setNegativeButton("Nope", new DialogInterface.OnClickListener() {  //not removing items if cancel is done
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    }).show();


                }
                birthdayAdapter.notifyItemChanged(position);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                View itemView = viewHolder.itemView;

                if (dX > 0) {
                    background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + ((int) dX), itemView.getBottom());
                } else if (dX < 0) {
                    background.setBounds(itemView.getRight() + ((int) dX), itemView.getTop(), itemView.getRight(), itemView.getBottom());
                } else {
                    background.setBounds(0, 0, 0, 0);
                }

                background.draw(c);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchHelperCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshBirthdatesList();
    }

    private void refreshBirthdatesList() {
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