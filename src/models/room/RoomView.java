package models.room;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.function.Predicate;

/**
 * A view of a room
 * Seats can only be added or removed
 * No info about their booking status
 * Booking info is available if a Room is upcasted
 */
public class RoomView implements Serializable {

    protected ArrayList<ArrayList<Seat>> seats = null;
    protected int rows;
    protected int columns;
    private final RoomType type;
    private final int id;


    public RoomView(int rows, int columns, RoomType type, int id) {
        this.rows = rows;
        this.columns = columns;
        this.type = type;
        this.seats = new ArrayList<>(rows);
        this.id = id;

        for (int i = 0; i < rows; i++) {
            ArrayList<Seat> row = new ArrayList<Seat>(columns);
            for (int j = 0; j < columns; j++) {
                row.add(new Seat(i, j));
            }
            this.seats.add(row);
        }


    }

    public RoomView(ArrayList<ArrayList<Seat>> seats, RoomType type, int id) {
        this.seats = seats;
        this.type = type;
        this.id = id;
        this.rows = this.seats.size();
        this.columns = this.seats.get(0).size();

    }

    public void makeSeatAvailable(int row, int column) {
        if (this.seats != null) {
            Seat seat = this.seats.get(row).get(column);
            if (seat.getType() == SeatType.NON_EXISTENT) {
                seat.setType(SeatType.AVAILABLE);
            }
        }

    }


    /**
     * remove a seat from the room
     * Removing a seat won't affect the current bookings
     *
     * @param row
     * @param column
     */
    public void removeSeat(int row, int column) {
        if (this.seats != null) {
            this.seats.get(row).get(column).setType(SeatType.NON_EXISTENT);
        }

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

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }
}
