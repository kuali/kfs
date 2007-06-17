package org.kuali.module.pdp.xml;

import java.util.List;

import org.kuali.module.pdp.bo.PdpUser;


public interface PdpFileHandler {
  public void setFilename(String filename);
  public void setUser(PdpUser u);
  public void setMaxNoteLines(int lines);

  public void setHeader(XmlHeader header);
  public void setGroup(XmlGroup item);
  public void setTrailer(XmlTrailer trailer);

  public void setErrorMessage(String message);
  public List getErrorMessages();

  public XmlHeader getHeader();
  public XmlTrailer getTrailer();
  
  public void clear();
}
