import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods for parsing CSV rows and escaping CSV output.
 */
public final class CsvUtils {
    /**
     * Prevents instantiation of this utility class.
     */
    private CsvUtils() {
    }

    /**
     * Parses a single CSV line, supporting quoted values and escaped quotes.
     *
     * @param line raw CSV line
     * @return parsed field values
     */
    public static List<String> parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                // Two consecutive quotes inside a quoted field represent one literal quote.
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    // Toggle quoted mode so commas inside quotes are preserved as data.
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                // Only commas outside quotes terminate the current field.
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }

        // Flush the final field after the loop completes.
        fields.add(current.toString());
        return fields;
    }
}
