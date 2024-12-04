import java.util.*;
import java.io.*;

public class CSVFileWriter {

    private List<String[]> data;
    private static final String FILE_NAME = "sql-data.csv";

    public CSVFileWriter(List<String[]> data) {
        this.data = data;
    }

    /**
     * A method to write the data to a CSV file.
     */
    public void writeToCSV() {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for(String[] entry: data) {
                writer.write(String.join(",", entry));
                writer.newLine();
            }
        } catch (Exception e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }
    
}
