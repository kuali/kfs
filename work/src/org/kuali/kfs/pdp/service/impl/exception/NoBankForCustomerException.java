/*
 * Created on Oct 4, 2004
 *
 */
package org.kuali.module.pdp.service;

/**
 * @author jsissom
 *
 */
public class NoBankForCustomerException extends Exception {
  public NoBankForCustomerException() {
    super();
  }

  /**
   * @param arg0
   */
  public NoBankForCustomerException(String arg0) {
    super(arg0);
  }
}
