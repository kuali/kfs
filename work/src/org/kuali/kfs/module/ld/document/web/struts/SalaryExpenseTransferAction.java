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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rule.event.KualiDocumentEventBase;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.bo.TargetAccountingLine;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rule.event.AddAccountingLineEvent;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.kfs.web.struts.action.KualiBalanceInquiryReportMenuAction;
import org.kuali.kfs.web.struts.form.KualiAccountingDocumentFormBase;
import org.kuali.module.labor.bo.ExpenseTransferAccountingLine;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.document.SalaryExpenseTransferDocument;
import org.kuali.module.labor.rules.event.EmployeeIdChangedEventBase;
import org.kuali.module.labor.web.struts.form.SalaryExpenseTransferForm;
import org.kuali.rice.KNSServiceLocator;

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

        // Needed to be executed for each accounting line that may have been added.
        boolean rulePassed = runRule(new EmployeeIdChangedEventBase(salaryExpenseTransferForm.getDocument()));
        Map<String, String> requestParams = (Map<String, String>) request.getParameterMap();
        
        Collection<PersistableBusinessObject> rawValues = null;
        if (StringUtils.equals(Constants.MULTIPLE_VALUE, salaryExpenseTransferForm.getRefreshCaller())) {
            String lookupResultsSequenceNumber = salaryExpenseTransferForm.getLookupResultsSequenceNumber();
            if (StringUtils.isNotBlank(lookupResultsSequenceNumber)) {
                // actually returning from a multiple value lookup
                String lookupResultsBOClassName = salaryExpenseTransferForm.getLookupResultsBOClassName();
                Class lookupResultsBOClass = Class.forName(lookupResultsBOClassName);
                
                rawValues = KNSServiceLocator.getLookupResultsService().retrieveSelectedResultBOs(lookupResultsSequenceNumber, lookupResultsBOClass, GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier());
            }
        }
        
        if (rawValues != null) {
            for (PersistableBusinessObject bo : rawValues) {
                ExpenseTransferAccountingLine line = (ExpenseTransferAccountingLine) salaryExpenseTransferForm.getFinancialDocument().getSourceAccountingLineClass().newInstance();
                buildAccountingLineFromLedgerBalance((LedgerBalance) bo, line);
                salaryExpenseTransferForm.setUniversityFiscalYear(((LedgerBalance) bo).getUniversityFiscalYear());
                insertAccountingLine(true, salaryExpenseTransferForm, line);
            }
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#refresh(ActionMapping, ActionForm, HttpServletRequest, HttpServletResponse)
     */
    public ActionForward copyAllAccountingLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiAccountingDocumentFormBase financialDocumentForm = (KualiAccountingDocumentFormBase) form;
        for (Object line : financialDocumentForm.getFinancialDocument().getSourceAccountingLines()) {
            ExpenseTransferAccountingLine to = (ExpenseTransferAccountingLine) financialDocumentForm.getFinancialDocument().getTargetAccountingLineClass().newInstance();
            copyAccountingLine((ExpenseTransferAccountingLine) line, to);

            boolean rulePassed = runRule(new AddAccountingLineEvent(KFSConstants.NEW_TARGET_ACCT_LINE_PROPERTY_NAME, financialDocumentForm.getDocument(), to));
            
            // if the rule evaluation passed, let's add it
            if (rulePassed) {
                // add accountingLine
                SpringServiceLocator.getPersistenceService().retrieveNonKeyFields(line);
                insertAccountingLine(false, financialDocumentForm, to);
            }
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#refresh(ActionMapping, ActionForm, HttpServletRequest, HttpServletResponse)
     */
    public ActionForward copyAccountingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiAccountingDocumentFormBase financialDocumentForm = (KualiAccountingDocumentFormBase) form;

        AccountingDocument financialDocument = (AccountingDocument) financialDocumentForm.getDocument();

        int index = getSelectedLine(request);

        ExpenseTransferAccountingLine line = (ExpenseTransferAccountingLine) financialDocumentForm.getFinancialDocument().getTargetAccountingLineClass().newInstance();
        copyAccountingLine((ExpenseTransferAccountingLine) financialDocument.getSourceAccountingLine(index), line);

        boolean rulePassed = runRule(new AddAccountingLineEvent(KFSConstants.NEW_TARGET_ACCT_LINE_PROPERTY_NAME, financialDocumentForm.getDocument(), line));
        
        // if the rule evaluation passed, let's add it
        if (rulePassed) {
            // add accountingLine
            SpringServiceLocator.getPersistenceService().retrieveNonKeyFields(line);
            insertAccountingLine(false, financialDocumentForm, line);
        }
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Copies content from one accounting line to the other. Ignores Source or Target information.
     *
     * @param source line to copy from
     * @param target new line to copy data to
     */
    private void copyAccountingLine(ExpenseTransferAccountingLine source, ExpenseTransferAccountingLine target) {
        target.setChartOfAccountsCode(source.getChartOfAccountsCode());
        target.setAccountNumber(source.getAccountNumber());
        target.setSubAccountNumber(source.getSubAccountNumber());
        target.setPostingYear(source.getPostingYear());
        target.setPayrollEndDateFiscalYear(source.getPayrollEndDateFiscalYear());
        target.setFinancialObjectCode(source.getFinancialObjectCode());
        target.setFinancialSubObjectCode(source.getFinancialSubObjectCode());
        target.setBalanceTypeCode(source.getBalanceTypeCode());
        target.setPositionNumber(source.getPositionNumber());
        target.setAmount(source.getAmount());
        target.setEmplid(source.getEmplid());
    }
    
    /**
     * Translates <code>{@link LedgerBalance}</code> data into an <code>{@link ExpenseTransferAccountingLine}</code>
     *
     * @param bo <code>{@link LedgerBalance}</code> instance
     * @param line <code>{@link ExpenseTransferAccountingLine}</code> to copy data to
     */
    private void buildAccountingLineFromLedgerBalance(LedgerBalance bo, ExpenseTransferAccountingLine line) {
        line.setChartOfAccountsCode(bo.getChartOfAccountsCode());
        line.setAccountNumber(bo.getAccountNumber());
        if (!bo.getSubAccountNumber().startsWith("-")) {
            line.setSubAccountNumber(bo.getSubAccountNumber());
        }
        line.setPostingYear(bo.getUniversityFiscalYear());
        line.setPayrollEndDateFiscalYear(bo.getUniversityFiscalYear());
        line.setFinancialObjectCode(bo.getFinancialObjectCode());
        if (!bo.getFinancialSubObjectCode().startsWith("-")) {
            line.setFinancialSubObjectCode(bo.getFinancialSubObjectCode());
        }
        line.setBalanceTypeCode(bo.getFinancialBalanceTypeCode());
        line.setPositionNumber(bo.getPositionNumber());
        line.setAmount(bo.getAccountLineAnnualBalanceAmount());
        line.setEmplid(bo.getPersonPayrollIdentifier());
    }
        
    /**
     * Executes for the given event. This is more of a convenience method.
     *
     * @param event to run the rules for
     */
    private boolean runRule(KualiDocumentEventBase event) {
        // check any business rules

        boolean rulePassed = SpringServiceLocator.getKualiRuleService().applyRules(event);
        return rulePassed;
    }
}
