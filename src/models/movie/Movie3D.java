package models.movie;

import com.sun.javaws.exceptions.InvalidArgumentException;

import java.util.LinkedList;

public class Movie3D extends BaseMovie {

    private final Movie3DType type;

    public Movie3D(String title, LinkedList<String> starActors, int rating, String studio, int duration, Movie3DType type) throws InvalidArgumentException {
        super(title, starActors, rating, studio,duration);
        this.type = type;
    }

    @Override
    public String getType() {
        return this.type.name();
    }

    @Override
    public boolean is3D() {
        return true;
    }
}
