package vttp.batch5.paf.movies.repositories;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import jakarta.json.JsonArray;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import vttp.batch5.paf.movies.models.DirectorStats;
import vttp.batch5.paf.movies.models.Movie;

@Repository
public class MySQLMovieRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static final String SQL_CHECK_COUNT = "select count(*) from imdb";
    public static final String SQL_CHECK_MOVIE = "select count(*) from imdb where imdb_id = ?";
    public static final String SQL_INSERT_MOVIE = "insert into imdb (imdb_id, vote_average, vote_count, release_date, revenue, budget, runtime) values (?, ?, ?, ?, ?, ?, ?)";
    public static final String SQL_GET_BUDGET_REVENUE = "select revenue, budget from imdb where imdb_id = ?";

    public int checkDocumentCount(){
        
        int count = jdbcTemplate.queryForObject(SQL_CHECK_COUNT, Integer.class);

        return count;
    }
    

    public void batchInsertMovies(List<Movie> movies) {
      
      jdbcTemplate.batchUpdate(SQL_INSERT_MOVIE, new BatchPreparedStatementSetter() {

        @Override
        public void setValues(PreparedStatement ps, int i) throws SQLException {
            Movie movie = movies.get(i);
            ps.setString(1, movie.getImdbId());
            ps.setFloat(2, movie.getVoteAverage());
            ps.setInt(3, movie.getVoteCount());
            ps.setDate(4, movie.getReleaseDate());
            ps.setFloat(5, movie.getRevenue());
            ps.setFloat(6, movie.getBudget());
            ps.setInt(7, movie.getRuntime());

        }

        @Override
        public int getBatchSize() {
          return movies.size();
        }
        
      });

    }
  

    public DirectorStats getDirectorStats(String directorName, int numMovies, JsonArray movieIds) {

      float totalRevenue = 0;
      float totalBudget = 0;

      for (JsonValue id : movieIds) {
          String idString = ((JsonString) id).getString();
        SqlRowSet rs = jdbcTemplate.queryForRowSet(SQL_GET_BUDGET_REVENUE, idString);
          while (rs.next()){
            float revenue = rs.getFloat("revenue");
            float budget = rs.getFloat("budget");
            totalRevenue = totalRevenue + revenue;
            totalBudget  = totalBudget + budget;
          }
      }

      DirectorStats directorStats = new DirectorStats();
        directorStats.setDirectorName(directorName);
        directorStats.setNumMovies(numMovies);
        directorStats.setTotalBudget(totalBudget);
        directorStats.setTotalRevenue(totalRevenue);
        directorStats.setProfitLoss(totalRevenue - totalBudget);

      return directorStats;
    }


}
