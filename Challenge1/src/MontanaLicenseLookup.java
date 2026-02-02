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
 *
 * Author: Jace
 */
public class MontanaLicenseLookup {

    private Map<Integer, CountyInfo> counties = new HashMap<>();

    public static void main(String[] args) {
        MontanaLicenseLookup app = new MontanaLicenseLookup();

        // Attempt to load CSV data
        if (!app.loadCountyData("MontanaCounties.csv")) {
            System.out.println("Error loading county data. Please check the CSV file.");
            return;
        }

        app.run();
    }

    /**
     * Runs the license lookup program with user interaction.
     */
    public void run() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Welcome to Montana License Plate Lookup!");

            // Ask user once for the type of information to display
            String infoChoice = askInfoChoice(scanner);

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

                // Display the chosen information
                switch (infoChoice) {
                    case "1" -> System.out.println("County: " + info.getName());
                    case "2" -> System.out.println("County Seat: " + info.getSeat());
                    case "3" -> System.out.println(info);
                }
            }
        } // Scanner automatically closed here
    }

    /**
     * Prompts the user to choose what type of information to display.
     *
     * @param scanner Scanner for user input.
     * @return The user's choice ("1", "2", or "3").
     */
    private String askInfoChoice(Scanner scanner) {
        String choice;
        while (true) {
            System.out.println("What information would you like to see?");
            System.out.println("1 - County Name");
            System.out.println("2 - County Seat");
            System.out.println("3 - Both");
            choice = scanner.nextLine().trim();
            if (choice.matches("[1-3]")) break;
            System.out.println("Invalid choice. Please enter 1, 2, or 3.");
        }
        return choice;
    }

    /**
     * Loads county data from a CSV file into the counties map.
     * Malformed lines or missing data are skipped safely.
     *
     * @param filePath Path to the CSV file.
     * @return true if at least one county was loaded, false otherwise.
     */
    public boolean loadCountyData(String filePath) {
        boolean loadedAny = false;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            // Skip header
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length < 3) continue;

                String countyName = parts[0].trim();
                String seat = parts[1].trim();
                String prefixStr = parts[2].trim();

                if (!prefixStr.matches("\\d+")) continue;

                int prefix = Integer.parseInt(prefixStr);
                counties.put(prefix, new CountyInfo(countyName, seat));
                loadedAny = true;
            }
        } catch (Exception e) {
            System.out.println("Warning: Unable to read CSV file: " + e.getMessage());
        }

        return loadedAny;
    }
}
