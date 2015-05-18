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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ExceptionHandler;
import org.apache.struts.config.ExceptionConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles any AuthorizationException by logging it first and then passing it forward to an explanation page.
 */
public class AuthorizationExceptionHandler extends ExceptionHandler {
    
    private static final String AUTHORIZATION_EXCEPTION_HANDLER = "authorizationExceptionHandler";

    private static final Log LOG = LogFactory.getLog(AuthorizationExceptionHandler.class);
    
    /**
     * Logs the AuthorizationException before forwarding the user to the explanation page.
     * 
     * @see ExceptionHandler#execute(
     *      Exception, ExceptionConfig, ActionMapping, ActionForm,
     *      HttpServletRequest, HttpServletResponse)
     */
    @Override
    public ActionForward execute(Exception exception, ExceptionConfig exceptionConfig, ActionMapping mapping, ActionForm form, HttpServletRequest request, 
            HttpServletResponse response) {
        
        if (LOG.isTraceEnabled()) {
            String message = String.format("ENTRY %s", exception.getMessage());
            LOG.trace(message);
        }
        exception.printStackTrace();
        request.setAttribute(Globals.EXCEPTION_KEY, exception);
        
        ActionForward forward = mapping.findForward(AUTHORIZATION_EXCEPTION_HANDLER);
        
        if (LOG.isTraceEnabled()) {
            LOG.trace(String.format("EXIT %s", exception.getMessage()));
        }
        
        return forward;
    }

}
