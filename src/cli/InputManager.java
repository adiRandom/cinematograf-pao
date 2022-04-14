package cli;

import lib.scheduling.SchedulingManager;
import models.movie.Movie;
import models.movie.Movie3DType;
import models.movie.utils.MovieBuilder;
import models.room.RoomType;
import models.room.RoomView;
import repository.MovieRepository;

import java.util.LinkedList;
import java.util.Scanner;

public class InputManager {
    private boolean isDone;
    private Scanner inputScanner;
    private SchedulingManager schedulingManager;
    private MovieRepository movieRepository;

    public InputManager() {
        this.inputScanner = new Scanner(System.in);
        this.isDone = false;
        schedulingManager = SchedulingManager.getInstance();
        this.movieRepository = MovieRepository.getInstance();
    }

    private void showTopLevelCommands() {
        System.out.println("1. List all movies");
        System.out.println("2. List movies for date");
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
                case ADD_MOVIE: {
                    this.handleAddMovie();
                    break;
                }
                case ADD_ROOM: {
                    this.handleAddRoom();
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
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

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

    public boolean getIsDone() {
        return this.isDone;
    }

    public void getInput() {
        this.showTopLevelCommands();
        this.getCommand();
    }

}
