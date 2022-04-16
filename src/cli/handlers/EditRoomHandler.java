package cli.handlers;

import models.room.RoomView;
import utils.Pair;

import javax.naming.OperationNotSupportedException;
import java.util.List;

public class EditRoomHandler extends AbstractCommandHandler{
    @Override
    public void handleCommand() throws OperationNotSupportedException {
        List<RoomView> rooms = this.roomViewRepository.getAll();

        for (RoomView roomView : rooms) {
            System.out.println("Room " + roomView.getId());
        }
        System.out.println("--------");

        int roomId = this.getIntFromInput("Pick a room to edit with its number");
        RoomView roomView = rooms.get(roomId);

        if (roomView == null) {
            throw new IllegalArgumentException("No room with that number");
        }

        int editCount = this.getIntFromInput("How many seats do you want to edit?");

        System.out.println(roomView);

        for (int i = 0; i < editCount; i++) {
            Pair<Integer, Integer> seat = this.getPairOfIntsFromInput("Pick a sit to add or remove. Type the row number and column number");
            this.schedulingManager.toggleSeat(roomId, seat.getFirst(), seat.getSecond());
        }

        System.out.println("Room edited. All following movie schedulings will use the new layout");
    }
}
