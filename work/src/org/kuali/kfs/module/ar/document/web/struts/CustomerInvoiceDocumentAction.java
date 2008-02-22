/*
 * Copyright 2008 The Kuali Foundation.
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
import org.kuali.core.service.PersistenceService;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rule.event.AddAccountingLineEvent;
import org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase;
import org.kuali.kfs.web.struts.form.KualiAccountingDocumentFormBase;
import org.kuali.module.ar.ArConstants;
import org.kuali.module.ar.bo.AccountsReceivableDocumentHeader;
import org.kuali.module.ar.bo.CustomerInvoiceDetail;
import org.kuali.module.ar.document.CustomerInvoiceDocument;
import org.kuali.module.ar.rule.event.AddCustomerInvoiceDetailEvent;
import org.kuali.module.ar.service.AccountsReceivableDocumentHeaderService;
import org.kuali.module.ar.service.CustomerInvoiceDetailService;
import org.kuali.module.ar.web.struts.form.CustomerInvoiceDocumentForm;
import org.kuali.module.financial.service.UniversityDateService;

import edu.iu.uis.eden.exception.WorkflowException;

public class CustomerInvoiceDocumentAction extends KualiAccountingDocumentActionBase {
    
    /**
     * Makes a call to parent's createDocument method, but this method also defaults values for customer invoice document
     * 
     * @see org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase#createDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);
        CustomerInvoiceDocumentForm customerInvoiceDocumentForm = (CustomerInvoiceDocumentForm) kualiDocumentFormBase;
        CustomerInvoiceDocument customerInvoiceDocument = customerInvoiceDocumentForm.getCustomerInvoiceDocument();
        
        //set up the default values for customer invoice document
        customerInvoiceDocument.setupDefaultValues();
        
        //set up the default values for the AR DOC Header
        AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService = SpringContext.getBean(AccountsReceivableDocumentHeaderService.class);
        AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = accountsReceivableDocumentHeaderService.getNewAccountsReceivableDocumentHeaderForCurrentUser();
        accountsReceivableDocumentHeader.setDocumentNumber(customerInvoiceDocument.getDocumentNumber());
        customerInvoiceDocument.setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);
        
        //set up the default values for customer invoice detail add line
        CustomerInvoiceDetailService customerInvoiceDetailService = SpringContext.getBean(CustomerInvoiceDetailService.class);
        customerInvoiceDocumentForm.setNewSourceLine(customerInvoiceDetailService.getAddLineCustomerInvoiceDetailForCurrentUserAndYear() );
    }
    
    /**
     * Adds a CustomerInvoiceDetail instance created from the current "new customer invoice detail" line to the document
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    /*
    public ActionForward addCustomerInvoiceDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CustomerInvoiceDocumentForm customerInvoiceDocumentForm = (CustomerInvoiceDocumentForm) form;
        CustomerInvoiceDocument customerInvoiceDocument = customerInvoiceDocumentForm.getCustomerInvoiceDocument();

        CustomerInvoiceDetail newCustomerInvoiceDetail = customerInvoiceDocumentForm.getNewCustomerInvoiceDetail();
        newCustomerInvoiceDetail.setDocumentNumber(customerInvoiceDocument.getDocumentNumber());
        newCustomerInvoiceDetail.setPostingYear(SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear());
        
        //Reusing accounting line validation from AddAccountingLineEvent 
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new AddCustomerInvoiceDetailEvent(ArConstants.NEW_CUSTOMER_INVOICE_DETAIL_ERROR_PATH_PREFIX, customerInvoiceDocument, newCustomerInvoiceDetail));
        if (rulePassed) {
            // add customer invoice detail
            customerInvoiceDocument.addCustomerInvoiceDetail(newCustomerInvoiceDetail);

            //set up the default values for customer invoice detail add line
            CustomerInvoiceDetailService customerInvoiceDetailService = SpringContext.getBean(CustomerInvoiceDetailService.class);
            customerInvoiceDocumentForm.setNewCustomerInvoiceDetail(customerInvoiceDetailService.getAddLineCustomerInvoiceDetailForCurrentUserAndYear() );
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }*/
    
    /**
     * Deletes the selected customer invoice detail line from the document
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    /*
    public ActionForward deleteCustomerInvoiceDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CustomerInvoiceDocumentForm customerInvoiceDocumentForm = (CustomerInvoiceDocumentForm) form;
        CustomerInvoiceDocument customerInvoiceDocument = customerInvoiceDocumentForm.getCustomerInvoiceDocument();

        int indexOfLineToDelete = getLineToDelete(request);
        // delete customer invoice detail with specific line
        customerInvoiceDocument.deleteCustomerInvoiceDetail(indexOfLineToDelete);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }*/ 
    

    /**
     * Removed salesTax checking.  Need to verify if this check has be moved out later of the KualiAccountingDocumentActionBase class. If
     * so just use the parent class' insertSourceLine method.
     * 
     * @see org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase#insertSourceLine(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward insertSourceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiAccountingDocumentFormBase financialDocumentForm = (KualiAccountingDocumentFormBase) form;

        SourceAccountingLine line = financialDocumentForm.getNewSourceLine();
        boolean rulePassed = true;
        // check any business rules
        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new AddAccountingLineEvent(KFSConstants.NEW_SOURCE_ACCT_LINE_PROPERTY_NAME, financialDocumentForm.getDocument(), line));

        if (rulePassed) {
            // add accountingLine
            SpringContext.getBean(PersistenceService.class).refreshAllNonUpdatingReferences(line);
            insertAccountingLine(true, financialDocumentForm, line);

            // clear the used newTargetLine
            financialDocumentForm.setNewSourceLine(null);
            
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }    
}
