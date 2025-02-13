package vttp.batch5.paf.movies.models;

public class DirectorStats {
    
    private String directorName;
    private int numMovies;
    private float totalRevenue;
    private float totalBudget;
    private float profitLoss;


    public DirectorStats() {
    }


    @Override
    public String toString() {
        return "DirectorStats [directorName=" + directorName + ", numMovies=" + numMovies + ", totalRevenue="
                + totalRevenue + ", totalBudget=" + totalBudget + ", profitLoss=" + profitLoss + "]";
    }

    
    public String getDirectorName() {
        return directorName;
    }
    public void setDirectorName(String directorName) {
        this.directorName = directorName;
    }
    public int getNumMovies() {
        return numMovies;
    }
    public void setNumMovies(int numMovies) {
        this.numMovies = numMovies;
    }
    public float getTotalRevenue() {
        return totalRevenue;
    }
    public void setTotalRevenue(float totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
    public float getTotalBudget() {
        return totalBudget;
    }
    public void setTotalBudget(float totalBudget) {
        this.totalBudget = totalBudget;
    }
    public float getProfitLoss() {
        return profitLoss;
    }
    public void setProfitLoss(float profitLoss) {
        this.profitLoss = profitLoss;
    }

    
}
