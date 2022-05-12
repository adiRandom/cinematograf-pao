package cli.handlers;

import cli.utils.BookingDetails;
import lib.scheduling.utils.MovieScheduling;
import models.movie.Movie;
import models.room.Room;
import services.CSVService;
import utils.Pair;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class CommandHandlerUtils {

    static public void printMovieRuns(HashSet<Pair<Movie, MovieScheduling>> movieRuns, String reportName) {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        for (Pair<Movie, MovieScheduling> run : movieRuns) {
            Movie movie = run.getFirst();
            MovieScheduling scheduling = run.getSecond();
            System.out.println(scheduling.getId() + ". " + movie.getTitle() + " at " + format.format(scheduling.getStartTime()));
        }

        CSVService csvService = new CSVService<MovieScheduling>(reportName);
        try {
            csvService.write(movieRuns.stream().map(el -> el.getSecond()).collect(Collectors.toList()), new String[]{"room", "idService"});
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("------");
    }


}
