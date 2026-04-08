/**
 * Immutable team row model used for Teams.csv output.
 */
public class Team {
    private final String teamNumber;
    private final String advisor;
    private final String problem;
    private final String ranking;
    private final String institutionId;

    /**
     * Creates one team output record.
     *
     * @param teamNumber team registration number
     * @param advisor faculty advisor
     * @param problem problem choice
     * @param ranking final ranking designation
     * @param institutionId linked institution identifier
     */
    public Team(String teamNumber, String advisor, String problem, String ranking, String institutionId) {
        this.teamNumber = teamNumber;
        this.advisor = advisor;
        this.problem = problem;
        this.ranking = ranking;
        this.institutionId = institutionId;
    }

    /**
     * @return team registration number
     */
    public String getTeamNumber() {
        return teamNumber;
    }

    /**
     * @return faculty advisor
     */
    public String getAdvisor() {
        return advisor;
    }

    /**
     * @return problem choice
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

    /**
     * @return linked institution identifier
     */
    public String getInstitutionId() {
        return institutionId;
    }
}
