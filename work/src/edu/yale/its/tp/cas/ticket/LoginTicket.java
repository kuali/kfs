package edu.yale.its.tp.cas.ticket;

/**
  * LoginTicket has no internal state.  It is a dummy ticket that allows
  * the one-time-use cache functions to be used with "login tickets."
  */

public class LoginTicket extends Ticket {

    public String getUsername() {
	return null;
    }
}
