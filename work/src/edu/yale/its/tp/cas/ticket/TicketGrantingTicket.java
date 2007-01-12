package edu.yale.its.tp.cas.ticket;

/**
 * Represents a CAS ticket-granting ticket, typically vended as a cookie
 * (TGC).  This class represents, in the general sense, a ticket that
 * is used to grant other ticket; it becomes a "powerful" TGC only when
 * vended as such and stored in the TGC cache.
 */
public class TicketGrantingTicket extends Ticket {

  //*********************************************************************
  // Private, ticket state

  private String username;
  private boolean expired;

  //*********************************************************************
  // Constructor

  /** Constructs a new, immutable service ticket. */
  public TicketGrantingTicket(String username) {
    this.username = username;
    this.expired = false;
  }


  //*********************************************************************
  // Public interface

  /** Retrieves the ticket's username. */
  public String getUsername() {
    return username;
  }

  /**
   * Markes the ticket as expired, preventing its further use and
   * the validity of subordinate tickets "downstream" from it.
   */ 
  public void expire() {
    this.expired = true;
  }

  /** Returns true if the ticket is expired, false otherwise. */
  public boolean isExpired() {
    return expired;
  }

}
