package models.booking;

import models.movie.Movie;
import models.room.Seat;

import java.util.List;

public interface Booking {
    String getBookingDate();

    List<Seat> getBookingSeats();

    int getBookingRoomId();

    Movie getMovie();

    int getBookingId();

    /**
     * @return how much the movie cost
     */
    int getPrice();

    /**
     * @return True when the seats were bought and seats become Sold instead of Booked
     */
    boolean isPaid();
}
