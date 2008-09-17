package org.kuali.kfs.module.purap.exception;

/**
 * @author aapotts
 *
 */
public class FaxSubmissionError extends Error {

  /**
   * 
   */
  public FaxSubmissionError() {
    super();
  }

  /**
   * @param arg0
   */
  public FaxSubmissionError(String arg0) {
    super(arg0);
  }

  /**
   * @param arg0
   * @param arg1
   */
  public FaxSubmissionError(String arg0, Throwable arg1) {
    super(arg0, arg1);
  }

  /**
   * @param arg0
   */
  public FaxSubmissionError(Throwable arg0) {
    super(arg0);
  }

}
