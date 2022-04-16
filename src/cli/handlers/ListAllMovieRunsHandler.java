package cli.handlers;

import lib.scheduling.utils.MovieScheduling;
import models.movie.Movie;
import utils.Pair;

import java.util.HashSet;


public class ListAllMovieRunsHandler extends AbstractCommandHandler{
    @Override
    public void handleCommand() {
        HashSet<Pair<Movie, MovieScheduling>> allRuns = schedulingManager.getRuns();
        CommandHandlerUtils.printMovieRuns(allRuns);
    }
}
