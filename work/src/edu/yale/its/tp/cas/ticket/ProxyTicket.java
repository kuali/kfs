package edu.yale.its.tp.cas.ticket;

import java.util.*;

/**
 * Represents a CAS proxy ticket (PT).
 */
public class ProxyTicket extends ServiceTicket {

  //*********************************************************************
  // ProxyTicket-specific private state

  private ProxyGrantingTicket grantor;


  //*********************************************************************
  // Constructor

  /** Constructs a new, immutable proxy ticket. */
  public ProxyTicket(ProxyGrantingTicket t, String service) {
    /*
     * By convention, a proxy ticket is never taken to proceed from
     * an initial login.  (That is, "renew=true" will always fail for
     * a proxy ticket.)  Because of this, we pass "false" to the parent
     * class's constructor.
     */
    super(t, service, false);
    this.grantor = t;
  }


  //*********************************************************************
  // ProxyTicket-specific public interface

  /** Retrieves the proxy ticket's lineage -- its chain of "trust." */
  public List getProxies() {
    return grantor.getProxies();
  }

}
