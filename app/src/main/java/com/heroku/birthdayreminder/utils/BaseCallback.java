package com.heroku.birthdayreminder.utils;

import android.content.Context;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class BaseCallback<T> implements Callback<T> {
    private final Context context;

    public BaseCallback(Context context) {
        this.context = context;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            // launch login activity using `this.context`
        } else {
            onSuccess(response.body());
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {

    }

    abstract void onSuccess(T response);
}
