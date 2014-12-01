/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
