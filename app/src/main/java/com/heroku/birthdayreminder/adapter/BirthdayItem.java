package com.heroku.birthdayreminder.adapter;

import com.heroku.birthdayreminder.models.Birthdate;

public class BirthdayItem extends ListItem {

    public Birthdate birthday;

    public BirthdayItem(Birthdate birthday) {
        this.birthday = birthday;
    }

    @Override
    public int getType() {
        return TYPE_BIRTHDAY;
    }
}
