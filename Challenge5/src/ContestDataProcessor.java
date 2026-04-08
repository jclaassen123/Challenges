import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 * Processes contest CSV data and writes normalized team and institution CSV outputs.
 */
public class ContestDataProcessor {
    /**
     * Reads the source CSV, builds deduplicated institution/team records, and writes output CSV files.
     *
     * @param inputPath source contest CSV path
     * @param outputDir destination directory for generated files
     * @return {@code true} when processing completes successfully, otherwise {@code false}
     */
    public boolean processContestData(Path inputPath, Path outputDir) {
        try (BufferedReader reader = Files.newBufferedReader(inputPath, StandardCharsets.UTF_8)) {
            String headerLine = reader.readLine();
            if (headerLine == null || headerLine.trim().isEmpty()) {
                System.err.println("Input CSV is empty.");
                return false;
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
                System.err.println("Missing required header(s): " + String.join(", ", missingHeaders));
                return false;
            }

            LinkedHashMap<String, Institution> institutionsByKey = new LinkedHashMap<>();
            List<Team> teams = new ArrayList<>();
            Set<String> seenTeamNumbers = new HashSet<>();

            String line;
            while ((line = reader.readLine()) != null) {
                // Skip blank lines so malformed spacing does not create empty records.
                if (line.trim().isEmpty()) {
                    continue;
                }

                List<String> fields = CsvUtils.parseCsvLine(line);
                String institutionName = getField(fields, headerIndex.get("institution"));
                String teamNumber = getField(fields, headerIndex.get("team number"));
                String city = getField(fields, headerIndex.get("city"));
                String stateProvince = getField(fields, headerIndex.get("state/province"));
                String country = getField(fields, headerIndex.get("country"));
                String advisor = getField(fields, headerIndex.get("advisor"));
                String problem = getField(fields, headerIndex.get("problem"));
                String ranking = getField(fields, headerIndex.get("ranking"));

                // Ignore invalid rows that cannot be represented in the normalized output.
                if (teamNumber.isEmpty() || institutionName.isEmpty()) {
                    continue;
                }

                String institutionKey = makeInstitutionKey(institutionName, city, stateProvince, country);
                Institution institution = institutionsByKey.get(institutionKey);
                if (institution == null) {
                    // IDs are deterministic by insertion order and easy to join in SQL imports.
                    String institutionId = String.format("INST%05d", institutionsByKey.size() + 1);
                    institution = new Institution(institutionId, institutionName, city, stateProvince, country);
                    institutionsByKey.put(institutionKey, institution);
                }

                // Keep first occurrence of each team number to avoid duplicates.
                if (seenTeamNumbers.add(teamNumber)) {
                    teams.add(new Team(teamNumber, advisor, problem, ranking, institution.getInstitutionId()));
                }
            }

            boolean institutionsWritten = writeInstitutions(outputDir.resolve("Institutions.csv"), institutionsByKey.values());
            boolean teamsWritten = writeTeams(outputDir.resolve("Teams.csv"), teams);
            return institutionsWritten && teamsWritten;
        } catch (Exception e) {
            System.err.println("Unable to process input file '" + inputPath + "': " + e.getMessage());
            return false;
        }
    }

    /**
     * Builds a lookup of normalized header name to column index.
     *
     * @param headers raw header row fields
     * @return map of normalized header labels to their column positions
     */
    private Map<String, Integer> buildHeaderIndex(List<String> headers) {
        Map<String, Integer> index = new HashMap<>();
        for (int i = 0; i < headers.size(); i++) {
            String normalized = normalizeHeader(headers.get(i));
            // Keep the first index if duplicate headers appear.
            if (!normalized.isEmpty() && !index.containsKey(normalized)) {
                index.put(normalized, i);
            }
        }
        return index;
    }

    /**
     * Ensures all required headers are present before processing data rows.
     *
     * @param headerIndex map of normalized header names to indexes
     * @param requiredHeaders required normalized header names
     * @return list of missing required headers (empty list means validation passed)
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
     * Normalizes header text for robust column matching across yearly files.
     *
     * @param header raw header field
     * @return normalized lower-case header with BOM artifacts and extra spaces removed
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
     * Builds a normalized composite key for deduplicating institutions.
     *
     * @param name institution name
     * @param city institution city
     * @param stateProvince institution state or province
     * @param country institution country
     * @return normalized key in a fixed delimiter format
     */
    private String makeInstitutionKey(String name, String city, String stateProvince, String country) {
        return normalizeKeyPart(name) + "|"
                + normalizeKeyPart(city) + "|"
                + normalizeKeyPart(stateProvince) + "|"
                + normalizeKeyPart(country);
    }

    /**
     * Normalizes one text segment used in a deduplication key.
     *
     * @param value raw text value
     * @return lower-case, trimmed value with internal whitespace collapsed
     */
    private String normalizeKeyPart(String value) {
        return value == null ? "" : value.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    /**
     * Reads a field by index and safely returns an empty string when index is out of bounds.
     *
     * @param fields parsed CSV row values
     * @param index column index
     * @return trimmed field value or empty string when missing
     */
    private String getField(List<String> fields, int index) {
        if (index < 0 || index >= fields.size()) {
            return "";
        }
        return fields.get(index).trim();
    }

    /**
     * Writes deduplicated institutions to Institutions.csv using the required column order.
     *
     * @param outputPath destination CSV path
     * @param institutions institutions to write
     * @return {@code true} when writing succeeds, otherwise {@code false}
     */
    private boolean writeInstitutions(Path outputPath, Iterable<Institution> institutions) {
        try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            writer.write("Institution ID,Institution Name,City,State/Province,Country");
            writer.newLine();
            for (Institution institution : institutions) {
                writer.write(CsvUtils.escapeCsv(institution.getInstitutionId()));
                writer.write(",");
                writer.write(CsvUtils.escapeCsv(institution.getInstitutionName()));
                writer.write(",");
                writer.write(CsvUtils.escapeCsv(institution.getCity()));
                writer.write(",");
                writer.write(CsvUtils.escapeCsv(institution.getStateProvince()));
                writer.write(",");
                writer.write(CsvUtils.escapeCsv(institution.getCountry()));
                writer.newLine();
            }
            return true;
        } catch (Exception e) {
            System.err.println("Unable to write institutions file '" + outputPath + "': " + e.getMessage());
            return false;
        }
    }

    /**
     * Writes team rows to Teams.csv using the required column order.
     *
     * @param outputPath destination CSV path
     * @param teams team records to write
     * @return {@code true} when writing succeeds, otherwise {@code false}
     */
    private boolean writeTeams(Path outputPath, List<Team> teams) {
        try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            writer.write("Team Number,Advisor,Problem,Ranking,Institution ID");
            writer.newLine();
            for (Team team : teams) {
                writer.write(CsvUtils.escapeCsv(team.getTeamNumber()));
                writer.write(",");
                writer.write(CsvUtils.escapeCsv(team.getAdvisor()));
                writer.write(",");
                writer.write(CsvUtils.escapeCsv(team.getProblem()));
                writer.write(",");
                writer.write(CsvUtils.escapeCsv(team.getRanking()));
                writer.write(",");
                writer.write(CsvUtils.escapeCsv(team.getInstitutionId()));
                writer.newLine();
            }
            return true;
        } catch (Exception e) {
            System.err.println("Unable to write teams file '" + outputPath + "': " + e.getMessage());
            return false;
        }
    }
}
