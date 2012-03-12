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
 * This interface allows different methods for handling timeouts for Authentication
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface TimeoutHandler {
    
    /**
     * Determines the timeout based on the context and initiator
     *
     * @param args the args passed depends on the requirements of the TimeoutHandler Implementation
     *
     * @return the timeout in seconds.  Returns 0 to disable timeouts.
     */
    public int getTimeout(Map<String,Object> args);
    
    
    /**
     * Determines if the timeout has expired
     *
     * @param args the args passed depends on the requirements of the TimeoutHandler Implementation
     *
     * @return true if the session has timed out.
     */
    public boolean hasTimedOut(Map<String,Object> args);

}
