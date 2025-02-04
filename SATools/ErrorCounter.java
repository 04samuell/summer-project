package SATools;

import java.sql.*;
import java.util.*;

public class ErrorCounter {

    static String[] table_names = { "SQL_FLUFF", "SQL_LINT", "SQL_CHECK" };

    static final String FLUFF_JOIN  = "SELECT project_name, sql_files.commit_hash, sql_files.file_name, fluff_summary FROM sql_files INNER JOIN sql_fluff ON sql_files.commit_hash = sql_fluff.commit_hash AND sql_files.file_name = sql_fluff.file_name";
    static final String LINT_JOIN = "SELECT project_name, sql_files.commit_hash, sql_files.file_name, lint_summary FROM sql_files INNER JOIN sql_lint ON sql_files.commit_hash = sql_lint.commit_hash AND sql_files.file_name = sql_lint.file_name";
    static final String CHECK_JOIN = "SELECT project_name, sql_files.commit_hash, sql_files.file_name, check_summary FROM sql_files INNER JOIN sql_check ON sql_files.commit_hash = sql_check.commit_hash AND sql_files.file_name = sql_check.file_name";

    public static void main(String[] args) {

        Connection connection = Database.ConnectToH2.getDatabaseConnection(); // Establish connection to H2 database

    }

    private static ResultSet getErrorCodes(Connection connection) {
        try {
            return connection.createStatement().executeQuery(FLUFF_JOIN);
        } catch (SQLException e) {
            System.err.println("Error getting error codes: " + e.getMessage());
        }
        return null;
    }

}
