import java.util.*;
import java.io.*;
import java.nio.file.*;

public class CSVFileWriter {

    private List<String[]> data;
    private static final String SUB_DIRECTORY = "SQLDataCollection\\Results";
    private static final String FILE_NAME = "sql-data.csv";
    private static final String HEADER = "Project Name,Commit Hash,Author,Date Time,File Name,SQL,SQL Context,SQL Change";

    public CSVFileWriter(List<String[]> data) {
        this.data = data;
    }

    /**
     * A method to write the data to a CSV file.
     */
    public void writeToCSV() {

        Path path = Paths.get(SUB_DIRECTORY, FILE_NAME);

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()))) {
            writer.write(String.join(",", HEADER));
            writer.newLine();
            for(String[] entry: data) {
                writer.write(String.join(",", entry));
                writer.newLine();
            }
        } catch (Exception e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    public String getFileName() {
        return SUB_DIRECTORY + "\\" + FILE_NAME;
    }
    
}
