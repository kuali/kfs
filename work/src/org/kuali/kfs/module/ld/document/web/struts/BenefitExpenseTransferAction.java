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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.util.ObjectUtil;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.bo.LaborLedgerPendingEntry;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.web.struts.form.BenefitExpenseTransferForm;
import org.kuali.module.labor.web.struts.form.ExpenseTransferDocumentFormBase;

/**
 * Struts Action class for the Benefit Expense Transfer Document.
 */
public class BenefitExpenseTransferAction extends ExpenseTransferDocumentActionBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BenefitExpenseTransferAction.class);

    /**
     * Gets the Business object class name
     * 
     * @param expenseTransferDocumentFormBase ExpenseTransferDocumentForm type
     * @return String classname
     * @see org.kuali.module.labor.web.struts.action.ExpenseTransferDocumentActionBase#getLookupResultsBOClassName(org.kuali.module.labor.web.struts.form.ExpenseTransferDocumentFormBase)
     */
    @Override
    protected String getLookupResultsBOClassName(ExpenseTransferDocumentFormBase expenseTransferDocumentForm) {
        return LedgerBalance.class.getName();
    }

    /**
     * @param expenseTransferDocumentFormBase ExpenseTransferDocumentForm type
     * @param balance LedgerBalance type
     * @return none
     * @see org.kuali.module.labor.web.struts.action.ExpenseTransferDocumentActionBase#resetLookupFields(org.kuali.module.labor.web.struts.form.ExpenseTransferDocumentFormBase,
     *      org.kuali.module.labor.bo.LedgerBalance)
     */
    @Override
    protected void resetLookupFields(ExpenseTransferDocumentFormBase expenseTransferDocumentForm, LedgerBalance balance) {
        BenefitExpenseTransferForm benefitExpenseTransferForm = (BenefitExpenseTransferForm) expenseTransferDocumentForm;
        ObjectUtil.buildObject(benefitExpenseTransferForm, balance);
    }

    /**
     * @param mapping ActionMapping
     * @param form ActionForm
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @see org.kuali.core.web.struts.action.KualiAction#performLookup(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward performLookup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // parse out the business object name from our methodToCall parameter
        String fullParameter = (String) request.getAttribute(KFSConstants.METHOD_TO_CALL_ATTRIBUTE);
        String boClassName = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_BOPARM_LEFT_DEL, KFSConstants.METHOD_TO_CALL_BOPARM_RIGHT_DEL);

        if (!StringUtils.equals(boClassName, LaborLedgerPendingEntry.class.getName())) {
            return super.performLookup(mapping, form, request, response);
        }

        String path = super.performLookup(mapping, form, request, response).getPath();
        path = path.replaceFirst(KFSConstants.LOOKUP_ACTION, LaborConstants.LONG_ROW_TABLE_INRUIRY_ACTION);
        return new ActionForward(path, true);
    }
}