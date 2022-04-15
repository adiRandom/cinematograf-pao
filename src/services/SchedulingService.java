package services;

import lib.scheduling.utils.MovieScheduling;
import models.movie.Movie;
import models.room.RoomType;
import models.room.RoomView;
import repository.MovieRepository;
import repository.RoomViewRepository;
import utils.Pair;

import javax.naming.OperationNotSupportedException;
import java.util.*;

public class SchedulingService {
    private RoomViewRepository roomViewRepository;
    /**
     * All scehdulings for each room
     */
    private final HashMap<Integer, ArrayList<MovieScheduling>> movieSchedulings;
    /**
     * A mapping between scheduling id and Scheduling
     */
    private final HashMap<Integer, MovieScheduling> allSchedulings;


    public SchedulingService(HashMap<Integer, ArrayList<MovieScheduling>> movieSchedulings, HashMap<Integer, MovieScheduling> allSchedulings) {
        this.roomViewRepository = RoomViewRepository.getInstance();
        this.movieSchedulings = movieSchedulings;
        this.allSchedulings = allSchedulings;
    }


    /**
     * Create a scheduling for a movie at a date in a room
     *
     * @param index The index in the scheduling  list where this new scheduling should be inserted to keep it sorted
     * @return the scheduling id
     */

    public int createScheduling(Movie movie, Date date, RoomView roomView, int index) {

        MovieScheduling movieScheduling = new MovieScheduling(movie.getId(), date,
                // calculate the end date
                new Date(date.getTime() + (long) movie.getDuration() * 60 * 1000),
                roomView);

        ArrayList<MovieScheduling> bookingList = this.movieSchedulings.get(roomView.getId());
        if (bookingList == null) {
            // First booking for this room
            bookingList = new ArrayList<MovieScheduling>();
            bookingList.add(movieScheduling);
            this.movieSchedulings.put(roomView.getId(), bookingList);
        } else {
            if (bookingList.size() <= index) {
                bookingList.add(movieScheduling);
            } else {
                bookingList.add(index, movieScheduling);
            }
        }

        this.allSchedulings.put(movieScheduling.getMovieId(), movieScheduling);
        return movieScheduling.getId();
    }

    /**
     * Find the first available spot for that duration and book a room
     *
     * @param roomType The type of room
     * @param movie    The movie to be scheduled
     * @return The scheduling id
     */
    public int scheduleMovie(RoomType roomType, Movie movie) throws IllegalArgumentException {
        // get all the rooms for this type
        List<RoomView> compatibleRooms = roomViewRepository.whereAll(room -> room.getType() == roomType);
        if (compatibleRooms.size() == 0) {
            throw new IllegalArgumentException("No room for this type of movie");
        }


        // Hold the best fitting room
        Date soonestDateAvailable = null;
        RoomView chosenRoomView = null;
        int newBookingIndex = 0;

        // Go through all rooms and find the one that that can book this movie sooner
        for (RoomView room : compatibleRooms) {
            ArrayList<MovieScheduling> movieSchedulingsForRoom = this.movieSchedulings.get(room.getId());

            // First movie inserted for this room
            // Nothing can be sooner than the current time so set the current date as the soonest available
            if (movieSchedulingsForRoom == null || movieSchedulingsForRoom.size() == 0) {
                soonestDateAvailable = new Date();
                chosenRoomView = room;
                break;
            }


            // Find the first available spot long enough for this movie
            for (int i = 0; i < movieSchedulingsForRoom.size(); i++) {
                Date availableDate = null;

                if (i == movieSchedulingsForRoom.size() - 1) {
                    // Nothing after this booking, we can use this spot
                    availableDate = movieSchedulingsForRoom.get(i).getEndTime();
                } else {

                    if (i == 0) {
                        // Also try to fit the movie between Now and the first scheduling start time
                        Date now = new Date();
                        long spotSize = movieSchedulingsForRoom.get(i).getStartTime().getTime() -
                                now.getTime();
                        long spotSizeInMinutes = spotSize / 1000 / 60;
                        if (spotSizeInMinutes >= movie.getDuration()) {
                            availableDate = now;
                        }

                        // Check if we can fit the scheduling as the first scheduling of the room before moving on to fitting it between scehdulings
                        if (availableDate != null && (soonestDateAvailable == null ||
                                availableDate.before(soonestDateAvailable))) {
                            soonestDateAvailable = availableDate;
                            // Remember where we should insert the scheduling if this is the soonest available date
                            newBookingIndex = 0;
                            chosenRoomView = room;
                            break;
                        }
                    }

                    //See if we can squeeze in the movie between the current scheduling and the next one
                    long spotSize = movieSchedulingsForRoom.get(i + 1).getStartTime().getTime() -
                            movieSchedulingsForRoom.get(i).getEndTime().getTime();

                    long spotSizeInMinutes = spotSize / 1000 / 60;
                    if (spotSizeInMinutes >= movie.getDuration()) {
                        availableDate = movieSchedulingsForRoom.get(i).getEndTime();
                    }
                }

                // If it's the first room picked or we found a better date for the scehduling, save the info of the room
                if (availableDate != null && (soonestDateAvailable == null ||
                        availableDate.before(soonestDateAvailable))) {
                    soonestDateAvailable = availableDate;
                    // Remember where we should insert the scheduling if this is the soonest available date
                    newBookingIndex = i + 1;
                    chosenRoomView = room;
                    break;
                }
            }

        }


        return createScheduling(movie, soonestDateAvailable, chosenRoomView, newBookingIndex);

    }

