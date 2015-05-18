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

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ExceptionHandler;
import org.apache.struts.config.ExceptionConfig;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.kns.util.IncidentReportUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * This class is the exception handler for the base exception class java.lang.Throwable
 * and is defined as global exception in the struts-config.xml. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class StrutsExceptionIncidentHandler extends ExceptionHandler {
    private static final Logger LOG=
        Logger.getLogger(StrutsExceptionIncidentHandler.class);
    
    /**
     * This is defined in struts-config.xml for forwarding this exception to a specified
     * exception handler.
     * <p>Value is exceptionIncidentHandler
     */
    public static final String EXCEPTION_INCIDENT_HANDLER="exceptionIncidentHandler";
    
    /**
     * This overridden method extract exception information such as component name,
     * user name and email, etc.
     * 
     * @see ExceptionHandler#execute(
     * Exception,
     *  ExceptionConfig,
     *   ActionMapping,
     *    ActionForm,
     *     HttpServletRequest,
     *      HttpServletResponse)
     */
    public ActionForward execute(Exception exception,
            ExceptionConfig exceptionConfig,
            ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response) {
        
        if (LOG.isTraceEnabled()) {
            String message=String.format("ENTRY %s", exception.getMessage());
            LOG.trace(message);
        }
        
        LOG.error("Exception being handled by Exception Handler", exception);

        String documentId="";
        if (form instanceof KualiDocumentFormBase) {
            KualiDocumentFormBase docForm=(KualiDocumentFormBase)form;
            if (docForm.getDocument() != null) {
            	documentId=docForm.getDocument().getDocumentNumber();
            }
        }
        
        Map<String, String> properties = IncidentReportUtils.populateRequestForIncidentReport(exception, documentId, form.getClass().getSimpleName(), request);
        
        ActionForward forward=mapping.findForward(EXCEPTION_INCIDENT_HANDLER);
        
        if (LOG.isTraceEnabled()) {
            String message=String.format("ENTRY %s%n%s%n%s",
                    exception.getMessage(),
                    properties.toString(),
                    (forward==null)?"null":forward.getPath());
            LOG.trace(message);
        }

        return forward;
    }

}

