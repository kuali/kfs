/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.bc.document.validation.impl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.A21SubAccount;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.fp.service.FiscalYearFunctionControlService;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.BCConstants.AccountSalarySettingOnlyCause;
import org.kuali.kfs.module.bc.BCConstants.MonthSpreadDeleteType;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionMonthly;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionGeneralLedger;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;
import org.kuali.kfs.module.bc.document.service.BenefitsCalculationService;
import org.kuali.kfs.module.bc.document.service.BudgetDocumentService;
import org.kuali.kfs.module.bc.document.service.BudgetParameterService;
import org.kuali.kfs.module.bc.document.service.SalarySettingService;
import org.kuali.kfs.module.bc.document.validation.AddBudgetConstructionDocumentRule;
import org.kuali.kfs.module.bc.document.validation.AddPendingBudgetGeneralLedgerLineRule;
import org.kuali.kfs.module.bc.document.validation.DeleteMonthlySpreadRule;
import org.kuali.kfs.module.bc.document.validation.DeletePendingBudgetGeneralLedgerLineRule;
import org.kuali.kfs.module.bc.document.validation.SaveMonthlyBudgetRule;
import org.kuali.kfs.module.bc.util.BudgetParameterFinder;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.core.api.util.type.TypeUtils;
import org.kuali.rice.kns.rules.TransactionalDocumentRuleBase;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.krad.datadictionary.DataDictionary;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.exception.InfrastructureException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.PersistenceService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;

