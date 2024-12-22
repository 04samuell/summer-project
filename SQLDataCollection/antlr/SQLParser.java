import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.ParseTree;

public class SQLParser {

    public SQLParser() {}

    public String parseText(String commitPatch) {
        StringBuilder sqlStatements = new StringBuilder();

        // Split the commit patch into lines. This helps handle multi-line patches
        // and isolates SQL statements within the context of a patch.
        String[] lines = commitPatch.split("\\r?\\n"); // Handles both \r\n and \n

        for (String line : lines) {
            if (line.length() > 0 && line.startsWith("+") || line.startsWith("-")) {
                line = line.substring(1).trim();
            }
            try {
                // Create a CharStream from the current line
                CharStream input = CharStreams.fromString(line);

                // Create a lexer that feeds off of the input CharStream
                MySQLLexer lexer = new MySQLLexer(input);

                // Create a buffer of tokens pulled from the lexer
                CommonTokenStream tokens = new CommonTokenStream(lexer);

                // Create a parser that feeds off the tokens buffer
                MySQLParser parser = new MySQLParser(tokens);

                // Attempt to parse the line as a SQL statement.
                parser.removeErrorListeners(); // Remove default error listeners
                parser.addErrorListener(new NoErrorListener()); // Add our custom one

                ParseTree tree = parser.query(); // Parse the line as a SQL statement

                // If no syntax errors were found, append the line to the SQL statements
                if (parser.getNumberOfSyntaxErrors() == 0) {
                    sqlStatements.append(line).append(System.lineSeparator());
                }

            } catch (Exception e) {}
        }

        return sqlStatements.toString();
    }

    static class NoErrorListener extends BaseErrorListener {
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
                String msg, RecognitionException e) {
        }
    }

}
