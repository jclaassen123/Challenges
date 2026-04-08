# Contest Data Splitter (Java)

This project reads a contest results CSV (same column format as `2015.csv`) and generates two normalized output files:

- `Institutions.csv`
- `Teams.csv`

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

The parser supports standard CSV quoting (including commas inside quoted fields).

## Output Files

### `Institutions.csv`
Columns:

- `Institution ID`
- `Institution Name`
- `City`
- `State/Province`
- `Country`

One row per distinct institution record (based on normalized Institution + City + State/Province + Country).

### `Teams.csv`
Columns:

- `Team Number`
- `Advisor`
- `Problem`
- `Ranking`
- `Institution ID`

One row per distinct team number, linked back to `Institutions.csv` by `Institution ID`.

## Build

```bash
javac -d out src/*.java
```

## Run

Default input/output:

```bash
java -cp out ContestDataSplitter
```

- Reads: `2015.csv`
- Writes: `./Institutions.csv`, `./Teams.csv`

Custom input and output directory:

```bash
java -cp out ContestDataSplitter <input_csv_path> <output_directory>
```

Example:

```bash
java -cp out ContestDataSplitter 2016.csv output
```

## Notes

- The program is designed to work with other years as long as the CSV uses the same column structure.
- It handles malformed BOM text in the first header (for files that start with `ï»¿Institution`).
