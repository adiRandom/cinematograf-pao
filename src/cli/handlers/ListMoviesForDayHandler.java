package cli.handlers;

import lib.scheduling.utils.MovieScheduling;
import models.movie.Movie;
import utils.Pair;

import java.util.Date;
import java.util.HashSet;

public class ListMoviesForDayHandler extends AbstractCommandHandler{
    @Override
    public void handleCommand() {
        Date date = this.getDateFromInput(false).getFirst();
        HashSet<Pair<Movie, MovieScheduling>> allRuns = schedulingManager.getRunsForDay(date);
        CommandHandlerUtils.printMovieRuns(allRuns);
    }
}
