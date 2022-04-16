package cli.handlers;

import lib.scheduling.SchedulingManager;
import repository.MovieRepository;
import repository.RoomViewRepository;
import utils.Pair;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;

public abstract class AbstractCommandHandler implements CommandHandler {
    private Scanner inputScanner;
    protected SchedulingManager schedulingManager;
    protected MovieRepository movieRepository;
    protected RoomViewRepository roomViewRepository;

    public AbstractCommandHandler() {
        this.inputScanner = new Scanner(System.in);
        schedulingManager = SchedulingManager.getInstance();
        this.movieRepository = MovieRepository.getInstance();
        this.roomViewRepository = RoomViewRepository.getInstance();
    }


    protected String getStringFromInput(String prompt) {
        System.out.println(prompt);
        System.out.print(">> ");
        return this.inputScanner.next();
    }

    protected int getIntFromInput(String prompt) {
        System.out.println(prompt);
        System.out.print(">> ");
        return this.inputScanner.nextInt();
    }

    protected boolean getBooleanFromInput(String prompt, String affirmative, String negative) {
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
    protected Pair<Date, Boolean> getDateFromInput(boolean withTimePrompt) {
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

    protected Pair<Integer, Integer> getPairOfIntsFromInput(String prompt) {
        System.out.println(prompt);
        System.out.print(">> ");
        return new Pair<>(this.inputScanner.nextInt(), this.inputScanner.nextInt());
    }

}
