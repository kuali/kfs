package org.kuali.module.pdp.exception;

public class PdpError extends Error {
  public PdpError() {
    super();
  }

  public PdpError(String message) {
    super(message);
  }

  public PdpError(String message, Throwable arg1) {
    super(message, arg1);
  }

  public PdpError(Throwable arg0) {
    super(arg0);
  }
}
