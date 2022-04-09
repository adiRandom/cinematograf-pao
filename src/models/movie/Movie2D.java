package models.movie;

import java.util.LinkedList;

public class Movie2D extends BaseMovie {

    private final Movie2DType type;

    public Movie2D(String title, LinkedList<String> starActors, int rating, String studio, Movie2DType type) {
        super(title, starActors, rating, studio);
        this.type = type;
    }

    @Override
    public int getType() {
        return this.type.ordinal();
    }
}
