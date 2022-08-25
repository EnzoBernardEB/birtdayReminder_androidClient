package com.heroku.birthdayreminder.models;

public enum RegisterErrorsMessage {
    USERNAME("Error: Username is already taken!"),
    EMAIL("Error: Email is already in use!");

    private String message;

    RegisterErrorsMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return this.message;
    }
}
