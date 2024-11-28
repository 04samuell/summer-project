import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * CommitLogParser.java
 * 
 * A class to parse commit log files by filtering for SQL commits. 
 */
public class CommitLogParser {

    private File commitLog;
    private static final String COMMIT_SPLITTER = "";

    public CommitLogParser(File commitLog) {
        this.commitLog = commitLog;
    }

    /**
     * A method to turn a commit log file into a String array of commits containing SQL
     * 
     * @return String[] of commits containing SQL
     * @throws FileNotFoundException
     */
    public String[] getSQLCommits() throws FileNotFoundException{
        StringBuilder result = new StringBuilder();
        Scanner sc = new Scanner(this.commitLog);
        sc.useDelimiter(COMMIT_SPLITTER);
        while(sc.hasNext()) {
            String commit = sc.next();
            if(containsSQL(commit)) {
                result.append(commit);
            }
        }
        sc.close();

        return result.toString().split(COMMIT_SPLITTER);
    }

    /**
     * Helper method. Given a commit, determines if it contains SQL
     * 
     * @param commit the commit
     * @return true if the commit contains SQL, false otherwise
     */
    private boolean containsSQL(String commit) {
        // Logic to determine if a commit contains SQL
        return Math.random() > 0.5;
    }
    
}
