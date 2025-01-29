package Database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.util.*;

/**
 * Class to connect to H2 database and formulate .sql files from entries.
 * 
 * Run using:
 * javac -cp lib/h2-2.3.232.jar Database/ConnectToH2.java
 * java -cp lib/h2-2.3.232.jar Database/ConnectToH2.java
 */
public class ConnectToH2 {

    static final String JDBC_URL = "jdbc:h2:C:/Users/04sam/sql_project";
    static final String USERNAME = "sa";
    static final String PASSWORD = "";

    static List<String> sqlEntries = new ArrayList<>();
    static List<String> projectNames = new ArrayList<>();
    static List<String> commitHashes = new ArrayList<>();
    static List<String> fileNames = new ArrayList<>();

    public static void main(String[] args) throws SQLException {
        Connection connection = getDatabaseConnection(); // Establish connection to H2 database

        getSQLEntries(connection); // Get SQL entries from database
        System.out.println("Number of SQL entries: " + sqlEntries.size());

        try {
            makeSQLFiles(); // Create .sql files from entries
        } catch (IOException e) {
            System.err.println("Error making SQL files: " + e.getMessage());
        }
    }

    /**
     * Establish connection to H2 database.
     * @return Connection object to H2 database
     */
    private static Connection getDatabaseConnection() {
        Connection dbConnection;
        try {
            dbConnection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            System.out.println("Connected to H2 database!");
            return dbConnection;
        } catch (SQLException e) {
            System.err.println("Error connecting to H2 database: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get SQL entries from database by querying the 'sql_files' table and updating the lists.
     * @param connection The Database Connection object
     */
    private static void getSQLEntries(Connection connection) {
        try {
            ResultSet result = connection.createStatement().executeQuery("SELECT project_name, commit_hash, file_name, sql FROM sql_files;");
            while (result.next()) {
                projectNames.add(result.getString("project_name"));
                commitHashes.add(result.getString("commit_hash"));
                fileNames.add(result.getString("file_name"));
                sqlEntries.add(result.getString("sql"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting SQL entries: " + e.getMessage());
        }
    }

    /**
     * Store each of the SQL entries in a separate .sql file
     * @throws IOException
     */
    private static void makeSQLFiles() throws IOException {
        String prevProjectName = "";
        int count = 1;

        for (int i = 0; i < sqlEntries.size(); i++) {
            String projectName = projectNames.get(i);

            if (!projectName.equals(prevProjectName)) {
                count = 1;
            } else {
                count++;
            }

            String fileName = "Datasets/SQLFiles/" + projectName + "-" + count + ".sql";
            String header = "--" + commitHashes.get(i) + "   " + fileNames.get(i) + "\n";

            Files.write(Paths.get(fileName), header.getBytes(), StandardOpenOption.CREATE); // Write the header
            Files.write(Paths.get(fileName), sqlEntries.get(i).getBytes(), StandardOpenOption.APPEND); // Write the SQL entry 
            prevProjectName = projectName;
        }
    }
}
