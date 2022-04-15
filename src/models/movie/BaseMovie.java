package models.movie;


import java.util.LinkedList;

public abstract class BaseMovie implements Movie {

    private final String title;
    private final LinkedList<String> starActors;
    private final int rating;
    private final String studio;
    private final int duration;
    private final int id;

    private static int nextId = 0;

    public BaseMovie(String title, LinkedList<String> starActors, int rating, String studio, int duration) throws IllegalArgumentException {

        if (rating > 10 || rating < 0) {
            throw new IllegalArgumentException("Rating needs to be an integer between 0 and 10");
        }

        this.title = title;
        this.starActors = starActors;
        this.rating = rating;
        this.studio = studio;
        this.duration = duration;
        this.id = BaseMovie.nextId++;
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

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        StringBuilder rep = new StringBuilder(this.title + " with a score of " + String.valueOf(this.rating) + " starring ");
        for (String actor : this.starActors) {
            rep.append(actor).append(", ");
        }
        return rep.toString();
    }
}
