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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.util.ObjectUtil;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.document.SalaryExpenseTransferDocument;
import org.kuali.module.labor.web.struts.form.ExpenseTransferDocumentFormBase;
import org.kuali.module.labor.web.struts.form.SalaryExpenseTransferForm;

/**
 * Struts action class for Salary Expense Transfer Document. This class extends the parent KualiTransactionalDocumentActionBase
 * class, which contains all common action methods. Since the SEP follows the basic transactional document pattern, there are no
 * specific actions that it has to implement; however, this empty class is necessary for integrating into the framework.
 */
public class SalaryExpenseTransferAction extends ExpenseTransferDocumentActionBase {
    /**
     * Resets lookup fields for salary expense transfer action
     * 
     * @see org.kuali.module.labor.web.struts.action.ExpenseTransferDocumentActionBase#resetLookupFields(org.kuali.module.labor.web.struts.form.ExpenseTransferDocumentFormBase, org.kuali.module.labor.bo.LedgerBalance)
     */
    @Override
    protected void resetLookupFields(ExpenseTransferDocumentFormBase expenseTransferDocumentForm, LedgerBalance balance) {
        SalaryExpenseTransferForm benefitExpenseTransferForm = (SalaryExpenseTransferForm) expenseTransferDocumentForm;
        ObjectUtil.buildObject(benefitExpenseTransferForm, balance);
    }

    /**
     * If user is approving document, capture the object code balances for comparison in business rules on route
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#docHandler(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.docHandler(mapping, form, request, response);

        SalaryExpenseTransferDocument salaryExpenseDocument = (SalaryExpenseTransferDocument) ((KualiDocumentFormBase) form).getDocument();
        if (salaryExpenseDocument.getDocumentHeader().getWorkflowDocument().isApprovalRequested()) {
            salaryExpenseDocument.setApprovalObjectCodeBalances(salaryExpenseDocument.getUnbalancedObjectCodes());
        }

        return forward;
    }

}
