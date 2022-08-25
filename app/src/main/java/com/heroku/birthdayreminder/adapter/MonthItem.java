package com.heroku.birthdayreminder.adapter;

public class MonthItem extends ListItem{

    public int number;
    public String month;

    public MonthItem(int number, String month) {
        this.number = number;
        this.month = month;
    }

    @Override
    public int getType() {
        return TYPE_MONTH;
    }
}
