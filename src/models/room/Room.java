package models.room;

import com.sun.javaws.exceptions.InvalidArgumentException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.function.Predicate;

public class Room {


    private ArrayList<ArrayList<Seat>> seats = null;
    private final int rows;
    private final int columns;
    private final RoomType type;
    private final int id;
    private int availableSeats;

    Room(int rows, int columns, RoomType type, int id) {
        this.rows = rows;
        this.columns = columns;
        this.type = type;
        this.seats = new ArrayList<>(rows);
        this.id = id;
        this.availableSeats = rows * columns;

        for (int i = 0; i < rows; i++) {
            ArrayList<Seat> row = new ArrayList<Seat>(columns);
            for (int j = 0; j < columns; j++) {
                row.set(j, new Seat(i, j));
            }
            this.seats.set(i, row);
        }


    }

    Room(ArrayList<ArrayList<Seat>> seats, RoomType type, int id) {
        this.seats = seats;
        this.type = type;
        this.id = id;
        this.rows = this.seats.size();
        this.columns = this.seats.get(0).size();
        this.availableSeats = rows * columns;
    }

    public void removeSeat(int row, int column) {
        if (this.seats != null) {
            this.seats.get(row).get(column).setType(SeatType.NON_EXISTENT);
            this.availableSeats--;
        }

    }

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

    public int getId() {
        return id;
    }

    public RoomType getType() {
        return type;
    }


    public ArrayList<ArrayList<Seat>> getSeats() {
        return seats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public LinkedList<Seat> getSeatsWhere(Predicate<Seat> predicate) {
        LinkedList<Seat> collected = new LinkedList<>();
        for (ArrayList<Seat> row : this.seats) {
            for (Seat seat : row) {
                if (predicate.test(seat)) {
                    collected.push(seat);
                }
            }
        }
        return collected;
    }
}
