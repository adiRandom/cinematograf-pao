package cli.handlers;

import models.room.RoomView;

import javax.naming.OperationNotSupportedException;
import java.util.List;

public class RemoveRoomHandler extends AbstractCommandHandler{

    @Override
    public void handleCommand() throws OperationNotSupportedException {
        List<RoomView> rooms = this.roomViewRepository.getAll();

        for (RoomView roomView : rooms) {
            System.out.println("Room " + roomView.getId());
        }
        System.out.println("--------");

        int roomId = this.getIntFromInput("Pick a room to edit with its number");

        this.roomViewRepository.deleteItem(roomId);
        System.out.println("Room removed.");
    }
}
