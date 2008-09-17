package org.kuali.kfs.module.purap.exception;

/**
 * @author aapotts
 *
 */
public class FaxServerUnavailableError extends Error {

  /**
   * 
   */
  public FaxServerUnavailableError() {
    super();
  }

  /**
   * @param arg0
   */
  public FaxServerUnavailableError(String arg0) {
    super(arg0);
  }

  /**
   * @param arg0
   * @param arg1
   */
  public FaxServerUnavailableError(String arg0, Throwable arg1) {
    super(arg0, arg1);
  }

  /**
   * @param arg0
   */
  public FaxServerUnavailableError(Throwable arg0) {
    super(arg0);
  }

}
