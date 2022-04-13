package models.booking;

import models.movie.Movie;
import models.movie.Movie3DType;
import models.room.Seat;

import java.util.Date;
import java.util.LinkedList;

public class Booking3D extends Booking2D {
    private boolean withGlasses;

    public Booking3D(LinkedList<Seat> bookedSeats, int bookedRoom, Date bookingDate, Movie movie, boolean withGlasses) {
        super(bookedSeats, bookedRoom, bookingDate, movie);
        this.withGlasses = withGlasses;
    }


    public boolean isWithGlasses() {
        return withGlasses;
    }

    public void setWithGlasses(boolean withGlasses) {
        this.withGlasses = withGlasses;
    }
}
