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
