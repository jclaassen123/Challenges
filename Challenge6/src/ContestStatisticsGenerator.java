import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Entry point for generating contest statistics from a yearly results CSV.
 */
public class ContestStatisticsGenerator {
    /**
     * Runs the contest analysis.
     *
     * @param args optional arguments:
     *             args[0] = input CSV path (default: 2015.csv)
     *             args[1] = output report path (default: ContestAnalysis.txt)
     */
    public static void main(String[] args) {
        Path inputPath = args.length > 0 ? Paths.get(args[0]) : Paths.get("2015.csv");
        Path outputPath = args.length > 1 ? Paths.get(args[1]) : Paths.get("ContestAnalysis.txt");

        // Create the destination directory first so report generation can succeed cleanly.
        if (!ensureOutputParentExists(outputPath)) {
            System.exit(1);
            return;
        }

        ContestStatsProcessor processor = new ContestStatsProcessor();
        boolean success = processor.process(inputPath, outputPath);
        if (success) {
            System.out.println("Generated: " + outputPath.toAbsolutePath());
        } else {
            System.exit(1);
        }
    }

    /**
     * Ensures the parent directory for the report file exists before processing.
     *
     * @param outputPath destination report path
     * @return {@code true} when the directory exists or was created, otherwise {@code false}
     */
    private static boolean ensureOutputParentExists(Path outputPath) {
        try {
            Path parent = outputPath.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            return true;
        } catch (Exception e) {
            System.err.println("Unable to prepare output path '" + outputPath + "': " + e.getMessage());
            return false;
        }
    }
}
