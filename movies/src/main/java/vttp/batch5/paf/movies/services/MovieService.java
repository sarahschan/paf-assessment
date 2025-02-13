package vttp.batch5.paf.movies.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vttp.batch5.paf.movies.models.Movie;
import vttp.batch5.paf.movies.repositories.MongoMovieRepository;
import vttp.batch5.paf.movies.repositories.MySQLMovieRepository;

@Service
public class MovieService {

    @Autowired
    MongoMovieRepository mongoMovieRepository;

    @Autowired
    MySQLMovieRepository mySQLMovieRepository;

    public boolean allDataLoaded() {

      if (mongoMovieRepository.getDocumentCount() > 0 && mySQLMovieRepository.checkDocumentCount() > 0) {
        return true;
      }

      return false;

    }

    @Transactional
    public void saveToDatabases(List<Movie> movies) {
      
      List<Movie> moviesToInsert = new ArrayList<>();

      for (int i=0; i < movies.size(); i++){
        moviesToInsert.add(movies.get(i));

        if ((moviesToInsert.size() == 25)) {
          try {
            mySQLMovieRepository.batchInsertMovies(moviesToInsert);
            mongoMovieRepository.batchInsertMovies(moviesToInsert);
            moviesToInsert.clear();
          } catch (Exception e) {
            
            System.out.println("ERROR on batch insert movies");

            // log error to MongoDB
            List<String> imdbIds = new ArrayList<>();
            for (Movie m : moviesToInsert) {
              imdbIds.add(m.getImdbId());
            }

            mongoMovieRepository.logError(imdbIds, e.getMessage());

            System.out.println("ERROR logged in mongoDB");

            continue;
          }
        }
      }

    }


  // TODO: Task 3
  // You may change the signature of this method by passing any number of
  // parameters
  // and returning any type
  public void getProlificDirectors() {
  }

  // TODO: Task 4
  // You may change the signature of this method by passing any number of
  // parameters
  // and returning any type
  public void generatePDFReport() {

  }

}
