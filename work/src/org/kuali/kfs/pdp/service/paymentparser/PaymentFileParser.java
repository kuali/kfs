/*
 * Created on Jun 25, 2004
 *
 */
package org.kuali.module.pdp.xml;

import java.io.InputStream;

import org.kuali.module.pdp.exception.FileReadException;


/**
 * @author jsissom
 *
 */
public interface PaymentFileParser {
  public abstract void setFileHandler(PdpFileHandler fileHandler);
  public abstract void parse(String filename) throws FileReadException;
  public void parse(InputStream stream) throws FileReadException;
}