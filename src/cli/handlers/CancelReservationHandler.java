package cli.handlers;

import javax.naming.OperationNotSupportedException;

public class CancelReservationHandler  extends AbstractCommandHandler{

    @Override
    public void handleCommand() throws OperationNotSupportedException {
        int bookingId = this.getIntFromInput("Enter booking id");
        this.schedulingManager.cancelBooking(bookingId);
        System.out.println("Reservation canceled");
    }
}
