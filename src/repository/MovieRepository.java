package repository;

import models.movie.Movie;

public class MovieRepository extends BaseRepository<Movie> {

    private static MovieRepository instance = null;

    private MovieRepository() {
        super("movie_repo");
    }


    public static MovieRepository getInstance() {
        if (instance == null) {
            instance = new MovieRepository();
        }

        return instance;
    }

}
