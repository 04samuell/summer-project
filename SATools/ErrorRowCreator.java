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
    public void createErrorRow(String projectName, String error, int count) {

        switch (this.tool) {
            case "LINT":
                createLintErrorRowEntry(projectName, error, count);
                break;
            case "FLUFF":
                createFluffErrorRowEntry(projectName, error, count);
                break;
            case "CHECK":
                createCheckErrorRowEntry(projectName, error, count);
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
    private void createLintErrorRowEntry(String projectName, String error, int count) {
        String insertLintQuery = "INSERT INTO lint_error_rows (project_name, error, count) VALUES ('" + projectName + "', '" + error + "', " + count + ");";
        executeQuery(insertLintQuery);
    }

    /**
     * Create an entry in the fluff error row table
     * @param projectName the project name
     * @param error the error
     * @param count the count of the error
     */
    private void createFluffErrorRowEntry(String projectName, String error, int count) {
        String insertFluffQuery = "INSERT INTO fluff_error_rows (project_name, error, count) VALUES ('" + projectName + "', '" + error + "', " + count + ");";
        executeQuery(insertFluffQuery);
    }

    /**
     * Create an entry in the check error row table
     * @param projectName the project name
     * @param error the error
     * @param count the count of the error
     */
    private void createCheckErrorRowEntry(String projectName, String error, int count) {
        String insertCheckQuery = "INSERT INTO check_error_rows (project_name, error, count) VALUES ('" + projectName + "', '" + error + "', " + count + ");";
        executeQuery(insertCheckQuery);
    }

    /**
     * Create the lint error row table
     */
    private void createLintErrorRowTable() {
        String createLintQuery = "CREATE TABLE lint_error_rows (project_name VARCHAR(255), error VARCHAR(255), count INT);";
        executeQuery(createLintQuery);
    }

    /**
     * Create the fluff error row table
     */
    private void createFluffErrorRowTable() {
        String createFluffQuery = "CREATE TABLE fluff_error_rows (project_name VARCHAR(255), error VARCHAR(255), count INT);";
        executeQuery(createFluffQuery);
    }

    /**
     * Create the check error row table
     */
    private void createCheckErrorRowTable() {
        String createCheckQuery = "CREATE TABLE check_error_rows (project_name VARCHAR(255), error VARCHAR(255), count INT);";
        executeQuery(createCheckQuery);
    }

}
