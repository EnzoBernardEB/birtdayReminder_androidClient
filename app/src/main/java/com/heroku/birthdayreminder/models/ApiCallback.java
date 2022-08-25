package com.heroku.birthdayreminder.models;

public interface ApiCallback {

    void fail(String json);
    void success(String json);
}