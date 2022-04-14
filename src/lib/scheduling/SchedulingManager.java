package lib.scheduling;

import lib.scheduling.utils.MovieScheduling;
import models.booking.Booking;
import models.booking.Booking2D;
import models.booking.Booking3D;
import models.movie.*;
import models.room.*;
import repository.MovieRepository;
import repository.RoomViewRepository;
import utils.Pair;

import javax.naming.OperationNotSupportedException;
import java.awt.print.Book;
import java.util.*;
import java.util.stream.Collectors;

public class SchedulingManager {
    private RoomViewRepository roomViewRepository;
    private MovieRepository movieRepository;
    private static SchedulingManager instance = null;
    private static int nextRoomId = 0;

    /**
     * All scehdulings for each room
     */
    private final HashMap<Integer, ArrayList<MovieScheduling>> movieSchedulings;
    /**
     * A mapping between scheduling id and Scheduling
     */
    private final HashMap<Integer, MovieScheduling> allSchedulings;

    /**
     * Map booking ids to scheduling ids
     */
    private final HashMap<Integer, Integer> bookingSchedulingMapping;

    private SchedulingManager() {
        this.roomViewRepository = RoomViewRepository.getInstance();
        this.movieRepository = MovieRepository.getInstance();
        this.movieSchedulings = new HashMap<>();
        this.allSchedulings = new HashMap<>();
        this.bookingSchedulingMapping = new HashMap<>();
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

    private int createScheduling(Movie movie, Date date, RoomView roomView, int index) {

        MovieScheduling movieScheduling = new MovieScheduling(movie.getId(), date,
                // calculate the end date
                new Date(date.getTime() + (long) movie.getDuration() * 60 * 1000),
                roomView);

        ArrayList<MovieScheduling> bookingList = this.movieSchedulings.get(roomView.getId());
        if (bookingList.size() <= index) {
            bookingList.add(movieScheduling);
        } else {
            bookingList.add(index, movieScheduling);
        }

        this.allSchedulings.put(movieScheduling.getMovieId(), movieScheduling);
        return movieScheduling.getId();
    }

    /**
     * Find the first available spot for that duration and book a room
     *
     * @param roomType The type of room
     * @param movie    The movie to be scheduled
     * @return The scheduling id
     */
    //TODO: Also make a check before first scheduled and now
    private int scheduleMovieUtil(RoomType roomType, Movie movie) throws IllegalArgumentException {
        // get all the rooms for this type
        List<RoomView> compatibleRooms = roomViewRepository.whereAll(room -> room.getType() == roomType);
        if (compatibleRooms.size() == 0) {
            throw new IllegalArgumentException("No room for this type of movie");
        }


        // Hold the best fitting room
        Date soonestDateAvailable = null;
        RoomView chosenRoomView = null;
        int newBookingIndex = 0;

        // Go through all rooms and find the one that that can book this movie sooner
        for (RoomView room : compatibleRooms) {
            ArrayList<MovieScheduling> movieSchedulings = this.movieSchedulings.get(room.getId());

            // First movie inserted for this room
            // Nothing can be sooner than the current time so set the current date as the soonest available
            if (movieSchedulings.size() == 0) {
                soonestDateAvailable = new Date();
                chosenRoomView = room;
                break;
            }


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
                    chosenRoomView = room;
                    break;
                }
            }

        }


