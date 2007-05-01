/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.labor.web.struts.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rule.event.KualiDocumentEventBase;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.kfs.web.struts.action.KualiBalanceInquiryReportMenuAction;
import org.kuali.kfs.web.struts.form.KualiAccountingDocumentFormBase;
import org.kuali.module.labor.bo.ExpenseTransferAccountingLine;
import org.kuali.module.labor.document.SalaryExpenseTransferDocument;
import org.kuali.module.labor.rules.event.EmployeeIdChangedEventBase;
import org.kuali.module.labor.web.struts.form.SalaryExpenseTransferForm;

/**
 * This class extends the parent KualiTransactionalDocumentActionBase class, which contains all common action methods. Since the SEP
 * follows the basic transactional document pattern, there are no specific actions that it has to implement; however, this empty
 * class is necessary for integrating into the framework.
 */
public class SalaryExpenseTransferAction extends LaborDocumentActionBase {
    private KualiBalanceInquiryReportMenuAction balanceInquiryAction;
    
    public SalaryExpenseTransferAction() {
        balanceInquiryAction = new KualiBalanceInquiryReportMenuAction();
    }
    
    /**
     * @see org.kuali.kfs.web.struts.action.KualiBalanceInquiryReportMenuAction#performBalanceInquiryLookup(ActionMapping, ActionForm, HttpServletRequest, HttpServletResponse)
     */
    public ActionForward performBalanceInquiryLookup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return balanceInquiryAction.performBalanceInquiryLookup(mapping, form, request, response);
    }



    /**
     * @see org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase#processAccountingLineOverrides(KualiAccountingDocumentFormBase)
     */
    @Override         
    protected void processAccountingLineOverrides(KualiAccountingDocumentFormBase transForm) {
        
        if (transForm.hasDocumentId()) {

            // Save the employee ID in all source and target accounting lines.
            TransactionalDocument transactionalDocument = (TransactionalDocument) transForm.getDocument();
            SalaryExpenseTransferDocument salaryExpenseTransferDocument = (SalaryExpenseTransferDocument) transactionalDocument;
            List<ExpenseTransferAccountingLine> accountingLines = new ArrayList();
            accountingLines.addAll((List<ExpenseTransferAccountingLine>) salaryExpenseTransferDocument.getSourceAccountingLines());
            accountingLines.addAll((List<ExpenseTransferAccountingLine>) salaryExpenseTransferDocument.getTargetAccountingLines());
           
            for (ExpenseTransferAccountingLine line : accountingLines) {
                line.setEmplid(salaryExpenseTransferDocument.getEmplid());
            }
            super.processAccountingLineOverrides(transForm);
        }
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#refresh(ActionMapping, ActionForm, HttpServletRequest, HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SalaryExpenseTransferForm salaryExpenseTransferForm = (SalaryExpenseTransferForm) form;

        boolean rulePassed = runRule(salaryExpenseTransferForm, new EmployeeIdChangedEventBase(salaryExpenseTransferForm.getDocument()));

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
}
    private boolean runRule(SalaryExpenseTransferForm salaryExpenseTransferFormForm, KualiDocumentEventBase event) {
        // check any business rules

        boolean rulePassed = SpringServiceLocator.getKualiRuleService().applyRules(event);
        return rulePassed;
    }
}
