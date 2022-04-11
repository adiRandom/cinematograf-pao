package repository;

import models.movie.Movie;

public class MovieRepository extends BaseRepository<Movie> {

    private static MovieRepository instance = null;

    private MovieRepository() {
        super();
    }

    public static MovieRepository getInstance() {
        if (instance == null) {
            instance = new MovieRepository();
        }

        return instance;
    }

}
