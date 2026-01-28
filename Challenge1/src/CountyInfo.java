/**
 * Represents information about a Montana county.
 * Stores the county's name and its county seat.
 * This class is used as a simple data container for license plate lookups.
 */
public class CountyInfo {

    /** The name of the county. */
    private String name;

    /** The county seat (city) of the county. */
    private String seat;

    /**
     * Constructs a CountyInfo object with the given name and county seat.
     *
     * @param name The name of the county.
     * @param seat The county seat city.
     */
    public CountyInfo(String name, String seat) {
        this.name = name;
        this.seat = seat;
    }

    /**
     * Returns the name of the county.
     *
     * @return The county name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the county seat (city).
     *
     * @return The county seat.
     */
    public String getSeat() {
        return seat;
    }

    /**
     * Returns a string representation of the county information.
     *
     * @return A string in the format "County Name (Seat: County Seat)".
     */
    @Override
    public String toString() {
        return name + " (Seat: " + seat + ")";
    }
}
