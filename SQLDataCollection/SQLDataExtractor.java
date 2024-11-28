import java.io.File;

/**
 * SQLDataExtractor.java
 * 
 * This class deals with extracting SQL data from commit logs and writing it to a database.
 */
public class SQLDataExtractor {

    private static int sqlCommitCount = 0;

    private static File[] projects; // List of files containing commit logs for each project
    private static String[][] commits; // For each project, a list of commits containing sql 
    private static String[][] sqlData; // Database ready data for each commit

    public static void main(String[] args) {

        // Get the commit log files for each project
        projects = getAllFiles();
        commits = new String[projects.length][];

        // Filter out the commits that don't contain SQL
        for(File project: projects) {
            CommitLogParser parser = new CommitLogParser(project);
            String[] sqlCommits = parser.getSQLCommits(); // returns an array where each entry is a commit containing sql
            commits[sqlCommitCount++] = sqlCommits;
        }

        /* 

        sqlData = new String[sqlCommitCount][];
        int i = 0;

        // Put each commit into a database ready format
        for(String[] projectCommits: commits) {
            for(String commit: projectCommits) {
                SQLExtractor extractor = new SQLExtractor(commit);
                sqlData[i++] = extractor.getFormattedEntry();
            }
        }

        // Write the data to the database using JDBC
        DatabaseWriter writer = new DatabaseWriter(sqlData);
        writer.commitToDatabase();

        */
        
    }


    /**
     * Helper method to get all files in the ProjectCommitLogs directory
     * @return array of Files in the ProjectCommitLogs directory
     */
    private static File[] getAllFiles() {
        File folder = new File("ProjectCommitLogs");
        return folder.listFiles();
    }

}