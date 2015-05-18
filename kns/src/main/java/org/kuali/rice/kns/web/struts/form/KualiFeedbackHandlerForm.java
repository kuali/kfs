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
package org.kuali.rice.kns.web.struts.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Feedback form which is used to collect feedback from a user and
 * then email it to a feedback mailing list
 */
public class KualiFeedbackHandlerForm extends KualiForm {

	private static final long serialVersionUID = -7641833777580490471L;

	private boolean cancel = false;
	private String description;
	private String documentId = ""; 
	private String componentName;

	/**
	 * @see org.kuali.rice.kns.web.struts.pojo.PojoForm#populate(HttpServletRequest)
	 */
	@Override
	public void populate(HttpServletRequest request) {
		super.populate(request);
		// ie explorer needs this.
		if(StringUtils.isNotBlank(request.getParameter(KRADConstants.CANCEL_METHOD + ".x")) &&
           StringUtils.isNotBlank(request.getParameter(KRADConstants.CANCEL_METHOD + ".y"))){
			    this.setCancel(true);
		}                
	}

	public void reset(ActionMapping mapping, HttpServletRequest request) {

		this.setMethodToCall(null);
		this.setRefreshCaller(null);
		this.setAnchor(null);
		this.setCurrentTabIndex(0);

		this.cancel = false;
		this.documentId = null;
		this.description = null;
		this.componentName = null;
	}

	public boolean isCancel() {
		return cancel;
	}

	public void setCancel(boolean cancel) {
		this.cancel = cancel;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}
}
