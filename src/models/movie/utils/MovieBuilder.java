package models.movie.utils;

import models.movie.*;

import java.util.LinkedList;

public class MovieBuilder {
    private String title;
    private LinkedList<String> starActors;
    private int rating;
    private String studio;
    private boolean is3D;
    private String type;

    public MovieBuilder() {
        this.clear();
    }

    public MovieBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public MovieBuilder withStarActor(String actor) {
        this.starActors.push(actor);
        return this;
    }

    public MovieBuilder withRating(int rating) {
        this.rating = rating;
        return this;
    }

    public MovieBuilder withStudio(String studio) {
        this.studio = studio;
        return this;
    }

    public MovieBuilder is3D(boolean is3D) {
        this.is3D = is3D;
        return this;
    }

    public MovieBuilder withType(String type) {
        this.type = type;
        return this;
    }

    public BaseMovie build() {
        if (is3D) {
            return new Movie3D(this.title, this.starActors, this.rating, this.studio, Movie3DType.valueOf(this.type));
        } else {
            return new Movie2D(this.title, this.starActors, this.rating, this.studio, Movie2DType.valueOf(this.type));
        }
    }

    public void clear(){
        this.title = "";
        this.starActors = new LinkedList<>();
        this.rating = 0;
        this.studio = "";
        this.is3D = false;
        this.type = "";
    }


}
