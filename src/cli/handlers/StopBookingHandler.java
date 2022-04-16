package cli.handlers;

import javax.naming.OperationNotSupportedException;

public class StopBookingHandler extends AbstractCommandHandler{
    private final ListAllMovieRunsHandler listAllMovieRunsHandler;

    public StopBookingHandler(){
        this.listAllMovieRunsHandler = new ListAllMovieRunsHandler();
    }

    @Override
    public void handleCommand() throws OperationNotSupportedException {
        //Print all movies
        this.listAllMovieRunsHandler.handleCommand();
        int schedulingId = this.getIntFromInput("Pick a scheduling");
        this.schedulingManager.stopBooking(schedulingId);
        System.out.println("Stopped booking for scheduling with id" + schedulingId);
    }
}
