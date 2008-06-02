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

import static org.kuali.kfs.KFSKeyConstants.ERROR_ZERO_AMOUNT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rule.event.KualiDocumentEventBase;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.core.web.struts.form.KualiForm;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.AccountingLineOverride;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rule.event.AddAccountingLineEvent;
import org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.bo.ExpenseTransferAccountingLine;
import org.kuali.module.labor.bo.ExpenseTransferSourceAccountingLine;
import org.kuali.module.labor.bo.ExpenseTransferTargetAccountingLine;
import org.kuali.module.labor.bo.LaborAccountingLineOverride;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.document.LaborExpenseTransferDocumentBase;
import org.kuali.module.labor.service.SegmentedLookupResultsService;
import org.kuali.module.labor.web.struts.form.ExpenseTransferDocumentFormBase;

import edu.iu.uis.eden.exception.WorkflowException;


/**
 * Base Struts Action class for Benefit Expense Transfer Document.
 */
public class ExpenseTransferDocumentActionBase extends KualiAccountingDocumentActionBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ExpenseTransferDocumentActionBase.class);

    /**
     * Takes care of storing the action form in the user session and forwarding to the balance inquiry lookup action.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward performBalanceInquiryLookup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ExpenseTransferDocumentFormBase financialDocumentForm = (ExpenseTransferDocumentFormBase) form;
        TransactionalDocument document = financialDocumentForm.getTransactionalDocument();

        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

        // parse out the important strings from our methodToCall parameter
        String fullParameter = (String) request.getAttribute(KFSConstants.METHOD_TO_CALL_ATTRIBUTE);

        // parse out business object class name for lookup
        String boClassName = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_BOPARM_LEFT_DEL, KFSConstants.METHOD_TO_CALL_BOPARM_RIGHT_DEL);
        if (StringUtils.isBlank(boClassName)) {
            throw new RuntimeException("Illegal call to perform lookup, no business object class name specified.");
        }

        // build the parameters for the lookup url
        Properties parameters = new Properties();
        String conversionFields = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_PARM1_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM1_RIGHT_DEL);
        if (StringUtils.isNotBlank(conversionFields)) {
            parameters.put(KFSConstants.CONVERSION_FIELDS_PARAMETER, conversionFields);
        }

        // pass values from form that should be pre-populated on lookup search
        String parameterFields = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_PARM2_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM2_RIGHT_DEL);
        if (StringUtils.isNotBlank(parameterFields)) {
            String[] lookupParams = parameterFields.split(KFSConstants.FIELD_CONVERSIONS_SEPERATOR);

            for (int i = 0; i < lookupParams.length; i++) {
                String[] keyValue = lookupParams[i].split(KFSConstants.FIELD_CONVERSION_PAIR_SEPERATOR);

                // hard-coded passed value
                if (StringUtils.contains(keyValue[0], "'")) {
                    parameters.put(keyValue[1], StringUtils.replace(keyValue[0], "'", ""));
                }
                // passed value should come from property
                else if (StringUtils.isNotBlank(request.getParameter(keyValue[0]))) {
                    parameters.put(keyValue[1], request.getParameter(keyValue[0]));
                }
            }
        }

        // grab whether or not the "return value" link should be hidden or not
        String hideReturnLink = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_PARM3_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM3_RIGHT_DEL);
        if (StringUtils.isNotBlank(hideReturnLink)) {
            parameters.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, hideReturnLink);
        }

        // anchor, if it exists
        if (form instanceof KualiForm && StringUtils.isNotEmpty(((KualiForm) form).getAnchor())) {
            parameters.put(KFSConstants.LOOKUP_ANCHOR, ((KualiForm) form).getAnchor());
        }

        // determine what the action path is
        String actionPath = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_PARM4_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM4_RIGHT_DEL);
        if (StringUtils.isBlank(actionPath)) {
            throw new IllegalStateException("The \"actionPath\" attribute is an expected parameter for the <kul:balanceInquiryLookup> tag - it " + "should never be blank.");
        }

        // now add required parameters
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, "search");
        parameters.put(KFSConstants.DOC_FORM_KEY, GlobalVariables.getUserSession().addObject(form));
        parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, boClassName);
        parameters.put(KFSConstants.RETURN_LOCATION_PARAMETER, basePath + mapping.getPath() + ".do");
        //parameters.put(GLConstants.LookupableBeanKeys.SEGMENTED_LOOKUP_FLAG_NAME, Boolean.TRUE.toString());

        String lookupUrl = UrlFactory.parameterizeUrl(basePath + "/" + actionPath, parameters);

        return new ActionForward(lookupUrl, true);
    }

    /**
     * Populates the lines of the ST or BT document from a balance lookup. First, the data must be retrieved based on the selected
     * ids persisted from the framework. The basic steps are: 1) Retrieve selected (row) ids that were persisted 2) Each id has
     * form: {db object id}.{period name}.{line amount} 3) Retrieve the balance records associated with the object ids 4)Build an
     * accounting line from the retrieved balance record, using parsed period name as the pay period, and parsed amount as the new
     * line amount. 5) Call insertAccountingLine
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#refresh(ActionMapping, ActionForm, HttpServletRequest,
     *      HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.refresh(mapping, form, request, response);

        ExpenseTransferDocumentFormBase expenseTransferDocumentForm = (ExpenseTransferDocumentFormBase) form;

        Collection<PersistableBusinessObject> rawValues = null;
        Map<String, Set<String>> segmentedSelection = new HashMap<String, Set<String>>();

        if (StringUtils.equals(KFSConstants.MULTIPLE_VALUE, expenseTransferDocumentForm.getRefreshCaller())) {
            String lookupResultsSequenceNumber = expenseTransferDocumentForm.getLookupResultsSequenceNumber();

            if (StringUtils.isNotBlank(lookupResultsSequenceNumber)) {
                // actually returning from a multiple value lookup
                Set<String> selectedIds = getSegmentedLookupResultsService().retrieveSetOfSelectedObjectIds(lookupResultsSequenceNumber, GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier());
                for (String selectedId : selectedIds) {
                    String selectedObjId = StringUtils.substringBefore(selectedId, ".");
                    String selectedMonthData = StringUtils.substringAfter(selectedId, ".");

                    if (!segmentedSelection.containsKey(selectedObjId)) {
                        segmentedSelection.put(selectedObjId, new HashSet<String>());
                    }
                    segmentedSelection.get(selectedObjId).add(selectedMonthData);
                }

                LOG.debug("Asking segmentation service for object ids " + segmentedSelection.keySet());
                rawValues = getSegmentedLookupResultsService().retrieveSelectedResultBOs(lookupResultsSequenceNumber, segmentedSelection.keySet(), LedgerBalance.class, GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier());
            }

            if (rawValues != null) {
                boolean isFirstBalance = true;
                for (PersistableBusinessObject bo : rawValues) {

                    // reset the form with the first leadge balance
                    if (isFirstBalance) {
                        resetLookupFields(expenseTransferDocumentForm, (LedgerBalance) bo);
                        isFirstBalance = false;
                    }

                    for (String selectedMonthData : segmentedSelection.get(bo.getObjectId())) {
                        String selectedPeriodName = StringUtils.substringBefore(selectedMonthData, ".");
                        String selectedPeriodAmount = StringUtils.substringAfter(selectedMonthData, ".");

                        if (LaborConstants.periodCodeMapping.containsKey(selectedPeriodName)) {
                            String periodCode = LaborConstants.periodCodeMapping.get(selectedPeriodName);
                            ExpenseTransferAccountingLine line = (ExpenseTransferAccountingLine) expenseTransferDocumentForm.getFinancialDocument().getSourceAccountingLineClass().newInstance();
                            LaborExpenseTransferDocumentBase financialDocument = (LaborExpenseTransferDocumentBase) expenseTransferDocumentForm.getDocument();

                            try {
                                KualiDecimal lineAmount = (new KualiDecimal(selectedPeriodAmount)).divide(new KualiDecimal(100));

                                // Notice that user tried to import an accounting line which has Zero amount
                                if (KualiDecimal.ZERO.compareTo(lineAmount) == 0) {
                                    GlobalVariables.getErrorMap().putError(KFSPropertyConstants.SOURCE_ACCOUNTING_LINES, ERROR_ZERO_AMOUNT, "an accounting line");
                                }
                                else {
                                    buildAccountingLineFromLedgerBalance((LedgerBalance) bo, line, lineAmount, periodCode);

                                    // SpringContext.getBean(KualiRuleService.class).applyRules(new
                                    // AddAccountingLineEvent(KFSConstants.NEW_SOURCE_ACCT_LINE_PROPERTY_NAME, financialDocument,
                                    // line));
                                    SpringContext.getBean(PersistenceService.class).retrieveNonKeyFields(line);

                                    insertAccountingLine(true, expenseTransferDocumentForm, line);
                                    updateAccountOverrideCode(line);
                                    processAccountingLineOverrides(line);
                                }
                            }
                            catch (Exception e) {
                                // No way to recover gracefully, so throw it back as a RuntimeException
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }

                Collections.sort((List<Comparable>) expenseTransferDocumentForm.getFinancialDocument().getSourceAccountingLines());
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Overload the method in order to have balance importing section be populated with the last search criteria
     * 
     * @see org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase#loadDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.loadDocument(kualiDocumentFormBase);
        ExpenseTransferDocumentFormBase expenseTransferDocumentForm = (ExpenseTransferDocumentFormBase) kualiDocumentFormBase;
        expenseTransferDocumentForm.populateSearchFields();
    }

    /**
     * This method copies all accounting lines from financial document form if they pass validation rules
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward copyAllAccountingLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ExpenseTransferDocumentFormBase financialDocumentForm = (ExpenseTransferDocumentFormBase) form;
        for (Object line : financialDocumentForm.getFinancialDocument().getSourceAccountingLines()) {
            ExpenseTransferAccountingLine to = (ExpenseTransferAccountingLine) financialDocumentForm.getFinancialDocument().getTargetAccountingLineClass().newInstance();
            copyAccountingLine((ExpenseTransferAccountingLine) line, to);

            boolean rulePassed = runRule(new AddAccountingLineEvent(KFSConstants.NEW_TARGET_ACCT_LINE_PROPERTY_NAME, financialDocumentForm.getDocument(), to));

            // if the rule evaluation passed, let's add it
            if (rulePassed) {
                // add accountingLine
                SpringContext.getBean(PersistenceService.class).retrieveNonKeyFields(line);
                insertAccountingLine(false, financialDocumentForm, to);
            }
            processAccountingLineOverrides(to);
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
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
    public ActionForward deleteAllSourceAccountingLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ExpenseTransferDocumentFormBase financialDocumentForm = (ExpenseTransferDocumentFormBase) form;
        financialDocumentForm.getFinancialDocument().setSourceAccountingLines(new ArrayList());

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
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
    public ActionForward deleteAllTargetAccountingLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ExpenseTransferDocumentFormBase financialDocumentForm = (ExpenseTransferDocumentFormBase) form;
        financialDocumentForm.getFinancialDocument().setTargetAccountingLines(new ArrayList());

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * Copy a single accounting line
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#copyAccountingLine(ActionMapping, ActionForm,
     *      HttpServletRequest, HttpServletResponse)
     */
    public ActionForward copyAccountingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ExpenseTransferDocumentFormBase financialDocumentForm = (ExpenseTransferDocumentFormBase) form;
        LaborExpenseTransferDocumentBase financialDocument = (LaborExpenseTransferDocumentBase) financialDocumentForm.getDocument();

        int index = getSelectedLine(request);

        ExpenseTransferAccountingLine line = (ExpenseTransferAccountingLine) financialDocumentForm.getFinancialDocument().getTargetAccountingLineClass().newInstance();
        copyAccountingLine((ExpenseTransferAccountingLine) financialDocument.getSourceAccountingLine(index), line);

        boolean rulePassed = runRule(new AddAccountingLineEvent(KFSConstants.NEW_TARGET_ACCT_LINE_PROPERTY_NAME, financialDocumentForm.getDocument(), line));

        // if the rule evaluation passed, let's add it
        if (rulePassed) {
            // add accountingLine
            SpringContext.getBean(PersistenceService.class).retrieveNonKeyFields(line);
            insertAccountingLine(false, financialDocumentForm, line);
        }
        processAccountingLineOverrides(line);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Reset the lookup fields in the given expense transfer form with the given ledger balance
     * 
     * @param expenseTransferDocumentForm the given expense transfer form
     * @param the given ledger balance
     */
    protected void resetLookupFields(ExpenseTransferDocumentFormBase expenseTransferDocumentForm, LedgerBalance balance) {
        expenseTransferDocumentForm.setUniversityFiscalYear(balance.getUniversityFiscalYear());
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
        target.setPayrollEndDateFiscalPeriodCode(source.getPayrollEndDateFiscalPeriodCode());
        target.setOverrideCode(source.getOverrideCode());
        target.setPayrollTotalHours(source.getPayrollTotalHours());
    }

    /**
     * Translates <code>{@link LedgerBalance}</code> data into an <code>{@link ExpenseTransferAccountingLine}</code>
     * 
     * @param bo <code>{@link LedgerBalance}</code> instance
     * @param line <code>{@link ExpenseTransferAccountingLine}</code> to copy data to
     */
    private void buildAccountingLineFromLedgerBalance(LedgerBalance bo, ExpenseTransferAccountingLine line, KualiDecimal amount, String periodCode) {
        line.setChartOfAccountsCode(bo.getChartOfAccountsCode());
        line.setAccountNumber(bo.getAccountNumber());

        if (!KFSConstants.getDashSubAccountNumber().equals(bo.getSubAccountNumber())) {
            line.setSubAccountNumber(bo.getSubAccountNumber());
        }

        line.setPostingYear(bo.getUniversityFiscalYear());
        line.setPayrollEndDateFiscalYear(bo.getUniversityFiscalYear());
        line.setFinancialObjectCode(bo.getFinancialObjectCode());

        if (!KFSConstants.getDashFinancialSubObjectCode().equals(bo.getFinancialSubObjectCode())) {
            line.setFinancialSubObjectCode(bo.getFinancialSubObjectCode());
        }

        line.setBalanceTypeCode(bo.getFinancialBalanceTypeCode());
        line.setPositionNumber(bo.getPositionNumber());
        line.setAmount(amount);
        line.setEmplid(bo.getEmplid());
        line.setPayrollEndDateFiscalPeriodCode(periodCode);
    }

    /**
     * Processes accounting line overrides for output to JSP
     * 
     * @see org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase#processAccountingLineOverrides(java.util.List)
     */
    @Override
    protected void processAccountingLineOverrides(List accountingLines) {
        if (!accountingLines.isEmpty()) {
            SpringContext.getBean(PersistenceService.class).retrieveReferenceObjects(accountingLines, AccountingLineOverride.REFRESH_FIELDS);

            for (Iterator i = accountingLines.iterator(); i.hasNext();) {
                AccountingLine line = (AccountingLine) i.next();
                LaborAccountingLineOverride.processForOutput(line);
            }
        }
    }

    /**
     * Clear all overrides that are not needed.
     * 
     * @see org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase#clearOverridesThatBecameUnneeded(org.kuali.kfs.bo.AccountingLine)
     */
    @Override
    protected void clearOverridesThatBecameUnneeded(AccountingLine formLine) {
        AccountingLineOverride currentlyNeeded = LaborAccountingLineOverride.determineNeededOverrides(formLine);
        AccountingLineOverride currentOverride = AccountingLineOverride.valueOf(formLine.getOverrideCode());
        if (!currentOverride.isValidMask(currentlyNeeded)) {
            // todo: handle unsupported combinations of overrides (not a problem until we allow certain multiple overrides)
        }
        formLine.setOverrideCode(currentOverride.mask(currentlyNeeded).getCode());
    }

    /**
     * For given accounting line, set the corresponding override code
     * 
     * @param line accounting line
     */
    private void updateAccountOverrideCode(ExpenseTransferAccountingLine line) {
        AccountingLineOverride override = LaborAccountingLineOverride.determineNeededOverrides(line);
        line.setOverrideCode(override.getCode());
    }

    /**
     * Executes for the given event. This is more of a convenience method.
     * 
     * @param event to run the rules for
     * @return true if rule passes
     */
    private boolean runRule(KualiDocumentEventBase event) {
        // check any business rules

        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(event);
        return rulePassed;
    }

    /**
     * Get the BO class name of the set of lookup results
     * 
     * @param expenseTransferDocumentForm the Struts form for expense transfer document
     * @return the BO class name of the set of lookup results
     */
    protected String getLookupResultsBOClassName(ExpenseTransferDocumentFormBase expenseTransferDocumentForm) {
        return expenseTransferDocumentForm.getLookupResultsBOClassName();
    }

    /**
     * @return SegmentedLookupResultsService
     */
    private SegmentedLookupResultsService getSegmentedLookupResultsService() {
        return SpringContext.getBean(SegmentedLookupResultsService.class);
    }


    /**
     * This method will revert a TargetAccountingLine by overwriting its current values with the values in the corresponding
     * baseline accountingLine. This assumes that the user presses the revert button for a specific accounting line on the document
     * and that the document is represented by a FinancialDocumentFormBase.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    @Override
    public ActionForward revertTargetLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ExpenseTransferDocumentFormBase financialDocumentForm = (ExpenseTransferDocumentFormBase) form;
        LaborExpenseTransferDocumentBase financialDocument = (LaborExpenseTransferDocumentBase) financialDocumentForm.getDocument();

        int revertIndex = getSelectedLine(request);

        ExpenseTransferTargetAccountingLine originalLine = (ExpenseTransferTargetAccountingLine) financialDocumentForm.getBaselineTargetAccountingLine(revertIndex);
        ExpenseTransferTargetAccountingLine brokenLine = (ExpenseTransferTargetAccountingLine) financialDocument.getTargetAccountingLine(revertIndex);

        SpringContext.getBean(PersistenceService.class).refreshAllNonUpdatingReferences(originalLine);

        // *always* revert (so that if someone manually changes the line to its original values, then hits revert, they won't get an
        // error message saying "couldn't revert")
        brokenLine.copyFrom(originalLine);
        if (super.isSalesTaxRequired((AccountingDocument) financialDocumentForm.getDocument(), brokenLine)) {
            brokenLine.setSalesTaxRequired(true);
        }
        financialDocumentForm.getTargetLineDecorator(revertIndex).setRevertible(false);
        GlobalVariables.getMessageList().add(KFSKeyConstants.MESSAGE_REVERT_SUCCESSFUL);

        // no business rules to check, no events to create
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * This method will revert a SourceAccountingLine by overwriting its current values with the values in the corresponding
     * baseline accountingLine. This assumes that the user presses the revert button for a specific accounting line on the document
     * and that the document is represented by a FinancialDocumentFormBase.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    @Override
    public ActionForward revertSourceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ExpenseTransferDocumentFormBase financialDocumentForm = (ExpenseTransferDocumentFormBase) form;
        LaborExpenseTransferDocumentBase financialDocument = (LaborExpenseTransferDocumentBase) financialDocumentForm.getDocument();
        int revertIndex = getSelectedLine(request);

        ExpenseTransferSourceAccountingLine originalLine = (ExpenseTransferSourceAccountingLine) financialDocumentForm.getBaselineSourceAccountingLine(revertIndex);
        ExpenseTransferSourceAccountingLine brokenLine = (ExpenseTransferSourceAccountingLine) financialDocument.getSourceAccountingLine(revertIndex);

        SpringContext.getBean(PersistenceService.class).refreshAllNonUpdatingReferences(originalLine);

        // *always* revert (so that if someone manually changes the line to its original values, then hits revert, they won't get an
        // error message saying "couldn't revert")
        brokenLine.copyFrom(originalLine);
        if (super.isSalesTaxRequired((AccountingDocument) financialDocumentForm.getDocument(), brokenLine)) {
            brokenLine.setSalesTaxRequired(true);
        }
        financialDocumentForm.getSourceLineDecorator(revertIndex).setRevertible(false);
        GlobalVariables.getMessageList().add(KFSKeyConstants.MESSAGE_REVERT_SUCCESSFUL);

        // no business rules to check, no events to create
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
}
