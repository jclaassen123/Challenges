import java.io.*;
import java.util.*;

/**
 * MontanaLicenseLookup allows users to look up Montana counties
 * by license plate prefix or by city name. Unknown cities are
 * added dynamically to the same CSV file for persistence.
 *
 * <p>The CSV file format is:</p>
 * <pre>
 * County,County Seat,License Plate Prefix,City
 * Anaconda-Deer Lodge,Anaconda,30,Anaconda
 * Beaverhead,Dillon,18,Dillon
 * ...
 * </pre>
 */
public class MontanaLicenseLookup {

    /** CSV file storing county, prefix, and city data */
    private static final String CSV_FILE = "MontanaCounties.csv";

    /**
     * Program entry point. Provides a menu for users to choose
     * prefix lookup, city lookup, or exit.
     *
     * @param args Command-line arguments (unused)
     */
    public static void main(String[] args) {

        // Map of license plate prefix → county information
        Map<Integer, CountyInfo> prefixToCounty = new HashMap<>();

        // Map of city name (lowercase) → license plate prefix
        Map<String, Integer> cityToPrefix = new HashMap<>();

        // Load all county and city data from CSV
        if (!loadData(prefixToCounty, cityToPrefix)) {
            System.out.println("Failed to load data.");
            return;
        }

        Scanner scanner = new Scanner(System.in);

        // Main program loop
        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("1 - Lookup by license plate prefix");
            System.out.println("2 - Lookup by city");
            System.out.println("3 - Exit");

            String choice = scanner.nextLine().trim();

            if (choice.equals("3")) {
                System.out.println("Goodbye!");
                break;
            }

            switch (choice) {
                case "1" -> lookupByPrefix(scanner, prefixToCounty);
                case "2" -> lookupByCity(scanner, prefixToCounty, cityToPrefix);
                default -> System.out.println("Invalid option.");
            }
        }

        scanner.close();
    }

    /**
     * Prompts the user to enter a license plate prefix and displays
     * the associated county information.
     *
     * @param scanner Scanner for user input
     * @param prefixToCounty Map of prefix → county info
     */
    private static void lookupByPrefix(
            Scanner scanner,
            Map<Integer, CountyInfo> prefixToCounty) {

        System.out.print("Enter license plate prefix: ");
        String input = scanner.nextLine().trim();

        // Validate input
        if (!input.matches("\\d+")) {
            System.out.println("Invalid prefix.");
            return;
        }

        int prefix = Integer.parseInt(input);
        CountyInfo info = prefixToCounty.get(prefix);

        if (info == null) {
            System.out.println("No county found.");
        } else {
            System.out.println(info);
        }
    }

    /**
     * Prompts the user to enter a city name and displays the
     * associated county information. If the city does not exist,
     * the user is prompted for its license plate prefix, and the
     * new city is added to the CSV file.
     *
     * @param scanner Scanner for user input
     * @param prefixToCounty Map of prefix → county info
     * @param cityToPrefix Map of city name → prefix
     */
    private static void lookupByCity(
            Scanner scanner,
            Map<Integer, CountyInfo> prefixToCounty,
            Map<String, Integer> cityToPrefix) {

        System.out.print("Enter city name: ");
        String cityInput = scanner.nextLine().trim();
        String cityKey = cityInput.toLowerCase();

        // City already exists in memory
        if (cityToPrefix.containsKey(cityKey)) {
            int prefix = cityToPrefix.get(cityKey);
            System.out.println(prefixToCounty.get(prefix));
            return;
        }

        // City not found: prompt for prefix
        System.out.println("City not found.");
        System.out.print("Enter license plate prefix: ");
        String prefixInput = scanner.nextLine().trim();

        // Validate input
        if (!prefixInput.matches("\\d+")) {
            System.out.println("Invalid prefix. City not saved.");
            return;
        }

        int prefix = Integer.parseInt(prefixInput);
        CountyInfo county = prefixToCounty.get(prefix);

        if (county == null) {
            System.out.println("No county exists with that prefix.");
            return;
        }

        // Append new city to CSV and update in-memory map
        appendCity(cityInput, county, prefix);
        cityToPrefix.put(cityKey, prefix);

        System.out.println("City added successfully.");
    }

    /**
     * Loads county and city data from the CSV file into memory.
     * Each row must have four columns: County, County Seat, Prefix, City.
     *
     * @param prefixToCounty Map to populate prefix → county info
     * @param cityToPrefix Map to populate city name → prefix
     * @return true if data loaded successfully; false otherwise
     */
    private static boolean loadData(
            Map<Integer, CountyInfo> prefixToCounty,
            Map<String, Integer> cityToPrefix) {

        boolean loaded = false;

        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            br.readLine(); // Skip header
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length < 4) continue;

                String county = parts[0].trim();
                String seat = parts[1].trim();
                int prefix = Integer.parseInt(parts[2].trim());
                String city = parts[3].trim();

                // Only add prefix → county once
                prefixToCounty.putIfAbsent(prefix, new CountyInfo(county, seat));

                // Map city → prefix
                cityToPrefix.put(city.toLowerCase(), prefix);

                loaded = true;
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV file.");
        }

        return loaded;
    }

    /**
     * Appends a new city to the CSV file in the format:
     * County,County Seat,Prefix,City
     *
     * @param city City name to add
     * @param county CountyInfo object containing county and seat
     * @param prefix License plate prefix
     */
    private static void appendCity(String city, CountyInfo county, int prefix) {
        try (FileWriter fw = new FileWriter(CSV_FILE, true)) {
            fw.write(county.getName() + "," +
                    county.getSeat() + "," +
                    prefix + "," +
                    city + "\n");
        } catch (IOException e) {
            System.out.println("Error writing to CSV.");
        }
    }
}
