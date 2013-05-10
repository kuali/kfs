/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.sec.web.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.config.ExceptionConfig;
import org.kuali.kfs.sec.SecConstants;
import org.kuali.kfs.sec.SecKeyConstants;
import org.kuali.kfs.sec.businessobject.AccessSecurityRestrictionInfo;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.web.struts.form.pojo.StrutsExceptionIncidentHandler;
import org.kuali.rice.krad.util.GlobalVariables;


/**
 * Checks for security access exception and forwards to security access error page
 */
public class SecurityExceptionIncidentHandler extends StrutsExceptionIncidentHandler {

    /**
     * @see org.kuali.rice.kns.web.struts.pojo.StrutsExceptionIncidentHandler#execute(java.lang.Exception, org.apache.struts.config.ExceptionConfig,
     *      org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(Exception exception, ExceptionConfig exceptionConfig, ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        AccessSecurityRestrictionInfo restrictionInfo = (AccessSecurityRestrictionInfo) GlobalVariables.getUserSession().retrieveObject(SecConstants.OPEN_DOCUMENT_SECURITY_ACCESS_DENIED_ERROR_KEY);
        if (restrictionInfo != null) {
            String accessMessage = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(SecKeyConstants.MESSAGE_OPEN_DOCUMENT_RESTRICTED);
            accessMessage = StringUtils.replace(accessMessage, "{0}", GlobalVariables.getUserSession().getPrincipalName());
            accessMessage = StringUtils.replace(accessMessage, "{1}", restrictionInfo.getDocumentNumber());
            accessMessage = StringUtils.replace(accessMessage, "{2}", restrictionInfo.getPropertyLabel());
            accessMessage = StringUtils.replace(accessMessage, "{3}", restrictionInfo.getRetrictedValue());
            request.setAttribute(SecConstants.ACCESS_ERROR_STRING_REQUEST_KEY, accessMessage);

            GlobalVariables.getUserSession().removeObject(SecConstants.OPEN_DOCUMENT_SECURITY_ACCESS_DENIED_ERROR_KEY);

            return mapping.findForward(SecConstants.ACCESS_DENIED_ERROR_FORWARD);
        }

        return super.execute(exception, exceptionConfig, mapping, form, request, response);
    }

}
