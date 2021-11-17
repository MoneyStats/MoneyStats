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

    // Statement
    public static final String POST_ADD_STATEMENT_SUMMARY = "Add new Statement";
    public static final String POST_ADD_STATEMENT_DESCRIPTION = "Process that allow an Statement add";
    public static final String GET_LIST_OF_DATE_SUMMARY = "Get List of all Date";
    public static final String GET_LIST_OF_DATE_DESCRIPTION = "Process that allow to get all the date";
    public static final String GET_LIST_STATEMENT_BY_DATE_SUMMARY = "List Statement By date";
    public static final String GET_LIST_STATEMENT_BY_DATE_DESCRIPTION = "Method that return a list of all Statement into that params date";

    // Wallet
    public static final String GET_ALL_WALLET_SUMMARY = "Get all Wallet List";
    public static final String GET_ALL_WALLET_DESCRIPTION = "Method that return a list of Wallet by user";
    public static final String POST_ADD_WALLET_SUMMARY = "Add Wallet";
    public static final String POST_ADD_WALLET_DESCRIPTION = "Method that allow the user to add a new Wallet";
    public static final String DELETE_WALLET_SUMMARY = "Delete Wallet";
    public static final String DELETE_WALLET_DESCRIPTION = "Method that allow the User to Delete a Wallet";
    public static final String GET_WALLET_STATEMENT_SUMMARY = "Get Wallet and Statement";
    public static final String GET_WALLET_STATEMENT_DESCRIPTION = "Methods that allow the user to get Wallet and Statement";
    public static final String PUT_UPDATE_WALLET_SUMMARY = "Update Wallet";
    public static final String PUT_UPDATE_WALLET_DESCRIPTION = "Methods that allow the user to update the Wallet";
    public static final String GET_WALLET_BY_ID_SUMMARY = "Wallet By ID";
    public static final String GET_WALLET_BY_ID_DESCRIPTION = "Method that allow the user to get the wallet by ID";

    // Homapage
    public static final String GET_REPORT_HOMEPAGE_SUMMARY = "Render Homepage";
    public static final String GET_REPORT_HOMEPAGE_DESCRIPTION = "Method that allow the user to render the Homepage";
    public static final String GET_PIE_GRAPH_SUMMARY = "Get the Pie Graph";
    public static final String GET_PIE_GRAPH_DESCRIPTION = "Method that allow the user to get the pieGraph into the Homepage";

    // Web Controller
    public static final String GET_LOGINPAGE_SUMMARY = "loginpage.html";
    public static final String GET_LOGINPAGE_DESCRIPTION = "return the HTML page";
    public static final String GET_LOGOUT_SUMMARY = "logout";
    public static final String GET_LOGOUT_DESCRIPTION = "get logout from server";

    // Database
    public static final String POST_EXPORT_DATABASE_SUMMARY = "Export DB";
    public static final String POST_EXPORT_DATABASE_DESCRIPTION = "Method that let the ADMIN to export the full db";
}
