package models.room;

public class Seat {
    private SeatType type;
    Seat(){
        this.type = SeatType.AVAILABLE;
    }


    public SeatType getType() {
        return type;
    }

    public void setType(SeatType type) {
        this.type = type;
    }
}
