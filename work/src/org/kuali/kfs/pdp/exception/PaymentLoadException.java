/*
 * Created on Jul 12, 2004
 *
 */
package org.kuali.module.pdp.exception;

import java.util.List;

/**
 * @author jsissom
 *
 */
public class PaymentLoadException extends PdpException {
  private List errors;

  public PaymentLoadException() {
    super();
  }

  public PaymentLoadException(String message) {
    super(message);
  }

  public List getErrors() {
    return errors;
  }

  public void setErrors(List errors) {
    this.errors = errors;
  }
}
