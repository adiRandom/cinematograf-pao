package models.movie;

import java.io.Serializable;
import java.util.List;

public interface Movie extends Serializable {
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
