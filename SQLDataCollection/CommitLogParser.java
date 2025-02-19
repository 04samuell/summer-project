import java.io.*;
import java.util.regex.*;

/**
 * CommitLogParser.java
 * 
 * A class to parse commit log files by filtering for SQL commits.
 */
public class CommitLogParser {

    private File commitLog;
    private int sqlCommitCounter = 0;
    static final String COMMIT_SPLITTER = "(?<=\\n)commit\\s+";

    static final String SQL_PATTERN = "(?i)" +
            "(SELECT\\s+[^\\s]+\\s+FROM\\s+[^\\s]+|" +
            "SELECT\\sDISTINCT+[^\\s]+\\s+FROM\\s+[^\\s]+|" +
            "INSERT\\s+INTO\\s+[^\\s]+\\s+VALUES\\s+\\(.*\\)|" +
            "UPDATE\\s+[^\\s]+\\s+SET\\s+[^\\s]+\\s+=\\s+.*|" +
            "DELETE\\s+FROM\\s+[^\\s]+|" +
            "CREATE\\s+TABLE\\s+\\w+|ALTER\\s+TABLE\\s+\\w+|DROP\\s+TABLE\\s+\\w+|" +
            "TRUNCATE\\s+TABLE\\s+\\w+|" +
            "USE\\s+\\w+\\s*;$" +
            "DROP\\s+DATAVERSE\\s+\\w+|" +
            "CREATE\\s+DATAVERSE\\s+\\w+|" +
            "CREATE\\s+DATASET\\s+\\w+|" +
            "CREATE\\s+INDEX\\s+\\w+|" +
            "CREATE\\s+TYPE\\s+\\w+|" +
            "WITH\\s+\\w+\\sAS\\s\\(|" +
            "CREATE\\sEXTERNAL\\sDATASET\\s\\w+|" +
            "CREATE\\sEXTERNAL\\sCOLLECTION\\s\\w+|" +
            "CREATE\\s+COLLECTION\\s+\\w+\\(.*\\))";

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
            reader.close(); // Free memory
        } catch (Exception e) {
            System.out.println("Error reading file: " + e.getMessage());
        } catch (OutOfMemoryError e) {
            System.out.println("File too large to read");
            return null;
        }
        String[] commitsArray = fileContent.toString().split(COMMIT_SPLITTER);
        fileContent = null; // Free up memory

        // Filter out the commits that don't contain SQL
        StringBuilder result = new StringBuilder();
        for (String commit : commitsArray) {
            if (containsSQL(commit)) {
                result.append(commit).append("\n\n\n\n"); // Four new lines indicates the end of a commit
                this.sqlCommitCounter++;
            }
        }

        commitsArray = null; // Free up memory
        return result.toString().split("\n\n\n\n");
    }

    /**
     * Helper method. Given a commit, determines if it contains SQL
     * 
     * @param commit the commit
     * @return true if the commit contains SQL, false otherwise
     */
    public static boolean containsSQL(String commit) {
        Pattern pattern = Pattern.compile(SQL_PATTERN);
        Matcher matcher = pattern.matcher(commit);

        return matcher.find(); // Returns true if any match is found
    }

    /**
     * Getter method for the number of SQL commits
     * 
     * @return the number of SQL commits
     */
    public int getNumberOfSQLCommits() {
        return this.sqlCommitCounter;
    }

}
