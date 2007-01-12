package edu.yale.its.tp.cas.ticket;

/**
 * A cache may throw InvalidTicketException when it encounters a ticket
 * inappropriate for the specific set of tickets it stores.  For instance,
 * an attempt to store an ST in a TGC cache might lead to an
 * InvalidTicket Exception.
 */
public class InvalidTicketException extends TicketException {

  public InvalidTicketException() {
    super();
  }

  public InvalidTicketException(String s) {
    super(s);
  }

}
