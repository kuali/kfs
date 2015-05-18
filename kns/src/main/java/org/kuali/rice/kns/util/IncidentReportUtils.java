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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.exception.KualiExceptionIncident;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * This is a description of what this class does - ewestfal don't forget to fill
 * this in.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public final class IncidentReportUtils {

	/**
     * Key to define the attribute stores exception properties such as
     * user email, user name, component name, etc.
     * <p>Value is exceptionProperties
     */
    public static final String EXCEPTION_PROPERTIES="exceptionProperties";
    
	private IncidentReportUtils() {
		throw new UnsupportedOperationException("do not call");
	}

	public static Map<String, String> populateRequestForIncidentReport(Exception exception,
			String documentId, String componentName, HttpServletRequest request) {

		// Create properties of form and user for additional information
		// to be displayed or passing through JSP
		Map<String, String> properties = new HashMap<String, String>();
		properties.put(KualiExceptionIncident.DOCUMENT_ID, documentId);
		String userEmail = "";
		String userName = "";
		String uuid = "";
		// No specific forward for the caught exception, use default logic
		// Get user information
		UserSession userSession = (UserSession) request.getSession()
				.getAttribute(KRADConstants.USER_SESSION_KEY);
		Person sessionUser = null;
		if (userSession != null) {
			sessionUser = userSession.getPerson();
		}
		if (sessionUser != null) {
			userEmail = sessionUser.getEmailAddressUnmasked();
			userName = sessionUser.getName();
			uuid = sessionUser.getPrincipalName();
		}
		properties.put(KualiExceptionIncident.USER_EMAIL, userEmail);
		properties.put(KualiExceptionIncident.USER_NAME, userName);
		properties.put(KualiExceptionIncident.UUID, uuid);
		properties.put(KualiExceptionIncident.COMPONENT_NAME, componentName);

		// Reset the exception so the forward action can read it
		request.setAttribute(Globals.EXCEPTION_KEY, exception);
		// Set exception current information
		request.setAttribute(EXCEPTION_PROPERTIES, properties);

		return properties;

	}
}
