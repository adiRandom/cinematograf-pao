package lib.scheduling.utils;

import java.util.Date;

public class MovieScheduling {
    private final int movieId;
    private final Date startTime;
    private final Date endTime;
    private final int id;
    private final int roomId;
    private static int nextSchedulingId = 0;

    public MovieScheduling(int movieId, Date startTime, Date endTime, int roomId) {
        this.movieId = movieId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.id = nextSchedulingId++;
        this.roomId = roomId;
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

    public int getRoomId() {
        return roomId;
    }
}
