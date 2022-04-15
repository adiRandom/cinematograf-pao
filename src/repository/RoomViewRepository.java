package repository;

import models.room.RoomView;

public class RoomViewRepository extends BaseRepository<RoomView> {
    private static RoomViewRepository instance = null;

    private RoomViewRepository() {
        super("room_repo");
    }

    public static RoomViewRepository getInstance() {
        if (instance == null) {
            instance = new RoomViewRepository();
        }

        return instance;
    }


}
