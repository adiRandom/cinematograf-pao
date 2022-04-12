package lib.scheduling;

import lib.scheduling.utils.MovieScheduling;
import models.movie.*;
import models.room.Room;
import models.room.RoomType;
import repository.MovieRepository;
import repository.RoomRepository;

import javax.naming.OperationNotSupportedException;
import java.util.*;

public class SchedulingManager {
    private RoomRepository roomRepository;
    private MovieRepository movieRepository;
    private static SchedulingManager instance = null;
    private static int nextRoomId = 0;

    private final HashMap<Integer, ArrayList<MovieScheduling>> movieSchedulings;

    private SchedulingManager() {
        this.roomRepository = RoomRepository.getInstance();
        this.movieRepository = MovieRepository.getInstance();
        this.movieSchedulings = new HashMap<>();
    }

    public static SchedulingManager getInstance() {
        if (instance == null) {
            instance = new SchedulingManager();
        }

        return instance;
    }

    /**
     * Create a scheduling for a movie at a date in a room
     *
     * @param index The index in the scheduling  list where this new scheduling should be inserted to keep it sorted
     * @return the scheduling id
     */

    private int createScheduling(Movie movie, Date date, Room room, int index) {

        MovieScheduling movieScheduling = new MovieScheduling(movie.getId(), date,
                new Date(date.getTime() + (long) movie.getDuration() * 60 * 1000));

        ArrayList<MovieScheduling> bookingList = this.movieSchedulings.get(room.getId());
        if (bookingList.size() <= index) {
            bookingList.add(movieScheduling);
        } else {
            bookingList.add(index, movieScheduling);
        }
        return movieScheduling.getId();
    }

    /**
     * Find the first available spot for that duration and book a room
     *
     * @param roomType The type of room
     * @param movie    The movie to be scheduled
     * @return The scheduling id
     */
    private int scheduleMovieUtil(RoomType roomType, Movie movie) {
        // get all the rooms for this type
        List<Room> compatibleRooms = roomRepository.whereAll(room -> room.getType() == roomType);
        // Hold the best fitting room
        Date soonestDateAvailable = null;
        Room chosenRoom = null;
        int newBookingIndex = 0;

        // Go through all rooms and find the one that that can book this movie sooner
        for (Room room : compatibleRooms) {
            ArrayList<MovieScheduling> movieSchedulings = this.movieSchedulings.get(room.getId());

            // Find the first available spot long enough for this movie
            for (int i = 0; i < movieSchedulings.size(); i++) {
                Date availableDate = null;

                if (i == movieSchedulings.size() - 1) {
                    // Nothing after this booking, we can use this spot
                    availableDate = movieSchedulings.get(i).getEndTime();
                } else {
                    //See if we can squeeze in this duration
                    long spotSize = movieSchedulings.get(i + 1).getStartTime().getTime() -
                            movieSchedulings.get(i).getEndTime().getTime();

                    long spotSizeInMinutes = spotSize / 1000 / 60;
                    if (spotSizeInMinutes >= movie.getDuration()) {
                        availableDate = movieSchedulings.get(i).getEndTime();
                    }
                }

                if (soonestDateAvailable == null ||
                        (availableDate != null && availableDate.before(soonestDateAvailable))) {
                    soonestDateAvailable = availableDate;
                    // Remember where we should insert the scheduling if this is the soonest available date
                    newBookingIndex = i + 1;
                    chosenRoom = room;
                    break;
                }
            }

        }
        return createScheduling(movie, soonestDateAvailable, chosenRoom, newBookingIndex);

    }

