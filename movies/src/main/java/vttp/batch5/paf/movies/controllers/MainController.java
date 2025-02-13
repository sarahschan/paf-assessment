package vttp.batch5.paf.movies.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.sf.jasperreports.engine.JRException;
import vttp.batch5.paf.movies.services.MovieService;

@RestController
@RequestMapping("")
public class MainController {


    @Autowired
    MovieService movieService;

    @Value("${my.name}")
    private String name;

    @Value("${my.batch}")
    private String batch;


    @GetMapping("/api/summary")
    public ResponseEntity<String> getTopDirectors(@RequestParam int count){

        List<String> results = movieService.getProlificDirectors(count);
        
        return ResponseEntity.ok().body(results.toString());
    }

   

    @GetMapping("/api/summary/pdf")
    public ResponseEntity<Resource> generateReport(@RequestParam int count) throws IOException, JRException {

        List<String> results = movieService.getProlificDirectors(count);

        movieService.generatePDFReport(name, batch, results);

        // Load the generated PDF file
        File pdfFile = new File("../data/directorReport.pdf"); // Ensure the path is correct

        if (!pdfFile.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Convert PDF file to ByteArrayResource
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(pdfFile.toPath()));

        // Set response headers
        return ResponseEntity.ok().body(resource);
    }
}