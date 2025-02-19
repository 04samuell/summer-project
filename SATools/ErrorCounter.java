package SATools;

import java.sql.*;
import java.util.*;

public class ErrorCounter {

    static List<String> projectNames = new ArrayList<String>();
    static List<String> toolSummaries = new ArrayList<String>();
    static HashMap<String, Integer> errors = new HashMap<>();

    static int totalErrorCount = 0;

    static final String FLUFF_JOIN = "SELECT project_name, fluff_summary FROM sql_files INNER JOIN sql_fluff ON sql_files.commit_hash = sql_fluff.commit_hash AND sql_files.file_name = sql_fluff.file_name";
    static final String LINT_JOIN = "SELECT project_name, sql_files.commit_hash, sql_files.file_name, lint_summary FROM sql_files INNER JOIN sql_lint ON sql_files.commit_hash = sql_lint.commit_hash AND sql_files.file_name = sql_lint.file_name";
    static final String CHECK_JOIN = "SELECT project_name, sql_files.commit_hash, sql_files.file_name, check_summary FROM sql_files INNER JOIN sql_check ON sql_files.commit_hash = sql_check.commit_hash AND sql_files.file_name = sql_check.file_name";

    static final String FLUFF_SUMMARY = "fluff_summary";
    static final String LINT_SUMMARY = "lint_summary";
    static final String CHECK_SUMMARY = "check_summary";

    public static void main(String[] args) {

        errors.put("TOTAL", 0); // Initialise total error count

        Connection connection = Database.ConnectToH2.getDatabaseConnection(); // Establish connection to H2 database
        getErrorCodes(connection, CHECK_JOIN, CHECK_SUMMARY); // Get error codes from database
        summariseErrors(); // Summarise errors 

        printHashMap(errors, "Total Errors"); // Print total error statistics

        listTopErrors(); // List top 5 errors
    }

    /**
     * Summarise the errors for each project
     */
    private static void summariseErrors() {
        String prevProject = "";
        HashMap<String, Integer> projectErrors = new HashMap<>();
        for (int i = 0; i < projectNames.size(); i++) {
            String projectName = projectNames.get(i);
            String toolSummary = toolSummaries.get(i);

            if (!projectName.equals(prevProject) && !prevProject.isEmpty()) {
                printHashMap(projectErrors, prevProject);
                projectErrors = new HashMap<>();
                projectErrors.put("TOTAL", 0);
            }

            if (!toolSummary.contains("NULL") && !toolSummary.contains("PRS")) { // remove and clause for results with PRS
                processError(toolSummary, projectErrors);
            }

            prevProject = projectName;
        }

        printHashMap(projectErrors, prevProject); // Print the last project!
    }

    /**
     * Given a summary, update the project hashmap and the total error hashmap
     * 
     * @param summary       the summary of errors
     * @param projectErrors the hashmap of errors for a project
     */
    private static void processError(String summary, HashMap<String, Integer> projectErrors) {
        int fileTotal = 0;
        String[] errorsSplit = summary.split(",");
        errorsSplit = Arrays.copyOf(errorsSplit, errorsSplit.length - 1); // Remove last element which is always TOTAL
        for (String keyValuePair : errorsSplit) {
            String[] kV = keyValuePair.split(":");
            String error = removeQutationMarks(kV[0].strip());
            int count = Integer.parseInt(kV[1].strip());
            fileTotal += count;
            totalErrorCount += count;

            projectErrors.put(error, projectErrors.getOrDefault(error, 0) + count); // Update project error statistics
            errors.put(error, errors.getOrDefault(error, 0) + count); // Update total error statistics
        }

        // Update totals
        projectErrors.put("TOTAL", projectErrors.getOrDefault("TOTAL", 0) + fileTotal); 
        errors.put("TOTAL", errors.getOrDefault("TOTAL", 0) + fileTotal);

    }

    /**
     * Add results from the database to the projectNames and toolSummaries lists
     * 
     * @param connection the connection to the database
     */
    private static void getErrorCodes(Connection connection, String query, String column) {
        try {
            ResultSet result = connection.createStatement().executeQuery(query);
            while (result.next()) {
                projectNames.add(result.getString("project_name"));
                String toolSummary = result.getString(column);
                if (toolSummary.contains("NULL")) {
                    toolSummaries.add("NULL");
                } else {
                    toolSummaries.add(removeCurlyBraces(toolSummary));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting error codes: " + e.getMessage());
        }
    }

    /**
     * Remove the curly braces from the summary 
     * @param summary the summary to remove the curly braces from
     * @return the summary without the curly braces
     */
    private static String removeCurlyBraces(String summary) {
        return summary.substring(1, summary.length() - 1);
    }

    /**
     * Remove the quotation marks from the summary
     * @param summary the summary to remove the quotation marks from
     * @return the summary without the quotation marks
     */
    private static String removeQutationMarks(String summary) {
        return summary.substring(2, summary.length() - 2);
    }

    /**
     * Method to print the HashMap
     * 
     * @param map     the hashmap to print
     * @param mapName the name of the hashmap
     */
    private static void printHashMap(HashMap<String, Integer> map, String mapName) {
        if (map.isEmpty())
            return;

        System.out.println("\n" + "*".repeat(10) + " Hashmap: " + mapName + " " + "*".repeat(10) + "\n");
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    /**
     * Method to list the top 5 most frequent errors
     */
    private static void listTopErrors() {
        System.out.println("\n" + "*".repeat(10) + " Top Errors " + "*".repeat(10) + "\n");
        errors.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(8)
                .forEach(System.out::println);
    }

}
