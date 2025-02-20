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

    public void executeQuery(String query) {
        try {
            connection.createStatement().execute(query);
        } catch (SQLException e) {
            System.err.println("Error executing query: " + e.getMessage());
        }
    }

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

    private void createLintErrorRowEntry(String projectName, String error, int count) {
        String insertLintQuery = "INSERT INTO lint_error_rows (project_name, error, count) VALUES ('" + projectName + "', '" + error + "', " + count + ");";
        executeQuery(insertLintQuery);
    }

    private void createFluffErrorRowEntry(String projectName, String error, int count) {
        String insertFluffQuery = "INSERT INTO fluff_error_rows (project_name, error, count) VALUES ('" + projectName + "', '" + error + "', " + count + ");";
        executeQuery(insertFluffQuery);
    }

    private void createCheckErrorRowEntry(String projectName, String error, int count) {
        String insertCheckQuery = "INSERT INTO check_error_rows (project_name, error, count) VALUES ('" + projectName + "', '" + error + "', " + count + ");";
        executeQuery(insertCheckQuery);
    }

    private void createLintErrorRowTable() {
        String createLintQuery = "CREATE TABLE lint_error_rows (project_name VARCHAR(255), error VARCHAR(255), count INT);";
        executeQuery(createLintQuery);
    }

    private void createFluffErrorRowTable() {
        String createFluffQuery = "CREATE TABLE fluff_error_rows (project_name VARCHAR(255), error VARCHAR(255), count INT);";
        executeQuery(createFluffQuery);
    }

    private void createCheckErrorRowTable() {
        String createCheckQuery = "CREATE TABLE check_error_rows (project_name VARCHAR(255), error VARCHAR(255), count INT);";
        executeQuery(createCheckQuery);
    }

}
