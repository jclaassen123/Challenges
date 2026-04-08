/**
 * Immutable contest entry model for one team row from the source CSV.
 */
public class ContestEntry {
    private final String institution;
    private final String teamNumber;
    private final String city;
    private final String stateProvince;
    private final String country;
    private final String advisor;
    private final String problem;
    private final String ranking;

    /**
     * Creates one parsed contest entry.
     *
     * @param institution institution name from the source CSV
     * @param teamNumber team registration number
     * @param city institution city
     * @param stateProvince institution state or province
     * @param country institution country
     * @param advisor faculty advisor
     * @param problem selected contest problem
     * @param ranking final ranking designation
     */
    public ContestEntry(
            String institution,
            String teamNumber,
            String city,
            String stateProvince,
            String country,
            String advisor,
            String problem,
            String ranking) {
        this.institution = institution;
        this.teamNumber = teamNumber;
        this.city = city;
        this.stateProvince = stateProvince;
        this.country = country;
        this.advisor = advisor;
        this.problem = problem;
        this.ranking = ranking;
    }

    /**
     * @return institution name from the source row
     */
    public String getInstitution() {
        return institution;
    }

    /**
     * @return team registration number
     */
    public String getTeamNumber() {
        return teamNumber;
    }

    /**
     * @return institution city
     */
    public String getCity() {
        return city;
    }

    /**
     * @return institution state or province
     */
    public String getStateProvince() {
        return stateProvince;
    }

    /**
     * @return institution country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @return faculty advisor name
     */
    public String getAdvisor() {
        return advisor;
    }

    /**
     * @return selected contest problem
     */
    public String getProblem() {
        return problem;
    }

    /**
     * @return final ranking designation
     */
    public String getRanking() {
        return ranking;
    }
}
