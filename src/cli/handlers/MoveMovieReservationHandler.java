package cli.handlers;

import cli.utils.BookingDetails;
import models.booking.Booking;

import javax.naming.OperationNotSupportedException;

public class MoveMovieReservationHandler extends AbstractBookingCommandHandler{
    @Override
    public void handleCommand() throws OperationNotSupportedException {
        int bookingId = this.getIntFromInput("Enter booking id");
        Booking booking = this.schedulingManager.getBooking(bookingId);
        // Get the details for the new booking
        BookingDetails bookingDetails =
                this.getDetailsForBooking(false, booking.getBookingSeats().size());

        this.schedulingManager.moveTicket(bookingId, bookingDetails);
        System.out.println("Tickets moved successfully");
    }
}
