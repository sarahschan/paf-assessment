package vttp.batch5.paf.movies.services;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.json.data.JsonDataSource;
import net.sf.jasperreports.pdf.JRPdfExporter;
import net.sf.jasperreports.pdf.SimplePdfExporterConfiguration;
import net.sf.jasperreports.pdf.SimplePdfReportConfiguration;
import vttp.batch5.paf.movies.models.DirectorStats;
import vttp.batch5.paf.movies.models.Movie;
import vttp.batch5.paf.movies.repositories.MongoMovieRepository;
import vttp.batch5.paf.movies.repositories.MySQLMovieRepository;

@Service
public class MovieService {

    @Autowired
    MongoMovieRepository mongoMovieRepository;

    @Autowired
    MySQLMovieRepository mySQLMovieRepository;

    @Value("${jasper.path}")
    private String jasperFilePath;

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



    public List<String> getProlificDirectors(int limit) {

        List<Document> getDirectorNames = mongoMovieRepository.findTopDirectors(limit);

        List<DirectorStats> directorStats = new ArrayList<>();

        for (Document d : getDirectorNames){

            JsonObject jsonObject = Json.createReader(new StringReader(d.toJson())).readObject();
            String directorName = jsonObject.getString("_id");
            int numMovies = jsonObject.getInt("count");

            JsonArray movidIds = jsonObject.getJsonArray("movies");
            
            DirectorStats directorStat = mySQLMovieRepository.getDirectorStats(directorName, numMovies, movidIds);
            directorStats.add(directorStat);
        }

        List<String> directorStatsJson = new ArrayList<>();

        for (DirectorStats d : directorStats) {
            JsonObject jsonObject = Json.createObjectBuilder()
              .add("director_name", d.getDirectorName())
              .add("movies_count", d.getNumMovies())
              .add("total_revenue", d.getTotalRevenue())
              .add("total_budget", d.getTotalBudget())
              .build();

            directorStatsJson.add(jsonObject.toString());
        }

        return directorStatsJson;

    }


public void generatePDFReport(String name, String batch, List<String> results) throws JRException, IOException {

      ClassPathResource jasperFile = new ClassPathResource(jasperFilePath);

        if (!jasperFile.exists()) {
          throw new FileNotFoundException("Jasper file not found in classpath: " + jasperFilePath);
      }

    InputStream reportStream = jasperFile.getInputStream();

        if (reportStream == null) {
            throw new FileNotFoundException("Jasper file not found in /data directory");
        }

        JsonObject reportData = Json.createObjectBuilder()
                .add("name", name)
                .add("batch", batch)
                .build();

        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        for (String s : results) {
            JsonObject jsonObject = Json.createReader(new StringReader(s)).readObject();
            jsonArrayBuilder.add(jsonObject);
        }
        JsonArray directorData = jsonArrayBuilder.build();


        JsonDataSource reportDS = new JsonDataSource(new ByteArrayInputStream(reportData.toString().getBytes()));
        JsonDataSource directorsDS = new JsonDataSource(new ByteArrayInputStream(directorData.toString().getBytes()));


        Map<String, Object> params = new HashMap<>();
        params.put("DIRECTOR_TABLE_DATASET", directorsDS);


        JasperPrint print = JasperFillManager.fillReport(reportStream, params, reportDS);


        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setExporterInput(new SimpleExporterInput(print));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput("directorReport.pdf"));


        SimplePdfReportConfiguration reportConfig = new SimplePdfReportConfiguration();
        reportConfig.setSizePageToContent(true);
        reportConfig.setForceLineBreakPolicy(false);

        SimplePdfExporterConfiguration exportConfig = new SimplePdfExporterConfiguration();
        exportConfig.setMetadataAuthor("Sarah");
        exportConfig.setEncrypted(true);
        exportConfig.setAllowedPermissionsHint("PRINTING");

        exporter.setConfiguration(reportConfig);
        exporter.setConfiguration(exportConfig);

        exporter.exportReport();

        System.out.println("PDF Report generated successfully: directorReport.pdf");
    }
}
