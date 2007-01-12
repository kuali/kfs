package edu.yale.its.tp.cas.ticket;

/**
 * A cache may throw DuplicateTicketException if it encounters an attempt
 * to add a duplicate ticket.
 */
public class DuplicateTicketException extends TicketException { 

  public DuplicateTicketException() {
    super();
  }

}
