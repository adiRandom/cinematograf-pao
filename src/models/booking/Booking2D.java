package models.booking;

import models.movie.Movie;
import models.room.Seat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

public class Booking2D implements Booking {

    private LinkedList<Seat> bookedSeats;
    private int bookedRoom;
    /**
     * The time in ms
     */
    private int bookingDate;
    private final Movie movie;

    public Booking2D(LinkedList<Seat> bookedSeats, int bookedRoom, int bookingDate, Movie movie) {
        this.bookedSeats = bookedSeats;
        this.bookedRoom = bookedRoom;
        this.bookingDate = bookingDate;
        this.movie = movie;
    }

    @Override
    public String getBookingDate() {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(bookingDate);
        return format.format(calendar.getTime());
    }

    @Override
    public List<Seat> getBookingSeats() {
        return this.bookedSeats;
    }

    @Override
    public int getBookingRoom() {
        return this.bookedRoom;
    }

    @Override
    public Movie getMovie() {
        return this.movie;
    }

    @Override
    public void moveBooking(int newDate, List<Seat> newSeats, int newRoom) {
        this.bookingDate = newDate;
        this.bookedRoom = newRoom;
        this.bookedSeats = new LinkedList<>(newSeats);
    }
}
