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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.web.struts.form.KualiFeedbackHandlerForm;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.exception.AuthorizationException;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.KualiFeedbackService;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * This class handles when the feedback form is submitted.
 * It invokes the KualiFeedbackService to send the feedback email
 */

public class KualiFeedbackHandlerAction extends KualiAction {

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (findMethodToCall(form, request) == null || findMethodToCall(form, request).equals("docHandler")) {
			return executeFeedback(mapping, form, request, response);
		}
		else {
			return super.execute(mapping, form, request, response);
		}
	}

	public ActionForward executeFeedback(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ActionForward returnForward = null;
		KualiFeedbackHandlerForm formObject = (KualiFeedbackHandlerForm) form;
		if (!formObject.isCancel()) {
			populateRequest(form, request);
			returnForward = mapping.findForward(RiceConstants.MAPPING_BASIC);
		}
		else {
			returnForward = mapping.findForward(KRADConstants.MAPPING_CANCEL);
		}

		return returnForward;
	}

	public ActionForward submitFeedback(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (form instanceof KualiFeedbackHandlerForm) {
			KualiFeedbackHandlerForm feedbackForm = (KualiFeedbackHandlerForm) form;
			KualiFeedbackService reporterService = KRADServiceLocatorWeb.getKualiFeedbackService();
			reporterService.sendFeedback(feedbackForm.getDocumentId(), feedbackForm.getComponentName(), feedbackForm.getDescription());
		}
		return mapping.findForward(KRADConstants.MAPPING_CLOSE);
	}

	private void populateRequest(ActionForm form, HttpServletRequest request) {
		UserSession userSession = (UserSession) request.getSession().getAttribute(KRADConstants.USER_SESSION_KEY);
		Person sessionUser = null;
		if (userSession != null) {
			sessionUser = userSession.getPerson();
		}
		if (sessionUser != null) {
			request.setAttribute("principalName", sessionUser.getPrincipalName());
		}
	}

	@Override
	protected void checkAuthorization(ActionForm form, String methodToCall) throws AuthorizationException {
	}
}
