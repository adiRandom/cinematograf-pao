package models.booking;

import models.movie.Movie;
import models.movie.Movie3DType;
import models.room.Seat;

import java.util.LinkedList;

public class Booking3D extends Booking2D {
    private final boolean withGlasses;

    public Booking3D(LinkedList<Seat> bookedSeats, int bookedRoom, int bookingDate, Movie movie, boolean withGlasses) {
        super(bookedSeats, bookedRoom, bookingDate, movie);
        this.withGlasses = withGlasses;
    }


    public boolean isWithGlasses() {
        return withGlasses;
    }
}
