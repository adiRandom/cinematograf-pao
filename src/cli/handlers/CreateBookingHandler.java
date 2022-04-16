package cli.handlers;

import cli.utils.BookingDetails;
import models.booking.Booking;

import javax.naming.OperationNotSupportedException;

public class CreateBookingHandler extends AbstractBookingCommandHandler{
    @Override
    public void handleCommand() throws OperationNotSupportedException {
        BookingDetails bookingDetails = this.getDetailsForBooking(false, null);

        Booking booking = this.schedulingManager.bookMovie(bookingDetails);
        System.out.println("Booking successful");
        System.out.println(booking);
    }
}
