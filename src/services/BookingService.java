package services;

import lib.scheduling.SchedulingManager;
import lib.scheduling.utils.MovieScheduling;
import models.booking.Booking;
import models.booking.Booking2D;
import models.booking.Booking3D;
import models.movie.Movie;
import models.room.Room;
import models.room.Seat;
import models.room.SeatType;
import repository.MovieRepository;
import repository.RoomViewRepository;
import utils.Pair;

import java.util.ArrayList;
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


    public BookingService(HashMap<Integer, MovieScheduling> allSchedulings, HashMap<Integer, Integer> bookingSchedulingMapping) {
        this.allSchedulings = allSchedulings;
        this.movieRepository = MovieRepository.getInstance();
        this.bookingSchedulingMapping = bookingSchedulingMapping;
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

}
