package cli.handlers;

import cli.utils.BookingDetails;
import lib.scheduling.utils.MovieScheduling;
import models.movie.Movie;
import models.room.Room;
import utils.Pair;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.LinkedList;

public class CommandHandlerUtils {

    static public void printMovieRuns(HashSet<Pair<Movie, MovieScheduling>> movieRuns) {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        for (Pair<Movie, MovieScheduling> run : movieRuns) {
            Movie movie = run.getFirst();
            MovieScheduling scheduling = run.getSecond();
            System.out.println(scheduling.getId() + ". " + movie.getTitle() + " at " + format.format(scheduling.getStartTime()));
        }
        System.out.println("------");
    }


}
