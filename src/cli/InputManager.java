package cli;

import cli.handlers.*;


import javax.naming.OperationNotSupportedException;
import java.util.*;


public class InputManager {
    private boolean isDone;
    private Scanner inputScanner;
    AddMovieHandler addMovieHandler;
    AddRoomHandler addRoomHandler;
    BuyTicketHandler buyTicketHandler;
    CancelReservationHandler cancelReservationHandler;
    CreateBookingHandler createBookingHandler;
    EditRoomHandler editRoomHandler;
    ListAllMovieRunsHandler listAllMovieRunsHandler;
    ListMoviesForDayHandler listMoviesForDayHandler;
    MoveMovieReservationHandler moveMovieReservationHandler;
    PrintMovieDetailsHandler printMovieDetailsHandler;
    RemoveRoomHandler removeRoomHandler;
    ScheduleMovieHandler scheduleMovieHandler;
    StopBookingHandler stopBookingHandler;

    public InputManager() {
        this.inputScanner = new Scanner(System.in);
        this.isDone = false;
        this.addMovieHandler = new AddMovieHandler();
        this.addRoomHandler = new AddRoomHandler();
        this.buyTicketHandler = new BuyTicketHandler();
        this.cancelReservationHandler = new CancelReservationHandler();
        this.createBookingHandler = new CreateBookingHandler();
        this.editRoomHandler = new EditRoomHandler();
        this.listAllMovieRunsHandler = new ListAllMovieRunsHandler();
        this.listMoviesForDayHandler = new ListMoviesForDayHandler();
        this.moveMovieReservationHandler = new MoveMovieReservationHandler();
        this.printMovieDetailsHandler = new PrintMovieDetailsHandler();
        this.removeRoomHandler = new RemoveRoomHandler();
        this.scheduleMovieHandler = new ScheduleMovieHandler();
        this.stopBookingHandler = new StopBookingHandler();
    }

    private void showTopLevelCommands() {
        System.out.println("1. List all movies");
        System.out.println("2. List movies for day");
        System.out.println("3. Book movie");
        System.out.println("4. Buy tickets");
        System.out.println("5. Cancel reservation");
        System.out.println("6. Move booking");
        System.out.println("7. Add movie");
        System.out.println("8. Schedule movie");
        System.out.println("9. Add room");
        System.out.println("10. Edit room");
        System.out.println("11. Remove room");
        System.out.println("12. Stop booking");
        System.out.println("13. Get movie details");
        System.out.println("14. Exit");
        System.out.print(">> ");
    }

    private void getCommand() {
        int commandCode = this.inputScanner.nextInt();
        // Get the enum code
        commandCode--;
        Options command = Options.getOptionForCode(commandCode);
        this.handleCommand(command);
    }

    private void handleCommand(Options command) {
        try {
            switch (command) {
                case LIST_MOVIES: {
                    // This prints a report as well
                    this.listAllMovieRunsHandler.handleCommand();
                    break;
                }
                case LIST_MOVIES_FOR_DATE: {
                    // This prints a report as well

                    this.listMoviesForDayHandler.handleCommand();
                    break;
                }
                case BOOK_MOVIE: {
                    this.createBookingHandler.handleCommand();
                    break;
                }
                case BUY_TICKET: {
                    this.buyTicketHandler.handleCommand();
                    break;
                }
                case CANCEL_RESERVATION: {
                    this.cancelReservationHandler.handleCommand();
                    break;
                }
                case MOVE_TICKET: {
                    this.moveMovieReservationHandler.handleCommand();
                    break;
                }
                case ADD_MOVIE: {
                    this.addMovieHandler.handleCommand();
                    break;
                }
                case ADD_ROOM: {
                    this.addRoomHandler.handleCommand();
                    break;
                }
                case SCHEDULE_MOVIE: {
                    this.scheduleMovieHandler.handleCommand();
                    break;
                }
                case GET_MOVIE_DETAILS: {
                    // This prints a report as well
                    this.printMovieDetailsHandler.handleCommand();
                    break;
                }
                case STOP_BOOKING: {
                    this.stopBookingHandler.handleCommand();
                    break;
                }
                case DELETE_ROOM: {
                    this.removeRoomHandler.handleCommand();
                    break;
                }
                case EDIT_ROOM: {
                    this.editRoomHandler.handleCommand();
                    break;
                }
                case EXIT: {
                    this.isDone = true;
                    break;
                }
                default: {
                    break;
                }
            }
        } catch (IllegalArgumentException | OperationNotSupportedException e) {
            System.out.println(e.getMessage());
        }

    }


    public boolean getIsDone() {
        return this.isDone;
    }

    public void getInput() {
        this.showTopLevelCommands();
        this.getCommand();
    }


}
