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
     * @return the SQL statements in the String
     */
    private String getSQLString(String diff) {
        // modify to use antlr
        StringBuilder result = new StringBuilder();
        Pattern pattern = Pattern.compile(CommitLogParser.SQL_PATTERN, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(diff);
        while (matcher.find()) {
            result.append(matcher.group()).append("\n");
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

}
