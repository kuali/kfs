/*
 * Copyright 2005-2009 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
