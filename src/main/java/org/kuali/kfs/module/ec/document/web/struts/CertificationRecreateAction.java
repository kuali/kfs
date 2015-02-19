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
