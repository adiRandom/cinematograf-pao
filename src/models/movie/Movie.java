package models.movie;

import java.util.List;

public interface Movie {
    String getTitle();

    List<String> getStarActors();

    int getRating();

    String getStudio();

    String getType();

    /**
     * Get duration of the movie in minutes
     */
    int getDuration();

    int getId();

    boolean is3D();
}