    public Pair<Long, Long> getDateIntervalForDay(Date date) {
        // Get the permitted dates
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long permittedStartDate = calendar.getTimeInMillis();

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        long permittedEndDate = calendar.getTimeInMillis();
        return new Pair<>(permittedStartDate, permittedEndDate);
    }

    /**
     * Find a room that can hold this movie at a given date
     *
     * @param roomType The type of room
     * @param movie    The movie to be scheduled
     * @param exact    If true, it will schedule at exact time and day. If false, only the day will be taken into account
     * @return The scheduling id
     */
    public int scheduleMovie(RoomType roomType, Movie movie, Date date, boolean exact) throws OperationNotSupportedException, IllegalArgumentException {
        // get all the rooms for this type
        List<RoomView> compatibleRoomViews = roomViewRepository.whereAll(room -> room.getType() == roomType);
        if (compatibleRoomViews.size() == 0) {
            throw new IllegalArgumentException("No room for this type of movie");
        }

        // Hold the best fitting room
        Date soonestDateAvailable = null;
        RoomView chosenRoomView = null;
        int newBookingIndex = 0;

        // Go through all rooms and find the one that that can book this movie sooner
        if (exact) {
            if (date.getTime() < new Date().getTime()) {
                // Don't allow a scehduling in the past
                throw new IllegalArgumentException("This date is in the past");
            }

            long startTime = date.getTime();
            // Find a room free at that exact date
            for (RoomView room : compatibleRoomViews) {
                ArrayList<MovieScheduling> movieSchedulingsForRoom = this.movieSchedulings.get(room.getId());

                // First movie inserted for this room
                // The exact date specified is available
                if (movieSchedulingsForRoom == null || movieSchedulingsForRoom.size() == 0) {
                    soonestDateAvailable = date;
                    chosenRoomView = room;
                    break;
                }

                // Find the first available spot long enough for this movie
                for (int i = 0; i < movieSchedulingsForRoom.size(); i++) {
                    if (i == movieSchedulingsForRoom.size() - 1) {
                        // Nothing after this booking, we could use this spot
                        long currentEndTime = movieSchedulingsForRoom.get(i).getEndTime().getTime();


                        if (currentEndTime <= startTime) {
                            soonestDateAvailable = date;
                            chosenRoomView = room;
                            newBookingIndex = i + 1;
                            break;
                        }

                    } else {

                        if (i == 0) {
                            // See if the specified date is between Now and the start time of the first scheduling
                            Date now = new Date();
                            long spotSize = movieSchedulingsForRoom.get(i).getStartTime().getTime() -
                                    now.getTime();
                            long spotSizeInMinutes = spotSize / 1000 / 60;

                            // Check if we can fit the scheduling as the first scheduling of the room before moving on to fitting it between scehdulings
                            if (movieSchedulingsForRoom.get(i).getStartTime().getTime() <= startTime &&
                                    now.getTime() >= startTime &&
                                    spotSizeInMinutes >= movie.getDuration()) {

                                soonestDateAvailable = date;
                                chosenRoomView = room;
                                newBookingIndex = 0;
                                break;
                            }
                        }

                        // See if date is between this scheduling and the next
                        long currentEndTime = movieSchedulingsForRoom.get(i).getEndTime().getTime();
                        long nextStartTime = movieSchedulingsForRoom.get(i + 1).getStartTime().getTime();
                        //See if we can squeeze in this duration
                        long spotSize = movieSchedulingsForRoom.get(i + 1).getStartTime().getTime() -
                                movieSchedulingsForRoom.get(i).getEndTime().getTime();

                        long spotSizeInMinutes = spotSize / 1000 / 60;

                        if (currentEndTime <= startTime &&
                                nextStartTime >= startTime &&
                                spotSizeInMinutes >= movie.getDuration()) {
                            soonestDateAvailable = date;
                            chosenRoomView = room;
                            newBookingIndex = i + 1;
                            break;
                        }

                    }
                }
            }


        } else {
            // Get the permitted dates
            Pair<Long, Long> permittedDate = getDateIntervalForDay(date);
            long permittedStartDate = permittedDate.getFirst();
            long permittedEndDate = permittedDate.getSecond();

            long todayStartTime = getDateIntervalForDay(new Date()).getFirst();

            if (permittedStartDate < todayStartTime) {
                // Don't allow a scheduling in the past
                throw new IllegalArgumentException("This date is in the past");
            }

            for (RoomView room : compatibleRoomViews) {
                ArrayList<MovieScheduling> movieSchedulingsForRoom = this.movieSchedulings.get(room.getId());
                // First movie inserted for this room
                // Set the start time as the date for the scheduling
                if (movieSchedulingsForRoom == null || movieSchedulingsForRoom.size() == 0) {
                    soonestDateAvailable = new Date(permittedStartDate);
                    chosenRoomView = room;
                    break;
                }

                // Find the first available spot long enough for this movie
                for (int i = 0; i < movieSchedulingsForRoom.size(); i++) {
                    Date availableDate = null;
                    MovieScheduling movieScheduling = movieSchedulingsForRoom.get(i);


                    // Check if we went past the allowed interval
                    if (movieScheduling.getEndTime().getTime() > permittedEndDate) {
                        // From this point onwards, all available spots for this room will be past the allowed interval
                        break;
                    }

                    if (i == 0) {
                        // Also try to fit the movie between Now and the first scheduling start time
                        // Now will hold either the current time or the permited start time of the schedling,
                        // whichever is later

                        Date now = new Date();
                        if (now.getTime() < permittedStartDate) {
                            now = new Date(permittedStartDate);
                        }

                        // If we picked the permittedStartDate as now and the first scheduling starts after that,
                        // spotSize will be negative
                        // and the next if will prevent us from picking that interval as the scehduling date
                        long spotSize = movieSchedulingsForRoom.get(i).getStartTime().getTime() -
                                now.getTime();
                        long spotSizeInMinutes = spotSize / 1000 / 60;
                        if (spotSizeInMinutes >= movie.getDuration()) {
                            availableDate = now;
                        }

                        // Check if we can fit the scheduling as the first scheduling of the room before moving on to fitting it between scehdulings
                        if (availableDate != null && (soonestDateAvailable == null ||
                                availableDate.before(soonestDateAvailable))) {
                            soonestDateAvailable = availableDate;
                            // Remember where we should insert the scheduling if this is the soonest available date
                            newBookingIndex = 0;
                            chosenRoomView = room;
                            break;
                        }
                    }


                    // Check if this movie ends in the permitted interval
                    // this will mean that the possible spot is in the interval
                    if (movieScheduling.getEndTime().getTime() >= permittedStartDate) {

                        // A possible spot will be in the allowed interval
                        if (i == movieSchedulingsForRoom.size() - 1) {
                            // Nothing after this booking, we can use this spot
                            availableDate = movieScheduling.getEndTime();
                        } else {

                            //See if we can squeeze in this duration
                            long spotSize = movieSchedulingsForRoom.get(i + 1).getStartTime().getTime() -
                                    movieScheduling.getEndTime().getTime();

                            long spotSizeInMinutes = spotSize / 1000 / 60;
                            if (spotSizeInMinutes >= movie.getDuration()) {
                                availableDate = movieSchedulingsForRoom.get(i).getEndTime();
                            }
                        }
                    }


                    if (availableDate != null && (soonestDateAvailable == null ||
                            availableDate.before(soonestDateAvailable))) {
                        soonestDateAvailable = availableDate;
                        // Remember where we should insert the scheduling if this is the soonest available date
                        newBookingIndex = i + 1;
                        chosenRoomView = room;
                        break;
                    }
                }
            }


        }
        if (soonestDateAvailable != null) {
            // We can book this movie at that time
            return createScheduling(movie, soonestDateAvailable, chosenRoomView, newBookingIndex);
        } else {
            throw new OperationNotSupportedException("No room available for this movie at this time");
        }

    }

    public void cancelRun(int schedulingId) {
        MovieScheduling scheduling = allSchedulings.get(schedulingId);
        if (scheduling != null) {
            // Remove from the list of scheduling of the room
            movieSchedulings.get(scheduling.getRoom().getId()).remove(scheduling);
            allSchedulings.remove(schedulingId);
        }
    }
}
