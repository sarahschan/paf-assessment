package vttp.batch5.paf.movies;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MoviesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoviesApplication.class, args);

		System.out.println("Current working directory: " + new File(".").getAbsolutePath());

	}

}
