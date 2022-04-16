package cli.handlers;


import cli.utils.BookingDetails;
import models.room.Room;
import utils.Pair;

import java.util.LinkedList;

/**
 * Command handler for booking related commands
 */
public abstract class AbstractBookingCommandHandler extends AbstractCommandHandler {
    private final ListAllMovieRunsHandler listAllMovieRunsHandler;

    protected AbstractBookingCommandHandler() {
        this.listAllMovieRunsHandler = new ListAllMovieRunsHandler();
    }

    /**
     * Get the scheduling id and seats for a booking
     *
     * @param wantToBuy True if this si for selling a ticket
     */
    protected BookingDetails
    getDetailsForBooking(boolean wantToBuy, Integer knownNumberOfSeats) {
        // List all the movies
        this.listAllMovieRunsHandler.handleCommand();
        int schedulingId = this.getIntFromInput("Pick a movie");
        boolean isMovie3D = this.schedulingManager.isSchedulingFor3D(schedulingId);
        int numberOfSeats = knownNumberOfSeats == null ? this.getIntFromInput("How many seats?") : knownNumberOfSeats;

        String canBook = this.schedulingManager.canBook(schedulingId, numberOfSeats, wantToBuy);
        if (canBook != null) {
            // Can book is the reason why the movie can't be booked., so print it
            System.out.println(canBook);
            return null;
        }

        Room room = this.schedulingManager.getRoomForRun(schedulingId);
        LinkedList<Pair<Integer, Integer>> seats = new LinkedList<>();

        System.out.println(room);
        for (int i = 0; i < numberOfSeats; i++) {
            seats.push(this.getPairOfIntsFromInput("Pick a seat. Enter the row number and column number."));
        }

        if (isMovie3D) {
            boolean wantGlasses = this.getBooleanFromInput("Do you want 3D glasses? Yes or No", "Yes", "No");
            return new BookingDetails(seats, schedulingId, wantGlasses);
        }

        return new BookingDetails(seats, schedulingId, false);
    }
}
