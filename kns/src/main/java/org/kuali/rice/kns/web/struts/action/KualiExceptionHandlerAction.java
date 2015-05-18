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
package org.kuali.rice.kns.web.struts.action;

import org.apache.log4j.Logger;
import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.kns.util.IncidentReportUtils;
import org.kuali.rice.kns.web.struts.form.KualiExceptionIncidentForm;
import org.kuali.rice.krad.exception.KualiExceptionIncident;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.KualiExceptionIncidentService;
import org.kuali.rice.krad.util.KRADConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the struts action class for handling the exception for Kuali
 * applications.
 * 
 */
public class KualiExceptionHandlerAction extends Action {
	private static final Logger LOG = Logger
			.getLogger(KualiExceptionHandlerAction.class);

	/**
	 * This overridden method dispatches action to be taken based on
	 * "methodToCall" parameter. The exception is processed when there is no
	 * "methodToCall" specified.
	 * 
	 * @see Action#execute(ActionMapping,
	 *      ActionForm,
	 *      HttpServletRequest,
	 *      HttpServletResponse)
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return executeException(mapping, form, request, response);
	}

	/**
	 * This overridden method processes the exception and post exception (when
	 * user either submit/cancel the exception JSP page).
	 * <ul>
	 * <li>ProcessDefinition application Exception - Exception is stored in Http Request</li>
	 * <li>ProcessDefinition exception incident reporting - No exception, only form data</li>
	 * </ul>
	 * 
	 * @see Action#execute(ActionMapping,
	 *      ActionForm,
	 *      HttpServletRequest,
	 *      HttpServletResponse)
	 */
	public ActionForward executeException(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		if (LOG.isDebugEnabled()) {
			String lm = String.format("ENTRY %s%n%s", form.getClass()
					.getSimpleName(), request.getRequestURI());
			LOG.debug(lm);
		}

		// Get exception thrown
		Exception e = (Exception) request.getAttribute(Globals.EXCEPTION_KEY);

		// Initialize defined action mapping from struts-config
		ActionForward returnForward = null;

		// In case there is no exception, either a post back after page was
		// filled in
		// or just an error from directly accessing this struts action
		if (e == null) {
			if (form instanceof KualiExceptionIncidentForm) {
				KualiExceptionIncidentForm formObject = (KualiExceptionIncidentForm) form;
				// Manage conditions: submit or cancel
				if (!formObject.isCancel()) {
					// Locate the post exception handler service. The service id
					// is
					// defined in the application properties
					// Only process the post exception handling when the
					// service
					// is specified
					KualiExceptionIncidentService reporterService = KRADServiceLocatorWeb
							.getKualiExceptionIncidentService();
					// An instance of the ExceptionIncident is created by
					// the
					// ExceptionIncidentService
					Map reducedMap = new HashMap();
					Enumeration<String> names = request.getParameterNames();
					while (names.hasMoreElements()) {
						String name = names.nextElement();
						reducedMap.put(name, request.getParameter(name));
					}
					KualiExceptionIncident exceptionIncident = reporterService
							.getExceptionIncident(reducedMap);
					// Report the incident
					reporterService.report(exceptionIncident);
				} else {
					// Set return after canceling
					ActionForward cancelForward = mapping
							.findForward(KRADConstants.MAPPING_CANCEL);
					if (cancelForward == null) {
						cancelForward = returnForward;
					} else {
						returnForward = cancelForward;
					}
				}
			}
		} else {
			// ProcessDefinition the received exception from HTTP request
			returnForward = processException(mapping, form, request, e);
		}

		// Not specified, return
		if (returnForward == null) {
			returnForward = mapping.findForward(KRADConstants.MAPPING_CLOSE);
		}

		if (LOG.isDebugEnabled()) {
			String lm = String.format("EXIT %s",
					(returnForward == null) ? "null" : returnForward.getPath());
			LOG.debug(lm);
		}

		return returnForward;
	}

	/**
	 * This method process the caught exception by creating an exception
	 * information properties list and forward these properties to the exception
	 * incident handler JSP.
	 * 
	 * @param exception
	 * @param mapping
	 * @param request
	 * @param documentId
	 *            Id of the document that Struts threw exception during its
	 *            processing. null if not the document processing that caused
	 *            the exception
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	protected ActionForward processException(ActionMapping mapping,
			ActionForm form, HttpServletRequest request, Exception exception)
			throws Exception {
		// Only process the exception handling when the service
		// is specified
		KualiExceptionIncidentService reporterService = KRADServiceLocatorWeb
				.getKualiExceptionIncidentService();
		// Get exception properties from the Http Request
		Map<String, String> properties = (Map<String, String>) request
				.getAttribute(IncidentReportUtils.EXCEPTION_PROPERTIES);
		// Construct the exception incident object
		KualiExceptionIncident ei = reporterService.getExceptionIncident(
				exception, properties);
		// Set full exception properties in Http Request and forward to JSP
		request.setAttribute(KualiExceptionHandlerAction.class
				.getSimpleName(), ei.toProperties());
		return mapping.findForward(RiceConstants.MAPPING_BASIC);
	}
}
