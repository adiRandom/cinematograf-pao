package models.movie;

import java.util.LinkedList;

public class Movie3D extends BaseMovie {

    private final Movie3DType type;

    public Movie3D(String title, LinkedList<String> starActors, int rating, String studio, Movie3DType type) {
        super(title, starActors, rating, studio);
        this.type = type;
    }

    @Override
    public int getType() {
        return this.type.ordinal();
    }
}
