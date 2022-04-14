package models.room;

import com.sun.javaws.exceptions.InvalidArgumentException;

import java.util.ArrayList;
import java.util.function.Predicate;

public class Room extends RoomView {
    private int availableSeats;

    Room(int rows, int columns, RoomType type, int id) {
        super(rows, columns, type, id);
        this.availableSeats = rows * columns;
    }

    Room(ArrayList<ArrayList<Seat>> seats, RoomType type, int id) {
        super(seats, type, id);
        this.availableSeats = this.rows * this.columns;
    }

    public Room(RoomView roomView){
        super(roomView.getSeats(),roomView.getType(),roomView.getId());
        this.availableSeats = this.rows * this.columns;
    }

    @Override
    public void removeSeat(int row, int column) {
        if (this.seats != null) {
            this.seats.get(row).get(column).setType(SeatType.NON_EXISTENT);
            this.availableSeats--;
        }

    }

    @Override
    public void makeSeatAvailable(int row, int column) {
        if (this.seats != null) {
            this.seats.get(row).get(column).setType(SeatType.AVAILABLE);
            this.availableSeats++;
        }

    }

    public void makeSeatsAvailable(Predicate<Seat> predicate) {
        for (ArrayList<Seat> row : this.seats) {
            for (Seat seat : row) {
                if (predicate.test(seat)) {
                    seat.setType(SeatType.AVAILABLE);
                    this.availableSeats++;
                }
            }
        }
    }

    public void bookSeat(int row, int column) throws InvalidArgumentException {
        if (this.seats != null) {
            Seat seat = this.seats.get(row).get(column);
            if (seat.getType() != SeatType.AVAILABLE) {
                throw new InvalidArgumentException(new String[]{
                        "Can't book this seat",
                        String.valueOf(row),
                        String.valueOf(column)});
            }
            seat.setType(SeatType.BOOKED);
            this.availableSeats--;
        }
    }

    public void buySeat(int row, int column) {
        if (this.seats != null) {
            this.seats.get(row).get(column).setType(SeatType.SOLD);
            this.availableSeats--;
        }
    }

    public void clearRoom() {
        this.makeSeatsAvailable(seat -> seat.getType() == SeatType.SOLD);
    }

    public int getAvailableSeats() {
        return availableSeats;
    }
}
