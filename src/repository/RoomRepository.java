package repository;

import models.room.Room;

public class RoomRepository extends BaseRepository<Room> {
    private static RoomRepository instance = null;

    private RoomRepository() {
        super();
    }

    public static RoomRepository getInstance() {
        if (instance == null) {
            instance = new RoomRepository();
        }

        return instance;
    }
}
