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

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.krad.exception.ExceptionIncident;
import org.kuali.rice.krad.util.KRADConstants;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class is the action form for all Question Prompts.
 * 
 * 
 */
public class KualiExceptionIncidentForm extends KualiForm {
    private static final long serialVersionUID = 831951332440283401L;
    private static Logger LOG=Logger.getLogger(KualiExceptionIncidentForm.class); 
    
    /**
     * The form properties that should be populated in order for the toMap() method to function properly.
     */
    private static final Set<String> PROPS_NEEDED_FOR_MAP = new HashSet<String>();
    static {
    	PROPS_NEEDED_FOR_MAP.add(ExceptionIncident.DOCUMENT_ID);
    	PROPS_NEEDED_FOR_MAP.add(ExceptionIncident.USER_EMAIL);
    	PROPS_NEEDED_FOR_MAP.add(ExceptionIncident.USER_NAME);
    	PROPS_NEEDED_FOR_MAP.add(ExceptionIncident.UUID);
    	PROPS_NEEDED_FOR_MAP.add(ExceptionIncident.COMPONENT_NAME);
    	PROPS_NEEDED_FOR_MAP.add(ExceptionIncident.DESCRIPTION);
    	PROPS_NEEDED_FOR_MAP.add(ExceptionIncident.EXCEPTION_REPORT_SUBJECT);
    	PROPS_NEEDED_FOR_MAP.add(ExceptionIncident.EXCEPTION_MESSAGE);
    	PROPS_NEEDED_FOR_MAP.add(ExceptionIncident.DISPLAY_MESSAGE);
    	PROPS_NEEDED_FOR_MAP.add(ExceptionIncident.STACK_TRACE);
    }
    
    /**
     * Flag to determine whether it's cancel action
     */
    private boolean cancel=false;
    /**
     * Object containing exception information
     */
//    private KualiExceptionIncident exceptionIncident;
    /**
     * The error subject created from current settings and thrown exception
     */
     private String exceptionReportSubject;
    /**
     * The error message
     */
     private String exceptionMessage;
     /**
      * The error message to be displayed
      */
      private String displayMessage;
     /**
     * Additional message from user
     */
     private String description;
     /**
      * Document id. it's blank if not a document process
      */
     private String documentId=""; 
     /**
      * Session user email address
      */
     private String userEmail="";
     /**
      * Session user name
      */
     private String principalName="";
     /**
      * Session user name
      */
     private String userName="";
     /**
      * Detail message not for displaying
      */
     private String stackTrace;
     /**
     * Form that threw the exception
     */
    private String componentName;

    /**
     * @see org.kuali.rice.krad.web.struts.pojo.PojoForm#populate(HttpServletRequest)
     */
    public void populate(HttpServletRequest request) {
        
        super.populate(request);
        
        // KULRICE-4402: ie explorer needs this.
        if(notNull(request.getParameter(KRADConstants.CANCEL_METHOD + ".x")) && notNull(request.getParameter(
                KRADConstants.CANCEL_METHOD + ".y"))){
        	this.setCancel(true);
        }                
    }
    
    private boolean notNull(String s){
    	if(s != null && !"".equals(s)){
    		return true;
    	}else 
    		return false;
    }

    /*
     * Reset method - reset attributes of form retrieved from session otherwise
     * we will always call docHandler action
     * @param mapping
     * @param request
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        
        this.setMethodToCall(null);
        this.setRefreshCaller(null);
        this.setAnchor(null);
        this.setCurrentTabIndex(0);
        
        this.cancel=false;
        this.documentId=null;
        this.componentName=null;
        this.description=null;
        this.displayMessage=null;
        this.exceptionMessage=null;
        this.stackTrace=null;
        this.userEmail=null;
        this.userName=null;
        this.principalName=null;

    }
    
    /**
     * This method return list of required information contained by the jsp in both
     * display and hidden properties.
     * 
     * @return
     * <p>Example:
     * <code>
     * documentId, 2942084
     * userEmail, someone@somewhere
     * userName, some name
     * componentFormName, Form that threw exception name
     * exceptionMessage, Error message from exception
     * displayMessage, Either exception error message or generic exception error message
     * stackTrace, Exception stack trace here
     * </code>
     * 
     */
    public Map<String, String> toMap() {
        if (LOG.isTraceEnabled()) {
            String message=String.format("ENTRY");
            LOG.trace(message);
        }
        
        Map<String, String> map=new HashMap<String, String>();
        map.put(ExceptionIncident.DOCUMENT_ID, this.documentId);
        map.put(ExceptionIncident.USER_EMAIL, this.userEmail);
        map.put(ExceptionIncident.USER_NAME, this.userName);
        map.put(ExceptionIncident.UUID, this.principalName);
        map.put(ExceptionIncident.COMPONENT_NAME, this.componentName);
        map.put(ExceptionIncident.DESCRIPTION, this.description);
        map.put(ExceptionIncident.EXCEPTION_REPORT_SUBJECT, this.exceptionReportSubject);
        map.put(ExceptionIncident.EXCEPTION_MESSAGE, this.exceptionMessage);
        map.put(ExceptionIncident.DISPLAY_MESSAGE, this.displayMessage);
        map.put(ExceptionIncident.STACK_TRACE, this.stackTrace);
        
        if (LOG.isTraceEnabled()) {
            String message=String.format("ENTRY %s", map.toString());
            LOG.trace(message);
        }
        
        return map;
    }

