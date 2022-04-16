package services;

import lib.scheduling.utils.MovieScheduling;
import models.booking.Booking;
import models.movie.Movie;
import models.room.Room;
import repository.MovieRepository;
import utils.Pair;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

public class SchedulingInfoService {
    private MovieRepository movieRepository;

    private final SchedulingService schedulingService;
    /**
     * A mapping between scheduling id and Scheduling
     */
    private final HashMap<Integer, MovieScheduling> allSchedulings;
    private final HashMap<Integer, Booking> allBookings;


    public SchedulingInfoService(SchedulingService schedulingService,
                                 HashMap<Integer, MovieScheduling> allSchedulings,
                                 HashMap<Integer, Booking> allBookings) {
        this.allBookings = allBookings;
        this.movieRepository = MovieRepository.getInstance();
        this.schedulingService = schedulingService;
        this.allSchedulings = allSchedulings;
    }

    /**
     * Get all the movie from a day
     *
     * @param date The day for which we are querying. Hours, minutes and seconds aren't relevant for this param
     * @return All the movies and the scheduling info
     */
    public HashSet<Pair<Movie, MovieScheduling>> getRunsForDay(Date date) {
        // Get the permitted dates
        Pair<Long, Long> permittedDate = this.schedulingService.getDateIntervalForDay(date);
        long permittedStartDate = permittedDate.getFirst();
        long permittedEndDate = permittedDate.getSecond();

        HashSet<Pair<Movie, MovieScheduling>> moviesForDay = new HashSet<>();

        for (MovieScheduling scheduling : this.allSchedulings.values()) {
            if (scheduling.getStartTime().getTime() >= permittedStartDate &&
                    scheduling.getEndTime().getTime() <= permittedEndDate) {
                moviesForDay.add(new Pair<>(this.movieRepository.getItemWithId(scheduling.getMovieId()),
                        scheduling));
            }
        }

        return moviesForDay;
    }

    /**
     * Get all the movie that will run from today on.
     *
     * @return All the movies and the scheduling info
     */
    public HashSet<Pair<Movie, MovieScheduling>> getRuns() {
        Date today = new Date();

        HashSet<Pair<Movie, MovieScheduling>> moviesForDay = new HashSet<>();

        for (MovieScheduling scheduling : this.allSchedulings.values()) {
            if (scheduling.getStartTime().getTime() >= today.getTime()) {
                moviesForDay.add(new Pair<>(this.movieRepository.getItemWithId(scheduling.getMovieId()),
                        scheduling));
            }
        }

        return moviesForDay;
    }

    /**
     * Return the room for a scheduling holding the booking info
     *
     * @param schedulingId
     */
    public Room getRoomForRun(int schedulingId) {
        MovieScheduling scheduling = allSchedulings.get(schedulingId);
        if (scheduling == null) {
            return null;
        }
        return scheduling.getRoom();
    }

    public Booking getBooking(int bookingId) throws IllegalArgumentException {
        Booking booking = this.allBookings.get(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException("No booking with this id");
        }
        return booking;
    }


    public boolean isSchedulingFor3D(int schedulingId) {
        MovieScheduling movieScheduling = this.allSchedulings.get(schedulingId);
        if (movieScheduling == null) {
            throw new IllegalArgumentException("No shceduling with this id");
        }
        int movieId = movieScheduling.getMovieId();
        Movie movie = this.movieRepository.getItemWithId(movieId);
        return movie.is3D();

    }
}
