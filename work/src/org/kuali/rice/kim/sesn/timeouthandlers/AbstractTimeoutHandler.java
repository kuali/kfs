/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kim.sesn.timeouthandlers;

/**
 * This Abstract Class allows the configuration of timeout fields.
 * TODO: change how TimoutHandlers choose fields from ndx to name.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public abstract class AbstractTimeoutHandler implements TimeoutHandler{

    private static final int MAX_IDLE_TIME_TIMEOUT_FIELD=1;
    private static final int LAST_ACCCESS_TIMEOUT_FIELD=2;
    private static final int DEFAULT_TIMEOUT_FIELD=MAX_IDLE_TIME_TIMEOUT_FIELD; 
    private int timeoutField=AbstractTimeoutHandler.DEFAULT_TIMEOUT_FIELD;
    
    /**
     * @return the timeoutField
     */
    public int getTimeoutField() {
        return this.timeoutField;
    }
    /**
     * @param timeoutField the timeoutField to set
     */
    public void setTimeoutField(int timeoutField) {
        this.timeoutField = timeoutField;
    }

}

