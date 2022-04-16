package cli;

import cli.utils.BookingDetails;
import lib.scheduling.SchedulingManager;
import lib.scheduling.utils.MovieScheduling;
import models.booking.Booking;
import models.movie.Movie;
import models.movie.utils.MovieBuilder;
import models.room.Room;
import models.room.RoomType;
import models.room.RoomView;
import repository.MovieRepository;
import repository.RoomViewRepository;
import utils.Pair;

import javax.naming.OperationNotSupportedException;
import java.text.SimpleDateFormat;
import java.util.*;


// TODO: break into handler class
public class InputManager {
    private boolean isDone;
    private Scanner inputScanner;
    private SchedulingManager schedulingManager;
    private MovieRepository movieRepository;
    private RoomViewRepository roomViewRepository;

    public InputManager() {
        this.inputScanner = new Scanner(System.in);
        this.isDone = false;
        schedulingManager = SchedulingManager.getInstance();
        this.movieRepository = MovieRepository.getInstance();
        this.roomViewRepository = RoomViewRepository.getInstance();
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

    private String getStringFromInput(String prompt) {
        System.out.println(prompt);
        System.out.print(">> ");
        return this.inputScanner.next();
    }

    private int getIntFromInput(String prompt) {
        System.out.println(prompt);
        System.out.print(">> ");
        return this.inputScanner.nextInt();
    }

    private boolean getBooleanFromInput(String prompt, String affirmative, String negative) {
        String input = "";
        while (!input.equals(affirmative) && !input.equals(negative)) {
            input = this.getStringFromInput(prompt);
        }
        return input.equals(affirmative);
    }

    /**
     * @param withTimePrompt Whether to ask for the hour and minute
     * @return The date from the input and a boolean if the hour was set
     */
    private Pair<Date, Boolean> getDateFromInput(boolean withTimePrompt) {
        int day = getIntFromInput("What day (1-31)?");
        // January is 0
        int month = getIntFromInput("What month (1-12)?") - 1;
        int year = getIntFromInput("What year?");

        int hour = 0;
        int minute = 0;

        if (withTimePrompt) {
            hour = getIntFromInput("What hour (24H)? Enter -1 to pick the time automatically");
            if (hour != -1) {
                minute = getIntFromInput("What minute?");
            }
        }

        Calendar calendar = new GregorianCalendar();
        if (hour != -1) {
            calendar.set(year, month, day, hour, minute);
        } else {
            calendar.set(year, month, day);
        }

        boolean isHourSet = withTimePrompt && hour != -1;
        return new Pair(calendar.getTime(), isHourSet);
    }

    private Pair<Integer, Integer> getPairOfIntsFromInput(String prompt) {
        System.out.println(prompt);
        System.out.print(">> ");
        return new Pair<>(this.inputScanner.nextInt(), this.inputScanner.nextInt());
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
                    this.handleListMovieRuns();
                    break;
                }
                case LIST_MOVIES_FOR_DATE: {
                    this.handleListMoviesForDate();
                    break;
                }
                case BOOK_MOVIE: {
                    this.handleCreateBooking();
                    break;
                }
                case BUY_TICKET: {
                    this.handleBuyTicket();
                    break;
                }
                case CANCEL_RESERVATION: {
                    this.handleCancelReservation();
                    break;
                }
                case MOVE_TICKET: {
                    this.handleMoveReservation();
                    break;
                }
                case ADD_MOVIE: {
                    this.handleAddMovie();
                    break;
                }
                case ADD_ROOM: {
                    this.handleAddRoom();
                    break;
                }
                case SCHEDULE_MOVIE: {
                    this.handleScheduleMovie();
                    break;
                }
                case GET_MOVIE_DETAILS: {
                    this.handlePrintMovieDetails();
                    break;
                }
                case STOP_BOOKING: {
                    this.handleStopBooking();
                    break;
                }
                case DELETE_ROOM: {
                    this.handleRemoveRoom();
                    break;
                }
                case EDIT_ROOM: {
                    this.handleEditRoom();
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

    private void printMovieRuns(HashSet<Pair<Movie, MovieScheduling>> movieRuns) {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        for (Pair<Movie, MovieScheduling> run : movieRuns) {
            Movie movie = run.getFirst();
            MovieScheduling scheduling = run.getSecond();
            System.out.println(scheduling.getId() + ". " + movie.getTitle() + " at " + format.format(scheduling.getStartTime()));
        }
        System.out.println("------");
    }


    private void handleListMovieRuns() {
        HashSet<Pair<Movie, MovieScheduling>> allRuns = schedulingManager.getRuns();
        printMovieRuns(allRuns);
    }


    private void handleListMoviesForDate() {
        Date date = this.getDateFromInput(false).getFirst();
        HashSet<Pair<Movie, MovieScheduling>> allRuns = schedulingManager.getRunsForDay(date);
        printMovieRuns(allRuns);
    }

    private void handleAddMovie() throws IllegalArgumentException {
        boolean is3D;
        MovieBuilder movieBuilder = new MovieBuilder();

        movieBuilder.withTitle(this.getStringFromInput("What is the title of the movie"));

        String actor = "";

        while (!actor.equals("Done")) {
            actor = this.getStringFromInput("Who's starring. Enter \"Done\" when finished");
            if (!actor.equals("Done")) {
                movieBuilder.withStarActor(actor);
            }
        }

        movieBuilder.withRating(this.getIntFromInput("Give it a rating from 0 to 10"));
        movieBuilder.withDuration(this.getIntFromInput("How long is the movie (in minutes)?"));
        movieBuilder.withStudio(this.getStringFromInput("What studio made the movie?"));

        is3D = this.getBooleanFromInput("Is the movie 3D? Yes or No", "Yes", "No");
        movieBuilder.is3D(is3D);

        if (is3D) {
            movieBuilder.withType(this.getStringFromInput("What type of movie is this? REGULAR, IMAX or MOVIE_4DX"));
        } else {
            movieBuilder.withType(this.getStringFromInput("What type of movie is this? REGULAR or MOVIE_4DX"));
        }
        Movie movie = movieBuilder.build();
        this.movieRepository.insertItem(movie.getId(), movie);
        System.out.println("Movie added!");
    }

    private void handleAddRoom() {
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

    private void handleScheduleMovie() throws OperationNotSupportedException {
        List<Movie> movies = movieRepository.getAll();
        for (Movie movie : movies) {

            System.out.println(movie.getId() + ". " + movie.getTitle());
        }
        Movie pickedMovie = null;
        while (pickedMovie == null) {
            int movieId = this.getIntFromInput("Pick a movie by its id");
            pickedMovie = movieRepository.getItemWithId(movieId);
        }
        boolean withDate = this.getBooleanFromInput("Do you want to schedule it at a particular date? Yes or No", "Yes", "No");
        if (!withDate) {
            int schedulingId = schedulingManager.scheduleMovie(pickedMovie);
            System.out.println("Scheduling successful. Id: " + Integer.toString(schedulingId));
        } else {
            Pair<Date, Boolean> dateFromInput = this.getDateFromInput(true);
            int schedulingId = schedulingManager.scheduleMovie(pickedMovie,
                    dateFromInput.getFirst(),
                    dateFromInput.getSecond());
            System.out.println("Scheduling successful. Id: " + schedulingId);
        }
    }

    /**
     * Get the scheduling id and seats for a booking
     *
     * @param wantToBuy True if this si for selling a ticket
     */
    private BookingDetails
    getDetailsForBooking(boolean wantToBuy, Integer knownNumberOfSeats) {
        this.handleListMovieRuns();
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


    /**
     * Handle picking a movie and seats to book tickets
     */
    private void handleCreateBooking() {
        BookingDetails bookingDetails = this.getDetailsForBooking(false, null);

        Booking booking = this.schedulingManager.bookMovie(bookingDetails);
        System.out.println("Booking successful");
        System.out.println(booking);

    }


    private void handleBuyTicket() {
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


    /**
     * Cancel booking or bought tickets
     */
    private void handleCancelReservation() {
        int bookingId = this.getIntFromInput("Enter booking id");
        this.schedulingManager.cancelBooking(bookingId);
        System.out.println("Reservation canceled");
    }

    private void handleMoveReservation() {
        int bookingId = this.getIntFromInput("Enter booking id");
        Booking booking = this.schedulingManager.getBooking(bookingId);
        // Get the details for the new booking
        BookingDetails bookingDetails =
                this.getDetailsForBooking(false, booking.getBookingSeats().size());

        this.schedulingManager.moveTicket(bookingId, bookingDetails);
        System.out.println("Tickets moved successfully");
    }

    private void handlePrintMovieDetails() {
        List<Movie> movies = this.movieRepository.getAll();
        for (Movie movie : movies) {
            System.out.println(movie.getId() + ". " + movie.getTitle());
        }
        System.out.println("------");
        int movieId = this.getIntFromInput("Pick a movie");

        Movie movie = this.movieRepository.getItemWithId(movieId);
        if (movie == null) {
            throw new IllegalArgumentException("No movie with that id");
        }

        System.out.println(movie);
    }

    public void handleStopBooking() {
        this.handleListMovieRuns();
        int schedulingId = this.getIntFromInput("Pick a scheduling");
        this.schedulingManager.stopBooking(schedulingId);
        System.out.println("Stopped booking for scheduling with id" + schedulingId);
    }

    public void handleEditRoom() {
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

    public void handleRemoveRoom() {
        List<RoomView> rooms = this.roomViewRepository.getAll();

        for (RoomView roomView : rooms) {
            System.out.println("Room " + roomView.getId());
        }
        System.out.println("--------");

        int roomId = this.getIntFromInput("Pick a room to edit with its number");

        this.roomViewRepository.deleteItem(roomId);
        System.out.println("Room removed.");

    }

    public boolean getIsDone() {
        return this.isDone;
    }

    public void getInput() {
        this.showTopLevelCommands();
        this.getCommand();
    }


}
