import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;

public class CommitFormatter {

    // Commit data
    private String commit;
    private String projectName;
    private String metadata;

    // Regex patterns
    private static final String FILE_SPLITTER = "diff --git\\s+";
    private static final String COMMIT_HASH_REGEX = "[a-f0-9]{40}";
    private static final String DATE_TIME_REGEX = "(?<=Date:\\s{3}).*";
    private static final String AUTHOR_REGEX = "(?<=Author:\\s).*?(?=\\s<)";
    private static final String FILE_NAME_REGEX = "b\\/([^\\s]+)";

    private int prevEndIndex = 0;

    public CommitFormatter(String commit, String projectName) {
        this.commit = commit;
        this.projectName = projectName;
    }

    /**
     * A method to turn a commit into row entries ready for the database.
     * 
     * Breaks the commit into individual files, files containing sql become row
     * entries.
     * 
     * @return List<String[]> of the commit in database ready format
     */
    public List<String[]> getRowEntries() {

        List<String[]> result = new ArrayList<String[]>();

        // Split the commit into individual files
        String[] commitFileList = this.commit.split(FILE_SPLITTER);
        this.metadata = commitFileList[0]; // first entry is commit metadata

        // Get commit metadata
        String commitHash = getCommitHash();
        String author = getAuthor();
        String dateTime = getDateTime();

        // For each file, turn into row entry if it contains sql
        for (int i = 1; i < commitFileList.length; i++) {
            String fileCodeChanges = commitFileList[i];
            String sql = getSQLString(fileCodeChanges);
            if (!sql.equals("")) { // Check if sql check came out empty, if not, make a row entry
                String fileName = getFileName(fileCodeChanges);
                if (!checkFileTypeValidity(fileName)) {
                    continue;
                }
                String[] entry = new String[8];
                entry[0] = projectName;
                entry[1] = commitHash;
                entry[2] = author;
                entry[3] = dateTime;
                entry[4] = fileName;
                entry[5] = sql;
                entry[6] = determineSQLCodeContext(fileCodeChanges) ? "1" : "0";
                entry[7] = determineSQLCodeChange(fileCodeChanges) ? "1" : "0";
                result.add(entry);
            }
        }

        return result;
    }

    /**
     * Given a String, extracts the SQL.
     * 
     * @param commitPatch the commit patch
     * @return the SQL statements in the String
     */
    private String getSQLString(String commitPatch) {

        StringBuilder sqlStatements = new StringBuilder();
        commitPatch = removePlusMinus(commitPatch);
        Pattern pattern = Pattern.compile(CommitLogParser.SQL_PATTERN);
        Matcher matcher = pattern.matcher(commitPatch);

        while (matcher.find()) {
            int startIndex = matcher.start();

            // Skip if string already processed
            if (startIndex < prevEndIndex) continue;

            if (commitPatch.charAt(startIndex - 2) == '/' || commitPatch.charAt(startIndex - 1) == '\'')
                continue; // if inside a comment block or single quote, skip

            boolean stringified = commitPatch.charAt(startIndex - 1) == '"';
            boolean embedded = commitPatch.charAt(startIndex - 2) == '(' || commitPatch.charAt(startIndex - 1) == '('; // SQL
                                                                                                                       // is
                                                                                                                       // embedded
                                                                                                                       // in
                                                                                                                       // string
            boolean assignment = commitPatch.charAt(startIndex - 3) == '=' || commit.charAt(startIndex - 2) == '='; // SQL
                                                                                                                    // is
                                                                                                                    // assignment
            boolean commented = commitPatch.charAt(startIndex - 2) == '-' || commitPatch.charAt(startIndex - 1) == '-'; // SQL
                                                                                                                        // is
                                                                                                                        // commented
            int endIndex;

            // Find end of statement based on preceding characters
            if (stringified) {
                endIndex = findEndOfStatementString(commitPatch, startIndex);
            } else if (embedded) {
                endIndex = findEndOfStatementEmbedded(commitPatch, startIndex);
            } else if (assignment) {
                endIndex = findEndOfStatementAssignment(commitPatch, startIndex);
            } else {
                endIndex = findEndOfStatement(commitPatch, startIndex);
            }

            if (commented) {
                sqlStatements.append("-- "); // if commented, then add comment prefix since pattern matcher will miss it
            }

            if (commitPatch.contains("SELECT * FROM *")) {
                continue;
            }

            // Append the SQL statement to the result
            if (endIndex != -1 && !commitPatch.contains("SELECT * FROM ") && !commitPatch.contains("delete from the")
                    && !commitPatch.contains("delete from what")) {
                prevEndIndex = endIndex;
                String sql = commitPatch.substring(startIndex, endIndex + 1);

                if (sql.contains("*/"))
                    continue; // if inside a comment block, skip
                if (sql.contains("{") && !sql.contains("}"))
                    continue; // pretty good indication that something went wrong

                sqlStatements.append(sql);
                if (commitPatch.charAt(endIndex) != ';') {
                    sqlStatements.append(";\n\n");
                } else {
                    sqlStatements.append("\n\n");
                }
            }

        }

        return sqlStatements.toString();
    }

    /**
     * Finds the end index of a statement that is embedded in a string.
     * @param patch the commit patch
     * @param startIndex the start index of the statement
     * @return the end index of the statement
     */
    private int findEndOfStatementString(String patch, int startIndex) {
        int endIndex = patch.indexOf('"', startIndex);
        return endIndex != -1 ? endIndex - 1 : -1;
    }

