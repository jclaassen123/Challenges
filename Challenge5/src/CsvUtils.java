import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods for CSV parsing and CSV-safe value escaping.
 */
public final class CsvUtils {
    /**
     * Utility class constructor is private to prevent instantiation.
     */
    private CsvUtils() {
    }

    /**
     * Parses a single CSV line into fields, handling quoted values and escaped quotes.
     *
     * @param line raw CSV line
     * @return list of parsed field values
     */
    public static List<String> parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                // Handle escaped quote inside a quoted field.
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    // Toggle quote mode when an unescaped quote is encountered.
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                // Commas only split fields when not inside quotes.
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }

        fields.add(current.toString());
        return fields;
    }

    /**
     * Escapes one field for CSV output according to RFC-style quoting rules.
     *
     * @param value raw field value
     * @return escaped CSV-safe string
     */
    public static String escapeCsv(String value) {
        String safe = value == null ? "" : value;
        boolean needsQuotes = safe.contains(",") || safe.contains("\"") || safe.contains("\n") || safe.contains("\r");
        if (!needsQuotes) {
            return safe;
        }

        // Double quotes inside quoted fields must be escaped as "".
        return "\"" + safe.replace("\"", "\"\"") + "\"";
    }
}
