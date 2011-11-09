/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ec.document.web.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ec.document.EffortCertificationDocument;
import org.kuali.kfs.module.ec.document.validation.event.LoadDetailLineEvent;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * To define Actions for Effort Recreate document.
 */
public class CertificationRecreateAction extends EffortCertificationAction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CertificationRecreateAction.class);

    
    /**
     * load the detail lines with the given information
     */
    public ActionForward loadDetailLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CertificationRecreateForm recreateForm = (CertificationRecreateForm) form;

        // when we return from the lookup, our next request's method to call is going to be refresh
        recreateForm.registerEditableProperty(KRADConstants.DISPATCH_REQUEST_PARAMETER);
        
        EffortCertificationDocument effortCertificationDocument = recreateForm.getEffortCertificationDocument();
        effortCertificationDocument.getEffortCertificationDetailLines().clear();

        recreateForm.forceInputAsUpperCase();
        effortCertificationDocument.setEmplid(recreateForm.getEmplid());
        effortCertificationDocument.setUniversityFiscalYear(recreateForm.getUniversityFiscalYear());
        effortCertificationDocument.setEffortCertificationReportNumber(recreateForm.getEffortCertificationReportNumber());
        
        if (recreateForm.validateImportingFieldValues(effortCertificationDocument)) {            
            boolean isRulePassed = this.invokeRules(new LoadDetailLineEvent(KFSConstants.EMPTY_STRING, KFSConstants.EMPTY_STRING, effortCertificationDocument));
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
}
