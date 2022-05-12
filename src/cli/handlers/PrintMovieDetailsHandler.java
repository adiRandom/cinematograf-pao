package cli.handlers;

import models.movie.Movie;
import services.CSVService;

import javax.naming.OperationNotSupportedException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class PrintMovieDetailsHandler extends AbstractCommandHandler {
    @Override
    public void handleCommand() throws OperationNotSupportedException {
        List<Movie> movies = this.movieRepository.getAll();
        for (Movie movie : movies) {
            System.out.println(movie.getId() + ". " + movie.getTitle());
        }
        System.out.println("------");
        int movieId = this.getIntFromInput("Pick a movie");

        Movie movie = this.movieRepository.getItemWithId(movieId);
        if (movie == null) {
            throw new IllegalArgumentException("No movie with that id");
        }

        System.out.println(movie);
        CSVService csvService = new CSVService<Movie>(movie.getTitle() + ".csv");
        try {
            csvService.write(Arrays.asList(movie), new String[]{"starActors","idService"});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
