package models.booking;

import models.movie.Movie;
import models.room.Seat;

import java.util.List;

public interface Booking {
    String getBookingDate();
    int getBookingType();
    List<Seat> getBookingSeats();
    int getBookingRoom();
    Movie getMovie();
    void moveBooking(int newDate, List<Seat> newSeats, int newRoom);

}