    /**
     * Find a room that can hold this movie at a given date
     *
     * @param roomType The type of room
     * @param movie    The movie to be scheduled
     * @param exact    If true, it will schedule at exact time and day. If false, only the day will be taken into account
     * @return The scheduling id
     */
    private int scheduleMovieUtil(RoomType roomType, Movie movie, Date date, boolean exact) throws OperationNotSupportedException {
        // get all the rooms for this type
        List<Room> compatibleRooms = roomRepository.whereAll(room -> room.getType() == roomType);
        // Hold the best fitting room
        Date soonestDateAvailable = null;
        Room chosenRoom = null;
        int newBookingIndex = 0;

        // Go through all rooms and find the one that that can book this movie sooner
        if (exact) {
            long startTime = date.getTime();
            // Find a room free at that exact date
            for (Room room : compatibleRooms) {
                ArrayList<MovieScheduling> movieSchedulings = this.movieSchedulings.get(room.getId());

                // Find the first available spot long enough for this movie
                for (int i = 0; i < movieSchedulings.size(); i++) {
                    if (i == movieSchedulings.size() - 1) {
                        // Nothing after this booking, we could use this spot
                        long currentEndTime = movieSchedulings.get(i).getEndTime().getTime();


                        if (currentEndTime <= startTime) {
                            soonestDateAvailable = date;
                            chosenRoom = room;
                            newBookingIndex = i + 1;
                            break;
                        }

                    } else {

                        // See if date is between this scheduling and the next
                        long currentEndTime = movieSchedulings.get(i).getEndTime().getTime();
                        long nextStartTime = movieSchedulings.get(i + 1).getStartTime().getTime();
                        //See if we can squeeze in this duration
                        long spotSize = movieSchedulings.get(i + 1).getStartTime().getTime() -
                                movieSchedulings.get(i).getEndTime().getTime();

                        long spotSizeInMinutes = spotSize / 1000 / 60;

                        if (currentEndTime <= startTime && nextStartTime >= startTime && spotSizeInMinutes >= movie.getDuration()) {
                            soonestDateAvailable = date;
                            chosenRoom = room;
                            newBookingIndex = i + 1;
                            break;
                        }

                    }
                }
            }


        } else {
            // Get the permitted dates
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            long permittedStartDate = calendar.getTimeInMillis();

            calendar.set(Calendar.HOUR, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);

            long permittedEndDate = calendar.getTimeInMillis();

            for (Room room : compatibleRooms) {
                ArrayList<MovieScheduling> movieSchedulings = this.movieSchedulings.get(room.getId());

                // Find the first available spot long enough for this movie
                for (int i = 0; i < movieSchedulings.size(); i++) {
                    Date availableDate = null;
                    MovieScheduling movieScheduling = movieSchedulings.get(i);


                    // Check if we went past the allowed interval
                    if (movieScheduling.getEndTime().getTime() > permittedEndDate) {
                        // From this point onwards, all available spots for this room will be past the allowed interval
                        break;
                    }

                    if (movieScheduling.getEndTime().getTime() >= permittedStartDate) {

                        // A possible spot will be in the allowed interval
                        if (i == movieSchedulings.size() - 1) {
                            // Nothing after this booking, we can use this spot
                            availableDate = movieScheduling.getEndTime();
                        } else {
                            //See if we can squeeze in this duration
                            long spotSize = movieSchedulings.get(i + 1).getStartTime().getTime() -
                                    movieScheduling.getEndTime().getTime();

                            long spotSizeInMinutes = spotSize / 1000 / 60;
                            if (spotSizeInMinutes >= movie.getDuration()) {
                                availableDate = movieSchedulings.get(i).getEndTime();
                            }
                        }
                    }


                    if (soonestDateAvailable == null ||
                            (availableDate != null && availableDate.before(soonestDateAvailable))) {
                        soonestDateAvailable = availableDate;
                        // Remember where we should insert the scheduling if this is the soonest available date
                        newBookingIndex = i + 1;
                        chosenRoom = room;
                        break;
                    }
                }
            }


        }
        if (soonestDateAvailable != null) {
            // We can book this movie at that time
            return createScheduling(movie, soonestDateAvailable, chosenRoom, newBookingIndex);
        } else {
            throw new OperationNotSupportedException("No room available for this movie at this time");
        }

    }

