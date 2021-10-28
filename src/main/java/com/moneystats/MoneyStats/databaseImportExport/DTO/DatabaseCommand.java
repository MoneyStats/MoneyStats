package com.moneystats.MoneyStats.databaseImportExport.DTO;

public enum DatabaseCommand {
  EXPORT_DUMP_COMMAND(System.getProperty("moneystats.export.database")),
  IMPORT_DUMP_COMMAND(System.getProperty("moneystats.import.database"));

  public final String value;

  DatabaseCommand(String value) {
    this.value = value;
  }
}