    /**
     * Finds the end index of a statement.
     * @param patch the commit patch
     * @param startIndex the start index of the statement
     * @return the end index of the statement
     */
    private int findEndOfStatement(String patch, int startIndex) {
        int endIndex = patch.indexOf(';', startIndex);
        return endIndex != -1 ? endIndex : -1;
    }


    /**
     * Finds the end index of a statement that is inside a method.
     * 
     * Find the last unmatched bracket since startindex excludes opening bracket
     * @param patch the commit patch
     * @param startIndex the start index of the statement
     * @return the end index of the statement
     */
    private int findEndOfStatementEmbedded(String patch, int startIndex) {
        int index = startIndex;
        int count = 1;
        while (count != 0) {
            if (index == patch.length()) {
                return -1;
            }
            char charIterator = patch.charAt(index++);
            if (charIterator == '(') {
                count++;
            } else if (charIterator == ')') {
                count--;
            }
        }

        return index - 3; // so as to ommit )" at the end
    }

    /**
     * Find the end index of a statement that is an assignment.
     * @param patch the commit patch
     * @param startIndex the start index of the statement
     * @return the end index of the statement
     */
    private int findEndOfStatementAssignment(String patch, int startIndex) {
        int endIndex = patch.indexOf(';', startIndex);
        if (endIndex == -1) {
            endIndex = patch.indexOf('\n', startIndex);
        }
        return endIndex != -1 ? endIndex - 2 : -1;
    }

    /**
     * Removes the '+' and '-' characters from the patch.
     * @param commitPatch the commit patch
     * @return the patch without '+' and '-' characters
     */
    private String removePlusMinus(String commitPatch) {
        StringBuilder result = new StringBuilder();
        String[] lines = commitPatch.split("\n");
        for (String line : lines) {
            if (line.length() > 0 && (line.startsWith("+") || line.startsWith("-"))) {
                line = line.substring(1);
            }
            result.append(line).append("\n");
        }
        return result.toString();
    }

    /**
     * Extract commit hash from metadata
     * 
     * @return the commit hash
     */
    private String getCommitHash() {
        Pattern pattern = Pattern.compile(COMMIT_HASH_REGEX);
        Matcher matcher = pattern.matcher(this.metadata);
        if (matcher.find()) {
            return matcher.group();
        }
        return null; // If no match is found
    }

    /**
     * Extract author from metadata
     * 
     * @return the author
     */
    private String getAuthor() {
        Pattern pattern = Pattern.compile(AUTHOR_REGEX);
        Matcher matcher = pattern.matcher(this.metadata);
        if (matcher.find()) {
            return matcher.group();
        }
        return null; // If no match is found
    }

    /**
     * Extract date and time from metadata
     * 
     * @return the date and time
     */
    private String getDateTime() {
        Pattern pattern = Pattern.compile(DATE_TIME_REGEX);
        Matcher matcher = pattern.matcher(this.metadata);
        if (matcher.find()) {
            return matcher.group();
        }
        return null; // If no match is found
    }

    /**
     * Extract file name from file changes
     * 
     * @param fileChanges the file changes
     * @return the file name
     */
    private String getFileName(String fileChanges) {
        Pattern pattern = Pattern.compile(FILE_NAME_REGEX);
        Matcher matcher = pattern.matcher(fileChanges);
        if (matcher.find()) {
            return matcher.group();
        }
        return null; // If no match is found
    }

    /**
     * Helper method for determinSQLCodeChange and determineSQLCodeContext
     * Given a String, determines if it contains SQL as per the SQL_PATTERN.
     * 
     * @param line the line to check
     * @return true if the line contains SQL, false otherwise
     */
    private boolean containsSQL(String line) {
        Pattern pattern = Pattern.compile(CommitLogParser.SQL_PATTERN);
        Matcher matcher = pattern.matcher(line);

        return matcher.find(); // Returns true if any match is found
    }

    /**
     * Given a file's code changes, determines if there is SQL in the code context.
     * 
     * @param fileChanges the file's code changes
     * @return true if any SQL is found in the code context, false otherwise
     */
    private boolean determineSQLCodeContext(String fileChanges) {
        // Split the changes into lines and iterate over each one
        String[] lines = fileChanges.split("\n");
        for (String line : lines) {
            if (containsSQL(line)) {
                // Check if the line is part of the context (not added or removed)
                if (!line.startsWith("+") && !line.startsWith("-")) {
                    return true; // SQL found in context
                }
            }
        }
        return false; // No SQL found in context
    }

    /**
     * Given a file's code changes, determines if there is SQL in the code change.
     * 
     * @param fileChanges the file's code changes
     * @return true if any SQL is found in the code change, false otherwise
     */
    private boolean determineSQLCodeChange(String fileChanges) {
        // Split the changes into lines and iterate over each one
        String[] lines = fileChanges.split("\n");
        for (String line : lines) {
            if (containsSQL(line)) {
                // Check if the line is part of the change (added or removed)
                if (line.startsWith("+") || line.startsWith("-")) {
                    return true; // SQL found in change
                }
            }
        }
        return false; // No SQL found in change
    }

    /**
     * Method to check if the file type is valid (java, sql, sqlpp, json)
     * @param fileName the name of the file
     * @return true if the file type is valid, false otherwise
     */
    private boolean checkFileTypeValidity(String fileName) {
        if (fileName.endsWith(".java") || fileName.endsWith(".sql") || fileName.endsWith(".sqlpp")
                || fileName.endsWith(".json")) {
            return true;
        }

        return false;
    }

}
