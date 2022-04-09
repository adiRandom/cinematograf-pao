package models.movie;

import java.util.List;

public interface Movie {
    String getTitle();

    List<String> getStarActors();

    int getRating();

    String getStudio();

    int getType();
}
