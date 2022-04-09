package models.booking;

import models.movie.Movie;
import models.room.Seat;

import java.util.LinkedList;

public class Booking3D extends BaseBooking {
    private boolean withGlasses;
    private final Movie3DType type;

    public Booking3D(LinkedList<Seat> bookedSeats, int bookedRoom, int bookingDate, Movie movie, Movie3DType type) {
        super(bookedSeats, bookedRoom, bookingDate, movie);
        this.type = type;
    }

    @Override
    public int getBookingType() {
        return this.type.ordinal();
    }
}