public class BudgetConstructionDocumentRules extends TransactionalDocumentRuleBase implements AddBudgetConstructionDocumentRule<BudgetConstructionDocument>, AddPendingBudgetGeneralLedgerLineRule<BudgetConstructionDocument, PendingBudgetConstructionGeneralLedger>, DeletePendingBudgetGeneralLedgerLineRule<BudgetConstructionDocument, PendingBudgetConstructionGeneralLedger>, DeleteMonthlySpreadRule<BudgetConstructionDocument>, SaveMonthlyBudgetRule<BudgetConstructionDocument, BudgetConstructionMonthly> {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionDocumentRules.class);

    // some services used here - other service refs are from parent classes
    // if this class is extended we may need to create protected getters
    protected static BudgetParameterService budgetParameterService = SpringContext.getBean(BudgetParameterService.class);
    protected static AccountingLineRuleHelperService accountingLineRuleHelper = SpringContext.getBean(AccountingLineRuleHelperService.class);
    protected static DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
    protected static SalarySettingService salarySettingService = SpringContext.getBean(SalarySettingService.class);
    protected static BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
    protected static FiscalYearFunctionControlService fiscalYearFunctionControlService = SpringContext.getBean(FiscalYearFunctionControlService.class);

    protected Collection<String> revenueObjectTypesParamValues = BudgetParameterFinder.getRevenueObjectTypes();
    protected Collection<String> expenditureObjectTypesParamValues = BudgetParameterFinder.getExpenditureObjectTypes();
    protected Collection<String> budgetAggregationCodesParamValues = BudgetParameterFinder.getBudgetAggregationCodes();
    protected Collection<String> fringeBenefitDesignatorCodesParamValues = BudgetParameterFinder.getFringeBenefitDesignatorCodes();
    protected Collection<String> salarySettingFundGroupsParamValues = BudgetParameterFinder.getSalarySettingFundGroups();
    protected Collection<String> salarySettingSubFundGroupsParamValues = BudgetParameterFinder.getSalarySettingSubFundGroups();

    // this field is highlighted for any errors found on an existing line
    protected static final String TARGET_ERROR_PROPERTY_NAME = KFSPropertyConstants.ACCOUNT_LINE_ANNUAL_BALANCE_AMOUNT;

    public BudgetConstructionDocumentRules() {
        super();
    }

    /**
     * @see org.kuali.kfs.module.bc.document.validation.AddBudgetConstructionDocumentRule#processAddBudgetConstructionDocumentRules(org.kuali.kfs.module.bc.document.BudgetConstructionDocument)
     */
    @Override
    public boolean processAddBudgetConstructionDocumentRules(BudgetConstructionDocument budgetConstructionDocument) {
        LOG.debug("processAddBudgetConstructionDocumentRules(Document) - start");

        MessageMap errors = GlobalVariables.getMessageMap();
        boolean isValid = true;

        // validate primitives for required field and formatting checks
        int originalErrorCount = errors.getErrorCount();
        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(budgetConstructionDocument);

        // check to see if any errors were reported
        int currentErrorCount = errors.getErrorCount();
        isValid &= (currentErrorCount == originalErrorCount);
        if (!isValid) {
            return isValid;
        }

        // can't create BC documents when in system view only mode
        // let the user know this up front
        if (!fiscalYearFunctionControlService.isBudgetUpdateAllowed(budgetConstructionDocument.getUniversityFiscalYear())) {
            errors.putError(KFSPropertyConstants.ACCOUNT_NUMBER, BCKeyConstants.MESSAGE_BUDGET_SYSTEM_VIEW_ONLY);
            isValid &= false;
        }

        // check existence of account first
        DataDictionary dd = dataDictionaryService.getDataDictionary();
        String pkeyValue = budgetConstructionDocument.getChartOfAccountsCode() + "-" + budgetConstructionDocument.getAccountNumber();
        isValid &= isValidAccount(budgetConstructionDocument.getAccount(), pkeyValue, dd, KFSPropertyConstants.ACCOUNT_NUMBER);
        if (isValid) {

            // run the rules checks preventing BC document creation - assumes account exists
            isValid &= this.isBudgetAllowed(budgetConstructionDocument, KFSPropertyConstants.ACCOUNT_NUMBER, errors, true, true);
        }

        if (!isValid) {

            // tell the user we can't create a new BC document along with the error reasons
            KNSGlobalVariables.getMessageList().add(BCKeyConstants.MESSAGE_BUDGET_NOCREATE_DOCUMENT);
        }

        LOG.debug("processAddBudgetConstructionDocumentRules(Document) - end");
        return isValid;
    }

    /**
     * Runs business rules prior to saving Budget Document proper. This is different than saving typical KFS documents in that the
     * document is not saved to the user's inbox. Saved Budget Documents must meet the same state requirements as the typical KFS
     * routed document, so required field checks must be done. Budget Documents can be opened by a user in edit mode multiple times
     * and while in edit mode documents can be pushed down the review hierarchy, monthly budgets and appointment funding updated,
     * benefits calculated, etc. Each of these operations require the document's data be in a consistent state with respect to
     * business rules before the operation be performed.
     *
     * @see org.kuali.rice.krad.rules.DocumentRuleBase#processSaveDocument(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean processSaveDocument(Document document) {
        LOG.debug("processSaveDocument(Document) - start");

        boolean isValid = true;

        // run through the attributes recursively and check dd stuff
        isValid &= isDocumentAttributesValid(document, true);

        if (isValid) {
            isValid &= processSaveBudgetDocumentRules((BudgetConstructionDocument) document, MonthSpreadDeleteType.NONE);
        }

        // no custom save rules since we are overriding and doing what we want here already

        LOG.debug("processSaveDocument(Document) - end");
        return isValid;
    }

    @Override
    public boolean processDeleteMonthlySpreadRules(BudgetConstructionDocument budgetConstructionDocument, MonthSpreadDeleteType monthSpreadDeleteType) {
        LOG.debug("processDeleteRevenueMonthlySpreadRules(Document) - start");

        boolean isValid = true;

        // run through the attributes recursively and check dd stuff
        isValid &= isDocumentAttributesValid(budgetConstructionDocument, true);

        if (isValid) {
            isValid &= processSaveBudgetDocumentRules(budgetConstructionDocument, monthSpreadDeleteType);
        }

        // no custom save rules since we are overriding and doing what we want here already

        LOG.debug("processDeleteRevenueMonthlySpreadRules(Document) - end");
        return isValid;

    }

    /**
     * Iterates through existing revenue and expenditure lines to do validation, ri checks on object/sub-object code and request
     * amount referential integrity checks against appointment funding and monthly detail amounts. Checks are performed when the
     * request amount has been updated, since initial add action, the last save event or since opening the document, whatever is
     * latest.
     *
     * @see org.kuali.module.budget.rule.SaveBudgetDocumentRule#processSaveBudgetDocumentRules(D)
     */
    public boolean processSaveBudgetDocumentRules(BudgetConstructionDocument budgetConstructionDocument, MonthSpreadDeleteType monthSpreadDeleteType) {

        MessageMap errors = GlobalVariables.getMessageMap();
        boolean doRevMonthRICheck = true;
        boolean doExpMonthRICheck = true;
        boolean isValid = true;
        int originalErrorCount;
        int currentErrorCount;

        // refresh only the doc refs we need
        List refreshFields = Collections.unmodifiableList(Arrays.asList(new String[] { KFSPropertyConstants.ACCOUNT, KFSPropertyConstants.SUB_ACCOUNT }));
        SpringContext.getBean(PersistenceService.class).retrieveReferenceObjects(budgetConstructionDocument, refreshFields);

        errors.addToErrorPath(KRADConstants.DOCUMENT_PROPERTY_NAME);

        if (monthSpreadDeleteType == MonthSpreadDeleteType.REVENUE) {
            doRevMonthRICheck = false;
            doExpMonthRICheck = true;
        }
        else {
            if (monthSpreadDeleteType == MonthSpreadDeleteType.EXPENDITURE) {
                doRevMonthRICheck = true;
                doExpMonthRICheck = false;
            }
        }

        // iterate and validate revenue lines
        isValid &= this.checkPendingBudgetConstructionGeneralLedgerLines(budgetConstructionDocument, errors, true, doRevMonthRICheck);

        // iterate and validate expenditure lines
        isValid &= this.checkPendingBudgetConstructionGeneralLedgerLines(budgetConstructionDocument, errors, false, doExpMonthRICheck);

        errors.removeFromErrorPath(KRADConstants.DOCUMENT_PROPERTY_NAME);

        return isValid;
    }

    /**
     * Checks a new PBGL line. Comprehensive checks are done.
     *
     * @param budgetConstructionDocument
     * @param pendingBudgetConstructionGeneralLedger
     * @param isRevenue
     * @return
     */
    @Override
    public boolean processAddPendingBudgetGeneralLedgerLineRules(BudgetConstructionDocument budgetConstructionDocument, PendingBudgetConstructionGeneralLedger pendingBudgetConstructionGeneralLedger, boolean isRevenue) {
        LOG.debug("processAddPendingBudgetGeneralLedgerLineRules() start");

        // List refreshFields;
        MessageMap errors = GlobalVariables.getMessageMap();
        boolean isValid = true;

        int originalErrorCount = errors.getErrorCount();

        // validate primitives for required field and formatting checks
        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(pendingBudgetConstructionGeneralLedger);

        // check to see if any errors were reported
        int currentErrorCount = errors.getErrorCount();
        isValid &= (currentErrorCount == originalErrorCount);

        if (isValid) {

            // refresh only the doc refs we need
            List refreshFields = Collections.unmodifiableList(Arrays.asList(new String[] { KFSPropertyConstants.ACCOUNT, KFSPropertyConstants.SUB_ACCOUNT }));
            SpringContext.getBean(PersistenceService.class).retrieveReferenceObjects(budgetConstructionDocument, refreshFields);
            // budgetConstructionDocument.getSubAccount().refreshReferenceObject(KFSPropertyConstants.A21_SUB_ACCOUNT);

            isValid &= this.checkPendingBudgetConstructionGeneralLedgerLine(budgetConstructionDocument, pendingBudgetConstructionGeneralLedger, errors, isRevenue, true);


            if (isValid) {
                // line checks ok - does line already exist in target revenue or expenditure list
                isValid &= isNewLineUnique(budgetConstructionDocument, pendingBudgetConstructionGeneralLedger, errors, isRevenue);
            }
        }

        if (!isValid) {
            LOG.info("business rule checks failed in processAddPendingBudgetGeneralLedgerLineRules in BudgetConstructionRules");
        }

        LOG.debug("processAddPendingBudgetGeneralLedgerLineRules() end");
        return isValid;
    }

    /**
     * Runs rules for deleting an existing revenue or expenditure line.
     *
     * @param budgetConstructionDocument
     * @param pendingBudgetConstructionGeneralLedger
     * @param isRevenue
     * @return
     */
    @Override
    public boolean processDeletePendingBudgetGeneralLedgerLineRules(BudgetConstructionDocument budgetConstructionDocument, PendingBudgetConstructionGeneralLedger pendingBudgetConstructionGeneralLedger, boolean isRevenue) {
        LOG.debug("processDeletePendingBudgetGeneralLedgerLineRules() start");

        MessageMap errors = GlobalVariables.getMessageMap();
        boolean isValid = true;

        // no delete allowed if base exists, the delete button shouldn't even exist in this case, but checking anyway
        if (pendingBudgetConstructionGeneralLedger.getFinancialBeginningBalanceLineAmount().isZero()) {
            isValid &= true;
        }
        else {
            isValid &= false;
            String pkeyVal = pendingBudgetConstructionGeneralLedger.getFinancialObjectCode() + "," + pendingBudgetConstructionGeneralLedger.getFinancialSubObjectCode();
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.ACCOUNT_LINE_ANNUAL_BALANCE_AMOUNT, BCKeyConstants.ERROR_NO_DELETE_ALLOWED_WITH_BASE, pkeyVal);
        }

        if (!isRevenue) {
            // no lines using fringe benefit target object codes allowed to be manually deleted by user
            // the lines are created by benefits calculation process
            // again the delete button shouldn't even exist
            isValid &= isNotFringeBenefitObject(fringeBenefitDesignatorCodesParamValues, pendingBudgetConstructionGeneralLedger, errors, false);

            // no deletion if salary setting option is turned on
            // and the line is a salary detail line and detail recs exist
            if (!SpringContext.getBean(SalarySettingService.class).isSalarySettingDisabled()) {
                if (pendingBudgetConstructionGeneralLedger.getLaborObject() != null) {
                    if (pendingBudgetConstructionGeneralLedger.getLaborObject().isDetailPositionRequiredIndicator()) {
                        if (pendingBudgetConstructionGeneralLedger.isPendingBudgetConstructionAppointmentFundingExists()) {
                            isValid &= false;
                            String pkeyVal = pendingBudgetConstructionGeneralLedger.getFinancialObjectCode() + "," + pendingBudgetConstructionGeneralLedger.getFinancialSubObjectCode();
                            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.ACCOUNT_LINE_ANNUAL_BALANCE_AMOUNT, BCKeyConstants.ERROR_NO_DELETE_ALLOWED_SALARY_DETAIL, pkeyVal);
                        }
                    }
                }
            }
            if (!SpringContext.getBean(BenefitsCalculationService.class).isBenefitsCalculationDisabled()) {

                // benefits calc is turned on, if the line is valid to remove and the request is not zero, set to calc benefits
                if (isValid && pendingBudgetConstructionGeneralLedger.getPositionObjectBenefit() != null && !pendingBudgetConstructionGeneralLedger.getPositionObjectBenefit().isEmpty()) {
                    budgetConstructionDocument.setBenefitsCalcNeeded(true);

                    // test if the line has monthly budgets
                    // this assumes business rule of non-zero monthly budget not allowed to sum to a zero annual amount
                    // that is, if annual amount is zero, the monthly record contains all zeros
                    if (pendingBudgetConstructionGeneralLedger.getBudgetConstructionMonthly() != null && !pendingBudgetConstructionGeneralLedger.getBudgetConstructionMonthly().isEmpty()) {
                        budgetConstructionDocument.setMonthlyBenefitsCalcNeeded(true);
                    }
                }
            }
        }

        LOG.debug("processDeletePendingBudgetGeneralLedgerLineRules() end");
        return isValid;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.validation.SaveMonthlyBudgetRule#processSaveMonthlyBudgetRules(org.kuali.kfs.module.bc.document.BudgetConstructionDocument,
     *      org.kuali.kfs.module.bc.businessobject.BudgetConstructionMonthly)
     */
    @Override
    public boolean processSaveMonthlyBudgetRules(BudgetConstructionDocument budgetConstructionDocument, BudgetConstructionMonthly budgetConstructionMonthly) {
        LOG.debug("processSaveMonthlyBudgetRules() start");

        budgetConstructionMonthly.refreshNonUpdateableReferences();
        PendingBudgetConstructionGeneralLedger pbgl = budgetConstructionMonthly.getPendingBudgetConstructionGeneralLedger();
        MessageMap errors = GlobalVariables.getMessageMap();
        boolean isValid = true;

        int originalErrorCount = errors.getErrorCount();

        // validate primitives for required field and formatting checks
        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(budgetConstructionMonthly);

        // check to see if any errors were reported
        int currentErrorCount = errors.getErrorCount();
        isValid &= (currentErrorCount == originalErrorCount);

        // Check special cleanup mode case and berate user on save of anything.
        // The user should delete the row, which bypasses this rule.
        if (!budgetConstructionDocument.isBudgetableDocument()) {
            isValid &= Boolean.FALSE;
            errors.putError(BCPropertyConstants.FINANCIAL_DOCUMENT_MONTH1_LINE_AMOUNT, BCKeyConstants.ERROR_BUDGET_DOCUMENT_NOT_BUDGETABLE, budgetConstructionDocument.getAccountNumber() + ";" + budgetConstructionDocument.getSubAccountNumber());
        }

        DataDictionary dd = dataDictionaryService.getDataDictionary();
        if (isValid) {

            ObjectCode objectCode = budgetConstructionMonthly.getFinancialObject();
            isValid &= isValidObjectCode(objectCode, budgetConstructionMonthly.getFinancialObjectCode(), dd, KFSPropertyConstants.FINANCIAL_OBJECT_CODE);

            if (StringUtils.isNotBlank(budgetConstructionMonthly.getFinancialSubObjectCode()) && !budgetConstructionMonthly.getFinancialSubObjectCode().equalsIgnoreCase(KFSConstants.getDashFinancialSubObjectCode())){
                SubObjectCode subObjectCode = budgetConstructionMonthly.getFinancialSubObject();
                isValid &= isValidSubObjectCode(subObjectCode, budgetConstructionMonthly.getFinancialSubObjectCode(), dd, KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
            }
        }

        if (isValid) {
            KualiInteger monthlyTotal = budgetConstructionMonthly.getFinancialDocumentMonthTotalLineAmount();
            if (!salarySettingService.isSalarySettingDisabled()) {
                if (pbgl.getLaborObject() != null && pbgl.getLaborObject().isDetailPositionRequiredIndicator()) {

                    // no request amount overrides allowed for salary setting detail lines
                    if (!monthlyTotal.equals(pbgl.getAccountLineAnnualBalanceAmount())) {
                        isValid &= false;
                        errors.putError(BCPropertyConstants.FINANCIAL_DOCUMENT_MONTH1_LINE_AMOUNT, BCKeyConstants.ERROR_MONTHLY_DETAIL_SALARY_OVERIDE, budgetConstructionMonthly.getFinancialObjectCode(), monthlyTotal.toString(), pbgl.getAccountLineAnnualBalanceAmount().toString());
                    }
                }
            }

            // check for monthly total adding to zero (makes no sense)
            if (monthlyTotal.isZero()) {
                boolean nonZeroMonthlyExists = false;
                nonZeroMonthlyExists |= budgetConstructionMonthly.getFinancialDocumentMonth1LineAmount().isNonZero();
                nonZeroMonthlyExists |= budgetConstructionMonthly.getFinancialDocumentMonth2LineAmount().isNonZero();
                nonZeroMonthlyExists |= budgetConstructionMonthly.getFinancialDocumentMonth3LineAmount().isNonZero();
                nonZeroMonthlyExists |= budgetConstructionMonthly.getFinancialDocumentMonth4LineAmount().isNonZero();
                nonZeroMonthlyExists |= budgetConstructionMonthly.getFinancialDocumentMonth5LineAmount().isNonZero();
                nonZeroMonthlyExists |= budgetConstructionMonthly.getFinancialDocumentMonth6LineAmount().isNonZero();
                nonZeroMonthlyExists |= budgetConstructionMonthly.getFinancialDocumentMonth7LineAmount().isNonZero();
                nonZeroMonthlyExists |= budgetConstructionMonthly.getFinancialDocumentMonth8LineAmount().isNonZero();
                nonZeroMonthlyExists |= budgetConstructionMonthly.getFinancialDocumentMonth9LineAmount().isNonZero();
                nonZeroMonthlyExists |= budgetConstructionMonthly.getFinancialDocumentMonth10LineAmount().isNonZero();
                nonZeroMonthlyExists |= budgetConstructionMonthly.getFinancialDocumentMonth11LineAmount().isNonZero();
                nonZeroMonthlyExists |= budgetConstructionMonthly.getFinancialDocumentMonth12LineAmount().isNonZero();
                if (nonZeroMonthlyExists) {
                    isValid &= false;
                    errors.putError(BCPropertyConstants.FINANCIAL_DOCUMENT_MONTH1_LINE_AMOUNT, BCKeyConstants.ERROR_MONTHLY_TOTAL_ZERO);
                }
            }
        }
        else {
            LOG.info("business rule checks failed in processSaveMonthlyBudgetRules in BudgetConstructionDocumentRules");
        }

        LOG.debug("processSaveMonthlyBudgetRules() end");
        return isValid;
    }

    /**
     * Iterates existing revenue or expenditure lines. Checks if request amount is non-zero or has changed and runs business rules
     * on the line.
     *
     * @param budgetConstructionDocument
     * @param errors
     * @param isRevenue
     * @return
     */
    protected boolean checkPendingBudgetConstructionGeneralLedgerLines(BudgetConstructionDocument budgetConstructionDocument, MessageMap errors, boolean isRevenue, boolean doMonthRICheck) {

        boolean isValid = true;
        boolean isReqAmountValid;
        int originalErrorCount;
        int currentErrorCount;
        List<PendingBudgetConstructionGeneralLedger> pendingBudgetConstructionGeneralLedgerLines;
        String linesErrorPath;


        if (isRevenue) {
            pendingBudgetConstructionGeneralLedgerLines = budgetConstructionDocument.getPendingBudgetConstructionGeneralLedgerRevenueLines();
            linesErrorPath = BCPropertyConstants.PENDING_BUDGET_CONSTRUCTION_GENERAL_LEDGER_REVENUE_LINES;
        }
        else {
            pendingBudgetConstructionGeneralLedgerLines = budgetConstructionDocument.getPendingBudgetConstructionGeneralLedgerExpenditureLines();
            linesErrorPath = BCPropertyConstants.PENDING_BUDGET_CONSTRUCTION_GENERAL_LEDGER_EXPENDITURE_LINES;
        }

        // iterate revenue or expenditure lines
        Integer index = 0;
        for (Iterator iter = pendingBudgetConstructionGeneralLedgerLines.iterator(); iter.hasNext(); index++) {

            PendingBudgetConstructionGeneralLedger element = (PendingBudgetConstructionGeneralLedger) iter.next();
            errors.addToErrorPath(linesErrorPath + "[" + index + "]");

            originalErrorCount = errors.getErrorCount();

            // run dd required field and format checks on request amount only, since only it can be changed by user
            // no sanity checks on hiddens and readonly field params
            validatePrimitiveFromDescriptor(element, TARGET_ERROR_PROPERTY_NAME, "", true);

            // check to see if any errors were reported
            currentErrorCount = errors.getErrorCount();
            isReqAmountValid = (currentErrorCount == originalErrorCount);
            isValid &= isReqAmountValid;

            // test for new errors from this point - if none, test if benefits calc required
            originalErrorCount = errors.getErrorCount();

            // has the request amount changed?
            boolean isRequestAmountChanged = (isReqAmountValid && (!element.getAccountLineAnnualBalanceAmount().equals(element.getPersistedAccountLineAnnualBalanceAmount())));

            // only do checks if request amount is non-zero and not equal to currently persisted amount
            // or the document is not budgetable and the request is non-zero
            if (isReqAmountValid && element.getAccountLineAnnualBalanceAmount().isNonZero()) {

                boolean isSalaryFringeLine = false;
                if (!isRevenue && fringeBenefitDesignatorCodesParamValues != null && element.getLaborObject() != null) {
                    isSalaryFringeLine = fringeBenefitDesignatorCodesParamValues.contains(element.getLaborObject().getFinancialObjectFringeOrSalaryCode());
                }
                boolean is2PLG = !isRevenue && element.getFinancialObjectCode().contentEquals(KFSConstants.BudgetConstructionConstants.OBJECT_CODE_2PLG);
                boolean isCleanupModeActionForceCheck = budgetConstructionDocument.isCleanupModeActionForceCheck();

                // Request notZero, do checks if user enters a change to a request amount or
                // (We are in cleanupMode and the current action (save or close-save) forces a cleanup mode check and
                // not 2PLG line and not salary fringe line)
                // This allows the user to use quick salary setting, monthly edit, global month delete to do cleanup work and
                // to print out values or push/pull before cleanup.
                if (isRequestAmountChanged || (doMonthRICheck && !is2PLG && !isSalaryFringeLine) || (!budgetConstructionDocument.isBudgetableDocument() && isCleanupModeActionForceCheck && !is2PLG && !isSalaryFringeLine)) {
                    isValid &= this.checkPendingBudgetConstructionGeneralLedgerLine(budgetConstructionDocument, element, errors, isRevenue, false);
                }
            }

            // Do RI type checks for request amount against monthly and salary setting detail if persisted amount changes
            // or a 2plg exists and the line is a salary setting detail line
            // Also tests if the line is has benefits associate and flags that a benefits calculation needs done.
            // Benefits calc is then called in the form action after successful rules check and save
            boolean forceTwoPlugRICheck = (budgetConstructionDocument.isContainsTwoPlug() && (element.getLaborObject() != null && element.getLaborObject().isDetailPositionRequiredIndicator()));

            // force monthly RI check if 2PLG and if request amount changes AND not a detail salary setting line
            boolean forceMonthlyRICheck = (budgetConstructionDocument.isContainsTwoPlug() && (element.getLaborObject() == null || !element.getLaborObject().isDetailPositionRequiredIndicator()));

            if (isReqAmountValid && (isRequestAmountChanged || forceTwoPlugRICheck)) {

                // check monthly for all rows
                if (doMonthRICheck || forceMonthlyRICheck) {
                    if (element.getBudgetConstructionMonthly() != null && !element.getBudgetConstructionMonthly().isEmpty()) {

                        BudgetConstructionMonthly budgetConstructionMonthly = element.getBudgetConstructionMonthly().get(0);
                        if (budgetConstructionMonthly != null) {
                            KualiInteger monthSum = KualiInteger.ZERO;
                            monthSum = monthSum.add(budgetConstructionMonthly.getFinancialDocumentMonth1LineAmount());
                            monthSum = monthSum.add(budgetConstructionMonthly.getFinancialDocumentMonth2LineAmount());
                            monthSum = monthSum.add(budgetConstructionMonthly.getFinancialDocumentMonth3LineAmount());
                            monthSum = monthSum.add(budgetConstructionMonthly.getFinancialDocumentMonth4LineAmount());
                            monthSum = monthSum.add(budgetConstructionMonthly.getFinancialDocumentMonth5LineAmount());
                            monthSum = monthSum.add(budgetConstructionMonthly.getFinancialDocumentMonth6LineAmount());
                            monthSum = monthSum.add(budgetConstructionMonthly.getFinancialDocumentMonth7LineAmount());
                            monthSum = monthSum.add(budgetConstructionMonthly.getFinancialDocumentMonth8LineAmount());
                            monthSum = monthSum.add(budgetConstructionMonthly.getFinancialDocumentMonth9LineAmount());
                            monthSum = monthSum.add(budgetConstructionMonthly.getFinancialDocumentMonth10LineAmount());
                            monthSum = monthSum.add(budgetConstructionMonthly.getFinancialDocumentMonth11LineAmount());
                            monthSum = monthSum.add(budgetConstructionMonthly.getFinancialDocumentMonth12LineAmount());

                            if (!monthSum.equals(element.getAccountLineAnnualBalanceAmount())) {
                                isValid &= false;
                                String pkeyVal = element.getFinancialObjectCode() + "," + element.getFinancialSubObjectCode();
                                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.ACCOUNT_LINE_ANNUAL_BALANCE_AMOUNT, BCKeyConstants.ERROR_MONTHLY_SUM_REQUEST_NOT_EQUAL, pkeyVal, monthSum.toString(), element.getAccountLineAnnualBalanceAmount().toString());
                            }
                        }
                    }
                }

                // check salary setting detail sum if expenditure line is a ss detail line
                // and salary setting option is turned on
                if (!SpringContext.getBean(SalarySettingService.class).isSalarySettingDisabled()) {
                    if (element.getLaborObject() != null) {
                        if (element.getLaborObject().isDetailPositionRequiredIndicator()) {

                            // sum the detail lines and compare against the accounting line request amount
                            KualiInteger salarySum = KualiInteger.ZERO;

                            // if salary setting detail exists, sum it otherwise default to zero
                            if (element.isPendingBudgetConstructionAppointmentFundingExists()) {

                                // run reportquery to get the salary request sum
                                salarySum = SpringContext.getBean(BudgetDocumentService.class).getPendingBudgetConstructionAppointmentFundingRequestSum(element);

                            }

                            if (!salarySum.equals(element.getAccountLineAnnualBalanceAmount())) {
                                isValid &= false;
                                String pkeyVal = element.getFinancialObjectCode() + "," + element.getFinancialSubObjectCode();
                                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.ACCOUNT_LINE_ANNUAL_BALANCE_AMOUNT, BCKeyConstants.ERROR_SALARY_SUM_REQUEST_NOT_EQUAL, pkeyVal, salarySum.toString(), element.getAccountLineAnnualBalanceAmount().toString());
                            }
                        }
                    }
                }

                // only do benefits calc needed test if the user changed something - not if forcing the RI check
                if (isReqAmountValid && !element.getAccountLineAnnualBalanceAmount().equals(element.getPersistedAccountLineAnnualBalanceAmount())) {

                    // if benefits calculation is turned on,
                    // check if the line is benefits related and call for calculation after save
                    if (!SpringContext.getBean(BenefitsCalculationService.class).isBenefitsCalculationDisabled()) {

                        // retest for added errors since testing this line started - if none, test if benefits calc required
                        currentErrorCount = errors.getErrorCount();
                        isReqAmountValid = (currentErrorCount == originalErrorCount);

                        if (isReqAmountValid && element.getPositionObjectBenefit() != null && !element.getPositionObjectBenefit().isEmpty()) {
                            budgetConstructionDocument.setBenefitsCalcNeeded(true);
                        }
                    }
                }
            }

            errors.removeFromErrorPath(linesErrorPath + "[" + index + "]");
        }

        return isValid;
    }

    /**
     * Checks a PBGL line. Assumes the line has been checked against the dd for formatting and if required
     *
     * @param budgetConstructionDocument
     * @param pendingBudgetConstructionGeneralLedger
     * @return
     */
    protected boolean checkPendingBudgetConstructionGeneralLedgerLine(BudgetConstructionDocument budgetConstructionDocument, PendingBudgetConstructionGeneralLedger pendingBudgetConstructionGeneralLedger, MessageMap errors, boolean isRevenue, boolean isAdd) {
        LOG.debug("checkPendingBudgetConstructionGeneralLedgerLine() start");

        boolean isValid = true;

        // now make sure all the necessary business objects are fully populated
        // this refreshes any refs not done by populate for display purposes auto-update="none"
        pendingBudgetConstructionGeneralLedger.refreshNonUpdateableReferences();

        isValid &= validatePBGLLine(pendingBudgetConstructionGeneralLedger, isAdd);
        if (isValid) {

            // all lines must have objects defined with financialBudgetAggregation = 'O';
            isValid &= isBudgetAggregationAllowed(budgetAggregationCodesParamValues, pendingBudgetConstructionGeneralLedger, errors, isAdd);

            isValid &= this.isBudgetAllowed(budgetConstructionDocument, KFSPropertyConstants.FINANCIAL_OBJECT_CODE, errors, isAdd, false);

            // revenue specific checks
            if (isRevenue) {

                // no revenue lines in CnG accounts or SDCI
                isValid &= isNotSalarySettingOnly(salarySettingFundGroupsParamValues, salarySettingSubFundGroupsParamValues, budgetConstructionDocument, pendingBudgetConstructionGeneralLedger, errors, isRevenue, isAdd);

                // line must use matching revenue object type
                isValid &= isObjectTypeAllowed(revenueObjectTypesParamValues, pendingBudgetConstructionGeneralLedger, errors, isRevenue, isAdd);

            }
            else {
                // expenditure specific checks

                // line must use matching expenditure object type
                isValid &= isObjectTypeAllowed(expenditureObjectTypesParamValues, pendingBudgetConstructionGeneralLedger, errors, isRevenue, isAdd);

                // no lines using labor objects in non-wage accounts
                isValid &= isNonWagesAccountNotLaborObject(budgetConstructionDocument, pendingBudgetConstructionGeneralLedger, errors, isAdd);

                // only lines using detail labor objects allowed in fund group CG and sfund group SDCI
                isValid &= isNotSalarySettingOnly(salarySettingFundGroupsParamValues, salarySettingSubFundGroupsParamValues, budgetConstructionDocument, pendingBudgetConstructionGeneralLedger, errors, isRevenue, isAdd);

                // no lines using fringe benefit target object codes allowed to be manually added by user
                // the lines are created by benefits calculation process
                isValid &= isNotFringeBenefitObject(fringeBenefitDesignatorCodesParamValues, pendingBudgetConstructionGeneralLedger, errors, isAdd);
            }

        }

        if (!isValid) {
            LOG.info("business rule checks failed in checkPendingBudgetConstructionGeneralLedgerLine in BudgetConstructionRules");
        }

        LOG.debug("checkPendingBudgetConstructionGeneralLedgerLine() end");
        return isValid;
    }

    protected boolean validatePBGLLine(PendingBudgetConstructionGeneralLedger pendingBudgetConstructionGeneralLedger, boolean isAdd) {
        if (pendingBudgetConstructionGeneralLedger == null) {
            throw new IllegalStateException(getKualiConfigurationService().getPropertyValueAsString(KFSKeyConstants.ERROR_DOCUMENT_NULL_ACCOUNTING_LINE));
        }

        // grab the service instance that will be needed by all the validate methods
        DataDictionary dd = dataDictionaryService.getDataDictionary();

        // retrieve each pbgl line object and validate
        boolean valid = true;

        // object code is required
        ObjectCode objectCode = pendingBudgetConstructionGeneralLedger.getFinancialObject();

        // this code calls a local version (not AccountingLineRuleHelper) of isValidObjectCode to add the bad value to the error
        // message
        if (isAdd) {
            valid &= isValidObjectCode(objectCode, pendingBudgetConstructionGeneralLedger.getFinancialObjectCode(), dd, KFSConstants.FINANCIAL_OBJECT_CODE_PROPERTY_NAME);
        }
        else {
            valid &= isValidObjectCode(objectCode, pendingBudgetConstructionGeneralLedger.getFinancialObjectCode(), dd, TARGET_ERROR_PROPERTY_NAME);
        }

        // sub object is not required
        if (StringUtils.isNotBlank(pendingBudgetConstructionGeneralLedger.getFinancialSubObjectCode()) && !pendingBudgetConstructionGeneralLedger.getFinancialSubObjectCode().equalsIgnoreCase(KFSConstants.getDashFinancialSubObjectCode())) {
            SubObjectCode subObjectCode = pendingBudgetConstructionGeneralLedger.getFinancialSubObject();

            // this code calls a local version (not AccountingLineRuleHelper) of isValidSubObjectCode to add the bad value to the
            // error message
            if (isAdd) {
                valid &= isValidSubObjectCode(subObjectCode, pendingBudgetConstructionGeneralLedger.getFinancialSubObjectCode(), dd, KFSConstants.FINANCIAL_SUB_OBJECT_CODE_PROPERTY_NAME);
            }
            else {
                valid &= isValidSubObjectCode(subObjectCode, pendingBudgetConstructionGeneralLedger.getFinancialSubObjectCode(), dd, TARGET_ERROR_PROPERTY_NAME);
            }
        }

        return valid;
    }

    /**
     * Validates a single primitive in a BO
     *
     * @param object
     * @param attributeName
     * @param errorPrefix
     * @param validateRequired
     */
    protected void validatePrimitiveFromDescriptor(Object object, String attributeName, String errorPrefix, boolean validateRequired) {

        try {
            PropertyDescriptor attributeDescriptor = PropertyUtils.getPropertyDescriptor(object, attributeName);
            validatePrimitiveFromDescriptor(object.getClass().getName(), object, attributeDescriptor, "", true);
        }
        catch (NoSuchMethodException e) {
            throw new InfrastructureException("unable to find propertyDescriptor for property '" + attributeName + "'", e);
        }
        catch (IllegalAccessException e) {
            throw new InfrastructureException("unable to access propertyDescriptor for property '" + attributeName + "'", e);
        }
        catch (InvocationTargetException e) {
            throw new InfrastructureException("unable to invoke methods for property '" + attributeName + "'", e);
        }
    }

    /**
     * Validates a primitive in a BO
     *
     * @param entryName
     * @param object
     * @param propertyDescriptor
     * @param errorPrefix
     * @param validateRequired
     */
    protected void validatePrimitiveFromDescriptor(String entryName, Object object, PropertyDescriptor propertyDescriptor, String errorPrefix, boolean validateRequired) {

        // validate the primitive attributes if defined in the dictionary
        if (null != propertyDescriptor && dataDictionaryService.isAttributeDefined(entryName, propertyDescriptor.getName())) {
            Object value = ObjectUtils.getPropertyValue(object, propertyDescriptor.getName());
            Class propertyType = propertyDescriptor.getPropertyType();

            if (TypeUtils.isStringClass(propertyType) || TypeUtils.isIntegralClass(propertyType) || TypeUtils.isDecimalClass(propertyType) || TypeUtils.isTemporalClass(propertyType)) {

                // check value format against dictionary
                if (value != null && StringUtils.isNotBlank(value.toString())) {
                    if (!TypeUtils.isTemporalClass(propertyType)) {
                        SpringContext.getBean(DictionaryValidationService.class).validate( object, entryName, propertyDescriptor.getName(), false);
                    }
                } else if (validateRequired) {
                    SpringContext.getBean(DictionaryValidationService.class).validate( object, entryName, propertyDescriptor.getName(), true);
                }
            }
        }
    }

    protected boolean isObjectTypeAllowed(Collection<String> paramValues, PendingBudgetConstructionGeneralLedger accountingLine, MessageMap errors, boolean isRevenue, boolean isAdd) {
        boolean isAllowed = true;

        if (paramValues != null) {
            if (!paramValues.contains(accountingLine.getFinancialObject().getFinancialObjectTypeCode())) {
                isAllowed = false;

                String targetErrorProperty;
                if (isAdd) {
                    targetErrorProperty = KFSPropertyConstants.FINANCIAL_OBJECT_CODE;
                }
                else {
                    targetErrorProperty = TARGET_ERROR_PROPERTY_NAME;
                }

                if (isRevenue) {
                    this.putError(errors, targetErrorProperty, KFSKeyConstants.ERROR_DOCUMENT_EXPENSE_ON_INCOME_SIDE, isAdd, accountingLine.getFinancialObjectCode());
                }
                else {
                    this.putError(errors, targetErrorProperty, KFSKeyConstants.ERROR_DOCUMENT_INCOME_ON_EXPENSE_SIDE, isAdd, accountingLine.getFinancialObjectCode());
                }
            }
        }
        else {
            isAllowed = false;
        }

        return isAllowed;
    }

    protected boolean isBudgetAggregationAllowed(Collection<String> paramValues, PendingBudgetConstructionGeneralLedger accountingLine, MessageMap errors, boolean isAdd) {
        boolean isAllowed = true;

        if (paramValues != null) {
            if (!paramValues.contains(accountingLine.getFinancialObject().getFinancialBudgetAggregationCd())) {
                isAllowed = false;

                this.putError(errors, KFSPropertyConstants.FINANCIAL_OBJECT_CODE, KFSKeyConstants.ERROR_DOCUMENT_INCORRECT_OBJ_CODE_WITH_BUDGET_AGGREGATION, isAdd, accountingLine.getFinancialObjectCode(), accountingLine.getFinancialObject().getFinancialBudgetAggregationCd());
            }
        }
        else {
            isAllowed = false;
        }

        return isAllowed;
    }

    protected boolean isNewLineUnique(BudgetConstructionDocument budgetConstructionDocument, PendingBudgetConstructionGeneralLedger newLine, MessageMap errors, boolean isRevenue) {
        boolean isUnique = true;
        List<PendingBudgetConstructionGeneralLedger> existingLines;

        if (isRevenue) {
            existingLines = budgetConstructionDocument.getPendingBudgetConstructionGeneralLedgerRevenueLines();
        }
        else {
            existingLines = budgetConstructionDocument.getPendingBudgetConstructionGeneralLedgerExpenditureLines();
        }

        if (BudgetConstructionRuleUtil.hasExistingPBGLLine(existingLines, newLine)) {
            isUnique = false;
            errors.putError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, BCKeyConstants.ERROR_BUDGET_LINE_EXISTS, newLine.getFinancialObjectCode() + "," + newLine.getFinancialSubObjectCode());
        }

        return isUnique;
    }

    protected boolean isNonWagesAccountNotLaborObject(BudgetConstructionDocument budgetConstructionDocument, PendingBudgetConstructionGeneralLedger accountingLine, MessageMap errors, boolean isAdd) {
        boolean isAllowed = true;

        if (budgetConstructionDocument.getAccount().getSubFundGroup() == null || !budgetConstructionDocument.getAccount().getSubFundGroup().isSubFundGroupWagesIndicator()) {
            if (accountingLine.getLaborObject() != null) {
                isAllowed = false;
                this.putError(errors, KFSPropertyConstants.FINANCIAL_OBJECT_CODE, BCKeyConstants.ERROR_LABOR_OBJECT_IN_NOWAGES_ACCOUNT, isAdd, accountingLine.getFinancialObjectCode());
            }
        }
        return isAllowed;
    }

    protected boolean isNotFringeBenefitObject(Collection<String> paramValues, PendingBudgetConstructionGeneralLedger accountingLine, MessageMap errors, boolean isAdd) {
        boolean isAllowed = true;

        if (paramValues != null) {
            if (accountingLine.getLaborObject() != null) {
                if (paramValues.contains(accountingLine.getLaborObject().getFinancialObjectFringeOrSalaryCode())) {
                    isAllowed = false;
                    this.putError(errors, KFSPropertyConstants.FINANCIAL_OBJECT_CODE, BCKeyConstants.ERROR_FRINGE_BENEFIT_OBJECT_NOT_ALLOWED, isAdd, accountingLine.getFinancialObjectCode());
                }
            }
        }
        else {
            isAllowed = false;
        }

        return isAllowed;
    }

    protected boolean isNotSalarySettingOnly(Collection<String> fundGroupParamValues, Collection<String> subfundGroupParamValues, BudgetConstructionDocument budgetConstructionDocument, PendingBudgetConstructionGeneralLedger accountingLine, MessageMap errors, boolean isRevenue, boolean isAdd) {
        boolean isAllowed = true;

        // check if account belongs to a fund or subfund that only allows salary setting lines
        AccountSalarySettingOnlyCause retVal = budgetParameterService.isSalarySettingOnlyAccount(budgetConstructionDocument);
        if (retVal != AccountSalarySettingOnlyCause.MISSING_PARAM) {
            if (retVal != AccountSalarySettingOnlyCause.NONE) {

                // the line must use an object that is a detail salary labor object
                if (isRevenue || accountingLine.getLaborObject() == null || !accountingLine.getLaborObject().isDetailPositionRequiredIndicator()) {

                    isAllowed = false;
                    if (retVal == AccountSalarySettingOnlyCause.FUND || retVal == AccountSalarySettingOnlyCause.FUND_AND_SUBFUND) {
                        this.putError(errors, KFSPropertyConstants.FINANCIAL_OBJECT_CODE, BCKeyConstants.ERROR_SALARY_SETTING_OBJECT_ONLY, isAdd, "fund " + budgetConstructionDocument.getAccount().getSubFundGroup().getFundGroupCode());

                    }
                    if (retVal == AccountSalarySettingOnlyCause.SUBFUND || retVal == AccountSalarySettingOnlyCause.FUND_AND_SUBFUND) {
                        this.putError(errors, KFSPropertyConstants.FINANCIAL_OBJECT_CODE, BCKeyConstants.ERROR_SALARY_SETTING_OBJECT_ONLY, isAdd, "subfund " + budgetConstructionDocument.getAccount().getSubFundGroup().getSubFundGroupCode());
                    }
                }
            }

        }
        else {
            // missing system parameter
            this.putError(errors, KFSPropertyConstants.FINANCIAL_OBJECT_CODE, BCKeyConstants.ERROR_SALARY_SETTING_OBJECT_ONLY_NO_PARAMETER, isAdd, budgetConstructionDocument.getAccount().getSubFundGroup().getFundGroupCode() + "," + budgetConstructionDocument.getAccount().getSubFundGroup().getSubFundGroupCode());
            isAllowed = false;
        }

        return isAllowed;
    }

    /**
     * runs rule checks that don't allow a budget
     *
     * @param budgetConstructionDocument
     * @param propertyName
     * @param errors
     * @param isAdd
     * @param isDocumentAdd
     * @return
     */
    protected boolean isBudgetAllowed(BudgetConstructionDocument budgetConstructionDocument, String propertyName, MessageMap errors, boolean isAdd, boolean isDocumentAdd) {
        boolean isAllowed = true;
        SimpleDateFormat tdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");

        // is account closed?
        if (!budgetConstructionDocument.getAccount().isActive()) {
            isAllowed = false;
            this.putError(errors, propertyName, KFSKeyConstants.ERROR_CLOSED, isAdd, "account: " + budgetConstructionDocument.getAccountNumber());
        }

        // is account expiration no budget allowed, currently < 1/1/(byfy-2)?
        Calendar expDate = BudgetConstructionRuleUtil.getNoBudgetAllowedExpireDate(budgetConstructionDocument.getUniversityFiscalYear());
        if (budgetConstructionDocument.getAccount().isExpired(expDate)) {
            isAllowed = false;
            this.putError(errors, propertyName, BCKeyConstants.ERROR_NO_BUDGET_ALLOWED, isAdd, budgetConstructionDocument.getAccountNumber(), tdf.format(budgetConstructionDocument.getAccount().getAccountExpirationDate()));

        }

        // is account a cash control account
        if (budgetConstructionDocument.getAccount().getBudgetRecordingLevelCode().equalsIgnoreCase(BCConstants.BUDGET_RECORDING_LEVEL_N)) {
            isAllowed = false;
            this.putError(errors, propertyName, BCKeyConstants.ERROR_BUDGET_RECORDING_LEVEL_NOT_ALLOWED, isAdd, budgetConstructionDocument.getAccountNumber(), BCConstants.BUDGET_RECORDING_LEVEL_N);
        }

        // grab the service instance that will be needed by all the validate methods
        DataDictionary dd = dataDictionaryService.getDataDictionary();

        if (StringUtils.isNotBlank(budgetConstructionDocument.getSubAccountNumber()) && !budgetConstructionDocument.getSubAccountNumber().equalsIgnoreCase(KFSConstants.getDashSubAccountNumber())) {
            SubAccount subAccount = budgetConstructionDocument.getSubAccount();

            // is subacct inactive or not exist?
            // this code calls a local version (not AccountingLineRuleHelper) of isValidSubAccount
            // to add the bad value to the error message
            if (isAdd) {
                if (isDocumentAdd) {
                    isAllowed &= this.isValidSubAccount(subAccount, budgetConstructionDocument.getSubAccountNumber(), dd, KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
                }
                else {
                    isAllowed &= this.isValidSubAccount(subAccount, budgetConstructionDocument.getSubAccountNumber(), dd, propertyName);
                }
            }
            else {
                isAllowed &= this.isValidSubAccount(subAccount, budgetConstructionDocument.getSubAccountNumber(), dd, TARGET_ERROR_PROPERTY_NAME);
            }

            // is subacct type cost share?
            // this hack is here since kuldev is missing one to one instances
            // and the RI ojb mapping produces an error when attempting to test if the
            // A21SubAccount attached to the document's SubAccount is null
            Map<String, Object> searchCriteria = new HashMap<String, Object>();
            searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, budgetConstructionDocument.getChartOfAccountsCode());
            searchCriteria.put(KFSPropertyConstants.ACCOUNT_NUMBER, budgetConstructionDocument.getAccountNumber());
            searchCriteria.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, budgetConstructionDocument.getSubAccountNumber());
            A21SubAccount a21SubAccount = businessObjectService.findByPrimaryKey(A21SubAccount.class, searchCriteria);
            if (ObjectUtils.isNotNull(a21SubAccount)) {
                if (a21SubAccount.getSubAccountTypeCode().equalsIgnoreCase(KFSConstants.SubAccountType.COST_SHARE)) {
                    isAllowed = false;
                    this.putError(errors, KFSPropertyConstants.SUB_ACCOUNT_NUMBER, BCKeyConstants.ERROR_SUB_ACCOUNT_TYPE_NOT_ALLOWED, isAdd, budgetConstructionDocument.getSubAccountNumber(), KFSConstants.SubAccountType.COST_SHARE);
                }
            }
        }

        return isAllowed;
    }

    public boolean isValidAccount(Account account, String value, DataDictionary dataDictionary, String errorPropertyName) {
        String label = dataDictionary.getBusinessObjectEntry(Account.class.getName()).getAttributeDefinition(KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME).getShortLabel();

        // make sure it exists
        if (ObjectUtils.isNull(account)) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, label + ":" + value);
            return false;
        }

        return true;
    }

    public boolean isValidSubAccount(SubAccount subAccount, String value, DataDictionary dataDictionary, String errorPropertyName) {
        String label = dataDictionary.getBusinessObjectEntry(SubAccount.class.getName()).getAttributeDefinition(KFSConstants.SUB_ACCOUNT_NUMBER_PROPERTY_NAME).getShortLabel();

        // make sure it exists
        if (ObjectUtils.isNull(subAccount)) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, label + ":" + value);
            return false;
        }

        // check to make sure it is active
        if (!subAccount.isActive()) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_DOCUMENT_SUB_ACCOUNT_INACTIVE, label + ":" + value);
            return false;
        }

        return true;
    }

    /**
     * Runs existence and active tests on the SubObjectCode reference This method is differenct than the one in
     * AccountingLineRuleHelper in that it adds the bad value to the errormessage This method signature should probably be added to
     * AccountingLineRuleHelper
     *
     * @param subObjectCode
     * @param value
     * @param dataDictionary
     * @param errorPropertyName
     * @return
     */
    public boolean isValidSubObjectCode(SubObjectCode subObjectCode, String value, DataDictionary dataDictionary, String errorPropertyName) {
        String label = dataDictionary.getBusinessObjectEntry(SubObjectCode.class.getName()).getAttributeDefinition(KFSConstants.FINANCIAL_SUB_OBJECT_CODE_PROPERTY_NAME).getShortLabel();

        // make sure it exists
        if (ObjectUtils.isNull(subObjectCode)) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, label + ":" + value);
            return false;
        }

        // check active flag
        if (!subObjectCode.isActive()) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_INACTIVE, label + ":" + value);
            return false;
        }
        return true;
    }

    /**
     * Runs existence and active tests on the ObjectCode reference This method is differenct than the one in
     * AccountingLineRuleHelper in that it adds the bad value to the errormessage This method signature should probably be added to
     * AccountingLineRuleHelper
     *
     * @param objectCode
     * @param value
     * @param dataDictionary
     * @param errorPropertyName
     * @return
     */
    public boolean isValidObjectCode(ObjectCode objectCode, String value, DataDictionary dataDictionary, String errorPropertyName) {
        String label = dataDictionary.getBusinessObjectEntry(ObjectCode.class.getName()).getAttributeDefinition(KFSConstants.FINANCIAL_OBJECT_CODE_PROPERTY_NAME).getShortLabel();

        // make sure it exists
        if (ObjectUtils.isNull(objectCode)) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, label + ":" + value);
            return false;
        }

        // check active status
        if (!objectCode.isFinancialObjectActiveCode()) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_INACTIVE, label + ":" + value);
            return false;
        }

        return true;
    }

    /**
     * puts error to errormap for propertyName if isAdd, otherwise the property name is replaced with value of
     * TARGET_ERROR_PROPERTY_NAME
     *
     * @param propertyName
     * @param errorKey
     * @param isAdd
     * @param errorParameters
     */
    protected void putError(MessageMap errors, String propertyName, String errorKey, boolean isAdd, String... errorParameters) {

        if (isAdd) {
            errors.putError(propertyName, errorKey, errorParameters);
        }
        else {
            errors.putError(TARGET_ERROR_PROPERTY_NAME, errorKey, errorParameters);
        }

    }
}
