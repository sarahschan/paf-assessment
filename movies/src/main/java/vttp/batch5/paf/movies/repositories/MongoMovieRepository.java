package vttp.batch5.paf.movies.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import vttp.batch5.paf.movies.models.Movie;

@Repository
public class MongoMovieRepository {

    @Autowired
    MongoTemplate mongoTemplate;

    public long getDocumentCount() {
        return mongoTemplate.count(new Query(), "imdb");
    }



    
    // db.errors.insert({
    //     "imdb_ids": <array of ids>,
    //     "error": <error message from exception>,
    //     "timestamp": <timestamp of exception occuring>
    //  })
    public void batchInsertMovies(List<Movie> moviesToInsert) {

        for (Movie m : moviesToInsert){
            
            Document toInsert = new Document();
                toInsert.put("_id", m.getImdbId());
                toInsert.put("title", m.getTitle());
                toInsert.put("directors", m.getDirectors());
                toInsert.put("overview", m.getOverview());
                toInsert.put("tagline", m.getTagline());
                toInsert.put("genres", m.getGenres());
                toInsert.put("imdb_rating", m.getImdbRating());
                toInsert.put("imdb_votes", m.getImdbVotes());

            mongoTemplate.insert(toInsert, "imdb");
        }

 }

 
    //  db.errors.insert({
    //     "imdb_ids": <array of ids>,
    //     "error": <error message from exception>,
    //     "timestamp": <timestamp of exception occuring>
    //  })
    public void logError(List<String> imdbIds, String message) {

        Document toInsert = new Document();
            toInsert.put("imdb_ids", imdbIds);
            toInsert.put("error", message);
            toInsert.put("timestamp", LocalDateTime.now());
        
        mongoTemplate.insert(toInsert, "errors");

    }

 // TODO: Task 3
 // Write the native Mongo query you implement in the method in the comments
 //
 //    native MongoDB query here
 //


}
