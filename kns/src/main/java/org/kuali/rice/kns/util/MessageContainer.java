/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.util;

import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.MessageMap;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * An adapter whose subclasses will make either an {@link MessageMap}'s warning or info messages available to the JSP layer
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public abstract class MessageContainer implements Serializable {
	private MessageMap errorMap;
	
	protected MessageContainer(MessageMap errorMap) {
		this.errorMap = errorMap;
	}
	
	protected MessageMap getMessageMap() {
		return errorMap;
	}
	
    public ActionMessages getRequestMessages() {
        ActionMessages requestErrors = new ActionMessages();
        for (Iterator<String> iter = getMessagePropertyNames().iterator(); iter.hasNext();) {
            String property = iter.next();
            List errorList = (List) getMessagesForProperty(property);

            for (Iterator iterator = errorList.iterator(); iterator.hasNext();) {
                ErrorMessage errorMessage = (ErrorMessage) iterator.next();

                // add ActionMessage with any parameters
                requestErrors.add(property, new ActionMessage(errorMessage.getErrorKey(), errorMessage.getMessageParameters()));
            }
        }
        return requestErrors;
    }
    
    public abstract int getMessageCount();
    
    public abstract List<String> getMessagePropertyList();

    protected abstract Set<String> getMessagePropertyNames();
    
    protected abstract List getMessagesForProperty(String propertyName);
}
