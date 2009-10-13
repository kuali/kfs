/*
 * Created on Mar 22, 2005
 *
 */
package org.kuali.kfs.module.purap.exception;

import org.kuali.rice.kns.exception.KualiException;

public class B2BConnectionException extends KualiException {

  public B2BConnectionException(String message) {
    super(message);
  }

  public B2BConnectionException(String message, Throwable t) {
    super(message, t);
  }

  public B2BConnectionException(Throwable t) {
    super(t);
  }

}
