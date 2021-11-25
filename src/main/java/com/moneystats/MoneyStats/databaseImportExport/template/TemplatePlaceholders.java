package com.moneystats.MoneyStats.databaseImportExport.template;

public class TemplatePlaceholders {

    // TODO: Remove if dont need it
    public static final String GET_EXPORT_DATABASE_TEMPLATE = "src/main/resources/templates/backup_database_template.sql";

    // Template Identifier
    public static final String EXPORT_TEMPLATE = "EXPORT_TEMPLATE";

    public static final String FILEPATH_RESET_COUNTER = "sql-script/ResetCounterScript.sql";
    public static final String FILEPATH_BACKUP = "backup/database/";
    public static final String FIX_TEXT = "\n";

    public static final String STATEMENT_PLACEHOLDER = "statement.placeholder";
    public static final String WALLET_PLACEHOLDER = "wallet.placeholder";
    public static final String USERS_PLACEHOLDER = "users.placeholder";
    public static final String DATABASE_PLACEHOLDER = "database.placeholder";
    public static final String DATE_PLACEHOLDER = "date.placeholder";

}
