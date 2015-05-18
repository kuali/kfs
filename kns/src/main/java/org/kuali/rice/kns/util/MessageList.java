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

import java.util.ArrayList;

/**
 * This is a description of what this class does - wliang don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class MessageList extends ArrayList<ErrorMessage> {
	public void add(String messageKey, String... messageParameters) {
		add(new ErrorMessage(messageKey, messageParameters));
	}
	
	public ActionMessages toActionMessages() {
		ActionMessages actionMessages = new ActionMessages();
		for (ErrorMessage errorMessage : this) {
			actionMessages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(errorMessage.getErrorKey(), errorMessage.getMessageParameters()));
		}
		return actionMessages;
	}
}
