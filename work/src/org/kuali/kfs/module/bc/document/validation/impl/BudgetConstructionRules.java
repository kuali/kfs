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
package org.kuali.module.budget.rules;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.RiceConstants;
import org.kuali.core.datadictionary.DataDictionary;
import org.kuali.core.document.Document;
import org.kuali.core.exceptions.InfrastructureException;
import org.kuali.core.rules.TransactionalDocumentRuleBase;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiInteger;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypeUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.AccountingLineRuleHelperService;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.BCKeyConstants;
import org.kuali.module.budget.BCParameterKeyConstants;
import org.kuali.module.budget.BCPropertyConstants;
import org.kuali.module.budget.BCConstants.AccountSalarySettingOnlyCause;
import org.kuali.module.budget.BCConstants.MonthSpreadDeleteType;
import org.kuali.module.budget.bo.BudgetConstructionMonthly;
import org.kuali.module.budget.bo.PendingBudgetConstructionAppointmentFunding;
import org.kuali.module.budget.bo.PendingBudgetConstructionGeneralLedger;
import org.kuali.module.budget.bo.SalarySettingExpansion;
import org.kuali.module.budget.document.BudgetConstructionDocument;
import org.kuali.module.budget.rule.AddPendingBudgetGeneralLedgerLineRule;
import org.kuali.module.budget.rule.DeleteMonthlySpreadRule;
import org.kuali.module.budget.rule.DeletePendingBudgetGeneralLedgerLineRule;
import org.kuali.module.budget.service.BenefitsCalculationService;
import org.kuali.module.budget.service.BudgetDocumentService;
import org.kuali.module.budget.service.BudgetParameterService;
import org.kuali.module.budget.service.SalarySettingService;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.bo.SubObjCd;

