package vttp.batch5.paf.movies.repositories;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import vttp.batch5.paf.movies.models.Movie;

@Repository
public class MySQLMovieRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static final String SQL_CHECK_COUNT = "select count(*) from imdb";
    public static final String SQL_CHECK_MOVIE = "select count(*) from imdb where imdb_id = ?";
    public static final String SQL_INSERT_MOVIE = "insert into imdb (imdb_id, vote_average, vote_count, release_date, revenue, budget, runtime) values (?, ?, ?, ?, ?, ?, ?)";

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
  
  // TODO: Task 3


}
