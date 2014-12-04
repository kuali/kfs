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
package org.kuali.kfs.module.endow.document.web.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.SecurityValuationMethod;
import org.kuali.kfs.module.endow.document.HoldingHistoryValueAdjustmentDocument;
import org.kuali.kfs.module.endow.document.service.ClassCodeService;
import org.kuali.kfs.module.endow.document.service.SecurityService;
import org.kuali.kfs.module.endow.document.service.SecurityValuationMethodService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentActionBase;

public class HoldingHistoryValueAdjustmentDocumentAction extends FinancialSystemTransactionalDocumentActionBase {

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.refresh(mapping, form, request, response);

        // To Determine if the refresh is coming from Security lookup
        refreshSecurityDetails(mapping, form, request, response);
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * Retrieves and sets the reference Security object on Source or Target transactionsecurity based on the looked up value.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward refreshSecurityDetails(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        HoldingHistoryValueAdjustmentDocumentForm ehvaForm = (HoldingHistoryValueAdjustmentDocumentForm) form;

        HoldingHistoryValueAdjustmentDocument ehvaDocument = (HoldingHistoryValueAdjustmentDocument) ehvaForm.getDocument();
        Security security = SpringContext.getBean(SecurityService.class).getByPrimaryKey(ehvaDocument.getSecurityId());

        ClassCode classCode = SpringContext.getBean(ClassCodeService.class).getByPrimaryKey(security.getSecurityClassCode());
        security.setClassCode(classCode);
        
        SecurityValuationMethod securityValuation = SpringContext.getBean(SecurityValuationMethodService.class).getByPrimaryKey(classCode.getValuationMethod());
        classCode.setSecurityValuationMethod(securityValuation);
        ehvaDocument.setSecurity(security);

        return null;
    }
    
}
