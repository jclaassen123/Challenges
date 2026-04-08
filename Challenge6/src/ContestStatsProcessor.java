import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Reads contest results and writes descriptive statistics to a report file.
 */
public class ContestStatsProcessor {
    private static final DecimalFormat AVERAGE_FORMAT = new DecimalFormat("0.00");

    /**
     * Aggregated institution statistics used while building the ordered team-count section.
     */
    private static final class InstitutionSummary {
        private final String displayName;
        private int teamCount;

        /**
         * Creates a new institution summary.
         *
         * @param displayName institution name to show in the report
         */
        private InstitutionSummary(String displayName) {
            this.displayName = displayName;
            this.teamCount = 0;
        }
    }

    /**
     * Processes the contest CSV and writes the requested analysis report.
     *
     * @param inputPath source contest CSV
     * @param outputPath destination report file
     * @return {@code true} when processing succeeds, otherwise {@code false}
     */
    public boolean process(Path inputPath, Path outputPath) {
        try {
            List<ContestEntry> entries = readEntries(inputPath);
            if (entries.isEmpty()) {
                System.err.println("Input CSV did not contain any valid contest rows.");
                return false;
            }

            writeReport(outputPath, entries);
            return true;
        } catch (Exception e) {
            System.err.println("Unable to process input file '" + inputPath + "': " + e.getMessage());
            return false;
        }
    }

    /**
     * Reads the source CSV into distinct team entries while validating required columns.
     *
     * @param inputPath source contest CSV
     * @return parsed, deduplicated contest entries
     * @throws Exception when the file cannot be read or required headers are missing
     */
    private List<ContestEntry> readEntries(Path inputPath) throws Exception {
        try (BufferedReader reader = Files.newBufferedReader(inputPath, StandardCharsets.UTF_8)) {
            String headerLine = reader.readLine();
            if (headerLine == null || headerLine.trim().isEmpty()) {
                throw new IllegalArgumentException("Input CSV is empty.");
            }

            List<String> headers = CsvUtils.parseCsvLine(headerLine);
            Map<String, Integer> headerIndex = buildHeaderIndex(headers);
            List<String> missingHeaders = validateRequiredHeaders(headerIndex, List.of(
                    "institution",
                    "team number",
                    "city",
                    "state/province",
                    "country",
                    "advisor",
                    "problem",
                    "ranking"
            ));
            if (!missingHeaders.isEmpty()) {
                throw new IllegalArgumentException(
                        "Missing required header(s): " + String.join(", ", missingHeaders));
            }

            List<ContestEntry> entries = new ArrayList<>();
            Set<String> seenTeamNumbers = new HashSet<>();
            String line;
            while ((line = reader.readLine()) != null) {
                // Ignore blank rows so formatting noise does not produce empty entries.
                if (line.trim().isEmpty()) {
                    continue;
                }

                List<String> fields = CsvUtils.parseCsvLine(line);
                String institution = getField(fields, headerIndex.get("institution"));
                String teamNumber = getField(fields, headerIndex.get("team number"));
                String city = getField(fields, headerIndex.get("city"));
                String stateProvince = getField(fields, headerIndex.get("state/province"));
                String country = getField(fields, headerIndex.get("country"));
                String advisor = getField(fields, headerIndex.get("advisor"));
                String problem = getField(fields, headerIndex.get("problem"));
                String ranking = getField(fields, headerIndex.get("ranking"));

                // Skip rows that cannot produce a valid distinct team record.
                if (institution.isEmpty() || teamNumber.isEmpty() || !seenTeamNumbers.add(teamNumber)) {
                    continue;
                }

                entries.add(new ContestEntry(
                        institution,
                        teamNumber,
                        city,
                        stateProvince,
                        country,
                        advisor,
                        problem,
                        ranking
                ));
            }

            return entries;
        }
    }

