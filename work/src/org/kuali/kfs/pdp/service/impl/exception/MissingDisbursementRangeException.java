/*
 * Created on Aug 24, 2004
 *
 */
package org.kuali.module.pdp.service;

/**
 * @author jsissom
 *
 */
public class MissingDisbursementRangeException extends Exception {
  public MissingDisbursementRangeException() {
    super();
  }

  public MissingDisbursementRangeException(String arg0) {
    super(arg0);
  }

  public MissingDisbursementRangeException(String arg0, Throwable arg1) {
    super(arg0, arg1);
  }

  public MissingDisbursementRangeException(Throwable arg0) {
    super(arg0);
  }
}
