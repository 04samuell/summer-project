import java.util.*;
import java.io.*;
import java.nio.file.*;

public class CSVFileWriter {

    private Path path = Paths.get(SUB_DIRECTORY, FILE_NAME);
    private BufferedWriter writer;
    private static final String SUB_DIRECTORY = "Datasets";
    private static final String FILE_NAME = "sql-data.csv";
    private static final String HEADER = "Project Name,Commit Hash,Author,Date Time,File Name,Additions,Deletions,SQL,SQL Context,SQL Change";

    /**
     * Constructor for CSVFileWriter.
     * 
     * Creates the file writer object and initialises it with header.
     */
    public CSVFileWriter() {
        try {
            this.writer = new BufferedWriter(new FileWriter(path.toFile()));
            writer.write(String.join(",", HEADER));
            writer.newLine();
        } catch (Exception e) {
            System.out.println("Error creating CSV File: " + e.getMessage());
        }
    }

    /**
     * A method to write the data to the writer object.
     * 
     * @param data list of data to be written.
     */
    public void writeToCSV(List<String[]> data) {
        try {
            for (String[] entry : data) {
                writer.write(String.join(",", entry));
                writer.newLine();
            }
        } catch (Exception e) {
            System.out.println("Error appending to file: " + e.getMessage());
        }
    }

    /**
     * Method to get the file name of the eventual CSV file.
     * 
     * @return the name of the csv file.
     */
    public String getFileName() {
        return SUB_DIRECTORY + "\\" + FILE_NAME;
    }

}
