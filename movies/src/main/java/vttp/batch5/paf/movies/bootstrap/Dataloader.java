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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import vttp.batch5.paf.movies.models.Movie;
import vttp.batch5.paf.movies.repositories.MongoMovieRepository;
import vttp.batch5.paf.movies.repositories.MySQLMovieRepository;
import vttp.batch5.paf.movies.services.MovieService;

@Component
public class Dataloader {

    @Value("${movies.file.path}")
    private String zipFilePath;

    @Autowired
    private MovieService movieService;

    @PostConstruct
    public void loadData() throws ParseException {
        System.out.println("Configured zip file path: " + zipFilePath);
        System.out.println("Resolved absolute path: " + new File(zipFilePath).getAbsolutePath());

        File file = new File(zipFilePath);
            if (!file.exists()) {
            System.err.println("ERROR: File does not exist at " + file.getAbsolutePath());
        }

        List<Movie> movies = readFile();

        for (Movie m : movies) {
            System.out.println(m);
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
                                movie.setVoteAverage((float) jsonObject.getJsonNumber("vote_average").doubleValue());
                                movie.setVoteCount(jsonObject.getInt("vote_count"));
                                movie.setReleaseDate(new java.sql.Date(releasedDate.getTime()));
                                movie.setRevenue((float) jsonObject.getJsonNumber("revenue").doubleValue());
                                movie.setBudget((float) jsonObject.getJsonNumber("budget").doubleValue());
                                movie.setRuntime(jsonObject.getInt("runtime"));

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



    // @PostConstruct
    // public void loadData() {
    //     if (isDataLoaded()) {
    //         System.out.println("Data already loaded. Skipping bootstrap process.");
    //         return;
    //     }

    //     List<Movie> movies = readMoviesFromZip(zipFilePath);
    //     movieRepository1.saveAll(movies);
    //     movieRepository2.saveAll(movies);

    //     System.out.println("Movies successfully loaded into databases.");
    // }



    // private Movie processMovie(JsonObject jsonObject) {
    //     return new Movie(
    //         jsonObject.getInt("id", 0),
    //         jsonObject.getString("title", ""),
    //         jsonObject.getString("release_date", "").startsWith("2018") ? 2018 : 
    //         Integer.parseInt(jsonObject.getString("release_date", "0").split("-")[0]),
    //         jsonObject.getInt("rating", 0),
    //         jsonObject.getBoolean("is_featured", false)
    //     );
    // }
