/*
 * Created on Mar 10, 2005
 *
 */
package org.kuali.kfs.module.purap.exception;

/**
 * @author local-jsissom
 *
 */
public class B2BRemoteError extends Error {

  /**
   * 
   */
  public B2BRemoteError() {
    super();
  }

  /**
   * @param arg0
   */
  public B2BRemoteError(String arg0) {
    super(arg0);
  }

  /**
   * @param arg0
   * @param arg1
   */
  public B2BRemoteError(String arg0, Throwable arg1) {
    super(arg0, arg1);
  }

  /**
   * @param arg0
   */
  public B2BRemoteError(Throwable arg0) {
    super(arg0);
  }
}
