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
package org.kuali.module.labor.web.struts.action;

import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.util.ObjectUtil;
import org.kuali.module.labor.web.struts.form.BenefitExpenseTransferForm;
import org.kuali.module.labor.web.struts.form.ExpenseTransferDocumentFormBase;

/**
 * This class is the action class for the Benefit Expense Transfer document.
 */
public class BenefitExpenseTransferAction extends ExpenseTransferDocumentActionBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BenefitExpenseTransferAction.class);

    /**
     * @see org.kuali.module.labor.web.struts.action.ExpenseTransferDocumentActionBase#getLookupResultsBOClassName(org.kuali.module.labor.web.struts.form.ExpenseTransferDocumentFormBase)
     */
    @Override
    protected String getLookupResultsBOClassName(ExpenseTransferDocumentFormBase expenseTransferDocumentForm) {
        return LedgerBalance.class.getName();
    }

    /**
     * @see org.kuali.module.labor.web.struts.action.ExpenseTransferDocumentActionBase#resetLookupFields(org.kuali.module.labor.web.struts.form.ExpenseTransferDocumentFormBase,
     *      org.kuali.module.labor.bo.LedgerBalance)
     */
    @Override
    protected void resetLookupFields(ExpenseTransferDocumentFormBase expenseTransferDocumentForm, LedgerBalance balance) {
        BenefitExpenseTransferForm benefitExpenseTransferForm = (BenefitExpenseTransferForm) expenseTransferDocumentForm;
        ObjectUtil.buildObject(benefitExpenseTransferForm, balance);
    }
}