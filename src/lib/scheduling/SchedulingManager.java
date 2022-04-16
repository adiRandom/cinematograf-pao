package lib.scheduling;

import cli.utils.BookingDetails;
import lib.scheduling.utils.MovieScheduling;
import models.booking.Booking;
import models.movie.*;
import models.room.*;
import repository.RoomViewRepository;
import services.*;
import utils.Pair;

import javax.naming.OperationNotSupportedException;
import java.util.*;

public class SchedulingManager {
    private RoomViewRepository roomViewRepository;
    private static SchedulingManager instance = null;
    private final IdService idService;

    private final SchedulingService schedulingService;
    private final SchedulingInfoService schedulingInfoService;
    private final BookingService bookingService;
    private final HashMap<Integer, Booking> allBookings;


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
        this.idService = IdService.getInstance();

        HashMap<Integer, ArrayList<MovieScheduling>> _movieSchedulings;
        HashMap<Integer, MovieScheduling> _allSchedulings;
        HashMap<Integer, Integer> _bookingSchedulingMapping;
        HashMap<Integer, Booking> _allBookings;


        // Try to load the data from files
        try {
            _movieSchedulings = (HashMap<Integer, ArrayList<MovieScheduling>>) SerializationService.readObject("movie_schedulings.txt");

            _allSchedulings = (HashMap<Integer, MovieScheduling>) SerializationService.readObject("all_schedulings.txt");
            _bookingSchedulingMapping = (HashMap<Integer, Integer>) SerializationService.readObject("bookings_schedulings.txt");
            _allBookings = (HashMap<Integer, Booking>) SerializationService.readObject("all_bookings.txt");


        } catch (Exception e) {
            _movieSchedulings = new HashMap<>();
            _allSchedulings = new HashMap<>();
            _bookingSchedulingMapping = new HashMap<>();
            _allBookings = new HashMap<>();
        }

        this.movieSchedulings = _movieSchedulings;
        this.allSchedulings = _allSchedulings;
        this.bookingSchedulingMapping = _bookingSchedulingMapping;
        this.allBookings = _allBookings;

        this.schedulingService = new SchedulingService(this.movieSchedulings, this.allSchedulings);
        this.schedulingInfoService = new SchedulingInfoService(this.schedulingService, this.allSchedulings, allBookings);
        this.bookingService = new BookingService(this.allSchedulings, this.bookingSchedulingMapping, allBookings);
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
     * @param movie The movie ti be scheduled
     * @return The scheduling id
     */
    public int scheduleMovie(Movie movie) throws IllegalArgumentException {
        try {
            Movie3D movie3D = (Movie3D) movie;
            switch (Movie3DType.valueOf(movie3D.getType())) {
                case REGULAR: {
                    return this.schedulingService.scheduleMovie(RoomType.REGULAR_3D, movie);
                }
                case IMAX: {
                    return this.schedulingService.scheduleMovie(RoomType.IMAX, movie);
                }
                case MOVIE_4DX: {
                    return this.schedulingService.scheduleMovie(RoomType.ROOM_4DX_3D, movie);
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
                    return this.schedulingService.scheduleMovie(RoomType.REGULAR_2D, movie);
                }
                case MOVIE_4DX: {
                    return this.schedulingService.scheduleMovie(RoomType.ROOM_4DX_3D, movie);
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
                    return this.schedulingService.scheduleMovie(RoomType.REGULAR_3D, movie, date, exact);
                }
                case IMAX: {
                    return this.schedulingService.scheduleMovie(RoomType.IMAX, movie, date, exact);
                }
                case MOVIE_4DX: {
                    return this.schedulingService.scheduleMovie(RoomType.ROOM_4DX_3D, movie, date, exact);
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
                    return this.schedulingService.scheduleMovie(RoomType.REGULAR_2D, movie, date, exact);
                }
                case MOVIE_4DX: {
                    return this.schedulingService.scheduleMovie(RoomType.ROOM_4DX_3D, movie, date, exact);
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
        schedulingService.cancelRun(schedulingId);
    }

    /**
     * Get all the movie from a day
     *
     * @param date The day for which we are querying. Hours, minutes and seconds aren't relevant for this param
     * @return All the movies and the scheduling info
     */
    public HashSet<Pair<Movie, MovieScheduling>> getRunsForDay(Date date) {
        return this.schedulingInfoService.getRunsForDay(date);
    }

    /**
     * Get all the movie that will run from today on.
     *
     * @return All the movies and the scheduling info
     */
    public HashSet<Pair<Movie, MovieScheduling>> getRuns() {
        return this.schedulingInfoService.getRuns();
    }

    /**
     * Return the room for a scheduling holding the booking info
     *
     * @param schedulingId
     */
    public Room getRoomForRun(int schedulingId) {
        return this.schedulingInfoService.getRoomForRun(schedulingId);
    }

    public void stopBooking(int schedulingId) {
        this.bookingService.stopBooking(schedulingId);
    }

    /**
     * Check if you can make a booking for this scheduling for a number of seats
     *
     * @param wantToBuy True if this is a selling and not a simple booking
     * @return null if you can book or a string with the reason you can't book
     */
    public String canBook(int schedulingId, int numberOfSeats, boolean wantToBuy) throws IllegalArgumentException {
        return this.bookingService.canBook(schedulingId, numberOfSeats, wantToBuy);

    }


    public Booking bookMovie(BookingDetails  bookingDetails) throws IllegalArgumentException {
        return this.bookingService.bookMovie(bookingDetails);
    }

    public void cancelBooking(int bookingId) throws IllegalArgumentException {
        Booking booking = this.allBookings.get(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException("No booking with this id");
        }
        this.bookingService.cancelBooking(booking);
    }

    public Booking moveTicket(int bookingId, BookingDetails newBookingDetails)
            throws IllegalArgumentException {
        return this.bookingService.moveTicket(this.getBooking(bookingId), newBookingDetails);
    }


    /**
     * Buy tickets and return a paid booking
     *
     * @return
     */
    public Booking buyTicket(BookingDetails bookingDetails) throws IllegalArgumentException {
        return this.bookingService.buyTicket(bookingDetails);
    }


    /**
     * Buy the seats for the booking and mark it as paid
     */
    public void buyTicket(int bookingId) throws IllegalArgumentException {
        this.bookingService.buyTicket(this.getBooking(bookingId));
    }


    public void addRoom(int rows, int columns, RoomType roomType) {
        RoomView roomView = new RoomView(rows, columns, roomType, idService.getRoomId());
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

    public void saveToDisk() {
        SerializationService.writeObject(this.movieSchedulings, "movie_schedulings.txt");
        SerializationService.writeObject(this.allSchedulings, "all_schedulings.txt");
        SerializationService.writeObject(this.bookingSchedulingMapping, "bookings_schedulings.txt");
        SerializationService.writeObject(this.allBookings, "all_bookings.txt");
    }

    public Booking getBooking(int bookingId) {
        return this.schedulingInfoService.getBooking(bookingId);
    }

    public boolean isSchedulingFor3D(int schedulingId) {
        return this.schedulingInfoService.isSchedulingFor3D(schedulingId);
    }

}
