/**
 * Represents information about a Montana county.
 * Stores the county name and its county seat.
 *
 * This class is used when looking up county data
 * based on a license plate prefix.
 */
public class CountyInfo {

    /** The name of the county */
    private String name;

    /** The county seat (main city) */
    private String seat;

    /**
     * Constructs a CountyInfo object.
     *
     * @param name Name of the county
     * @param seat County seat city
     */
    public CountyInfo(String name, String seat) {
        this.name = name;
        this.seat = seat;
    }

    /**
     * Gets the county name.
     *
     * @return county name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the county seat.
     *
     * @return county seat
     */
    public String getSeat() {
        return seat;
    }

    /**
     * Returns a formatted string describing the county.
     *
     * @return formatted county description
     */
    @Override
    public String toString() {
        return name + " (Seat: " + seat + ")";
    }
}
