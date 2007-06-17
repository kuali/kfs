/*
 * Created on Jul 21, 2004
 *
 */
package org.kuali.module.pdp.exception;

public class SystemUnavailableError extends PdpError {

  public SystemUnavailableError() {
    super();
  }

  public SystemUnavailableError(String message) {
    super(message);
  }

  public SystemUnavailableError(String message, Throwable arg1) {
    super(message, arg1);
  }

  public SystemUnavailableError(Throwable arg0) {
    super(arg0);
  }
}
