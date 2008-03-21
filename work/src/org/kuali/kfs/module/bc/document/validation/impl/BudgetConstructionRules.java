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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.core.datadictionary.DataDictionary;
import org.kuali.core.exceptions.InfrastructureException;
import org.kuali.core.rules.TransactionalDocumentRuleBase;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypeUtils;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.AccountingLineRuleHelperService;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.budget.BCKeyConstants;
import org.kuali.module.budget.BCParameterKeyConstants;
import org.kuali.module.budget.bo.PendingBudgetConstructionGeneralLedger;
import org.kuali.module.budget.document.BudgetConstructionDocument;
import org.kuali.module.budget.rule.AddPendingBudgetGeneralLedgerLineRule;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.SubObjCd;

public class BudgetConstructionRules extends TransactionalDocumentRuleBase implements AddPendingBudgetGeneralLedgerLineRule<BudgetConstructionDocument, PendingBudgetConstructionGeneralLedger> {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionRules.class);

    // some services used here - other service refs are from parent classes
    // if this class is extended we may need to create protected getters
    private static ParameterService parameterService = SpringContext.getBean(ParameterService.class);
    private static AccountingLineRuleHelperService accountingLineRuleHelper = SpringContext.getBean(AccountingLineRuleHelperService.class);
    private static DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);

    private List revenueObjectTypesParamValues;
    private List expenditureObjectTypesParamValues;
    private List budgetAggregationCodesParamValues;
    private List fringeBenefitDesignatorCodesParamValues;
    private List salarySettingFundGroupsParamValues;
    private List salarySettingSubFundGroupsParamValues;

    public BudgetConstructionRules() {
        super();

        // TODO may want to get all the system param values here once, instead of the process*Rules methods??
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

        List refreshFields;
        ErrorMap errors = GlobalVariables.getErrorMap();
        boolean isValid = true;

        int originalErrorCount = errors.getErrorCount();
        Class docClass = budgetConstructionDocument.getClass();

        // get the system parameters we will use here
        revenueObjectTypesParamValues = this.getParameterValues(docClass, BCParameterKeyConstants.REVENUE_OBJECT_TYPES);
        expenditureObjectTypesParamValues = this.getParameterValues(docClass, BCParameterKeyConstants.EXPENDITURE_OBJECT_TYPES);
        budgetAggregationCodesParamValues = this.getParameterValues(docClass, BCParameterKeyConstants.BUDGET_AGGREGATION_CODES);
        fringeBenefitDesignatorCodesParamValues = this.getParameterValues(docClass, BCParameterKeyConstants.FRINGE_BENEFIT_DESIGNATOR_CODES);
        salarySettingFundGroupsParamValues = this.getParameterValues(docClass, BCParameterKeyConstants.SALARY_SETTING_FUND_GROUPS);
        salarySettingSubFundGroupsParamValues = this.getParameterValues(docClass, BCParameterKeyConstants.SALARY_SETTING_SUB_FUND_GROUPS);

        // now make sure all the necessary business objects are fully populated
        // this refreshes any refs where auto-update="none" even though already done by populate for display purposes
        // TODO may want to explicitly refresh only the needed refs instead of shotgun?
        pendingBudgetConstructionGeneralLedger.refreshNonUpdateableReferences();

        // refresh only the doc refs we need
        refreshFields = Collections.unmodifiableList(Arrays.asList(new String[] { KFSPropertyConstants.ACCOUNT, KFSPropertyConstants.SUB_ACCOUNT }));
        SpringContext.getBean(PersistenceService.class).retrieveReferenceObjects(budgetConstructionDocument, refreshFields);

        // validate required field checks in addition to format checks
        getDictionaryValidationService().validateBusinessObject(pendingBudgetConstructionGeneralLedger);

        // check to see if any errors were reported
        int currentErrorCount = errors.getErrorCount();
        isValid &= (currentErrorCount == originalErrorCount);

        if (isValid) {

            // Perform the generic checks - checks
            // of each attribute in addition to existence
            isValid &= validatePBGLLine(pendingBudgetConstructionGeneralLedger);

            if (isValid) {

                // all new lines must have objects defined with financialBudgetAggregation = 'O';
                isValid &= isBudgetAggregationAllowed(budgetAggregationCodesParamValues, pendingBudgetConstructionGeneralLedger, errors);

                // TODO add other generic restriction checks

                // revenue specific checks
                if (isRevenue) {

                    // line must use matching revenue object type
                    isValid &= isObjectTypeAllowed(revenueObjectTypesParamValues, pendingBudgetConstructionGeneralLedger, errors, isRevenue);

                }
                else {
                    // expenditure specific checks

                    // line must use matching expenditure object type
                    isValid &= isObjectTypeAllowed(expenditureObjectTypesParamValues, pendingBudgetConstructionGeneralLedger, errors, isRevenue);

                    // no lines using labor objects in non-wage accounts
                    isValid &= isNonWagesAccountNotLaborObject(budgetConstructionDocument, pendingBudgetConstructionGeneralLedger, errors);

                    // only lines using detail labor objects allowed in fund group CG and sfund group SDCI
                    isValid &= isNotSalarySettingOnly(salarySettingFundGroupsParamValues, salarySettingSubFundGroupsParamValues, budgetConstructionDocument, pendingBudgetConstructionGeneralLedger, errors);

                    // no lines using fringe benefit target object codes allowed to be manually added by user
                    // the lines are created by benefits calculation process
                    isValid &= isNotFringeBenefitObject(fringeBenefitDesignatorCodesParamValues, pendingBudgetConstructionGeneralLedger, errors);
                }
            }

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
     * Checks an existing PBGL line. Only checks the request amount's required and formatting validity. Then, If the request amount
     * is non-zero, the line is also checked for valid object, sub-object and if it jibes with any monthly amount and salary setting
     * totals (if salary detail line). TODO still needs fleshed out and instead of using the non-zero request test add a previous
     * request var to the PBGL BO and as a hidden in the JSP and test for differences as an indicator it was touched. This will
     * probably be called from a method that does the iteration of revenue and expenditures
     * 
     * @param budgetConstructionDocument
     * @param pendingBudgetConstructionGeneralLedger
     * @return
     */
    private boolean checkPendingBudgetConstructionGeneralLedgerLine(BudgetConstructionDocument budgetConstructionDocument, PendingBudgetConstructionGeneralLedger pendingBudgetConstructionGeneralLedger) {
        LOG.debug("checkPendingBudgetConstructionGeneralLedgerLine() start");

        ErrorMap errors = GlobalVariables.getErrorMap();
        boolean isValid = true;

        int originalErrorCount = errors.getErrorCount();

        // TODO this will go in the calling class where iterating the lines
        Class docClass = budgetConstructionDocument.getClass();

        // get the system parameters we will use in the calling class where iterating the lines
        // TODO set any paramValue instance vars needed here

        // TODO add document level ref refreshes in the calling class where iterating the lines

        // now make sure all the necessary business objects are fully populated
        // this refreshes any refs not done by populate for display purposes auto-update="none"
        // TODO should this just refresh the refs we need?
        pendingBudgetConstructionGeneralLedger.refreshNonUpdateableReferences();

        // TODO use validatePrimitiveFromDescriptor to validate request amount only when saving
        // then call validatePBGLLine only when amount is non-zero for now - the call should happen when request value is changed
        // need to add an original request amount instance var to the BO and use it for testing if the line needs re-validated
        // probably init the original once in populate if null, then reinit on every successful rules evaluation.

        // validate required field checks in addition to format checks
        String attributeName = "pendingBudgetConstructionGeneralLedger.accountLineAnnualBalanceAmount";

        // TODO using validateDocumentAttribute instead of local validatePrimitiveFromDescriptor - remove when tested
        // validatePrimitiveFromDescriptor(pendingBudgetConstructionGeneralLedger, attributeName, "", true);
        getDictionaryValidationService().validateDocumentAttribute(budgetConstructionDocument, attributeName, "");

        // check to see if any errors were reported
        int currentErrorCount = errors.getErrorCount();
        isValid &= (currentErrorCount == originalErrorCount);

        if (isValid && pendingBudgetConstructionGeneralLedger.getAccountLineAnnualBalanceAmount().isNonZero()) {

            isValid &= validatePBGLLine(pendingBudgetConstructionGeneralLedger);
            if (isValid) {

                // TODO add other object restriction checks

                // TODO add checks against any monthly and salary detail sums

            }

        }

        if (!isValid) {
            LOG.info("business rule checks failed in checkPendingBudgetConstructionGeneralLedgerLine in BudgetConstructionRules");
        }

        LOG.debug("checkPendingBudgetConstructionGeneralLedgerLine() end");
        return isValid;
    }

    private boolean validatePBGLLine(PendingBudgetConstructionGeneralLedger pendingBudgetConstructionGeneralLedger) {
        if (pendingBudgetConstructionGeneralLedger == null) {
            throw new IllegalStateException(getKualiConfigurationService().getPropertyString(KFSKeyConstants.ERROR_DOCUMENT_NULL_ACCOUNTING_LINE));
        }

        // grab the service instance that will be needed by all the validate methods
        DataDictionary dd = dataDictionaryService.getDataDictionary();

        // retrieve each pbgl line object and validate
        boolean valid = true;

        // object code is required
        ObjectCode objectCode = pendingBudgetConstructionGeneralLedger.getFinancialObject();
        valid &= accountingLineRuleHelper.isValidObjectCode(objectCode, dd);

        // sub object is not required
        if (StringUtils.isNotBlank(pendingBudgetConstructionGeneralLedger.getFinancialSubObjectCode())) {
            SubObjCd subObjectCode = pendingBudgetConstructionGeneralLedger.getFinancialSubObject();
            valid &= accountingLineRuleHelper.isValidSubObjectCode(subObjectCode, dd);
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

    private boolean isObjectTypeAllowed(List paramValues, PendingBudgetConstructionGeneralLedger accountingLine, ErrorMap errors, boolean isRevenue) {
        boolean isAllowed = true;

        if (paramValues != null) {
            if (!paramValues.contains(accountingLine.getFinancialObject().getFinancialObjectTypeCode())) {
                isAllowed = false;
                if (isRevenue) {
                    errors.putError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, KFSKeyConstants.ERROR_DOCUMENT_EXPENSE_ON_INCOME_SIDE, accountingLine.getFinancialObjectCode());
                }
                else {
                    errors.putError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, KFSKeyConstants.ERROR_DOCUMENT_INCOME_ON_EXPENSE_SIDE, accountingLine.getFinancialObjectCode());
                }
            }
        }
        else {
            isAllowed = false;
        }

        return isAllowed;
    }

    private boolean isBudgetAggregationAllowed(List paramValues, PendingBudgetConstructionGeneralLedger accountingLine, ErrorMap errors) {
        boolean isAllowed = true;

        if (paramValues != null) {
            if (!paramValues.contains(accountingLine.getFinancialObject().getFinancialBudgetAggregationCd())) {
                isAllowed = false;
                errors.putError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, KFSKeyConstants.ERROR_DOCUMENT_INCORRECT_OBJ_CODE_WITH_BUDGET_AGGREGATION, accountingLine.getFinancialObjectCode(), accountingLine.getFinancialObject().getFinancialBudgetAggregationCd());
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

    private boolean isNonWagesAccountNotLaborObject(BudgetConstructionDocument budgetConstructionDocument, PendingBudgetConstructionGeneralLedger accountingLine, ErrorMap errors) {
        boolean isAllowed = true;

        if (!budgetConstructionDocument.getAccount().getSubFundGroup().isSubFundGroupWagesIndicator()) {
            if (accountingLine.getLaborObject() != null) {
                isAllowed = false;
                errors.putError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, BCKeyConstants.ERROR_LABOR_OBJECT_IN_NOWAGES_ACCOUNT, accountingLine.getFinancialObjectCode());
            }
        }
        return isAllowed;
    }

    private boolean isNotFringeBenefitObject(List paramValues, PendingBudgetConstructionGeneralLedger accountingLine, ErrorMap errors) {
        boolean isAllowed = true;

        if (paramValues != null) {
            if (accountingLine.getLaborObject() != null) {
                if (paramValues.contains(accountingLine.getLaborObject().getFinancialObjectFringeOrSalaryCode())) {
                    isAllowed = false;
                    errors.putError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, BCKeyConstants.ERROR_FRINGE_BENEFIT_OBJECT_NOT_ALLOWED, accountingLine.getFinancialObjectCode());
                }
            }
        }
        else {
            isAllowed = false;
        }

        return isAllowed;
    }

    private boolean isNotSalarySettingOnly(List fundGroupParamValues, List subfundGroupParamValues, BudgetConstructionDocument budgetConstructionDocument, PendingBudgetConstructionGeneralLedger accountingLine, ErrorMap errors) {
        boolean isAllowed = true;

        // check if account belongs to a fund or subfund that only allows salary setting lines
        if (fundGroupParamValues != null) {
            if (subfundGroupParamValues != null) {

                // is the account in a salary setting only fund group or sub-fund group
                String fundGroup = budgetConstructionDocument.getAccount().getSubFundGroup().getFundGroupCode();
                String subfundGroup = budgetConstructionDocument.getAccount().getSubFundGroup().getSubFundGroupCode();
                if (fundGroupParamValues.contains(fundGroup) || subfundGroupParamValues.contains(subfundGroup)) {

                    // the line must use an object that is a detail salary labor object
                    if (accountingLine.getLaborObject() == null || !accountingLine.getLaborObject().isDetailPositionRequiredIndicator()) {

                        isAllowed = false;
                        if (fundGroupParamValues.contains(fundGroup)) {
                            errors.putError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, BCKeyConstants.ERROR_SALARY_SETTING_OBJECT_ONLY, "fund " + fundGroup);

                        }
                        if (subfundGroupParamValues.contains(subfundGroup)) {
                            errors.putError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, BCKeyConstants.ERROR_SALARY_SETTING_OBJECT_ONLY, "subfund " + subfundGroup);
                        }
                    }
                }
            }
            else {
                isAllowed = false;
            }
        }
        else {
            isAllowed = false;
        }

        return isAllowed;
    }

    private List getParameterValues(Class componentClass, String parameterName) {
        List paramValues = null;

        if (parameterService.parameterExists(componentClass, parameterName)) {
            paramValues = parameterService.getParameterValues(componentClass, parameterName);
        }
        else {
            LOG.info("Can't find system parameter " + parameterName);
        }
        return paramValues;
    }
}
