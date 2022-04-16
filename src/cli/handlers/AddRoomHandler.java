package cli.handlers;

import models.room.RoomType;

public class AddRoomHandler extends AbstractCommandHandler{
    @Override
    public void handleCommand() {
        int rows, columns;
        rows = this.getIntFromInput("How many rows of seats");
        columns = this.getIntFromInput("How many columns of seats");

        boolean is3D = this.getBooleanFromInput("Is the room for 3D movies? Yes or No", "Yes", "No");
        RoomType type;

        if (is3D) {
            type = RoomType.valueOf(this.getStringFromInput("What type of room is this? REGULAR_3D, IMAX or ROOM_4DX_3D"));
        } else {
            type = RoomType.valueOf(this.getStringFromInput("What type of room is this? REGULAR_2D or ROOM_4DX"));
        }
        schedulingManager.addRoom(rows, columns, type);

        System.out.println("Room added!");
    }
}