public class BudgetConstructionRules extends TransactionalDocumentRuleBase implements AddPendingBudgetGeneralLedgerLineRule<BudgetConstructionDocument, PendingBudgetConstructionGeneralLedger>, DeletePendingBudgetGeneralLedgerLineRule<BudgetConstructionDocument, PendingBudgetConstructionGeneralLedger>, DeleteMonthlySpreadRule<BudgetConstructionDocument> {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionRules.class);

    // some services used here - other service refs are from parent classes
    // if this class is extended we may need to create protected getters
    private static ParameterService parameterService = SpringContext.getBean(ParameterService.class);
    private static BudgetParameterService budgetParameterService = SpringContext.getBean(BudgetParameterService.class);
    private static AccountingLineRuleHelperService accountingLineRuleHelper = SpringContext.getBean(AccountingLineRuleHelperService.class);
    private static DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);

    private List revenueObjectTypesParamValues;
    private List expenditureObjectTypesParamValues;
    private List budgetAggregationCodesParamValues;
    private List fringeBenefitDesignatorCodesParamValues;
    private List salarySettingFundGroupsParamValues;
    private List salarySettingSubFundGroupsParamValues;

    // this field is highlighted for any errors found on an existing line
    private static final String TARGET_ERROR_PROPERTY_NAME = KFSPropertyConstants.ACCOUNT_LINE_ANNUAL_BALANCE_AMOUNT;

    public BudgetConstructionRules() {
        super();

        // TODO may want to get all the system param values here once, instead of the process*Rules methods??
    }

    /**
     * Runs business rules prior to saving Budget Document proper. This is different than saving typical KFS documents in that the
     * document is not saved to the user's inbox. Saved Budget Documents must meet the same state requirements as the typical KFS
     * routed document, so required field checks must be done. Budget Documents can be opened by a user in edit mode multiple times
     * and while in edit mode documents can be pushed down the review hierarchy, monthly budgets and appointment funding updated,
     * benefits calculated, etc. Each of these operations require the document's data be in a consistent state with respect to
     * business rules before the operation be performed.
     * 
     * @see org.kuali.core.rules.DocumentRuleBase#processSaveDocument(org.kuali.core.document.Document)
     */
    @Override
    public boolean processSaveDocument(Document document) {
        LOG.debug("processSaveDocument(Document) - start");

        boolean isValid = true;

        // run through the attributes recursively and check dd stuff
        isValid &= isDocumentAttributesValid(document, true);

        if (isValid) {
            // TODO may need to add doc or bcdoc to error path?
            isValid &= processSaveBudgetDocumentRules((BudgetConstructionDocument) document, MonthSpreadDeleteType.NONE);
        }

        // no custom save rules since we are overriding and doing what we want here already

        LOG.debug("processSaveDocument(Document) - end");
        return isValid;
    }
    
    public boolean processDeleteMonthlySpreadRules(BudgetConstructionDocument budgetConstructionDocument, MonthSpreadDeleteType monthSpreadDeleteType){
        LOG.debug("processDeleteRevenueMonthlySpreadRules(Document) - start");

        boolean isValid = true;

        // run through the attributes recursively and check dd stuff
        isValid &= isDocumentAttributesValid(budgetConstructionDocument, true);

        if (isValid) {
            // TODO may need to add doc or bcdoc to error path?
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

        ErrorMap errors = GlobalVariables.getErrorMap();
        boolean doRevMonthRICheck = true;
        boolean doExpMonthRICheck = true;
        boolean isValid = true;
        int originalErrorCount;
        int currentErrorCount;

        Class docClass = budgetConstructionDocument.getClass();

        // TODO move this to a method and replace call
        // get the system parameters we will use here
        revenueObjectTypesParamValues = budgetParameterService.getParameterValues(docClass, BCParameterKeyConstants.REVENUE_OBJECT_TYPES);
        expenditureObjectTypesParamValues = budgetParameterService.getParameterValues(docClass, BCParameterKeyConstants.EXPENDITURE_OBJECT_TYPES);
        budgetAggregationCodesParamValues = budgetParameterService.getParameterValues(docClass, BCParameterKeyConstants.BUDGET_AGGREGATION_CODES);
        fringeBenefitDesignatorCodesParamValues = budgetParameterService.getParameterValues(docClass, BCParameterKeyConstants.FRINGE_BENEFIT_DESIGNATOR_CODES);
        salarySettingFundGroupsParamValues = budgetParameterService.getParameterValues(docClass, BCParameterKeyConstants.SALARY_SETTING_FUND_GROUPS);
        salarySettingSubFundGroupsParamValues = budgetParameterService.getParameterValues(docClass, BCParameterKeyConstants.SALARY_SETTING_SUB_FUND_GROUPS);

        // TODO move this to a method and replace with call
        // refresh only the doc refs we need
        List refreshFields = Collections.unmodifiableList(Arrays.asList(new String[] { KFSPropertyConstants.ACCOUNT, KFSPropertyConstants.SUB_ACCOUNT }));
        SpringContext.getBean(PersistenceService.class).retrieveReferenceObjects(budgetConstructionDocument, refreshFields);
        // budgetConstructionDocument.getSubAccount().refreshReferenceObject(KFSPropertyConstants.A21_SUB_ACCOUNT);

        errors.addToErrorPath(RiceConstants.DOCUMENT_PROPERTY_NAME);
        
        if (monthSpreadDeleteType == MonthSpreadDeleteType.REVENUE){
            doRevMonthRICheck = false;
            doExpMonthRICheck = true;
        } else {
            if (monthSpreadDeleteType == MonthSpreadDeleteType.EXPENDITURE){
                doRevMonthRICheck = true;
                doExpMonthRICheck = false;
            }
        }

        // iterate and validate revenue lines
        isValid &= this.checkPendingBudgetConstructionGeneralLedgerLines(budgetConstructionDocument, errors, true, doRevMonthRICheck);

        // iterate and validate expenditure lines
        isValid &= this.checkPendingBudgetConstructionGeneralLedgerLines(budgetConstructionDocument, errors, false, doExpMonthRICheck);

        errors.removeFromErrorPath(RiceConstants.DOCUMENT_PROPERTY_NAME);

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
    public boolean processAddPendingBudgetGeneralLedgerLineRules(BudgetConstructionDocument budgetConstructionDocument, PendingBudgetConstructionGeneralLedger pendingBudgetConstructionGeneralLedger, boolean isRevenue) {
        LOG.debug("processAddPendingBudgetGeneralLedgerLineRules() start");

        // List refreshFields;
        ErrorMap errors = GlobalVariables.getErrorMap();
        boolean isValid = true;

        Class docClass = budgetConstructionDocument.getClass();

        // TODO move this to a method and replace with a call
        // get the system parameters we will use here
        revenueObjectTypesParamValues = budgetParameterService.getParameterValues(docClass, BCParameterKeyConstants.REVENUE_OBJECT_TYPES);
        expenditureObjectTypesParamValues = budgetParameterService.getParameterValues(docClass, BCParameterKeyConstants.EXPENDITURE_OBJECT_TYPES);
        budgetAggregationCodesParamValues = budgetParameterService.getParameterValues(docClass, BCParameterKeyConstants.BUDGET_AGGREGATION_CODES);
        fringeBenefitDesignatorCodesParamValues = budgetParameterService.getParameterValues(docClass, BCParameterKeyConstants.FRINGE_BENEFIT_DESIGNATOR_CODES);
        salarySettingFundGroupsParamValues = budgetParameterService.getParameterValues(docClass, BCParameterKeyConstants.SALARY_SETTING_FUND_GROUPS);
        salarySettingSubFundGroupsParamValues = budgetParameterService.getParameterValues(docClass, BCParameterKeyConstants.SALARY_SETTING_SUB_FUND_GROUPS);

        int originalErrorCount = errors.getErrorCount();

        // validate primatives for required field and formatting checks
        getDictionaryValidationService().validateBusinessObject(pendingBudgetConstructionGeneralLedger);

        // check to see if any errors were reported
        int currentErrorCount = errors.getErrorCount();
        isValid &= (currentErrorCount == originalErrorCount);

        if (isValid) {

            // TODO move this to a method and replace with call
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
    public boolean processDeletePendingBudgetGeneralLedgerLineRules(BudgetConstructionDocument budgetConstructionDocument, PendingBudgetConstructionGeneralLedger pendingBudgetConstructionGeneralLedger, boolean isRevenue) {
        LOG.debug("processDeletePendingBudgetGeneralLedgerLineRules() start");

        ErrorMap errors = GlobalVariables.getErrorMap();
        boolean isValid = true;
        Class docClass = budgetConstructionDocument.getClass();

        // TODO move this to a method and replace with a call
        // get the system parameters we will use here
        fringeBenefitDesignatorCodesParamValues = budgetParameterService.getParameterValues(docClass, BCParameterKeyConstants.FRINGE_BENEFIT_DESIGNATOR_CODES);

        // no delete allowed if base exists, the delete button shouldn't even exist in this case, but checking anyway
        if (pendingBudgetConstructionGeneralLedger.getFinancialBeginningBalanceLineAmount().isZero()) {
            isValid &= true;
        }
        else {
            isValid &= false;
            String pkeyVal = pendingBudgetConstructionGeneralLedger.getFinancialObjectCode() + "," + pendingBudgetConstructionGeneralLedger.getFinancialSubObjectCode();
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.ACCOUNT_LINE_ANNUAL_BALANCE_AMOUNT, BCKeyConstants.ERROR_NO_DELETE_ALLOWED_WITH_BASE, pkeyVal);
        }

        if (!isRevenue) {
            // no lines using fringe benefit target object codes allowed to be manually deleted by user
            // the lines are created by benefits calculation process
            // again the delete button shouldn't even exist
            isValid &= isNotFringeBenefitObject(fringeBenefitDesignatorCodesParamValues, pendingBudgetConstructionGeneralLedger, errors, false);

            // no deletion if salary setting option is turned on 
            // and the line is a salary detail line and detail recs exist
            if (!SpringContext.getBean(SalarySettingService.class).isSalarySettingDisabled()){
                if (pendingBudgetConstructionGeneralLedger.getLaborObject() != null) {
                    if (pendingBudgetConstructionGeneralLedger.getLaborObject().isDetailPositionRequiredIndicator()) {
                        if (pendingBudgetConstructionGeneralLedger.isPendingBudgetConstructionAppointmentFundingExists()) {
                            isValid &= false;
                            String pkeyVal = pendingBudgetConstructionGeneralLedger.getFinancialObjectCode() + "," + pendingBudgetConstructionGeneralLedger.getFinancialSubObjectCode();
                            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.ACCOUNT_LINE_ANNUAL_BALANCE_AMOUNT, BCKeyConstants.ERROR_NO_DELETE_ALLOWED_SALARY_DETAIL, pkeyVal);
                        }
                    }
                }
            }
            if (!SpringContext.getBean(BenefitsCalculationService.class).isBenefitsCalculationDisabled()){

                // benefits calc is turned on, if the line is valid to remove and the request is not zero, set to calc benefits
                if (isValid && pendingBudgetConstructionGeneralLedger.getPositionObjectBenefit() != null && !pendingBudgetConstructionGeneralLedger.getPositionObjectBenefit().isEmpty()){
                    budgetConstructionDocument.setBenefitsCalcNeeded(true);
                    
                    // test if the line has monthly budgets
                    // this assumes business rule of non-zero monthly budget not allowed to sum to a zero annual amount
                    // that is, if annual amount is zero, the monthly record contains all zeros
                    if (pendingBudgetConstructionGeneralLedger.getBudgetConstructionMonthly() != null && !pendingBudgetConstructionGeneralLedger.getBudgetConstructionMonthly().isEmpty()){
                        budgetConstructionDocument.setMonthlyBenefitsCalcNeeded(true);                        
                    }
                }
            }
        }

        LOG.debug("processDeletePendingBudgetGeneralLedgerLineRules() end");
        return isValid;
    }

    /**
     * Iterates existing revenue or expenditure lines. Checks if request amount is non-zero and runs business rules on the line.
     * TODO In addition to using the non-zero request test add a previous request var to the PBGL BO and as a hidden in the JSP and
     * test for differences as an indicator it was touched. update method comments when done here.
     * 
     * @param budgetConstructionDocument
     * @param errors
     * @param isRevenue
     * @return
     */
    private boolean checkPendingBudgetConstructionGeneralLedgerLines(BudgetConstructionDocument budgetConstructionDocument, ErrorMap errors, boolean isRevenue, boolean doMonthRICheck) {

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
            // TODO may not need this since required/format check is done as part of form populate??
            validatePrimitiveFromDescriptor(element, TARGET_ERROR_PROPERTY_NAME, "", true);

            // TODO can validateDocumentAttribute be used on non primative instead of local validatePrimitiveFromDescriptor?? -
            // remove when tested
            // getDictionaryValidationService().validateDocumentAttribute(budgetConstructionDocument, TARGET_ERROR_PROPERTY_NAME,
            // "");

            // check to see if any errors were reported
            currentErrorCount = errors.getErrorCount();
            isReqAmountValid = (currentErrorCount == originalErrorCount);
            isValid &= isReqAmountValid;

            // test for new errors from this point - if none, test if benefits calc required
            originalErrorCount = errors.getErrorCount();

            // only do checks if request amount is non-zero and not equal to currently persisted amount
            if (isReqAmountValid && element.getAccountLineAnnualBalanceAmount().isNonZero()) {
                if (!element.getAccountLineAnnualBalanceAmount().equals(element.getPersistedAccountLineAnnualBalanceAmount())) {
                    isValid &= this.checkPendingBudgetConstructionGeneralLedgerLine(budgetConstructionDocument, element, errors, isRevenue, false);
                }
            }

            // do RI type checks for request amount against monthly and salary setting detail if persisted amount changes
            // also check if benefits calc needs done
            if (isReqAmountValid && !element.getAccountLineAnnualBalanceAmount().equals(element.getPersistedAccountLineAnnualBalanceAmount())) {

                // check monthly for all rows
                if (doMonthRICheck){
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
                            
                            if (!monthSum.equals(element.getAccountLineAnnualBalanceAmount())){
                                isValid &= false;
                                String pkeyVal = element.getFinancialObjectCode() + "," + element.getFinancialSubObjectCode();
                                GlobalVariables.getErrorMap().putError(KFSPropertyConstants.ACCOUNT_LINE_ANNUAL_BALANCE_AMOUNT, BCKeyConstants.ERROR_MONTHLY_SUM_REQUEST_NOT_EQUAL, pkeyVal, monthSum.toString(), element.getAccountLineAnnualBalanceAmount().toString());
                            }
                        }
                    }
                }

                // check salary setting detail sum if expenditure line is a ss detail line
                // and salary setting option is turned on
                if (!SpringContext.getBean(SalarySettingService.class).isSalarySettingDisabled()){
                    if (element.getLaborObject() != null) {
                        if (element.getLaborObject().isDetailPositionRequiredIndicator()) {
                            
                            // sum the detail lines and compare against the accounting line request amount
                            KualiInteger salarySum = KualiInteger.ZERO;
                            
                            // if salary setting detail exists, sum it otherwise default to zero
                            if (element.isPendingBudgetConstructionAppointmentFundingExists()) {

                                // run reportquery to get the salary request sum
                                salarySum = SpringContext.getBean(BudgetDocumentService.class).getPendingBudgetConstructionAppointmentFundingRequestSum(element);

                            }

                            if (!salarySum.equals(element.getAccountLineAnnualBalanceAmount())){
                                isValid &= false;
                                String pkeyVal = element.getFinancialObjectCode() + "," + element.getFinancialSubObjectCode();
                                GlobalVariables.getErrorMap().putError(KFSPropertyConstants.ACCOUNT_LINE_ANNUAL_BALANCE_AMOUNT, BCKeyConstants.ERROR_SALARY_SUM_REQUEST_NOT_EQUAL, pkeyVal, salarySum.toString(), element.getAccountLineAnnualBalanceAmount().toString());
                            }
                        }
                    }
                }
                // if benefits calculation is turned on, check if the line is benefits related and call for calculation after save 
                if (!SpringContext.getBean(BenefitsCalculationService.class).isBenefitsCalculationDisabled()){
                    // retest for added errors since testing this line started - if none, test if benefits calc required
                    currentErrorCount = errors.getErrorCount();
                    isReqAmountValid = (currentErrorCount == originalErrorCount);

                    if (isReqAmountValid && element.getPositionObjectBenefit() != null && !element.getPositionObjectBenefit().isEmpty()){
                        budgetConstructionDocument.setBenefitsCalcNeeded(true);
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
    private boolean checkPendingBudgetConstructionGeneralLedgerLine(BudgetConstructionDocument budgetConstructionDocument, PendingBudgetConstructionGeneralLedger pendingBudgetConstructionGeneralLedger, ErrorMap errors, boolean isRevenue, boolean isAdd) {
        LOG.debug("checkPendingBudgetConstructionGeneralLedgerLine() start");

        boolean isValid = true;

        // now make sure all the necessary business objects are fully populated
        // this refreshes any refs not done by populate for display purposes auto-update="none"
        // TODO should this just refresh the refs we need?
        pendingBudgetConstructionGeneralLedger.refreshNonUpdateableReferences();

        isValid &= validatePBGLLine(pendingBudgetConstructionGeneralLedger, isAdd);
        if (isValid) {

            // all lines must have objects defined with financialBudgetAggregation = 'O';
            isValid &= isBudgetAggregationAllowed(budgetAggregationCodesParamValues, pendingBudgetConstructionGeneralLedger, errors, isAdd);

            // TODO add other generic restriction checks
            isValid &= this.isBudgetAllowed(budgetConstructionDocument, errors, isAdd);

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

    private boolean validatePBGLLine(PendingBudgetConstructionGeneralLedger pendingBudgetConstructionGeneralLedger, boolean isAdd) {
        if (pendingBudgetConstructionGeneralLedger == null) {
            throw new IllegalStateException(getKualiConfigurationService().getPropertyString(KFSKeyConstants.ERROR_DOCUMENT_NULL_ACCOUNTING_LINE));
        }
        // TODO needed for hack code only
        // ErrorMap errors = GlobalVariables.getErrorMap();

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

        // TODO figure out which way to go to handle better error message
        // // this code is a hack to add the bad value to the error message
        // if (isAdd){
        // if (!accountingLineRuleHelper.isValidObjectCode(objectCode, dd)){
        // valid = false;
        // errors.replaceError("financialObjectCode", "error.existence", "error.existence",
        // "Object:"+pendingBudgetConstructionGeneralLedger.getFinancialObjectCode());
        // errors.replaceError("financialObjectCode", "error.inactive", "error.inactive",
        // "Object:"+pendingBudgetConstructionGeneralLedger.getFinancialObjectCode());
        // }
        // } else {
        // if (!accountingLineRuleHelper.isValidObjectCode(objectCode, dd, TARGET_ERROR_PROPERTY_NAME)){
        // valid = false;
        // errors.replaceError(TARGET_ERROR_PROPERTY_NAME, "error.existence", "error.existence",
        // "Object:"+pendingBudgetConstructionGeneralLedger.getFinancialObjectCode());
        // errors.replaceError(TARGET_ERROR_PROPERTY_NAME, "error.inactive", "error.inactive",
        // "Object:"+pendingBudgetConstructionGeneralLedger.getFinancialObjectCode());
        // }
        // }

        // sub object is not required
        if (StringUtils.isNotBlank(pendingBudgetConstructionGeneralLedger.getFinancialSubObjectCode()) && !pendingBudgetConstructionGeneralLedger.getFinancialSubObjectCode().equalsIgnoreCase(KFSConstants.getDashFinancialSubObjectCode())) {
            SubObjCd subObjectCode = pendingBudgetConstructionGeneralLedger.getFinancialSubObject();

            // this code calls a local version (not AccountingLineRuleHelper) of isValidSubObjectCode to add the bad value to the
            // error message
            if (isAdd) {
                valid &= isValidSubObjectCode(subObjectCode, pendingBudgetConstructionGeneralLedger.getFinancialSubObjectCode(), dd, KFSConstants.FINANCIAL_SUB_OBJECT_CODE_PROPERTY_NAME);
            }
            else {
                valid &= isValidSubObjectCode(subObjectCode, pendingBudgetConstructionGeneralLedger.getFinancialSubObjectCode(), dd, TARGET_ERROR_PROPERTY_NAME);
            }

            // TODO figure out which way to go to handle better error message
            // // this code is a hack to add the bad value to the error message
            // if (!accountingLineRuleHelper.isValidSubObjectCode(subObjectCode, dd)){
            // valid = false;
            // if (isAdd){
            // errors.replaceError("financialSubObjectCode", "error.existence", "error.existence",
            // "SubObject:"+pendingBudgetConstructionGeneralLedger.getFinancialSubObjectCode());
            // errors.replaceError("financialSubObjectCode", "error.inactive", "error.inactive",
            // "SubObject:"+pendingBudgetConstructionGeneralLedger.getFinancialSubObjectCode());
            // } else {
            // if (errors.fieldHasMessage(errors.getKeyPath((String) "financialSubObjectCode", true), "error.existence")){
            // errors.remove(errors.getKeyPath((String) "financialSubObjectCode", true));
            // errors.putError(TARGET_ERROR_PROPERTY_NAME, "error.existence",
            // "SubObject:"+pendingBudgetConstructionGeneralLedger.getFinancialSubObjectCode());
            //                        
            // }
            // if (errors.fieldHasMessage(errors.getKeyPath((String) "financialSubObjectCode", true), "error.inactive")){
            // errors.remove(errors.getKeyPath((String) "financialSubObjectCode", true));
            // errors.putError(TARGET_ERROR_PROPERTY_NAME, "error.inactive",
            // "SubObject:"+pendingBudgetConstructionGeneralLedger.getFinancialSubObjectCode());
            // }
            // }
            // }
        }

        return valid;
    }

    /**
     * Validates a single primitive in a BO TODO May want to add this to DictionaryValidationService and change signature to pass in
     * a subset list of attributeNames to validate in the BO. Also, may not need this if we can somehow get/use a
     * BudgetConstructionDocument on expansion screens
     * 
     * @param object
     * @param attributeName
     * @param errorPrefix
     * @param validateRequired
     */
    private void validatePrimitiveFromDescriptor(Object object, String attributeName, String errorPrefix, boolean validateRequired) {

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
     * Validates a primitive in a BO TODO this is lifted from DictionaryValidationService since it is private. If the
     * validatePrimitiveFromDescriptor using an attribute name is added to DictionaryValidationService, we won't need this Also, may
     * not need this if we can somehow get/use a BudgetConstructionDocument on expansion screens
     * 
     * @param entryName
     * @param object
     * @param propertyDescriptor
     * @param errorPrefix
     * @param validateRequired
     */
    private void validatePrimitiveFromDescriptor(String entryName, Object object, PropertyDescriptor propertyDescriptor, String errorPrefix, boolean validateRequired) {

        // validate the primitive attributes if defined in the dictionary
        if (null != propertyDescriptor && dataDictionaryService.isAttributeDefined(entryName, propertyDescriptor.getName())) {
            Object value = ObjectUtils.getPropertyValue(object, propertyDescriptor.getName());
            Class propertyType = propertyDescriptor.getPropertyType();

            if (TypeUtils.isStringClass(propertyType) || TypeUtils.isIntegralClass(propertyType) || TypeUtils.isDecimalClass(propertyType) || TypeUtils.isTemporalClass(propertyType)) {

                // check value format against dictionary
                if (value != null && StringUtils.isNotBlank(value.toString())) {
                    if (!TypeUtils.isTemporalClass(propertyType)) {
                        getDictionaryValidationService().validateAttributeFormat(entryName, propertyDescriptor.getName(), value.toString(), errorPrefix + propertyDescriptor.getName());
                    }
                }
                else if (validateRequired) {
                    getDictionaryValidationService().validateAttributeRequired(entryName, propertyDescriptor.getName(), value, Boolean.FALSE, errorPrefix + propertyDescriptor.getName());
                }
            }
        }
    }

    private boolean isObjectTypeAllowed(List paramValues, PendingBudgetConstructionGeneralLedger accountingLine, ErrorMap errors, boolean isRevenue, boolean isAdd) {
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

    private boolean isBudgetAggregationAllowed(List paramValues, PendingBudgetConstructionGeneralLedger accountingLine, ErrorMap errors, boolean isAdd) {
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

    private boolean isNewLineUnique(BudgetConstructionDocument budgetConstructionDocument, PendingBudgetConstructionGeneralLedger newLine, ErrorMap errors, boolean isRevenue) {
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

    private boolean isNonWagesAccountNotLaborObject(BudgetConstructionDocument budgetConstructionDocument, PendingBudgetConstructionGeneralLedger accountingLine, ErrorMap errors, boolean isAdd) {
        boolean isAllowed = true;

        if (budgetConstructionDocument.getAccount().getSubFundGroup() == null || !budgetConstructionDocument.getAccount().getSubFundGroup().isSubFundGroupWagesIndicator()) {
            if (accountingLine.getLaborObject() != null) {
                isAllowed = false;
                this.putError(errors, KFSPropertyConstants.FINANCIAL_OBJECT_CODE, BCKeyConstants.ERROR_LABOR_OBJECT_IN_NOWAGES_ACCOUNT, isAdd, accountingLine.getFinancialObjectCode());
            }
        }
        return isAllowed;
    }

    private boolean isNotFringeBenefitObject(List paramValues, PendingBudgetConstructionGeneralLedger accountingLine, ErrorMap errors, boolean isAdd) {
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

    private boolean isNotSalarySettingOnly(List fundGroupParamValues, List subfundGroupParamValues, BudgetConstructionDocument budgetConstructionDocument, PendingBudgetConstructionGeneralLedger accountingLine, ErrorMap errors, boolean isRevenue, boolean isAdd) {
        boolean isAllowed = true;

        // check if account belongs to a fund or subfund that only allows salary setting lines
        AccountSalarySettingOnlyCause retVal = budgetParameterService.isSalarySettingOnlyAccount(budgetConstructionDocument);
        if (retVal != AccountSalarySettingOnlyCause.MISSING_PARAM){
            if (retVal != AccountSalarySettingOnlyCause.NONE){
                
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
            
        } else {
            // TODO need error message for this
            // missing system parameter
            isAllowed = false;
        }

        return isAllowed;
    }

    /**
     * runs rule checks that don't allow a budget
     * 
     * @param budgetConstructionDocument
     * @param errors
     * @param isRevenue
     * @param isAdd
     * @return
     */
    private boolean isBudgetAllowed(BudgetConstructionDocument budgetConstructionDocument, ErrorMap errors, boolean isAdd) {
        boolean isAllowed = true;
        SimpleDateFormat tdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");

        // is account closed?
        if (budgetConstructionDocument.getAccount().isAccountClosedIndicator()) {
            isAllowed = false;
            this.putError(errors, KFSPropertyConstants.FINANCIAL_OBJECT_CODE, KFSKeyConstants.ERROR_CLOSED, isAdd, "account: " + budgetConstructionDocument.getAccountNumber());
        }

        // is account expiration no budget allowed, currently < 1/1/(byfy-2)?
        Calendar expDate = BudgetConstructionRuleUtil.getNoBudgetAllowedExpireDate(budgetConstructionDocument.getUniversityFiscalYear());
        if (budgetConstructionDocument.getAccount().isExpired(expDate)) {
            isAllowed = false;
            this.putError(errors, KFSPropertyConstants.FINANCIAL_OBJECT_CODE, BCKeyConstants.ERROR_NO_BUDGET_ALLOWED, isAdd, budgetConstructionDocument.getAccountNumber(), tdf.format(budgetConstructionDocument.getAccount().getAccountExpirationDate()));

        }

        // is account a cash control account
        if (budgetConstructionDocument.getAccount().getBudgetRecordingLevelCode().equalsIgnoreCase(BCConstants.BUDGET_RECORDING_LEVEL_N)) {
            isAllowed = false;
            this.putError(errors, KFSPropertyConstants.FINANCIAL_OBJECT_CODE, BCKeyConstants.ERROR_BUDGET_RECORDING_LEVEL_NOT_ALLOWED, isAdd, budgetConstructionDocument.getAccountNumber(), BCConstants.BUDGET_RECORDING_LEVEL_N);
        }

        // grab the service instance that will be needed by all the validate methods
        DataDictionary dd = dataDictionaryService.getDataDictionary();

        if (StringUtils.isNotBlank(budgetConstructionDocument.getSubAccountNumber()) && !budgetConstructionDocument.getSubAccountNumber().equalsIgnoreCase(KFSConstants.getDashSubAccountNumber())) {
            SubAccount subAccount = budgetConstructionDocument.getSubAccount();

            // is subacct inactive or not exist?
            // this code calls a local version (not AccountingLineRuleHelper) of isValidSubAccount to add the bad value to the error
            // message
            if (isAdd) {
                isAllowed &= this.isValidSubAccount(subAccount, budgetConstructionDocument.getSubAccountNumber(), dd, KFSConstants.FINANCIAL_SUB_OBJECT_CODE_PROPERTY_NAME);
            }
            else {
                isAllowed &= this.isValidSubAccount(subAccount, budgetConstructionDocument.getSubAccountNumber(), dd, TARGET_ERROR_PROPERTY_NAME);
            }

            // is subacct type cost share?
            // TODO this expects a a21_sub_account, kuldev doesn't have one to one instances
            budgetConstructionDocument.getSubAccount().refreshReferenceObject(KFSPropertyConstants.A21_SUB_ACCOUNT);
            if (budgetConstructionDocument.getSubAccount().getA21SubAccount() != null) {
                if (budgetConstructionDocument.getSubAccount().getA21SubAccount().getSubAccountTypeCode().equalsIgnoreCase(BCConstants.SUB_ACCOUNT_TYPE_COST_SHARE)) {
                    isAllowed = false;
                    this.putError(errors, KFSPropertyConstants.FINANCIAL_OBJECT_CODE, BCKeyConstants.ERROR_SUB_ACCOUNT_TYPE_NOT_ALLOWED, isAdd, budgetConstructionDocument.getAccountNumber(), BCConstants.SUB_ACCOUNT_TYPE_COST_SHARE);
                }
            }
        }

        return isAllowed;
    }

    public boolean isValidSubAccount(SubAccount subAccount, String value, DataDictionary dataDictionary, String errorPropertyName) {
        String label = dataDictionary.getBusinessObjectEntry(SubAccount.class.getName()).getAttributeDefinition(KFSConstants.SUB_ACCOUNT_NUMBER_PROPERTY_NAME).getShortLabel();

        // make sure it exists
        if (ObjectUtils.isNull(subAccount)) {
            GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, label + ":" + value);
            return false;
        }

        // check to make sure it is active
        if (!subAccount.isSubAccountActiveIndicator()) {
            GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_DOCUMENT_SUB_ACCOUNT_INACTIVE, label + ":" + value);
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
    public boolean isValidSubObjectCode(SubObjCd subObjectCode, String value, DataDictionary dataDictionary, String errorPropertyName) {
        String label = dataDictionary.getBusinessObjectEntry(SubObjCd.class.getName()).getAttributeDefinition(KFSConstants.FINANCIAL_SUB_OBJECT_CODE_PROPERTY_NAME).getShortLabel();

        // make sure it exists
        if (ObjectUtils.isNull(subObjectCode)) {
            GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, label + ":" + value);
            return false;
        }

        // check active flag
        if (!subObjectCode.isFinancialSubObjectActiveIndicator()) {
            GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_INACTIVE, label + ":" + value);
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
            GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, label + ":" + value);
            return false;
        }

        // check active status
        if (!objectCode.isFinancialObjectActiveCode()) {
            GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_INACTIVE, label + ":" + value);
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
    private void putError(ErrorMap errors, String propertyName, String errorKey, boolean isAdd, String... errorParameters) {

        if (isAdd) {
            errors.putError(propertyName, errorKey, errorParameters);
        }
        else {
            errors.putError(TARGET_ERROR_PROPERTY_NAME, errorKey, errorParameters);
        }

    }
}
