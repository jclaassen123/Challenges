# Montana License Lookup

Author: Jace

## Overview

MontanaLicenseLookup is a simple Java program that lets users look up Montana counties by license plate prefix. It reads county data from a CSV file and allows the user to display the county name, county seat, or both.

---

## Requirements

- Java 21
- IntelliJ IDEA (or any Java IDE)
- CSV file: MontanaCounties.csv in the project root directory

CSV Columns:
- County Name
- County Seat
- Prefix

---

## How to Run in IntelliJ

1. Open the project in IntelliJ.
2. Ensure MontanaCounties.csv is in the project root (same folder as src).
3. Open MontanaLicenseLookup.java.
4. Click the green Run button next to the main method.
5. Follow the prompts:
   - Choose the type of information to display.
   - Enter numeric license plate prefixes.
   - Type exit to quit.

---

## How to Run from the Terminal

1. Open a terminal and navigate to the project root folder.
2. Compile the program by typing:

javac MontanaLicenseLookup.java CountyInfo.java

3. Run the program by typing:

java MontanaLicenseLookup

4. Follow the prompts:
   - Choose the type of information to display.
   - Enter numeric license plate prefixes.
   - Type exit to quit.

---

## Design Notes

The program loads all county data into memory from the CSV file for fast lookup. It uses object-oriented design so that the lookup functionality is reusable, and it asks the user once for the type of information to display to make the interface simpler.
