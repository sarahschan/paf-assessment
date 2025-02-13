package vttp.batch5.paf.movies.bootstrap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.json.Json;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import vttp.batch5.paf.movies.models.Movie;
import vttp.batch5.paf.movies.repositories.MongoMovieRepository;
import vttp.batch5.paf.movies.services.MovieService;

@Component
public class Dataloader {

    @Value("${movies.file.path}")
    private String zipFilePath;

    @Autowired
    private MovieService movieService;

    @Autowired
    MongoMovieRepository mongoMovieRepository;

    @PostConstruct
    public void loadData() throws ParseException {

        System.out.println("Configured zip file path: " + zipFilePath);
        System.out.println("Resolved absolute path: " + new File(zipFilePath).getAbsolutePath());


        if (!movieService.allDataLoaded()) {

            File file = new File(zipFilePath);
            if (!file.exists()) {
                System.err.println("ERROR: File does not exist at " + file.getAbsolutePath());
            }

            List<Movie> movies = readFile();

            movieService.saveToDatabases(movies);

            System.out.println("Movies loaded to databases");

        } else {
            System.out.println("Database already populated, skipping bootstrapping");
        }

    }

    public List<Movie> readFile() throws ParseException  {
        
        List<Movie> movies = new ArrayList<>();

        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath))) {
            
            ZipEntry entry;

            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.getName().endsWith(".json")) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(zipInputStream, StandardCharsets.UTF_8))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            JsonObject jsonObject = Json.createReader(new StringReader(line)).readObject();

                            String dateString = jsonObject.getString("release_date");
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            Date releasedDate = sdf.parse(dateString);
                            Date before2018 = sdf.parse("2018-01-01");

                            if (releasedDate.before(before2018)){
                                continue;
                            }

                            Movie movie = new Movie();
                            movie.setImdbId(jsonObject.getString("imdb_id"));

                            JsonValue voteAverageValue = jsonObject.get("vote_average");
                            if (voteAverageValue instanceof JsonNumber) {
                                movie.setVoteAverage((float) jsonObject.getJsonNumber("vote_average").doubleValue());
                            }

                            movie.setVoteCount(jsonObject.getInt("vote_count"));
                            movie.setReleaseDate(new java.sql.Date(releasedDate.getTime()));

                            JsonValue revenueValue = jsonObject.get("revenue");
                            if (revenueValue instanceof JsonNumber) {
                                movie.setRevenue((float) jsonObject.getJsonNumber("revenue").doubleValue());
                            }

                            JsonValue budgetValue = jsonObject.get("budget");
                            if (budgetValue instanceof JsonNumber) {
                                movie.setBudget((float) jsonObject.getJsonNumber("budget").doubleValue());
                            }

                            movie.setRuntime(jsonObject.getInt("runtime"));
                            movie.setTitle(jsonObject.getString("title"));
                            movie.setDirectors(jsonObject.getString("director"));
                            movie.setOverview(jsonObject.getString("overview"));
                            movie.setTagline(jsonObject.getString("tagline"));
                            movie.setGenres(jsonObject.getString("genres"));

                            JsonValue imdbRatingValue = jsonObject.get("imdb_rating");
                            if (imdbRatingValue instanceof JsonNumber) {
                                movie.setImdbRating(((JsonNumber) imdbRatingValue).intValue());
                            }

                            JsonValue imdbVotesValue = jsonObject.get("imdb_votes");
                            if (imdbVotesValue instanceof JsonNumber) {
                                movie.setImdbVotes(((JsonNumber) imdbVotesValue).intValue());
                            }

                            movies.add(movie);

                        }
                    }
                }

            }

        } catch (IOException e) {
                e.printStackTrace();
        }

        return movies;

    }
}