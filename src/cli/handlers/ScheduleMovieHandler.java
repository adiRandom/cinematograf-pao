package cli.handlers;

import models.movie.Movie;
import utils.Pair;

import javax.naming.OperationNotSupportedException;
import java.util.Date;
import java.util.List;

public class ScheduleMovieHandler extends AbstractCommandHandler {
    @Override
    public void handleCommand() throws OperationNotSupportedException {
        List<Movie> movies = movieRepository.getAll();
        for (Movie movie : movies) {

            System.out.println(movie.getId() + ". " + movie.getTitle());
        }
        Movie pickedMovie = null;
        while (pickedMovie == null) {
            int movieId = this.getIntFromInput("Pick a movie by its id");
            pickedMovie = movieRepository.getItemWithId(movieId);
        }
        boolean withDate = this.getBooleanFromInput("Do you want to schedule it at a particular date? Yes or No", "Yes", "No");
        if (!withDate) {
            int schedulingId = schedulingManager.scheduleMovie(pickedMovie);
            System.out.println("Scheduling successful. Id: " + Integer.toString(schedulingId));
        } else {
            Pair<Date, Boolean> dateFromInput = this.getDateFromInput(true);
            int schedulingId = schedulingManager.scheduleMovie(pickedMovie,
                    dateFromInput.getFirst(),
                    dateFromInput.getSecond());
            System.out.println("Scheduling successful. Id: " + schedulingId);
        }
    }
}
