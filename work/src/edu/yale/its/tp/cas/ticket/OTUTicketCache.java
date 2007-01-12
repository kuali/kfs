package edu.yale.its.tp.cas.ticket;

/**
 * Represents a cache of tickets, each of which may be retrieved only
 * once.  That is, retrieval entails deletion.  Expiration also occurs
 * for inactivity.
 */
public abstract class OTUTicketCache extends ActiveTicketCache {

  /**
   * Constucts a new OTUTicketCache that will, additionally,
   * expire tickets after <i>tolerance</i> seconds of inactivity.
   */
  public OTUTicketCache(int tolerance) {
    super(tolerance);
  }

  // inherit Javadoc
  public Ticket getTicket(String ticketId) {
    Ticket t = super.getTicket(ticketId);
    deleteTicket(ticketId);
    return t;
  }
}
