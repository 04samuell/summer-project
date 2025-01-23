import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * SQLDataExtractor.java
 * 
 * This class deals with extracting SQL data from commit logs and writing it to
 * a database.
 */
public class SQLDataExtractor {

    private static File[] projects; // List of files containing commit logs for each project
    private static String[] projectNames; // List of project names
    private static List<String[]> sqlData = new ArrayList<>(); // Database ready data for each commit
    private static CSVFileWriter writer = new CSVFileWriter();

    public static void main(String[] args) {

        // Get the commit log files for each project
        projects = getAllFiles();
        //projects = new File[]{(new File("SQLDataCollection\\ProjectCommitLogs\\oodt-commits.txt"))}; 

        projectNames = new String[projects.length];

        System.out.println("*** SQLDataExtraction - Number of projects: " + projects.length + " ***\n");

        // For each project, filter, format and append to csv file
        for (int i = 0; i < projects.length; i++) {
            CommitLogParser parser = new CommitLogParser(projects[i]);
            projectNames[i] = getProjectName(projects[i]);
            System.out.print("Parsing project: " + getProjectName(projects[i]) + ". ");
            String[] sqlCommits = parser.getSQLCommits(); // Filter out non-SQL commits
            System.out.println("Parsing complete");
            formatEntries(sqlCommits, projectNames[i]); // Format the remaining commits
        }

        System.out.println("*** Process completed! ***\n\n*** Output stored as " +  writer.getFileName() + "***");
    }

    /**
     * Method to format entries into database ready format. Appends to csv file 
     * 
     * @param sqlCommits the commits that contain sql.
     * @param projectName the name of the project.
     */
    private static void formatEntries(String[] sqlCommits, String projectName) {
        System.out.print("Formatting entries for project: " + projectName + ". ");
        sqlData = new ArrayList<>();
        int entryCount = 0;
        for (String commit : sqlCommits) {
            CommitFormatter formatter = new CommitFormatter(commit, projectName);
            List<String[]> rowEntries = formatter.getRowEntries(); // turns commit into db ready entry
            for (String[] entry : rowEntries) {
                sqlData.add(entry);
                entryCount++;
            }
        }

        System.out.println("Formatting complete - Number of SQL entries: " + entryCount);
        writer.writeToCSV(sqlData);
        System.out.println("Finished writing to CSV\n");
    }


    /**
     * Helper method to get all files in the ProjectCommitLogs directory
     * 
     * @return array of Files in the ProjectCommitLogs directory
     */
    private static File[] getAllFiles() {
        File folder = new File("SQLDataCollection\\ProjectCommitLogs");
        return folder.listFiles();
    }

    /**
     * Helper method to get the project name from a file
     * 
     * @param project the file containing the project
     * @return the name of the project
     */
    private static String getProjectName(File project) {
        String name = project.getName();
        return name.substring(0, name.lastIndexOf("-"));
    }

}