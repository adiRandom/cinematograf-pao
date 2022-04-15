package services;

import java.io.IOException;
import java.io.Serializable;

public class IdService implements Serializable {
    private int movieId;
    private int bookingId;
    private int roomId;
    private int schedulingId;
    private static IdService instance = null;

    public int getMovieId() {
        return movieId++;
    }

    public int getBookingId() {
        return bookingId++;
    }

    public int getRoomId() {
        return roomId++;
    }

    public int getSchedulingId() {
        return schedulingId++;
    }

    private IdService() {
        try {
            IdService localData = (IdService) SerializationService.readObject("ids.txt");
            this.schedulingId = localData.getSchedulingId();
            this.movieId = localData.getMovieId();
            this.roomId = localData.getRoomId();
            this.bookingId = localData.getBookingId();
        } catch (IOException | ClassCastException | ClassNotFoundException e) {
            this.bookingId = 0;
            this.movieId = 0;
            this.roomId = 0;
            this.schedulingId = 0;
        }
    }

    public static IdService getInstance() {
        if (instance == null) {
            instance = new IdService();
        }
        return instance;
    }

    public void saveToDisk() {
        SerializationService.writeObject(this, "ids.txt");
    }
}
