package models.room;


import java.util.ArrayList;
import java.util.LinkedList;
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

    public Room(RoomView roomView) {
        // Init the seats with those in the roomView param for the rows and columns init.
        super(roomView.seats, roomView.getType(), roomView.getId());

        // Immediately replace with an empty list for a deep copy
        this.seats = new ArrayList<>(this.rows);

        for (int i = 0; i < roomView.getRows(); i++) {
            ArrayList<Seat> row = new ArrayList<Seat>(columns);
            for (int j = 0; j < roomView.getColumns(); j++) {
                row.add(new Seat(i, j));
                if (roomView.getSeats().get(i).get(j).getType() == SeatType.NON_EXISTENT) {
                    // This seat doesn't exist
                    row.get(j).setType(SeatType.NON_EXISTENT);
                }
            }
            seats.add(row);
        }

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

    public void bookSeat(int row, int column) throws IllegalArgumentException {
        if (this.seats != null) {
            Seat seat = this.seats.get(row).get(column);
            if (seat.getType() != SeatType.AVAILABLE) {
                throw new IllegalArgumentException(
                        "Can't book this seat " +
                                String.valueOf(row) +
                                "/" +
                                String.valueOf(column));
            }
            seat.setType(SeatType.BOOKED);
            this.availableSeats--;
        }
    }

    public void buySeat(int row, int column) throws IllegalArgumentException {
        if (this.seats != null) {
            Seat seat = this.seats.get(row).get(column);
            switch (seat.getType()) {
                case NON_EXISTENT: {
                    throw new IllegalArgumentException("Seat doesn't exist");
                }
                case SOLD: {
                    throw new IllegalArgumentException(
                            "Can't book this seat " +
                                    String.valueOf(row) +
                                    "/" +
                                    String.valueOf(column));
                }
                case BOOKED: {
                    seat.setType(SeatType.SOLD);
                }
                case AVAILABLE: {
                    seat.setType(SeatType.SOLD);
                    this.availableSeats--;
                }
            }
        }
    }


    public void clearRoom() {
        this.makeSeatsAvailable(seat -> seat.getType() == SeatType.SOLD);
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (ArrayList<Seat> row : this.seats) {
            for (Seat seat : row) {
                switch (seat.getType()) {
                    case AVAILABLE: {
                        stringBuilder.append("_");
                        break;
                    }
                    case BOOKED:
                    case SOLD: {
                        stringBuilder.append("X");
                        break;
                    }
                    case NON_EXISTENT: {
                        stringBuilder.append(" ");
                    }
                }
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
