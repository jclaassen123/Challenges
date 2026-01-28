import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * MontanaLicenseLookup allows users to look up Montana counties
 * based on the numeric prefix of a license plate.
 *
 * <p>The program reads county data from a CSV file, stores it in memory,
 * and allows repeated queries for county information. Users can choose
 * to display the county name, county seat, or both.</p>
 */
public class MontanaLicenseLookup {

    public static void main(String[] args) {
        // Map storing license plate prefix -> CountyInfo
        Map<Integer, CountyInfo> counties = new HashMap<>();

        // Attempt to load CSV data
        if (!loadCountyData("MontanaCounties.csv", counties)) {
            System.out.println("Error loading county data. Please check the CSV file.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Montana License Plate Lookup!");

        while (true) {
            System.out.println("\nEnter a license plate prefix (or type 'exit' to quit):");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Goodbye!");
                break;
            }

            // Validate numeric input
            if (!input.matches("\\d+")) {
                System.out.println("Invalid input! Please enter a numeric prefix.");
                continue;
            }

            int prefix = Integer.parseInt(input);

            CountyInfo info = counties.get(prefix);
            if (info == null) {
                System.out.println("No county found for prefix " + prefix);
                continue;
            }

            // Ask user which information to display
            System.out.println("What information would you like to see?");
            System.out.println("1 - County Name");
            System.out.println("2 - County Seat");
            System.out.println("3 - Both");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> System.out.println("County: " + info.getName());
                case "2" -> System.out.println("County Seat: " + info.getSeat());
                case "3" -> System.out.println(info);
                default -> System.out.println("Invalid choice, showing both by default: " + info);
            }
        }

        scanner.close();
    }

    /**
     * Loads county data from a CSV file into the provided map.
     * Malformed lines or missing data are skipped safely.
     *
     * @param filePath Path to the CSV file.
     * @param map      Map to store prefix -> CountyInfo.
     * @return true if at least one county was loaded, false otherwise.
     */
    private static boolean loadCountyData(String filePath, Map<Integer, CountyInfo> map) {
        boolean loadedAny = false;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            // Skip header
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length < 3) {
                    // Skip lines with missing columns
                    continue;
                }

                String countyName = parts[0].trim();
                String seat = parts[1].trim();
                String prefixStr = parts[2].trim();

                // Ensure prefix is numeric
                if (!prefixStr.matches("\\d+")) {
                    continue;
                }

                int prefix = Integer.parseInt(prefixStr);

                map.put(prefix, new CountyInfo(countyName, seat));
                loadedAny = true;
            }
        } catch (Exception e) {
            System.out.println("Warning: Unable to read CSV file: " + e.getMessage());
        }

        return loadedAny;
    }
}
