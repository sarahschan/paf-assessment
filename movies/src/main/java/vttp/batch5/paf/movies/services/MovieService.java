package vttp.batch5.paf.movies.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
