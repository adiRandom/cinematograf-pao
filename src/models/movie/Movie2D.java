package models.movie;


import java.util.LinkedList;

public class Movie2D extends BaseMovie {

    private final Movie2DType type;

    public Movie2D(String title, LinkedList<String> starActors, int rating, String studio, int duration, Movie2DType type) throws IllegalArgumentException {
        super(title, starActors, rating, studio, duration);
        this.type = type;
    }

    @Override
    public String getType() {
        return this.type.name();
    }

    @Override
    public boolean is3D() {
        return false;
    }
}
