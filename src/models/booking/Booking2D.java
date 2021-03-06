package models.booking;

import models.movie.Movie;
import models.movie.Movie2D;
import models.movie.Movie2DType;
import models.room.Seat;
import services.IdService;

import java.text.SimpleDateFormat;
import java.util.*;

public class Booking2D implements Booking {

    protected final LinkedList<Seat> bookedSeats;
    protected final int bookedRoomId;
    /**
     * The time in ms
     */
    protected final Date bookingDate;
    protected final Movie movie;
    protected final int bookingId;
    protected boolean isPaid;
    private IdService idService;


    public Booking2D(LinkedList<Seat> bookedSeats, int bookedRoomId, Date bookingDate, Movie movie) {
        this.bookedSeats = bookedSeats;
        this.bookedRoomId = bookedRoomId;
        this.bookingDate = bookingDate;
        this.movie = movie;
        this.idService = IdService.getInstance();
        this.bookingId = idService.getBookingId();
        this.isPaid = false;
    }

    public Booking2D(LinkedList<Seat> bookedSeats, int bookedRoomId, Date bookingDate, Movie movie, boolean isPaid) {
        this.bookedSeats = bookedSeats;
        this.bookedRoomId = bookedRoomId;
        this.bookingDate = bookingDate;
        this.movie = movie;
        this.idService = IdService.getInstance();
        this.bookingId = idService.getBookingId();
        this.isPaid = isPaid;
    }

    @Override
    public String getBookingDate() {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(bookingDate);
        return format.format(calendar.getTime());
    }

    @Override
    public List<Seat> getBookingSeats() {
        return this.bookedSeats;
    }

    @Override
    public int getBookingRoomId() {
        return this.bookedRoomId;
    }

    @Override
    public Movie getMovie() {
        return this.movie;
    }

    @Override
    public int getBookingId() {
        return this.bookingId;
    }

    @Override
    public int getPrice() {
        switch (Movie2DType.valueOf(this.movie.getType())) {
            case REGULAR: {
                return 22 * this.bookedSeats.size();
            }
            case MOVIE_4DX: {
                return 40 * this.bookedSeats.size();
            }
            default: {
                return 0;
            }
        }
    }

    @Override
    public boolean isPaid() {
        return this.isPaid;
    }

    @Override
    public void setIsPaid(boolean isPaid) {
        this.isPaid = isPaid;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Booking number ");
        stringBuilder
                .append(this.bookingId)
                .append(" for ")
                .append(this.movie.getTitle())
                .append(" for ")
                .append(this.bookingDate.toString())
                .append(" in room ")
                .append(this.bookedRoomId)
                .append(" for seats ");
        for (Seat seat : this.bookedSeats) {
            stringBuilder.append(seat.getRow())
                    .append("/")
                    .append(seat.getColumn())
                    .append(", ");
        }
        stringBuilder.append("To buy the seats it will cost ").append(this.getPrice());

        return stringBuilder.toString();
    }


}
