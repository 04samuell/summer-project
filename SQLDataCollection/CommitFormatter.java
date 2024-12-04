import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;

public class CommitFormatter {

    // Commit data
    private String commit;
    private String metadata;

    // Regex patterns
    private static final String FILE_SPLITTER = "diff --git\\s+";
    private static final String COMMIT_HASH_REGEX = "[a-f0-9]{40}";
    private static final String DATE_TIME_REGEX = "(?<=Date:\\s{3}).*";
    private static final String AUTHOR_REGEX = "(?<=Author:\\s).*?(?=\\s<)";
    private static final String FILE_NAME_REGEX = "b\\/([^\\s]+)";

    public CommitFormatter(String commit) {
        this.commit = commit;
    }

    /**
     * A method to turn a commit into row entries ready for the database.
     * 
     * Breaks the commit into individual files, files containing sql become row entries.
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
        for(int i = 1; i < commitFileList.length; i++) {
            String fileCodeChanges = commitFileList[i];
            String sql = getSQLString(fileCodeChanges);
            if(!sql.equals("")) { // Check if sql check came out empty, if not, make a row entry
                String fileName = getFileName(fileCodeChanges);
                String[] entry = new String[5];
                entry[0] = commitHash;
                entry[1] = author;
                entry[2] = dateTime;
                entry[3] = fileName;
                entry[4] = sql;
                result.add(entry);
            }
        }
        
        return result;
    }

    /**
     * Given a String, extracts the SQL.
     * @return the SQL statements in the String
     */
    private String getSQLString(String diff) {
        StringBuilder result = new StringBuilder();
        Pattern pattern = Pattern.compile(CommitLogParser.SQL_PATTERN);
        Matcher matcher = pattern.matcher(diff);
        while (matcher.find()) {
            result.append(matcher.group()).append("\n");
        }
        return result.toString();
    }

    /**
     * Extract commit hash from metadata
     * @return the commit hash
     */
    private String getCommitHash() {
        Pattern pattern = Pattern.compile(COMMIT_HASH_REGEX);
        Matcher matcher = pattern.matcher(this.metadata);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;  // If no match is found
    }

    /**
     * Extract author from metadata
     * @return the author
     */
    private String getAuthor() {
        Pattern pattern = Pattern.compile(AUTHOR_REGEX);
        Matcher matcher = pattern.matcher(this.metadata);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;  // If no match is found
    }

    /**
     * Extract date and time from metadata
     * @return the date and time
     */
    private String getDateTime() {
        Pattern pattern = Pattern.compile(DATE_TIME_REGEX);
        Matcher matcher = pattern.matcher(this.metadata);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;  // If no match is found
    }

    /**
     * Extract file name from file changes
     * @param fileChanges the file changes
     * @return the file name
     */
    private String getFileName(String fileChanges) {
        Pattern pattern = Pattern.compile(FILE_NAME_REGEX);
        Matcher matcher = pattern.matcher(fileChanges);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;  // If no match is found
    }
    
}
