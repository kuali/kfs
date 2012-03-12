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

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class uses the same timeout for all authentication methods and applications
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class GlobalTimeoutHandler extends AbstractTimeoutHandler {
    private int timeoutPeriod;
    
    private static final Log logger = LogFactory.getLog(GlobalTimeoutHandler.class);

    /**
     * @return the timeoutPeriod
     */
    public int getTimeoutPeriod() {
        return this.timeoutPeriod;
    }

    /**
     * @param timeoutPeriod the timeoutPeriod to set
     */
    public void setTimeoutPeriod(int timeoutPeriod) {
        this.timeoutPeriod = timeoutPeriod;
    }

    /**
     * Determines the timeout based on the context and initiator
     *
     * @param args the args in this implementation aren't used
     *
     * @return the timeout in seconds
     */
    public int getTimeout(Map args) {
        return timeoutPeriod; 
    }
    
    /**
     * 
     * Examines results from session to determine if timeout has expired 
     * 
     * @see org.kuali.rice.kim.client.timeouthandlers.TimeoutHandler#hasTimedOut(java.lang.Object[])
     */
    public boolean hasTimedOut(Map args) {
        boolean bRet = true;
        Long maxIdleTime = (Long)args.get("maxIdleTime");
        
        if (maxIdleTime <= timeoutPeriod) {
            logger.debug("Not timed out: " + maxIdleTime + " " + timeoutPeriod);
            bRet = false;
        } else {
            logger.debug("Timed out: " + maxIdleTime + " " + timeoutPeriod);
        }
        return bRet;
    }

}

