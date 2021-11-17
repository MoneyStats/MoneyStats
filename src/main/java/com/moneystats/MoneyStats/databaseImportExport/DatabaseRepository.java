package com.moneystats.MoneyStats.databaseImportExport;

import com.moneystats.generic.timeTracker.LogTimeTracker;
import com.moneystats.generic.timeTracker.LoggerMethod;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.ZonedDateTime;

@Repository
public class DatabaseRepository {

    @Value("${spring.datasource.url}")
    private String dbAddress;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseService.class);

    @LoggerMethod(type = LogTimeTracker.ActionType.APP_REPOSITORY)
    public void executingResetCounterScript(String filePath) throws DatabaseException {
        Connection con = null;
        try {
            LOG.info("Getting Driver Connection");
            con = DriverManager.getConnection(dbAddress, username, password);
            LOG.info("Database Connected {}", dbAddress);
        } catch (SQLException e) {
            LOG.info("Error during Database Connection");
            throw new DatabaseException(DatabaseException.Code.CONNECTION_FAILED);
        }
        ScriptRunner sr = new ScriptRunner(con);
        // Creating a reader object
        Reader reader = null;
        try {
            LOG.info("Executing Script");
            reader = new BufferedReader(new FileReader(filePath));
            LOG.info("Script Executed at {}", ZonedDateTime.now());
        } catch (FileNotFoundException e) {
            LOG.error("File Not found");
            throw new DatabaseException(DatabaseException.Code.FILE_NOT_FOUND);
        }
        // Running the script
        sr.runScript(reader);
    }
}
