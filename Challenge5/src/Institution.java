/**
 * Immutable institution row model used for Institutions.csv output.
 */
public class Institution {
    private final String institutionId;
    private final String institutionName;
    private final String city;
    private final String stateProvince;
    private final String country;

    /**
     * Creates one institution output record.
     *
     * @param institutionId generated institution identifier
     * @param institutionName institution name
     * @param city city value
     * @param stateProvince state/province value
     * @param country country value
     */
    public Institution(String institutionId, String institutionName, String city, String stateProvince, String country) {
        this.institutionId = institutionId;
        this.institutionName = institutionName;
        this.city = city;
        this.stateProvince = stateProvince;
        this.country = country;
    }

    /**
     * @return generated institution identifier
     */
    public String getInstitutionId() {
        return institutionId;
    }

    /**
     * @return institution name
     */
    public String getInstitutionName() {
        return institutionName;
    }

    /**
     * @return city value
     */
    public String getCity() {
        return city;
    }

    /**
     * @return state/province value
     */
    public String getStateProvince() {
        return stateProvince;
    }

    /**
     * @return country value
     */
    public String getCountry() {
        return country;
    }
}
