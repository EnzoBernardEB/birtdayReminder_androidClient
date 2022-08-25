package com.heroku.birthdayreminder.strategy.registerError;

import android.content.Context;
import android.widget.EditText;

import com.heroku.birthdayreminder.R;
import com.heroku.birthdayreminder.databinding.ActivityRegisterBinding;
import com.heroku.birthdayreminder.models.RegisterErrorsMessage;


public class EmailAlreadyUsed implements RegisterErrorStrategy{
    ActivityRegisterBinding activityRegisterBinding;

    @Override
    public boolean isError(String message) {
        if(message.equalsIgnoreCase(RegisterErrorsMessage.EMAIL.getMessage()))
            return true;
        return false;
    }

    @Override
    public EditText setErrorFocus(Context context, ActivityRegisterBinding activityRegisterBinding) {
        activityRegisterBinding.email.setError(context.getString(R.string.email_already_used));

        return activityRegisterBinding.email;
    }
}
