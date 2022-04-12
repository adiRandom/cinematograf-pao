package models.room;

import java.util.ArrayList;

public class Room {
    private ArrayList<ArrayList<Seat>> seats = null;
    private final int rows;
    private final int columns;
    private final RoomType type;
    private final int id;

    Room(int rows, int columns, RoomType type, int id) {
        this.rows = rows;
        this.columns = columns;
        this.type = type;
        this.seats = new ArrayList<>(rows);
        this.id = id;
        for (int i = 0; i < rows; i++) {
            ArrayList<Seat> row = new ArrayList<Seat>(columns);
            for (int j = 0; j < columns; j++) {
                row.set(j, new Seat());
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
    }

    public void removeSeat(int row, int column) {
        if (this.seats != null) {
            this.seats.get(row).get(column).setType(SeatType.NON_EXISTENT);
        }
    }

    public void makeSeatAvailable(int row, int column) {
        if (this.seats != null) {
            this.seats.get(row).get(column).setType(SeatType.AVAILABLE);
        }
    }

    public void bookSeat(int row, int column) {
        if (this.seats != null) {
            this.seats.get(row).get(column).setType(SeatType.BOOKED);
        }
    }

    public void buySeat(int row, int column) {
        if (this.seats != null) {
            this.seats.get(row).get(column).setType(SeatType.SOLD);
        }
    }

    public void clearRoom(){
        for(ArrayList<Seat> row :this.seats){
            for(Seat seat : row){
                if(seat.getType() == SeatType.SOLD) {
                    seat.setType(SeatType.AVAILABLE);
                }
            }
        }
    }

    public int getId() {
        return id;
    }

    public RoomType getType() {
        return type;
    }
}
