import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommitFormatter {

    private String commit;

    public CommitFormatter(String commit) {
        this.commit = commit;
    }

    /**
     * A method to turn a commit into a database ready format
     * 
     * @return String[] of the commit in database ready format
     */
    public String[] getRowEntry() {
        // Logic to format the commit into a database ready format
        String[] result = new String[1];
        result[0] = getSQLString()!=null ? getSQLString() : "No SQL found";
        
        return result;
    }

    /**
     * Given a commit, extracts a list of SQL strings
     * 
     * @return a collection of SQL strings
     */
    private String getSQLString() {
        StringBuilder result = new StringBuilder();
        Pattern pattern = Pattern.compile(CommitLogParser.SQL_PATTERN);
        Matcher matcher = pattern.matcher(commit);
        while (matcher.find()) {
            result.append(matcher.group()).append("\n");
        }
        return result.toString();
    }
    
}
