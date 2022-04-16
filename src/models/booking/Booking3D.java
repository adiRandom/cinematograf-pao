package models.booking;

import models.movie.Movie;
import models.movie.Movie2DType;
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

    public Booking3D(LinkedList<Seat> bookedSeats, int bookedRoom, Date bookingDate, Movie movie, boolean isPaid, boolean withGlasses) {
        super(bookedSeats, bookedRoom, bookingDate, movie, isPaid);
        this.withGlasses = withGlasses;
    }

    public boolean isWithGlasses() {
        return withGlasses;
    }

    public void setWithGlasses(boolean withGlasses) {
        this.withGlasses = withGlasses;
    }

    @Override
    public int getPrice() {
        switch (Movie3DType.valueOf(this.movie.getType())) {
            case REGULAR: {
                if (this.withGlasses) {
                    return 30 * this.bookedSeats.size();
                } else {
                    return 27 * this.bookedSeats.size();

                }
            }
            case MOVIE_4DX: {
                if (this.withGlasses) {
                    return 47 * this.bookedSeats.size();
                } else {
                    return 45 * this.bookedSeats.size();
                }
            }
            case IMAX: {
                if (this.withGlasses) {
                    return 50 * this.bookedSeats.size();
                } else {
                    return 47 * this.bookedSeats.size();
                }
            }
            default: {
                return 0;
            }
        }
    }

}
