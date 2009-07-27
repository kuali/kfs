/*
 * Created on Jan 24, 2005
 *
 */
package org.kuali.kfs.module.purap.exception;

import java.io.Serializable;
import java.util.ArrayList;

public class ServiceError implements Serializable {
  private String tab;
  private String messageKey;
  private ArrayList parameters = new ArrayList();

  public ServiceError() {
    super();
  }

  public ServiceError(String tab,String messageKey) {
    super();
    this.tab = tab;
    this.messageKey = messageKey;
  }
  
  public void addParameter(String param) {
    parameters.add(param);
  }

  public Object[] getParameters() {
    return parameters.toArray();
  }
  
  public String getMessageKey() {
    return messageKey;
  }
  public void setMessageKey(String messageKey) {
    this.messageKey = messageKey;
  }

  public String getTab() {
    return tab;
  }
  public void setTab(String tab) {
    this.tab = tab;
  }
}
