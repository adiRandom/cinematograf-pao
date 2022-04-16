package cli.handlers;

import cli.utils.BookingDetails;

import javax.naming.OperationNotSupportedException;

public class BuyTicketHandler extends AbstractBookingCommandHandler{

    @Override
    public void handleCommand() throws OperationNotSupportedException {
        boolean withBooking = this.getBooleanFromInput("Do you have a booking? Yes or No", "Yes", "No");
        if (withBooking) {
            int bookingId = this.getIntFromInput("Enter booking id");
            this.schedulingManager.buyTicket(bookingId);
        } else {
            BookingDetails bookingDetails = this.getDetailsForBooking(false, null);
            this.schedulingManager.buyTicket(bookingDetails);
        }
        System.out.println("Tickets bought");
    }
}
