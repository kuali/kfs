/*
 * Created on Jul 2, 2004
 *
 */
package org.kuali.module.pdp.exception;

/**
 * @author jsissom
 *
 */
public class PdpOptimisticLockingException extends PdpException {

  /**
   * 
   */
  public PdpOptimisticLockingException() {
    super();
  }

  /**
   * @param message
   */
  public PdpOptimisticLockingException(String message) {
    super(message);
  }

}
