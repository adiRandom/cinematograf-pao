package models.room;

public class Seat {
    private SeatType type;
    private final int row;
    private final int column;

    public Seat(int row, int column){
        this.row = row;
        this.column = column;
        this.type = SeatType.AVAILABLE;
    }


    public SeatType getType() {
        return type;
    }

    public void setType(SeatType type) {
        this.type = type;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
