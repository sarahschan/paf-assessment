package vttp.batch5.paf.movies.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
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


    // db.imdb.aggregate([
    //     { $match: { "directors": { $ne: "" } } },
    //     { $unwind: "$directors" },  // If "directors" is an array
    //     { $group: {
    //         _id: "$directors",
    //         count: { $sum: 1 },
    //         movies: { $push: "$_id" }
    //     }},
    //     { $sort: { count: -1 } },
    //     { $limit: 5 }
    // ])
    public List<Document> findTopDirectors(int limit){

        Criteria criteria = Criteria.where("directors").ne("");
        MatchOperation matchCriteria = Aggregation.match(criteria);

        GroupOperation groupOperation = Aggregation.group("directors")
            .count().as("count")
            .push("$_id").as("movies");

        SortOperation sortOperation = Aggregation.sort(Sort.Direction.DESC, "count");

        LimitOperation takeTopN = Aggregation.limit(limit);

        Aggregation pipeline = Aggregation.newAggregation(matchCriteria, groupOperation, sortOperation, takeTopN);

        return mongoTemplate.aggregate(pipeline, "imdb", Document.class).getMappedResults();
    }


}