        return createScheduling(movie, soonestDateAvailable, chosenRoomView, newBookingIndex);

    }

    private Pair<Long, Long> getDateIntervalForDay(Date date) {
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
        return new Pair<>(permittedStartDate, permittedEndDate);
    }

    /**
     * Find a room that can hold this movie at a given date
     *
     * @param roomType The type of room
     * @param movie    The movie to be scheduled
     * @param exact    If true, it will schedule at exact time and day. If false, only the day will be taken into account
     * @return The scheduling id
     */
    //TODO: Also make a check before first scheduled and now
    private int scheduleMovieUtil(RoomType roomType, Movie movie, Date date, boolean exact) throws OperationNotSupportedException, IllegalArgumentException {
        // get all the rooms for this type
        List<RoomView> compatibleRoomViews = roomViewRepository.whereAll(room -> room.getType() == roomType);
        if (compatibleRoomViews.size() == 0) {
            throw new IllegalArgumentException("No room for this type of movie");
        }

        // Hold the best fitting room
        Date soonestDateAvailable = null;
        RoomView chosenRoomView = null;
        int newBookingIndex = 0;

        // Go through all rooms and find the one that that can book this movie sooner
        if (exact) {
            long startTime = date.getTime();
            // Find a room free at that exact date
            for (RoomView room : compatibleRoomViews) {
                ArrayList<MovieScheduling> movieSchedulings = this.movieSchedulings.get(room.getId());

                // First movie inserted for this room
                // The exact date specified is available
                if (movieSchedulings.size() == 0) {
                    soonestDateAvailable = date;
                    chosenRoomView = room;
                    break;
                }

                // Find the first available spot long enough for this movie
                for (int i = 0; i < movieSchedulings.size(); i++) {
                    if (i == movieSchedulings.size() - 1) {
                        // Nothing after this booking, we could use this spot
                        long currentEndTime = movieSchedulings.get(i).getEndTime().getTime();


                        if (currentEndTime <= startTime) {
                            soonestDateAvailable = date;
                            chosenRoomView = room;
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
                            chosenRoomView = room;
                            newBookingIndex = i + 1;
                            break;
                        }

                    }
                }
            }


        } else {
            // Get the permitted dates
            Pair<Long, Long> permittedDate = getDateIntervalForDay(date);
            long permittedStartDate = permittedDate.getFirst();
            long permittedEndDate = permittedDate.getSecond();

            for (RoomView room : compatibleRoomViews) {
                ArrayList<MovieScheduling> movieSchedulings = this.movieSchedulings.get(room.getId());
                // First movie inserted for this room
                // Set the start time as the date for the scheduling
                if (movieSchedulings.size() == 0) {
                    soonestDateAvailable = new Date(permittedStartDate);
                    chosenRoomView = room;
                    break;
                }

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
                        chosenRoomView = room;
                        break;
                    }
                }
            }


        }
        if (soonestDateAvailable != null) {
            // We can book this movie at that time
            return createScheduling(movie, soonestDateAvailable, chosenRoomView, newBookingIndex);
        } else {
            throw new OperationNotSupportedException("No room available for this movie at this time");
        }

    }

    private Booking buildBooking(MovieScheduling scheduling, List<Pair<Integer, Integer>> seats, boolean isPaid) {
        Booking booking;
        Movie movie = this.movieRepository.getItemWithId(scheduling.getMovieId());

        if (movie == null) {
            throw new IllegalArgumentException("No movie with this id");
        }

        // Get the seats at the specified positions
        LinkedList<Seat> bookedSeats = seats.stream()
                .map(seatLocation -> scheduling.getRoom().getSeats()
                        .get(seatLocation.getFirst())
                        .get(seatLocation.getSecond()))
                .collect(Collectors.toCollection(LinkedList::new));

        if (movie.is3D()) {
            booking = new Booking3D(bookedSeats, scheduling.getRoom().getId(), scheduling.getStartTime(), movie, isPaid, false);
        } else {
            booking = new Booking2D(bookedSeats, scheduling.getRoom().getId(), scheduling.getStartTime(), movie, isPaid);
        }

        this.bookingSchedulingMapping.put(booking.getBookingId(), scheduling.getId());

        return booking;
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
        MovieScheduling scheduling = allSchedulings.get(schedulingId);
        if (scheduling != null) {
            // Remove from the list of scheduling of the room
            movieSchedulings.get(scheduling.getRoom().getId()).remove(scheduling);
            allSchedulings.remove(schedulingId);
        }
    }

    /**
     * Get all the movie from a day
     *
     * @param date The day for which we are querying. Hours, minutes and seconds aren't relevant for this param
     * @return All the movies and the scheduling info
     */
    public HashSet<Pair<Movie, MovieScheduling>> getRunsForDay(Date date) {
        // Get the permitted dates
        Pair<Long, Long> permittedDate = getDateIntervalForDay(date);
        long permittedStartDate = permittedDate.getFirst();
        long permittedEndDate = permittedDate.getSecond();

        HashSet<Pair<Movie, MovieScheduling>> moviesForDay = new HashSet<>();

        for (MovieScheduling scheduling : this.allSchedulings.values()) {
            if (scheduling.getStartTime().getTime() >= permittedStartDate &&
                    scheduling.getEndTime().getTime() <= permittedEndDate) {
                moviesForDay.add(new Pair<>(this.movieRepository.getItemWithId(scheduling.getMovieId()),
                        scheduling));
            }
        }

        return moviesForDay;
    }

    /**
     * Get all the movie that will run from today on.
     *
     * @return All the movies and the scheduling info
     */
    public HashSet<Pair<Movie, MovieScheduling>> getRuns() {
        Date today = new Date();

        HashSet<Pair<Movie, MovieScheduling>> moviesForDay = new HashSet<>();

        for (MovieScheduling scheduling : this.allSchedulings.values()) {
            if (scheduling.getStartTime().getTime() >= today.getTime()) {
                moviesForDay.add(new Pair<>(this.movieRepository.getItemWithId(scheduling.getMovieId()),
                        scheduling));
            }
        }

        return moviesForDay;
    }

    /**
     * Return the room for a scheduling holding the booking info
     *
     * @param schedulingId
     */
    public RoomView getRoomForRun(int schedulingId) {
        MovieScheduling scheduling = allSchedulings.get(schedulingId);
        if (scheduling == null) {
            return null;
        }
        return scheduling.getRoom();
    }

    public void stopBooking(int schedulingId) {
        // Mark the scheduling as non-bookable
        MovieScheduling scheduling = this.allSchedulings.get(schedulingId);
        if (scheduling == null) {
            return;
        }

        scheduling.setCanBook(false);

        // Mark all booked seats that weren't bought as free
        scheduling.getRoom().makeSeatsAvailable(seat -> seat.getType() == SeatType.BOOKED);
    }

    /**
     * Check if you can make a booking for this scheduling for a number of seats
     *
     * @param wantToBuy True if this is a selling and not a simple booking
     * @return null if you can book or a string with the reason you can't book
     */
    public String canBook(int schedulingId, int numberOfSeats, boolean wantToBuy) throws IllegalArgumentException {
        MovieScheduling scheduling = allSchedulings.get(schedulingId);
        if (scheduling == null) {
            throw new IllegalArgumentException("No shceduling wiht this id");
        }

        if (!scheduling.getCanBook() && !wantToBuy) {
            return "Tickets for this running can't be booked anymore";
        }

        if (scheduling.getRoom().getSeatsWhere(seat -> seat.getType() == SeatType.AVAILABLE).size() >= numberOfSeats) {
            return null;
        } else {
            return "Not enough seats";
        }

    }


    // TODO: Check in the input class for this scheduling to have enough seats
    // TODO: Check in the input if you can book this scheduling
    public Booking bookMovie(int schedulingId, List<Pair<Integer, Integer>> seats) throws IllegalArgumentException {
        MovieScheduling scheduling = allSchedulings.get(schedulingId);
        if (scheduling == null) {
            throw new IllegalArgumentException("No shceduling wiht this id");
        }

        if (!scheduling.getCanBook()) {
            throw new IllegalArgumentException("Tickets for this running can't be booked anymore");
        }

        // Book the specified seats
        for (Pair<Integer, Integer> seat : seats) {
            scheduling.getRoom().bookSeat(seat.getFirst(), seat.getSecond());
        }

        return this.buildBooking(scheduling, seats, false);
    }

    public void cancelBooking(Booking booking) throws IllegalArgumentException {
        // Free up the previous seats
        Integer schedulingId = this.bookingSchedulingMapping.get(booking.getBookingId());
        if (schedulingId == null) {
            throw new IllegalArgumentException("Booking not existent");
        }

        MovieScheduling movieScheduling = this.allSchedulings.get(schedulingId);
        if (movieScheduling == null) {
            throw new IllegalArgumentException("Booking not existent");
        }
        Room room = movieScheduling.getRoom();

        for (Seat seat : booking.getBookingSeats()) {
            room.makeSeatAvailable(seat.getRow(), seat.getColumn());
        }

        //Remove the current booking
        this.bookingSchedulingMapping.remove(booking.getBookingId());
    }

    // TODO: Check in the input class for this scheduling to have enough seats
    // TODO: Check in the input if you can book this scheduling
    public Booking moveTicket(Booking booking, int newSchedulingId, List<Pair<Integer, Integer>> newSeats) throws IllegalArgumentException {
        this.cancelBooking(booking);
        // Create a new booking
        return this.bookMovie(newSchedulingId, newSeats);
    }


    /**
     * Buy tickets and return a paid booking
     *
     * @param schedulingId
     * @param seats
     * @return
     */
    public Booking buyTicket(int schedulingId, List<Pair<Integer, Integer>> seats) throws IllegalArgumentException {
        MovieScheduling scheduling = allSchedulings.get(schedulingId);
        if (scheduling == null) {
            throw new IllegalArgumentException("No shceduling wiht this id");
        }

        // Book the specified seats
        for (Pair<Integer, Integer> seat : seats) {
            scheduling.getRoom().buySeat(seat.getFirst(), seat.getSecond());
        }

        return this.buildBooking(scheduling, seats, true);
    }

    //TODO: Handle exception

    /**
     * Buy the seats for the booking and mark it as paid
     *
     * @param booking
     */
    public void buyTicket(Booking booking) throws IllegalArgumentException {
        Integer schedulingId = this.bookingSchedulingMapping.get(booking.getBookingId());
        if (schedulingId == null) {
            throw new IllegalArgumentException("Booking not existent");
        }

        MovieScheduling movieScheduling = this.allSchedulings.get(schedulingId);
        if (movieScheduling == null) {
            throw new IllegalArgumentException("Booking not existent");
        }

        if (!movieScheduling.getCanBook()) {
            // The booking was invalidated and can no longer be claimed
            throw new IllegalArgumentException("Booking can no longer be claimed");
        }

        // Book the specified seats
        for (Seat seat : booking.getBookingSeats()) {
            movieScheduling.getRoom().buySeat(seat.getRow(), seat.getColumn());
        }

        booking.setIsPaid(true);
    }


    public void addRoom(int rows, int columns, RoomType roomType) {
        RoomView roomView = new RoomView(rows, columns, roomType, nextRoomId++);
        this.roomViewRepository.insertItem(roomView.getId(), roomView);
    }

    //TODO: test that seat toggling doesn't affect rooms in schedulings

    /**
     * Add or remove seat for a room at a position
     */
    public void toggleSeat(int roomId, int row, int column) throws IllegalArgumentException {
        RoomView roomView = this.roomViewRepository.getItemWithId(roomId);
        if (roomView == null) {
            throw new IllegalArgumentException("No room with this id");
        }

        Seat seat = roomView.getSeatsWhere(seat_ -> seat_.getRow() == row && seat_.getColumn() == column).getFirst();
        if (seat.getType() == SeatType.NON_EXISTENT) {
            roomView.makeSeatAvailable(row, column);
        } else {
            roomView.removeSeat(row, column);
        }
    }

}
