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
package org.kuali.rice.kns.web.struts.form.pojo;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;

/**
 * begin Kuali Foundation modification
 * This interface defines methods that Pojo Forms must provide.
 * end Kuali Foundation modification
 */
// Kuali Foundation modification: original name: SLForm
public interface PojoForm {
    public void populate(HttpServletRequest request);

    // begin Kuali Foundation modification
    // cachedActionErrors() method removed
    public void postprocessRequestParameters(Map requestParameters);
    // end Kuali Foundation modification

    public Map getUnconvertedValues();

    public Object formatValue(Object value, String keypath, Class type);

    // begin Kuali Foundation modification
    public void processValidationFail();
    
    Set<String> getRequiredNonEditableProperties();
    
    void registerEditableProperty(String editablePropertyName);
    
    /**
     * Reinitializes the form to allow it to register the editable properties of the currently processing request.
     */
    void clearEditablePropertyInformation();

    Set<String> getEditableProperties();
    
    /**
     * 
     * This method adds the required property names, that are not directly editable by user on the html page, to a list, regardless of the context
     * in which they appear.  Request parameter names corresponding to these properties
     * will be populated into the form. 
     *
     */
    void addRequiredNonEditableProperties();
    
    /**
     * Sets the value of the "scope" attribute for the Struts action mapping corresponding to this form instance.  Note that this
     * method name is NOT in the syntax of the conventional POJO setter; this is to prevent clients from maliciously altering the value
     * of this parameter
     * 
     * @param scope
     */
    public void registerStrutsActionMappingScope(String scope);
    
   
    public void registerIsNewForm(boolean isNewForm);
    
    public boolean getIsNewForm();
    
    
    /**
	 * Returns whether a request parameter should be populated as a property of the form, assuming that the request parameter name
	 * corresponds to a property on the form.  This method makes no determination whether the request parameter is a property of the form, but rather
	 * from a security perspective, whether the framework should attempt to set the form property with the same name as the request parameter. 
	 * 
	 * @param requestParameterName the name of the request parameter
	 * @param request the HTTP request
	 * @return whether the parameter should be 
	 */
	public boolean shouldPropertyBePopulatedInForm(String requestParameterName, HttpServletRequest request);
	
	/**
	 * Returns a set of methodToCalls for which the system will bypass the session.  The return value of this method may depend ONLY upon the
	 * type of the class implementing it.  Each instance of an implementation of this interface
	 * must return the same result.  More formally, for 2 instances of this interfaces a1 and a2, if a1.getClass().equals(a2.getClass()), then
	 * a1.getMethodToCallsToBypassSessionRetrievalForGETRequests().equals(a2.getMethodToCallsToBypassSessionRetrievalForGETRequests())
	 * 
	 * NOTE: read Javadoc of {@link PojoFormBase#getMethodToCallsToBypassSessionRetrievalForGETRequests()} for important implementation details.
	 * 
	 * @return
	 */
	public Set<String> getMethodToCallsToBypassSessionRetrievalForGETRequests();
	
	/**
     * Sets the editable properties guid for this form
     * @param guid the key to the editable properties for this form
     */
    public abstract void setPopulateEditablePropertiesGuid(String guid);
    
    /**
     * Sets the guid associated with the edited properties associated with the action
     * 
     * @param guid the guid of the action editable properties
     */
    public abstract void setActionEditablePropertiesGuid(String guid);
    // end Kuali Foundation modification

}
