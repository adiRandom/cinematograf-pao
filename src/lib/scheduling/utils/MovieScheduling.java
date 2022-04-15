package lib.scheduling.utils;

import models.room.Room;
import models.room.RoomView;

import java.io.Serializable;
import java.util.Date;

public class MovieScheduling implements Serializable {
    private final int movieId;
    private final Date startTime;
    private final Date endTime;
    private final int id;
    private final Room room;
    private static int nextSchedulingId = 0;
    private boolean canBook;

    public MovieScheduling(int movieId, Date startTime, Date endTime, RoomView roomView) {
        this.movieId = movieId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.id = nextSchedulingId++;
        this.room = new Room(roomView);
        this.canBook = true;
    }

    public int getMovieId() {
        return movieId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public int getId() {
        return id;
    }

    public Room getRoom() {
        return this.room;
    }

    public boolean getCanBook() {
        return canBook;
    }

    public void setCanBook(boolean canBook) {
        this.canBook = canBook;
    }
}