    /**
     * Writes the full text report containing all required statistics.
     *
     * @param outputPath destination report file
     * @param entries parsed contest entries
     * @throws Exception when the report cannot be written
     */
    private void writeReport(Path outputPath, List<ContestEntry> entries) throws Exception {
        Map<String, InstitutionSummary> institutions = buildInstitutionCounts(entries);
        List<InstitutionSummary> sortedInstitutionCounts = new ArrayList<>(institutions.values());
        sortedInstitutionCounts.sort(
                Comparator.<InstitutionSummary>comparingInt(summary -> summary.teamCount)
                        .reversed()
                        .thenComparing(summary -> summary.displayName, String.CASE_INSENSITIVE_ORDER)
        );

        List<String> outstandingInstitutions = buildOutstandingInstitutionList(entries, institutions);
        List<ContestEntry> usTopTeams = buildUsTopTeams(entries);
        double averageTeamsPerInstitution = (double) entries.size() / institutions.size();

        try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            writer.write("Contest Analysis Report");
            writer.newLine();
            writer.write("=======================");
            writer.newLine();
            writer.newLine();

            writer.write("Average number of teams entered per institution: "
                    + AVERAGE_FORMAT.format(averageTeamsPerInstitution));
            writer.newLine();
            writer.newLine();

            writer.write("Institutions Entering the Most Teams");
            writer.newLine();
            writer.write("------------------------------------");
            writer.newLine();
            // Institutions are ordered by descending team count, then alphabetically for ties.
            for (InstitutionSummary institution : sortedInstitutionCounts) {
                writer.write(institution.displayName + " - " + institution.teamCount);
                writer.newLine();
            }
            writer.newLine();

            writer.write("Institutions with Outstanding Rankings");
            writer.newLine();
            writer.write("--------------------------------------");
            writer.newLine();
            if (outstandingInstitutions.isEmpty()) {
                writer.write("None");
                writer.newLine();
            } else {
                for (String institution : outstandingInstitutions) {
                    writer.write(institution);
                    writer.newLine();
                }
            }
            writer.newLine();

            writer.write("US Teams with Meritorious Ranking or Better");
            writer.newLine();
            writer.write("-------------------------------------------");
            writer.newLine();
            if (usTopTeams.isEmpty()) {
                writer.write("None");
                writer.newLine();
            } else {
                // Include the identifying columns that make each qualifying team easy to review.
                for (ContestEntry entry : usTopTeams) {
                    writer.write(entry.getTeamNumber()
                            + " | "
                            + entry.getInstitution()
                            + " | "
                            + entry.getCity()
                            + " | "
                            + entry.getStateProvince()
                            + " | "
                            + entry.getAdvisor()
                            + " | "
                            + entry.getProblem()
                            + " | "
                            + entry.getRanking());
                    writer.newLine();
                }
            }
        }
    }

    /**
     * Counts teams for each normalized institution.
     *
     * @param entries parsed contest entries
     * @return map of normalized institution key to summary information
     */
    private Map<String, InstitutionSummary> buildInstitutionCounts(List<ContestEntry> entries) {
        Map<String, InstitutionSummary> counts = new LinkedHashMap<>();
        for (ContestEntry entry : entries) {
            String key = makeInstitutionKey(entry);
            InstitutionSummary summary = counts.get(key);
            if (summary == null) {
                // Keep the first display name encountered while merging institution variants by location.
                summary = new InstitutionSummary(entry.getInstitution());
                counts.put(key, summary);
            }
            summary.teamCount++;
        }
        return counts;
    }

    /**
     * Collects institutions that earned an Outstanding-level ranking.
     *
     * @param entries parsed contest entries
     * @param institutionsByKey normalized institution summaries
     * @return alphabetically ordered institution names
     */
    private List<String> buildOutstandingInstitutionList(
            List<ContestEntry> entries,
            Map<String, InstitutionSummary> institutionsByKey) {
        Set<String> institutions = new HashSet<>();
        for (ContestEntry entry : entries) {
            if (isOutstanding(entry.getRanking())) {
                InstitutionSummary summary = institutionsByKey.get(makeInstitutionKey(entry));
                if (summary != null) {
                    institutions.add(summary.displayName);
                }
            }
        }

        List<String> sorted = new ArrayList<>(institutions);
        sorted.sort(String.CASE_INSENSITIVE_ORDER);
        return sorted;
    }

    /**
     * Collects all US teams whose ranking is Meritorious or better.
     *
     * @param entries parsed contest entries
     * @return qualifying US teams ordered by institution name then team number
     */
    private List<ContestEntry> buildUsTopTeams(List<ContestEntry> entries) {
        List<ContestEntry> matches = new ArrayList<>();
        for (ContestEntry entry : entries) {
            if (isUsTeam(entry.getCountry()) && isMeritoriousOrBetter(entry.getRanking())) {
                matches.add(entry);
            }
        }

        matches.sort(
                Comparator.comparing(ContestEntry::getInstitution, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(ContestEntry::getTeamNumber)
        );
        return matches;
    }

    /**
     * Checks whether a ranking should be treated as Outstanding for the report.
     *
     * @param ranking ranking text from the source CSV
     * @return {@code true} when the ranking is Outstanding
     */
    private boolean isOutstanding(String ranking) {
        String normalized = normalizeText(ranking);
        return normalized.startsWith("outstanding");
    }

    /**
     * Checks whether a ranking is Meritorious or above.
     *
     * @param ranking ranking text from the source CSV
     * @return {@code true} when ranking is Meritorious, Finalist, or Outstanding
     */
    private boolean isMeritoriousOrBetter(String ranking) {
        String normalized = normalizeText(ranking);
        return normalized.equals("meritorious")
                || normalized.equals("finalist")
                || normalized.startsWith("outstanding");
    }

    /**
     * Checks whether a team belongs to a US institution.
     *
     * @param country country text from the source CSV
     * @return {@code true} when the country value indicates the United States
     */
    private boolean isUsTeam(String country) {
        String normalized = normalizeText(country);
        return normalized.equals("usa")
                || normalized.equals("u.s.a.")
                || normalized.equals("united states")
                || normalized.equals("united states of america");
    }

    /**
     * Builds a normalized institution key that matches the Challenge 5 deduplication approach.
     *
     * @param entry contest entry used to build the key
     * @return normalized institution key
     */
    private String makeInstitutionKey(ContestEntry entry) {
        return normalizeText(entry.getInstitution())
                + "|"
                + normalizeText(entry.getCity())
                + "|"
                + normalizeText(entry.getStateProvince())
                + "|"
                + normalizeText(entry.getCountry());
    }

    /**
     * Builds a lookup from normalized header name to CSV column index.
     *
     * @param headers raw header values from the input CSV
     * @return map of normalized header names to column positions
     */
    private Map<String, Integer> buildHeaderIndex(List<String> headers) {
        Map<String, Integer> index = new HashMap<>();
        for (int i = 0; i < headers.size(); i++) {
            String normalized = normalizeHeader(headers.get(i));
            // Keep the first matching column if duplicate headers appear.
            if (!normalized.isEmpty() && !index.containsKey(normalized)) {
                index.put(normalized, i);
            }
        }
        return index;
    }

    /**
     * Verifies that all required headers are present before processing data rows.
     *
     * @param headerIndex normalized header lookup
     * @param requiredHeaders required normalized header names
     * @return list of missing headers, or an empty list when validation passes
     */
    private List<String> validateRequiredHeaders(Map<String, Integer> headerIndex, List<String> requiredHeaders) {
        List<String> missing = new ArrayList<>();
        for (String required : requiredHeaders) {
            if (!headerIndex.containsKey(required)) {
                missing.add(required);
            }
        }
        return missing;
    }

    /**
     * Normalizes a header label for case-insensitive matching and BOM cleanup.
     *
     * @param header raw header text
     * @return normalized header value
     */
    private String normalizeHeader(String header) {
        if (header == null) {
            return "";
        }

        String cleaned = header
                .replace("\uFEFF", "")
                .replace("ï»¿", "")
                .trim()
                .toLowerCase(Locale.ROOT);

        return cleaned.replaceAll("\\s+", " ");
    }

    /**
     * Normalizes arbitrary text for case-insensitive comparisons and keys.
     *
     * @param value raw text value
     * @return normalized lower-case text with collapsed whitespace
     */
    private String normalizeText(String value) {
        return value == null ? "" : value.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    /**
     * Safely returns a trimmed field value or an empty string when the index is out of bounds.
     *
     * @param fields parsed CSV row
     * @param index column index
     * @return trimmed field value or an empty string
     */
    private String getField(List<String> fields, int index) {
        if (index < 0 || index >= fields.size()) {
            return "";
        }
        return fields.get(index).trim();
    }
}
