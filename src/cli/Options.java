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
    EDIT_ROOM,
    ADD_ROOM,
    DELETE_ROOM,
    ADD_SCHEDULING,
    START_MOVIE,
    /**
     * All unclaimed bookings are deleted and those seats can be used
     * No more bookings allowed for this run
     */
    STOP_BOOKING,
    GET_MOVIE_DETAILS
}
