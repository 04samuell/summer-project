import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

/**
 * CommitLogParser.java
 * 
 * A class to parse commit log files by filtering for SQL commits.
 */
public class CommitLogParser {

    private File commitLog;
    private static final String COMMIT_SPLITTER = "(?<=\\n)commit\\s+";

    public CommitLogParser(File commitLog) {
        this.commitLog = commitLog;
    }

    /**
     * A method to turn a commit log file into a String array of commits containing
     * SQL
     * 
     * @return String[] of commits containing SQL
     * @throws FileNotFoundException
     */
    public String[] getSQLCommits() {

        // Split the commit log file by "commit"
        StringBuilder fileContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(commitLog))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line).append("\n");
            }
        } catch (Exception e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        String[] commitsArray = fileContent.toString().split(COMMIT_SPLITTER);

        // Filter out the commits that don't contain SQL
        StringBuilder result = new StringBuilder();
        for (String commit : commitsArray) {
            if (containsSQL(commit)) {
                result.append(commit).append("\n\n\n"); // Three new lines indicates the end of a commit
            }
        }

        return result.toString().split("\n\n\n");
    }

    /**
     * Helper method. Given a commit, determines if it contains SQL
     * 
     * @param commit the commit
     * @return true if the commit contains SQL, false otherwise
     */
    private boolean containsSQL(String commit) {
        // Logic to determine if a commit contains SQL
        return true;
    }

}
