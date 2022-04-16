package cli;

public enum Options {
    /**
     * List all movies
     */
    LIST_MOVIES,
    LIST_MOVIES_FOR_DATE,
    BOOK_MOVIE,
    BUY_TICKET,
    /**
     * Cancel both tickets and bookings
     */
    CANCEL_RESERVATION,
    MOVE_TICKET,
    ADD_MOVIE,
    SCHEDULE_MOVIE,
    ADD_ROOM,
    EDIT_ROOM,
    DELETE_ROOM,
    /**
     * All unclaimed bookings are deleted and those seats can be used
     * No more bookings allowed for this run
     */
    STOP_BOOKING,
    GET_MOVIE_DETAILS,
    EXIT;

    public static Options getOptionForCode(int code) {
        switch (code) {
            case 1: {
                return LIST_MOVIES_FOR_DATE;
            }
            case 2: {
                return BOOK_MOVIE;
            }
            case 3: {
                return BUY_TICKET;
            }
            case 4: {
                return CANCEL_RESERVATION;
            }
            case 5: {
                return MOVE_TICKET;
            }
            case 6: {
                return ADD_MOVIE;
            }
            case 7: {
                return SCHEDULE_MOVIE;
            }
            case 8: {
                return ADD_ROOM;
            }
            case 9: {
                return EDIT_ROOM;
            }
            case 10: {
                return DELETE_ROOM;
            }
            case 11: {
                return STOP_BOOKING;
            }
            case 12: {
                return GET_MOVIE_DETAILS;
            }
            case 13: {
                return EXIT;
            }
            default: {
                // Returns for 0 as well
                return LIST_MOVIES;
            }
        }
    }
}
