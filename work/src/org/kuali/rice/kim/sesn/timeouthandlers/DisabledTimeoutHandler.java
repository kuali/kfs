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

/**
 * This is a description of what this class does - lsymms don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class DisabledTimeoutHandler implements TimeoutHandler{
    
    /**
     * @return false
     * @see org.kuali.rice.kim.client.timeouthandlers.TimeoutHandler#hasTimedOut(java.lang.Object[])
     */
    public boolean hasTimedOut(Map<String,Object> args) {
        // TODO lsymms - THIS METHOD NEEDS JAVADOCS
        return false;
    }

    /**
     * Determines the timeout based on the context and initiator
     *
     * @param args the args in this implementation aren't used
     *
     * @return 0 which disables the timeout.
     */
    public int getTimeout(Map<String,Object> args) {
        return 0;
    }
}

