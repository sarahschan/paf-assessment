package vttp.batch5.paf.movies.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MySQLMovieRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static final String SQL_CHECK_COUNT = "select count(*) from imdb";

    public int checkDocumentCount(){
        
        int count = jdbcTemplate.queryForObject(SQL_CHECK_COUNT, Integer.class);

        return count;
    }
    


  // TODO: Task 2.3
  // You can add any number of parameters and return any type from the method
  public void batchInsertMovies() {
   
  }
  
  // TODO: Task 3


}
