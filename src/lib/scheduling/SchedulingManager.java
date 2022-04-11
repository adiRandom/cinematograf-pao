package lib.scheduling;

import com.sun.xml.internal.bind.v2.TODO;
import models.movie.Movie;
import models.room.Room;
import repository.MovieRepository;
import repository.RoomRepository;

import java.util.Date;

public class SchedulingManager {
    private RoomRepository roomRepository;
    private MovieRepository movieRepository;
    private static SchedulingManager instance = null;
    private static int nextId = 0;

    private SchedulingManager() {
        this.roomRepository = RoomRepository.getInstance();
        this.movieRepository = MovieRepository.getInstance();
    }

    public static SchedulingManager getInstance() {
        if (instance == null) {
            instance = new SchedulingManager();
        }

        return instance;
    }

    /**
     * Schedule a movie
     *
     * @param movie
     * @return The scheduling id
     */
    public int scheduleMovie(Movie movie) {
        // TODO: Implement
    }

    /**
     * Schedule a movie at a date
     *
     * @param movie
     * @param date
     * @param exact If true, it will schedule at exact time and day. If false, only the day will be taken into account
     * @return The scheduling id
     */
    public int scheduleMovie(Movie movie, Date date, boolean exact) {
        // TODO: Implement
    }

    public void cancelRun(int schedulingId) {
        // TODO: Implement
    }

    public void listRunsForDay(Date date) {
        //TODO: Implement
    }

    public Room getRoomForRun(int schedulingId) {
        //TODO: Implement
    }

    public void stopBooking(int schedulingId) {
        //TODO: Implement
    }

    public void bookMovie(int movieId, Date date){
        //TODO: Implement
    }

    public void moveTicket(int movieId, Date currentDate, Date newDate){
        //TODO: Implement
    }

    public void cancelBooking(int movieId, Date date){
        //TODO: Implement
    }

    public void buyTicket(int movieId, Date date){
        //TODO: Implement
    }

    /**
     * Play the movie at that date
     * We'll consider the movie finishes immediately and we'll just clear the room and remove its scheduling
     * @param movieId
     * @param date
     */
    public void startMovie(int movieId, Date date){
        //TODO: Implement
    }

}
