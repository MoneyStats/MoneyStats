package com.moneystats.generic;

public class SchemaDescription {

    // Authentication
    public static final String POST_LOGIN_SUMMARY = "Process to get logged into the MoneyStats Platform";
    public static final String POST_LOGIN_DESCRIPTION = "It use a AuthCredentialInput with username and password to get logged";
    public static final String POST_UPDATE_PASSWORD_SUMMARY = "Process to update the password";
    public static final String POST_UPDATE_PASSWORD_DESCRIPTION = "It use to update the password of the current user";
    public static final String POST_SIGN_UP_SUMMARY = "Sign Up Process";
    public static final String POST_SIGN_UP_DESCRIPTION = "Method that let me sign up a new user correctly";
    public static final String GET_USER_WITH_TOKEN_SUMMARY = "Get user via token";
    public static final String GET_USER_WITH_TOKEN_DESCRIPTION = "Method that le you get an user via token";
    public static final String GET_ADMIN_USER_SUMMARY = "get list of user connnected";
    public static final String GET_ADMIN_USER_DESCRIPTION = "Method that gets all the user register on the database";
    public static final String PUT_UPDATE_USER_SUMMARY = "update user process";
    public static final String PUT_UPDATE_USER_DESCRIPTION = "Method that allow you to update the current user";
    public static final String GET_CURRENT_USER_SUMMARY = "Return current user";
    public static final String GET_CURRENT_USER_DESCRIPTION = "Method that allow you to get the current user";

    // Category
    public static final String GET_CATEGORY_SUMMARY = "Get all Category";
    public static final String GET_CATEGORY_DESCRIPTION = "Method that return a list of category";
}
