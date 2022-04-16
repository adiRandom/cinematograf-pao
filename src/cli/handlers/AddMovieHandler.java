package cli.handlers;

import models.movie.Movie;
import models.movie.utils.MovieBuilder;

public class AddMovieHandler extends AbstractCommandHandler{
    @Override
    public void handleCommand() {
        boolean is3D;
        MovieBuilder movieBuilder = new MovieBuilder();

        movieBuilder.withTitle(this.getStringFromInput("What is the title of the movie"));

        String actor = "";

        while (!actor.equals("Done")) {
            actor = this.getStringFromInput("Who's starring. Enter \"Done\" when finished");
            if (!actor.equals("Done")) {
                movieBuilder.withStarActor(actor);
            }
        }

        movieBuilder.withRating(this.getIntFromInput("Give it a rating from 0 to 10"));
        movieBuilder.withDuration(this.getIntFromInput("How long is the movie (in minutes)?"));
        movieBuilder.withStudio(this.getStringFromInput("What studio made the movie?"));

        is3D = this.getBooleanFromInput("Is the movie 3D? Yes or No", "Yes", "No");
        movieBuilder.is3D(is3D);

        if (is3D) {
            movieBuilder.withType(this.getStringFromInput("What type of movie is this? REGULAR, IMAX or MOVIE_4DX"));
        } else {
            movieBuilder.withType(this.getStringFromInput("What type of movie is this? REGULAR or MOVIE_4DX"));
        }
        Movie movie = movieBuilder.build();
        this.movieRepository.insertItem(movie.getId(), movie);
        System.out.println("Movie added!");
    }
}
