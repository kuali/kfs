/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.ar.web.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.ar.ArConstants;
import org.kuali.module.ar.bo.AccountsReceivableDocumentHeader;
import org.kuali.module.ar.bo.CashControlDetail;
import org.kuali.module.ar.document.CashControlDocument;
import org.kuali.module.ar.rule.event.AddCashControlDetailEvent;
import org.kuali.module.ar.web.struts.form.CashControlDocumentForm;
import org.kuali.module.chart.bo.ChartUser;
import org.kuali.module.chart.lookup.valuefinder.ValueFinderUtil;

import edu.iu.uis.eden.exception.WorkflowException;

public class CashControlDocumentAction extends KualiTransactionalDocumentActionBase {

    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);
        CashControlDocumentForm form = (CashControlDocumentForm) kualiDocumentFormBase;
        CashControlDocument document = form.getCashControlDocument();
        

        //set up the default values for the AR DOC Header (SHOULD PROBABLY MAKE THIS A SERVICE)
        ChartUser currentUser = ValueFinderUtil.getCurrentChartUser();
        AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = new AccountsReceivableDocumentHeader();
        accountsReceivableDocumentHeader.setDocumentNumber(document.getDocumentNumber());
        accountsReceivableDocumentHeader.setProcessingChartOfAccountCode(currentUser.getChartOfAccountsCode());
        accountsReceivableDocumentHeader.setProcessingOrganizationCode(currentUser.getOrganizationCode());
        document.setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);
    }
    
    public ActionForward addCashControlDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CashControlDocumentForm cashControlDocForm = (CashControlDocumentForm) form;
        CashControlDocument document = cashControlDocForm.getCashControlDocument();
        
        CashControlDetail newCashControlDetail = cashControlDocForm.getNewCashControlDetail();
        newCashControlDetail.setDocumentNumber(document.getDocumentNumber());
                
        //rules
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new AddCashControlDetailEvent(ArConstants.NEW_CASH_CONTROL_DETAIL_ERROR_PATH_PREFIX, document, newCashControlDetail));
        if (rulePassed) {
            // add customer invoice detail
            document.addCashControlDetail(newCashControlDetail);
            // clear the used customer invoice detail
            //set up the default values for customer invoice detail add line
            cashControlDocForm.setNewCashControlDetail(new CashControlDetail());
        }
        
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    public ActionForward deleteCashControlDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CashControlDocumentForm cashControlDocForm = (CashControlDocumentForm) form;
        CashControlDocument document = cashControlDocForm.getCashControlDocument();
        
        int indexOfLineToDelete = getLineToDelete(request);
        document.deleteCashControlDetail(indexOfLineToDelete);
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    public ActionForward generateRefDoc(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CashControlDocumentForm cashControlDocForm = (CashControlDocumentForm) form;
        CashControlDocument document = cashControlDocForm.getCashControlDocument();
        String paymentMediumCode = document.getCustomerPaymentMediumCode();
        
        //PaymentMediumService service = SpringContext.getBean(PaymentMediumService.class);
        //PaymentMedium medium = service.getPaymentMedium(paymentMediumCode);
        
        
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC); 
    }
    
}
