package cli;

public enum Options {
    LIST_MOVIES,
    BOOK_MOVIE,
    BUY_TICKET,
    CANCEL_RESERVATION,
    MOVE_BOOKING,
    ADD_MOVIE,
    EDIT_ROOM,
    ADD_ROOM,
    DELETE_ROOM,
    ADD_SCHEDULING,
    START_MOVIE,
    FINISH_MOVIE,
    /**
     * All unclaimed bookings are deleted and those seats can be used
     * No more bookings allowed for this run
     */
    STOP_BOOKING,
    GET_MOVIE_DETAILS
}
