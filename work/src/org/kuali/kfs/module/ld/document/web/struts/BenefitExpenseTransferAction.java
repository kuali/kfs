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
package org.kuali.kfs.module.ld.document.web.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine;
import org.kuali.kfs.module.ld.businessobject.LaborLedgerPendingEntry;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.rice.core.api.util.type.KualiDecimal;

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
     * @see org.kuali.kfs.module.ld.document.web.struts.ExpenseTransferDocumentActionBase#getLookupResultsBOClassName(org.kuali.kfs.module.ld.document.web.struts.ExpenseTransferDocumentFormBase)
     */
    @Override
    protected String getLookupResultsBOClassName(ExpenseTransferDocumentFormBase expenseTransferDocumentForm) {
        return LedgerBalance.class.getName();
    }

    /**
     * @param expenseTransferDocumentFormBase ExpenseTransferDocumentForm type
     * @param balance LedgerBalance type
     * @return none
     * @see org.kuali.kfs.module.ld.document.web.struts.ExpenseTransferDocumentActionBase#resetLookupFields(org.kuali.kfs.module.ld.document.web.struts.ExpenseTransferDocumentFormBase,
     *      org.kuali.kfs.module.ld.businessobject.LedgerBalance)
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
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#performLookup(org.apache.struts.action.ActionMapping,
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
    
    /**
     * Delete all source accounting lines
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionMapping
     * @throws Exception
     */
    @Override
    public ActionForward deleteAllSourceAccountingLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BenefitExpenseTransferForm financialDocumentForm = (BenefitExpenseTransferForm) form;
        financialDocumentForm.getBenefitExpenseTransferDocument().setNextSourceLineNumber(KFSConstants.ONE.intValue());
        
        return super.deleteAllSourceAccountingLines(mapping, form, request, response);
    }
    
    /**
     * Delete all target accounting lines
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionMapping
     * @throws Exception
     */
    @Override
    public ActionForward deleteAllTargetAccountingLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BenefitExpenseTransferForm financialDocumentForm = (BenefitExpenseTransferForm) form;
        financialDocumentForm.getBenefitExpenseTransferDocument().setNextTargetLineNumber(KFSConstants.ONE.intValue());
        
        return super.deleteAllTargetAccountingLines(mapping, form, request, response);
    }

    /**
     * @see org.kuali.kfs.module.ld.document.web.struts.ExpenseTransferDocumentActionBase#buildAccountingLineFromLedgerBalance(org.kuali.kfs.module.ld.businessobject.LedgerBalance, org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine, org.kuali.rice.core.api.util.type.KualiDecimal, java.lang.String)
     */
    @Override
    protected void buildAccountingLineFromLedgerBalance(LedgerBalance ledgerBalance, ExpenseTransferAccountingLine line, KualiDecimal amount, String periodCode) {
        super.buildAccountingLineFromLedgerBalance(ledgerBalance, line, amount, periodCode);
        line.setEmplid(LaborConstants.getDashEmplId());
        line.setPositionNumber(LaborConstants.getDashPositionNumber());
        
    }
}