    /**
     * Schedule a movie
     *
     * @param movie The movie ti be scheduled
     * @return The scheduling id
     */
    public int scheduleMovie(Movie movie) throws IllegalArgumentException {
        try {
            Movie3D movie3D = (Movie3D) movie;
            switch (Movie3DType.valueOf(movie3D.getType())) {
                case REGULAR: {
                    return scheduleMovieUtil(RoomType.REGULAR_3D, movie);
                }
                case IMAX: {
                    return scheduleMovieUtil(RoomType.IMAX, movie);
                }
                case MOVIE_4DX: {
                    return scheduleMovieUtil(RoomType.ROOM_4DX_3D, movie);
                }
                default: {
                    // Unknown type of movie, throw error to indicate fail
                    throw new IllegalArgumentException("Unknown type of movie");
                }
            }
        } catch (ClassCastException e) {
            // Don't do anything in this case
            // It's a 2D movie, handled below
        }

        try {
            Movie2D movie2D = (Movie2D) movie;
            switch (Movie2DType.valueOf(movie2D.getType())) {
                case REGULAR: {
                    return scheduleMovieUtil(RoomType.REGULAR_2D, movie);
                }
                case MOVIE_4DX: {
                    return scheduleMovieUtil(RoomType.ROOM_4DX_3D, movie);
                }
                default: {
                    // Unknown type of movie, throw error to indicate fail
                    throw new IllegalArgumentException("Unknown type of movie");
                }
            }
        } catch (ClassCastException e) {
            // Unknown type of movie, throw error to indicate fail
            throw new IllegalArgumentException("Unknown type of movie");
        }
    }

    /**
     * Schedule a movie at a date
     *
     * @param movie
     * @param date
     * @param exact If true, it will schedule at exact time and day. If false, only the day will be taken into account
     * @return The scheduling id
     */
    public int scheduleMovie(Movie movie, Date date, boolean exact) throws IllegalArgumentException, OperationNotSupportedException {
        try {
            Movie3D movie3D = (Movie3D) movie;
            switch (Movie3DType.valueOf(movie3D.getType())) {
                case REGULAR: {
                    return scheduleMovieUtil(RoomType.REGULAR_3D, movie, date, exact);
                }
                case IMAX: {
                    return scheduleMovieUtil(RoomType.IMAX, movie, date, exact);
                }
                case MOVIE_4DX: {
                    return scheduleMovieUtil(RoomType.ROOM_4DX_3D, movie, date, exact);
                }
                default: {
                    // Unknown type of movie, throw error to indicate fail
                    throw new IllegalArgumentException("Unknown type of movie");
                }
            }
        } catch (ClassCastException e) {
            // Don't do anything in this case
            // It's a 2D movie, handled below
        }

        try {
            Movie2D movie2D = (Movie2D) movie;
            switch (Movie2DType.valueOf(movie2D.getType())) {
                case REGULAR: {
                    return scheduleMovieUtil(RoomType.REGULAR_2D, movie, date, exact);
                }
                case MOVIE_4DX: {
                    return scheduleMovieUtil(RoomType.ROOM_4DX_3D, movie, date, exact);
                }
                default: {
                    // Unknown type of movie, throw error to indicate fail
                    throw new IllegalArgumentException("Unknown type of movie");
                }
            }
        } catch (ClassCastException e) {
            // Unknown type of movie, throw error to indicate fail
            throw new IllegalArgumentException("Unknown type of movie");
        }
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

    public void bookMovie(int movieId, Date date) {
        //TODO: Implement
    }

    public void moveTicket(int movieId, Date currentDate, Date newDate) {
        //TODO: Implement
    }

    public void cancelBooking(int movieId, Date date) {
        //TODO: Implement
    }

    public void buyTicket(int movieId, Date date) {
        //TODO: Implement
    }

    /**
     * Play the movie at that date
     * We'll consider the movie finishes immediately and we'll just clear the room and remove its scheduling
     *
     * @param movieId
     * @param date
     */
    public void startMovie(int movieId, Date date) {
        //TODO: Implement
    }

    public void addRoom() {

    }

}
