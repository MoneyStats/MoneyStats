package com.moneystats.MoneyStats.databaseImportExport.template;

import com.moneystats.MoneyStats.commStats.statement.StatementException;

public class TemplateException extends Exception{
    private Code code;

    /**
     * Constructs a new exception with {@code null} as its detail message. The cause is not
     * initialized, and may subsequently be initialized by a call to {@link #initCause}.
     */
    public TemplateException(Code code) {
        this.code = code;
    }

    public Code getCode() {
        return code;
    }

    public static enum Code {
        TEMPLATE_NOT_FOUND,
        IMPOSSIBLE_TO_WRITE_THE_TEMPLATE
    }
}
