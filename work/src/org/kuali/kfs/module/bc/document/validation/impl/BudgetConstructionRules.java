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
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.core.datadictionary.DataDictionary;
import org.kuali.core.exceptions.InfrastructureException;
import org.kuali.core.rules.TransactionalDocumentRuleBase;
import org.kuali.core.service.DataDictionaryService;
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
import org.kuali.module.budget.BCParameterConstants;
import org.kuali.module.budget.bo.PendingBudgetConstructionGeneralLedger;
import org.kuali.module.budget.document.BudgetConstructionDocument;
import org.kuali.module.budget.rule.AddPendingBudgetGeneralLedgerLineRule;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.SubObjCd;

public class BudgetConstructionRules extends TransactionalDocumentRuleBase implements AddPendingBudgetGeneralLedgerLineRule<BudgetConstructionDocument, PendingBudgetConstructionGeneralLedger> {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionRules.class);

    // some services used here - other service refs are from parent classes
    // if this class is extended we may need to create protected getters
    private static ParameterService parameterService = SpringContext.getBean(ParameterService.class);
    private static AccountingLineRuleHelperService accountingLineRuleHelper = SpringContext.getBean(AccountingLineRuleHelperService.class);
    private static DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);

    public BudgetConstructionRules() {
        super();
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

        ErrorMap errors = GlobalVariables.getErrorMap();
        boolean isValid = true;

        int originalErrorCount = errors.getErrorCount();

        // now make sure all the necessary business objects are fully populated
        // this refreshes any refs where auto-update="none" even though already done by populate for display purposes
        pendingBudgetConstructionGeneralLedger.refreshNonUpdateableReferences();

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

                Class docClass = budgetConstructionDocument.getClass();

                // all new lines must have objects defined with financialBudgetAggregation = 'O';
                isValid &= isBudgetAggregationAllowed(docClass, pendingBudgetConstructionGeneralLedger, errors);

                // line must use matching expenditure-revenue object type
                isValid &= isObjectTypeAllowed(budgetConstructionDocument.getClass(), pendingBudgetConstructionGeneralLedger, errors, isRevenue);

                // does line already exist in target revenue or expenditure list
                isValid &= isNewLineUnique(budgetConstructionDocument, pendingBudgetConstructionGeneralLedger, errors, isRevenue);

                // TODO add other restriction checks

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
     * request var to the PBGL BO and as a hidden in the JSP and test for differences as an indicatore it was touched this will
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

        // now make sure all the necessary business objects are fully populated
        // this refreshes any refs not done by populate for display purposes auto-update="none"
        pendingBudgetConstructionGeneralLedger.refreshNonUpdateableReferences();

        // TODO use validatePrimitiveFromDescriptor to validate request amount only when saving
        // then call validatePBGLLine only when amount is non-zero for now - should the call happen when request value is changed?

        // validate required field checks in addition to format checks
        String attributeName = "accountLineAnnualBalanceAmount";
        validatePrimitiveFromDescriptor(pendingBudgetConstructionGeneralLedger, attributeName, "", true);

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

        // grab the two service instances that will be needed by all the validate methods
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
     * a subset list of attributeNames to validate in the BO
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
     * validatePrimitiveFromDescriptor using an attribute name is added to DictionaryValidationService, we won't need this
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

    public boolean isObjectTypeAllowed(Class documentClass, PendingBudgetConstructionGeneralLedger accountingLine, ErrorMap errors, boolean isRevenue) {
        boolean isAllowed = true;
        String paramKey;
        String errorKey;

        if (isRevenue) {
            paramKey = BCParameterConstants.REVENUE_OBJECT_TYPES;
            errorKey = KFSKeyConstants.ERROR_DOCUMENT_EXPENSE_ON_INCOME_SIDE;
        }
        else {
            paramKey = BCParameterConstants.EXPENDITURE_OBJECT_TYPES;
            errorKey = KFSKeyConstants.ERROR_DOCUMENT_INCOME_ON_EXPENSE_SIDE;
        }

        if (parameterService.parameterExists(documentClass, paramKey)) {
            List paramValues = parameterService.getParameterValues(documentClass, paramKey);
            if (!paramValues.contains(accountingLine.getFinancialObject().getFinancialObjectTypeCode())) {
                isAllowed = false;
                errors.putError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, errorKey, accountingLine.getFinancialObjectCode());
            }
        }
        else {
            isAllowed = false;
            LOG.info("Can't find system parameter " + paramKey);
        }

        return isAllowed;
    }

    public boolean isBudgetAggregationAllowed(Class documentClass, PendingBudgetConstructionGeneralLedger accountingLine, ErrorMap errors) {
        boolean isAllowed = true;

        if (parameterService.parameterExists(documentClass, BCParameterConstants.BUDGET_AGGREGATION_CODES)) {
            List paramValues = parameterService.getParameterValues(documentClass, BCParameterConstants.BUDGET_AGGREGATION_CODES);
            if (!paramValues.contains(accountingLine.getFinancialObject().getFinancialBudgetAggregationCd())) {
                isAllowed = false;
                errors.putError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, KFSKeyConstants.ERROR_DOCUMENT_INCORRECT_OBJ_CODE_WITH_BUDGET_AGGREGATION, accountingLine.getFinancialObjectCode(), accountingLine.getFinancialObject().getFinancialBudgetAggregationCd());
            }
        }
        else {
            isAllowed = false;
            LOG.info("Can't find system parameter " + BCParameterConstants.BUDGET_AGGREGATION_CODES);
        }

        return isAllowed;
    }

    public boolean isNewLineUnique(BudgetConstructionDocument budgetConstructionDocument, PendingBudgetConstructionGeneralLedger newLine, ErrorMap errors, boolean isRevenue) {
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

}
