package cli.utils;

import utils.Pair;

import java.util.LinkedList;

public class BookingDetails {
    private final LinkedList<Pair<Integer, Integer>> seats;
    private final int schedulingId;
    private final boolean with3DGlasses;

    public BookingDetails(LinkedList<Pair<Integer, Integer>> seats, int schedulingId, boolean with3DGlasses) {
        this.seats = seats;
        this.schedulingId = schedulingId;
        this.with3DGlasses = with3DGlasses;
    }

    public int getSchedulingId() {
        return schedulingId;
    }

    public boolean isWith3DGlasses() {
        return with3DGlasses;
    }

    public LinkedList<Pair<Integer, Integer>> getSeats() {
        return seats;
    }
}
