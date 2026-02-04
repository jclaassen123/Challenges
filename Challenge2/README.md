# Montana License Lookup

Author: Jace Claassen

## Overview

MontanaLicenseLookup is a Java program that allows users to:

- Lookup county information by license plate prefix.
- Lookup license plate prefix by city name.
- If the user enters a city that is not already in the data, the program prompts for its license plate prefix and automatically adds it to the CSV file for future lookups.

## Requirements

- Java 21
- IntelliJ IDEA (or any Java IDE)
- CSV file: MontanaCounties.csv in the project root directory
  - Columns: County, County Seat, License Plate Prefix, City

## How to Run in IntelliJ

1. Open the project in IntelliJ.
2. Ensure MontanaCounties.csv is in the project root (same folder as src).
3. Open MontanaLicenseLookup.java.
4. Click the green Run button next to the main method.
5. Follow the prompts:
   - Choose lookup mode (license plate prefix or city).
   - Enter numeric license plate prefixes or city names.
   - Type exit to quit the program.

## How to Run from the Terminal

1. Open a terminal and navigate to the project root folder.
2. Compile the program:

   javac MontanaLicenseLookup.java CountyInfo.java

3. Run the program:

   java MontanaLicenseLookup

4. Follow the on-screen prompts (choose lookup mode, enter prefix/city, exit with 3).

## Design Notes

The program loads all county and city data from the CSV file into memory at startup for fast lookup. Unknown cities are appended immediately to the CSV file to ensure persistence. City names are handled case-insensitively to make the interface user-friendly.
