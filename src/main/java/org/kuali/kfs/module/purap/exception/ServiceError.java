/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
