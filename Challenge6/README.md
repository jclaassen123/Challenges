# Contest Statistics Analyzer (Java)

This project reads a contest results CSV in the same format as `2015.csv` and generates a text report with the descriptive statistics required for Challenge 6.

The solution is implemented in Java and does not require any external dependencies beyond a JDK.

## Output

The program writes `ContestAnalysis.txt`, which contains:

- The average number of teams entered per institution
- An ordered list of institutions by number of teams entered
- A list of institutions with at least one team earning an Outstanding ranking
- A list of US teams earning Meritorious ranking or better

## Input Format

Expected header columns (case-insensitive):

- `Institution`
- `Team Number`
- `City`
- `State/Province`
- `Country`
- `Advisor`
- `Problem`
- `Ranking`

The parser supports standard CSV quoting, including commas inside quoted fields.

## Files Included

- `src/ContestStatisticsGenerator.java` - program entry point
- `src/ContestStatsProcessor.java` - CSV loading and report generation logic
- `src/ContestEntry.java` - contest row model
- `src/CsvUtils.java` - CSV parsing helper
- `2015.csv` - sample input file for the challenge

## Build

```bash
javac -d out src/*.java
```

## Run

Default input and output:

```bash
java -cp out ContestStatisticsGenerator
```

- Reads: `2015.csv`
- Writes: `ContestAnalysis.txt`

Custom input and output:

```bash
java -cp out ContestStatisticsGenerator <input_csv_path> <output_report_path>
```

Example:

```bash
java -cp out ContestStatisticsGenerator 2016.csv reports/analysis.txt
```

## Notes

- The solution is intended to work with other yearly contest CSV files that use the same column structure.
- The code handles BOM text in the first header column.
- The 2015 dataset labels top-ranked teams as `Outstanding Winner`, so the program treats rankings that start with `Outstanding` as Outstanding results.
- `ContestAnalysis.txt` is generated when the program runs and is ignored by git.
