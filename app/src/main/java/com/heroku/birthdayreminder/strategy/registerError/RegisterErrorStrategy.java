package com.heroku.birthdayreminder.strategy.registerError;

import android.content.Context;
import android.widget.EditText;

import com.heroku.birthdayreminder.databinding.ActivityRegisterBinding;

public interface RegisterErrorStrategy {
    public boolean isError(String message);

    public EditText setErrorFocus(Context context, ActivityRegisterBinding activityRegisterBinding);
}
