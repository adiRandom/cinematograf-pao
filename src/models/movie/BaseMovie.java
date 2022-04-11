package models.movie;

import com.sun.javaws.exceptions.InvalidArgumentException;

import java.util.LinkedList;

public abstract class BaseMovie implements Movie {

    private final String title;
    private final LinkedList<String> starActors;
    private final int rating;
    private final String studio;

    public BaseMovie(String title, LinkedList<String> starActors, int rating, String studio) throws InvalidArgumentException {

        if (rating > 10 || rating < 0) {
            throw new InvalidArgumentException(new String[]{"Rating needs to be an integer between 0 and 10"});
        }

        this.title = title;
        this.starActors = starActors;
        this.rating = rating;
        this.studio = studio;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public LinkedList<String> getStarActors() {
        return starActors;
    }

    @Override
    public int getRating() {
        return rating;
    }

    @Override
    public String getStudio() {
        return studio;
    }
}
