package SATools;

import java.sql.Connection;
import java.sql.SQLException;

public class ErrorRowCreator {

    public Connection connection;
    public String tool;

    public ErrorRowCreator(String tool, Connection connection) {
        this.tool = tool;
        this.connection = connection;
        createTable();
    }

    /**
     * Helper method to execute a query
     * @param query the query to execute.
     */
    public void executeQuery(String query) {
        try {
            connection.createStatement().execute(query);
        } catch (SQLException e) {
            System.err.println("Error executing query: " + e.getMessage());
        }
    }

    /**
     * Method to create the correct error row table based on the tool
     */
    private void createTable() {

        switch (this.tool) {
            case "LINT":
                createLintErrorRowTable();
                break;
            case "FLUFF":
                createFluffErrorRowTable();
                break;
            case "CHECK":
                createCheckErrorRowTable();
                break;
            default:
                System.out.println("Invalid tool: " + tool);
        }
    }

    /**
     * Crete an error row in the correct table based on the tool
     * @param projectName the project name
     * @param error the error
     * @param count the count of the error
     */
    public void createErrorRow(String commitHash, String fileName, String projectName, String error, int count) {

        switch (this.tool) {
            case "LINT":
                createLintErrorRowEntry(commitHash, fileName, projectName, error, count);
                break;
            case "FLUFF":
                createFluffErrorRowEntry(commitHash, fileName, projectName, error, count);
                break;
            case "CHECK":
                createCheckErrorRowEntry(commitHash, fileName, projectName, error, count);
                break;
            default:
                System.out.println("Invalid tool: " + tool);
        }
    }

    /**
     * Create an entry in the lint error row table
     * @param projectName the project name
     * @param error the error
     * @param count the count of the error
     */
    private void createLintErrorRowEntry(String commitHash, String fileName, String projectName, String error, int count) {
        String insertLintQuery = "INSERT INTO lint_error_rows (commit_hash, file_name, project_name, error, count) VALUES ('" + commitHash + "', '" + fileName + "', '" + projectName + "', '" + error + "', " + count + ");";

        executeQuery(insertLintQuery);
    }

    /**
     * Create an entry in the fluff error row table
     * @param projectName the project name
     * @param error the error
     * @param count the count of the error
     */
    private void createFluffErrorRowEntry(String commitHash, String fileName, String projectName, String error, int count) {
        String insertFluffQuery = "INSERT INTO fluff_error_rows (commit_hash, file_name, project_name, error, count) VALUES ('" + commitHash + "', '" + fileName + "', '" + projectName + "', '" + error + "', " + count + ");";
        executeQuery(insertFluffQuery);
    }

    /**
     * Create an entry in the check error row table
     * @param projectName the project name
     * @param error the error
     * @param count the count of the error
     */
    private void createCheckErrorRowEntry(String commitHash, String fileName, String projectName, String error, int count) {
        String insertCheckQuery = "INSERT INTO check_error_rows (commit_hash, file_name, project_name, error, count) VALUES ('" + commitHash + "', '" + fileName + "', '" + projectName + "', '" + error + "', " + count + ");";
        executeQuery(insertCheckQuery);
    }

    /**
     * Create the lint error row table
     */
    private void createLintErrorRowTable() {
        executeQuery("DROP TABLE IF EXISTS lint_error_rows;");
        String createLintQuery = "CREATE TABLE IF NOT EXISTS lint_error_rows (commit_hash VARCHAR(255), file_name VARCHAR(255), project_name VARCHAR(255), error VARCHAR(255), count INT);";
        executeQuery(createLintQuery);
    }

    /**
     * Create the fluff error row table
     */
    private void createFluffErrorRowTable() {
        executeQuery("DROP TABLE IF EXISTS fluff_error_rows;");
        String createFluffQuery = "CREATE TABLE fluff_error_rows (commit_hash VARCHAR(255), file_name VARCHAR(255), project_name VARCHAR(255), error VARCHAR(255), count INT);";
        executeQuery(createFluffQuery);
    }

    /**
     * Create the check error row table
     */
    private void createCheckErrorRowTable() {
        executeQuery("DROP TABLE IF EXISTS check_error_rows;");
        String createCheckQuery = "CREATE TABLE check_error_rows (commit_hash VARCHAR(255), file_name VARCHAR(255), project_name VARCHAR(255), error VARCHAR(255), count INT);";
        executeQuery(createCheckQuery);
    }

}
