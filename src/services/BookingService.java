package services;

import cli.utils.BookingDetails;
import lib.scheduling.utils.MovieScheduling;
import models.booking.Booking;
import models.booking.Booking2D;
import models.booking.Booking3D;
import models.movie.Movie;
import models.room.Room;
import models.room.Seat;
import models.room.SeatType;
import repository.MovieRepository;
import utils.Pair;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class BookingService {

    /**
     * A mapping between scheduling id and Scheduling
     */
    private final HashMap<Integer, MovieScheduling> allSchedulings;

    /**
     * Map booking ids to scheduling ids
     */
    private final HashMap<Integer, Integer> bookingSchedulingMapping;
    private MovieRepository movieRepository;
    private final HashMap<Integer, Booking> allBookings;


    public BookingService(HashMap<Integer, MovieScheduling> allSchedulings,
                          HashMap<Integer, Integer> bookingSchedulingMapping,
                          HashMap<Integer, Booking> allBookings) {
        this.allSchedulings = allSchedulings;
        this.allBookings = allBookings;
        this.movieRepository = MovieRepository.getInstance();
        this.bookingSchedulingMapping = bookingSchedulingMapping;
    }

    private Booking buildBooking(MovieScheduling scheduling, List<Pair<Integer, Integer>> seats, boolean isPaid, boolean with3DGlasses) {
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
            booking = new Booking3D(bookedSeats, scheduling.getRoom().getId(), scheduling.getStartTime(), movie, isPaid, with3DGlasses);
        } else {
            booking = new Booking2D(bookedSeats, scheduling.getRoom().getId(), scheduling.getStartTime(), movie, isPaid);
        }

        this.bookingSchedulingMapping.put(booking.getBookingId(), scheduling.getId());
        this.allBookings.put(booking.getBookingId(), booking);
        return booking;
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

    public Booking bookMovie(BookingDetails bookingDetails) throws IllegalArgumentException {
        MovieScheduling scheduling = allSchedulings.get(bookingDetails.getSchedulingId());
        if (scheduling == null) {
            throw new IllegalArgumentException("No shceduling wiht this id");
        }

        if (!scheduling.getCanBook()) {
            throw new IllegalArgumentException("Tickets for this running can't be booked anymore");
        }

        // Book the specified seats
        for (Pair<Integer, Integer> seat : bookingDetails.getSeats()) {
            scheduling.getRoom().bookSeat(seat.getFirst(), seat.getSecond());
        }

        return this.buildBooking(scheduling, bookingDetails.getSeats(), false, bookingDetails.isWith3DGlasses());
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
        this.allBookings.remove(booking.getBookingId());
    }

    public Booking moveTicket(Booking booking, BookingDetails newBookingDetails) throws IllegalArgumentException {
        // Check if the user is moving the booking to the same movie
        MovieScheduling movieScheduling = this.allSchedulings.get(newBookingDetails.getSchedulingId());

        if (booking.getMovie().getId() != movieScheduling.getMovieId()) {
            throw new IllegalArgumentException("You must move the reservation to the same movie");
        }
        this.cancelBooking(booking);
        // Create a new booking
        if (booking.isPaid()) {
            return this.buyTicket(newBookingDetails);
        } else {
            return this.bookMovie(newBookingDetails);
        }
    }


    /**
     * Buy tickets and return a paid booking
     *
     * @return
     */
    public Booking buyTicket(BookingDetails bookingDetails) throws IllegalArgumentException {
        MovieScheduling scheduling = allSchedulings.get(bookingDetails.getSchedulingId());
        if (scheduling == null) {
            throw new IllegalArgumentException("No shceduling wiht this id");
        }

        // Book the specified seats
        for (Pair<Integer, Integer> seat : bookingDetails.getSeats()) {
            scheduling.getRoom().buySeat(seat.getFirst(), seat.getSecond());
        }

        return this.buildBooking(scheduling, bookingDetails.getSeats(), true, bookingDetails.isWith3DGlasses());
    }


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

}
