import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * SQLDataExtractor.java
 * 
 * This class deals with extracting SQL data from commit logs and writing it to a database.
 */
public class SQLDataExtractor {

    private static File[] projects; // List of files containing commit logs for each project
    private static String[] projectNames; // List of project names 
    private static String[][] commits; // For each project, a list of commits containing sql 
    private static List<String[]> sqlData = new ArrayList<>(); // Database ready data for each commit

    public static void main(String[] args) {

        // Get the commit log files for each project
        //projects = getAllFiles();
        projects = new File[] {new File("SQLDataCollection\\ProjectCommitLogs\\asterixdb-commits.txt")}; // For testing purposes, only use the first project
        commits = new String[projects.length][];
        projectNames = new String[projects.length];

        // Filter out the commits that don't contain SQL
        for(int i = 0; i < projects.length; i++) {
            CommitLogParser parser = new CommitLogParser(projects[i]);
            projectNames[i] = getProjectName(projects[i]);
            commits[i] = parser.getSQLCommits(); // getSQLCommits turns a file into a list of commits containing SQL
            System.out.println("Finished parsing project: " + projectNames[i] + ". Number of commits containing SQL: " + commits[i].length);
        }

        System.out.println("Parsing Complete.");


        
        // Get formatted entries for first 20 commits (in first project)
        int projectCount = 0;
        for(String[] projectCommits: commits) {
            String projectName = projectNames[projectCount++];
            for(int i = 0 ; i < 20 ; i++) {
                String commit = projectCommits[i];
                CommitFormatter formatter = new CommitFormatter(commit, projectName);
                List<String[]> rowEntries = formatter.getRowEntries(); // getRowEntries turns a commit into a list of database ready entries
                for(String[] entry: rowEntries) {
                    sqlData.add(entry);
                }
            }
        }
        

        /* 
        // Put each commit into a database ready format
        int projectCount = 0;
        for(String[] projectCommits: commits) {
            String projectName = projectNames[projectCount++];
            System.out.println("Formatting entries for project: " + projectName);
            for(String commit: projectCommits) {
                CommitFormatter formatter = new CommitFormatter(commit, projectName);
                List<String[]> rowEntries = formatter.getRowEntries(); // getRowEntries turns a commit into a list of database ready entries
                for(String[] entry: rowEntries) {
                    sqlData.add(entry);
                }
            }
        }

        */

        System.out.println("Finished formatting entries.");


        // Write the data to a CSV file
        CSVFileWriter writer = new CSVFileWriter(sqlData);
        writer.writeToCSV();

        System.out.println("Finished writing to CSV file. Output stored as: " + writer.getFileName());
        
    }


    /**
     * Helper method to get all files in the ProjectCommitLogs directory
     * @return array of Files in the ProjectCommitLogs directory
     */
    private static File[] getAllFiles() {
        File folder = new File("SQLDataCollection\\ProjectCommitLogs");
        return folder.listFiles();
    }

    /**
     * Helper method to get the project name from a file
     * @param project the file containing the project
     * @return the name of the project
     */
    private static String getProjectName(File project) {
        String name = project.getName();
        return name.substring(0, name.lastIndexOf("-"));
    }

}