    /**
     * @return the cancel
     */
    public final boolean isCancel() {
        return this.cancel;
    }

    /**
     * @param cancel the cancel to set
     */
    public final void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    /**
     * @return the exceptionIncident
     */
//    public final KualiExceptionIncident getExceptionIncident() {
//        return this.exceptionIncident;
//    }

    /**
     * @return the description
     */
    public final String getDescription() {
        return this.description;
    }

    /**
     * @param description the description to set
     */
    public final void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the exceptionMessage
     */
    public final String getExceptionMessage() {
        return this.exceptionMessage;
    }

    /**
     * @param exceptionMessage the exceptionMessage to set
     */
    public final void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    /**
     * @return the displayMessage
     */
    public final String getDisplayMessage() {
        return this.displayMessage;
    }

    /**
     * @param displayMessage the displayMessage to set
     */
    public final void setDisplayMessage(String displayMessage) {
        this.displayMessage = displayMessage;
    }

    /**
     * @return the documentId
     */
    public final String getDocumentId() {
        return this.documentId;
    }

    /**
     * @param documentId the documentId to set
     */
    public final void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
    
    /**
     * @return the userEmail
     */
    public final String getUserEmail() {
        return this.userEmail;
    }

    /**
     * @param userEmail the userEmail to set
     */
    public final void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    /**
	 * @return the principalName
	 */
	public String getPrincipalName() {
		return this.principalName;
	}

	/**
	 * @param principalName the principalName to set
	 */
	public void setPrincipalName(String principalName) {
		this.principalName = principalName;
	}

	/**
     * @return the userName
     */
    public final String getUserName() {
        return this.userName;
    }

    /**
     * @param userName the userName to set
     */
    public final void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @param stackTrace the stackTrace to set
     */
    public final void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    /**
     * @return the stackTrace
     */
    public final String getStackTrace() {
        return this.stackTrace;
    }

    /**
     * @return the exceptionReportSubject
     */
    public final String getExceptionReportSubject() {
        return this.exceptionReportSubject;
    }

    /**
     * @param exceptionReportSubject the exceptionReportSubject to set
     */
    public final void setExceptionReportSubject(String exceptionReportSubject) {
        this.exceptionReportSubject = exceptionReportSubject;
    }

    /**
     * @return the componentName
     */
    public final String getComponentName() {
        return this.componentName;
    }

    /**
     * @param componentName the componentName to set
     */
    public final void setComponentName(String componentName) {
        this.componentName = componentName;
    }

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krad.web.struts.form.KualiForm#shouldMethodToCallParameterBeUsed(String, String, HttpServletRequest)
	 */
	@Override
	public boolean shouldMethodToCallParameterBeUsed(
			String methodToCallParameterName,
			String methodToCallParameterValue, HttpServletRequest request) {
		// we will allow all method to calls since the KualiExceptionHandlerAction will ignore the methodToCall
		return true;
	}

	/**
	 * @see org.kuali.rice.krad.web.struts.form.KualiForm#shouldPropertyBePopulatedInForm(String, HttpServletRequest)
	 */
	@Override
	public boolean shouldPropertyBePopulatedInForm(
			String requestParameterName, HttpServletRequest request) {
		if (PROPS_NEEDED_FOR_MAP.contains(requestParameterName)) {
			return true;
		}
		else if (KRADConstants.CANCEL_METHOD.equals(requestParameterName)) {
			return true;
		}
		return super.shouldPropertyBePopulatedInForm(requestParameterName, request);
	}
}

