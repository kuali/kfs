/*
 * Created on Mar 22, 2005
 *
 */
package org.kuali.kfs.module.purap.exception;

import org.kuali.rice.core.api.exception.KualiException;

public class B2BShoppingException extends KualiException {

  public B2BShoppingException(String message) {
    super(message);
  }

  public B2BShoppingException(String message, Throwable t) {
    super(message, t);
  }

  public B2BShoppingException(Throwable t) {
    super(t);
  }

}
