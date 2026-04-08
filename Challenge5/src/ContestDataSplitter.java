import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Entry point for splitting contest result CSV data into normalized output files.
 */
public class ContestDataSplitter {
    /**
     * Runs the CSV split process.
     *
     * @param args optional command line arguments:
     *             args[0] = input CSV path (default: 2015.csv),
     *             args[1] = output directory (default: current directory)
     */
    public static void main(String[] args) {
        Path inputPath = args.length > 0 ? Paths.get(args[0]) : Paths.get("2015.csv");
        Path outputDir = args.length > 1 ? Paths.get(args[1]) : Paths.get(".");

        if (!ensureOutputDirectory(outputDir)) {
            System.exit(1);
            return;
        }

        ContestDataProcessor processor = new ContestDataProcessor();
        boolean success = processor.processContestData(inputPath, outputDir);
        if (success) {
            System.out.println("Generated: " + outputDir.resolve("Institutions.csv"));
            System.out.println("Generated: " + outputDir.resolve("Teams.csv"));
        } else {
            System.exit(1);
        }
    }

    /**
     * Ensures the destination directory exists before processing begins.
     *
     * @param outputDir output directory path
     * @return {@code true} when directory exists or was created, otherwise {@code false}
     */
    private static boolean ensureOutputDirectory(Path outputDir) {
        try {
            Files.createDirectories(outputDir);
            return true;
        } catch (Exception e) {
            System.err.println("Unable to prepare output directory '" + outputDir + "': " + e.getMessage());
            return false;
        }
    }
}
