package models.booking;

import models.movie.Movie;
import models.room.Seat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

public class Booking2D extends BaseBooking {

    private final Movie2DType type;

    public Booking2D(LinkedList<Seat> bookedSeats, int bookedRoom, int bookingDate, Movie movie, Movie2DType type) {
        super(bookedSeats, bookedRoom, bookingDate, movie);
        this.type = type;
    }

    @Override
    public int getBookingType() {
        return this.type.ordinal();
    }
}
