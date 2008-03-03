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

import static org.kuali.kfs.rules.AccountingDocumentRuleBaseConstants.APPLICATION_PARAMETER.RESTRICTED_OBJECT_TYPE_CODES;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.core.datadictionary.DataDictionary;
import org.kuali.core.exceptions.InfrastructureException;
import org.kuali.core.rules.TransactionalDocumentRuleBase;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DictionaryValidationService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiInteger;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypeUtils;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.AccountingLineRuleHelperService;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.module.budget.BCKeyConstants;
import org.kuali.module.budget.BCParameterConstants;
import org.kuali.module.budget.bo.PendingBudgetConstructionGeneralLedger;
import org.kuali.module.budget.document.BudgetConstructionDocument;
import org.kuali.module.budget.rule.AddPendingBudgetGeneralLedgerLineRule;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.purap.document.CreditMemoDocument;

public class BudgetConstructionRules extends TransactionalDocumentRuleBase implements AddPendingBudgetGeneralLedgerLineRule<BudgetConstructionDocument, PendingBudgetConstructionGeneralLedger> {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionRules.class);

    /**
     * Checks a new PBGL line. Comprehensive checks are done.
     * 
     * TODO consider using private static vars to hold the various service handles used in this. Set the vars
     * in the constructor, ala SpringContext.getBean() 
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
        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(pendingBudgetConstructionGeneralLedger);

        // check to see if any errors were reported
        int currentErrorCount = errors.getErrorCount();
        isValid &= (currentErrorCount == originalErrorCount);

        if (isValid){

            // Perform the generic checks - checks 
            // of each attribute in addition to existence
            isValid &= validatePBGLLine(pendingBudgetConstructionGeneralLedger);
            
            if (isValid){

                // check valid expenditure-revenue object type
//                isValid &= isObjectTypeAllowed(budgetConstructionDocument.getClass(), pendingBudgetConstructionGeneralLedger, isRevenue);
                
                ParameterService parameterService =  SpringContext.getBean(ParameterService.class);
                
                //TODO add other object code restriction checks

                // fields used to check for unique line below
                List<String> comparableFields = new ArrayList<String>();
                comparableFields.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
                comparableFields.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);

                // expenditure-revenue specific checks
                // line can't already exist in the target list
                if (isRevenue) {


                    // TODO add other revenue specific checks

                    // line must use matching expenditure-revenue object type
                    if (parameterService.parameterExists(budgetConstructionDocument.getClass(),BCParameterConstants.REVENUE_OBJECT_TYPES)){
                        List paramValues = parameterService.getParameterValues(budgetConstructionDocument.getClass(),BCParameterConstants.REVENUE_OBJECT_TYPES); 
                        if (!isObjectTypeAllowed(pendingBudgetConstructionGeneralLedger, paramValues)){
                            isValid &= false;
                            errors.putError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, KFSKeyConstants.ERROR_DOCUMENT_EXPENSE_ON_INCOME_SIDE, pendingBudgetConstructionGeneralLedger.getFinancialObjectCode());
                        }
                    } else {
                        isValid &= false;
                        LOG.info("Can't find system parameter "+BCParameterConstants.REVENUE_OBJECT_TYPES);
                    }

                    if (BudgetConstructionRuleUtil.hasExistingPBGLLine(budgetConstructionDocument.getPendingBudgetConstructionGeneralLedgerRevenueLines(), pendingBudgetConstructionGeneralLedger, comparableFields)){
                        isValid &= false;
                        errors.putError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, BCKeyConstants.ERROR_BUDGET_LINE_EXISTS, pendingBudgetConstructionGeneralLedger.getFinancialObjectCode()+","+pendingBudgetConstructionGeneralLedger.getFinancialSubObjectCode());
                    }
                }
                else {

                    // TODO add other expenditure specific checks
                    
                    // line must use matching expenditure-revenue object type
                    if (parameterService.parameterExists(budgetConstructionDocument.getClass(),BCParameterConstants.EXPENDITURE_OBJECT_TYPES)){
                        List paramValues = parameterService.getParameterValues(budgetConstructionDocument.getClass(),BCParameterConstants.EXPENDITURE_OBJECT_TYPES); 
                        if (!isObjectTypeAllowed(pendingBudgetConstructionGeneralLedger, paramValues)){
                            isValid &= false;
                            errors.putError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, KFSKeyConstants.ERROR_DOCUMENT_INCOME_ON_EXPENSE_SIDE, pendingBudgetConstructionGeneralLedger.getFinancialObjectCode());
                        }
                    } else {
                        isValid &= false;
                        LOG.info("Can't find system parameter "+BCParameterConstants.EXPENDITURE_OBJECT_TYPES);
                    }

                    // does line already exist in target expenditure list
                    if (BudgetConstructionRuleUtil.hasExistingPBGLLine(budgetConstructionDocument.getPendingBudgetConstructionGeneralLedgerExpenditureLines(), pendingBudgetConstructionGeneralLedger, comparableFields)){
                        isValid &= false;
                        errors.putError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, BCKeyConstants.ERROR_BUDGET_LINE_EXISTS, pendingBudgetConstructionGeneralLedger.getFinancialObjectCode()+","+pendingBudgetConstructionGeneralLedger.getFinancialSubObjectCode());
                    }
                }
            }
        }

        if (!isValid) {
            LOG.info("business rule checks failed in processAddPendingBudgetGeneralLedgerLineRules in BudgetConstructionRules");
        }

        LOG.debug("processAddPendingBudgetGeneralLedgerLineRules() end");
        return isValid;
    }
    
    /**
     * Checks an existing PBGL line. Only checks the request amount's required and formatting validity.
     * Then, If the request amount is non-zero, the line is also checked for valid object, sub-object and
     * if it jibes with any monthly amount and salary setting totals (if salary detail line). 
     * 
     * TODO still needs fleshed out
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

//      TODO use validatePrimitiveFromDescriptor to validate request amount only when saving
//      then call validatePBGLLine only when amount is non-zero for now - should the call happen when request value is changed?

        // validate required field checks in addition to format checks
        String attributeName = "accountLineAnnualBalanceAmount";
        validatePrimitiveFromDescriptor(pendingBudgetConstructionGeneralLedger, attributeName, "", true);
        
        // check to see if any errors were reported
        int currentErrorCount = errors.getErrorCount();
        isValid &= (currentErrorCount == originalErrorCount);
        
        if (isValid && pendingBudgetConstructionGeneralLedger.getAccountLineAnnualBalanceAmount().isNonZero()){

            isValid &= validatePBGLLine(pendingBudgetConstructionGeneralLedger);
            if (isValid){
                
                //TODO add other object restriction checks
                
                //TODO add checks against any monthly and salary detail sums
                
            }
            
        }
        
        if (!isValid) {
            LOG.info("business rule checks failed in checkPendingBudgetConstructionGeneralLedgerLine in BudgetConstructionRules");
        }


//            // Check the amount entered
//            valid &= isAmountValid(financialDocument, accountingLine);
//
//
//            if (valid) { // the following checks assume existence, so if the above method failed, we don't want to call these
//                Class documentClass = getAccountingLineDocumentClass(financialDocument);
//
//                // Check the object code to see if it's restricted or not
//                valid &= isObjectCodeAllowed(documentClass, accountingLine);
//
//                // Check the object code type allowances
//                valid &= isObjectTypeAllowed(documentClass, accountingLine);
//
//                // Check the object sub-type code allowances
//                valid &= isObjectSubTypeAllowed(documentClass, accountingLine);
//
//                // Check the object level allowances
//                valid &= isObjectLevelAllowed(documentClass, accountingLine);
//
//                // Check the object consolidation allowances
//                valid &= isObjectConsolidationAllowed(documentClass, accountingLine);
//
//                // Check the sub fund group allowances
//                valid &= isSubFundGroupAllowed(documentClass, accountingLine);
//
//                // Check the fund group allowances
//                valid &= isFundGroupAllowed(documentClass, accountingLine);
//            }
//        }
//
//        if (!valid) {
//            LOG.info("business rule checks failed in processAccountingLine in KualiRuleServiceImpl");
//        }

        LOG.debug("checkPendingBudgetConstructionGeneralLedgerLine() end");
        return isValid;
    }

    private boolean validatePBGLLine(PendingBudgetConstructionGeneralLedger pendingBudgetConstructionGeneralLedger) {
        if (pendingBudgetConstructionGeneralLedger == null) {
            throw new IllegalStateException(SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSKeyConstants.ERROR_DOCUMENT_NULL_ACCOUNTING_LINE));
        }

        // use some of the validators here
        AccountingLineRuleHelperService accountingLineRuleHelper = SpringContext.getBean(AccountingLineRuleHelperService.class);
    
        // grab the two service instances that will be needed by all the validate methods
        DataDictionary dd = SpringContext.getBean(DataDictionaryService.class).getDataDictionary();

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
     * Validates a single primitive in a BO
     * TODO May want to add this to DictionaryValidationService and
     * change signature to pass in a subset list of attributeNames to validate in the BO
     * 
     * @param object
     * @param attributeName
     * @param errorPrefix
     * @param validateRequired
     */
    private void validatePrimitiveFromDescriptor(Object object, String attributeName, String errorPrefix, boolean validateRequired){

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
     * TODO this is lifted from DictionaryValidationService since it is private. If the validatePrimitiveFromDescriptor
     * using an attribute name is added to DictionaryValidationService, we won't need this
     * 
     * @param entryName
     * @param object
     * @param propertyDescriptor
     * @param errorPrefix
     * @param validateRequired
     */
    private void validatePrimitiveFromDescriptor(String entryName, Object object, PropertyDescriptor propertyDescriptor, String errorPrefix, boolean validateRequired) {
        // validate the primitive attributes if defined in the dictionary
        if (null != propertyDescriptor && SpringContext.getBean(DataDictionaryService.class).isAttributeDefined(entryName, propertyDescriptor.getName())) {
            Object value = ObjectUtils.getPropertyValue(object, propertyDescriptor.getName());
            Class propertyType = propertyDescriptor.getPropertyType();

            if (TypeUtils.isStringClass(propertyType) || TypeUtils.isIntegralClass(propertyType) || TypeUtils.isDecimalClass(propertyType) || TypeUtils.isTemporalClass(propertyType)) {

                // check value format against dictionary
                if (value != null && StringUtils.isNotBlank(value.toString())) {
                    if (!TypeUtils.isTemporalClass(propertyType)) {
                        SpringContext.getBean(DictionaryValidationService.class).validateAttributeFormat(entryName, propertyDescriptor.getName(), value.toString(), errorPrefix + propertyDescriptor.getName());
                    }
                }
                else if (validateRequired) {
                    SpringContext.getBean(DictionaryValidationService.class).validateAttributeRequired(entryName, propertyDescriptor.getName(), value, Boolean.FALSE, errorPrefix + propertyDescriptor.getName());
                }
            }
        }
    }

    private boolean isAccountingLineValueAllowed(Class documentClass, PendingBudgetConstructionGeneralLedger accountingLine, String parameterName, String propertyName, String userEnteredPropertyName) {
        boolean isAllowed = true;
        String exceptionMessage = "Invalid property name provided to BudgetConstructionRules isAccountingLineValueAllowed method: " + propertyName;
        try {
            String propertyValue = (String) PropertyUtils.getProperty(accountingLine, propertyName);
// TODO is this needed?
//            if (SpringContext.getBean(ParameterService.class).parameterExists(ParameterConstants.FINANCIAL_PROCESSING_DOCUMENT.class, parameterName)) {
//                isAllowed = SpringContext.getBean(ParameterService.class).getParameterEvaluator(ParameterConstants.FINANCIAL_PROCESSING_DOCUMENT.class, parameterName, propertyValue).evaluateAndAddError(SourceAccountingLine.class, propertyName, userEnteredPropertyName);
//            }
            if (SpringContext.getBean(ParameterService.class).parameterExists(documentClass, parameterName)) {
                isAllowed = SpringContext.getBean(ParameterService.class).getParameterEvaluator(documentClass, parameterName, propertyValue).evaluateAndAddError(PendingBudgetConstructionGeneralLedger.class, propertyName, userEnteredPropertyName);
            }
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(exceptionMessage, e);
        }
        catch (InvocationTargetException e) {
            throw new RuntimeException(exceptionMessage, e);
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException(exceptionMessage, e);
        }
        return isAllowed;
    }

    /**
     * This checks the accounting line's object type code to ensure that it is not a fund balance object type. This is a universal
     * business rule that all transaction processing documents should abide by.
     * 
     * @param accountingLine
     * @return boolean
     */
    public boolean isObjectTypeAllowed(Class documentClass, PendingBudgetConstructionGeneralLedger accountingLine, boolean isRevenue) {
        boolean isAllowed = true;
        
//TODO add literals to constants classes
        if (isRevenue){
            isAllowed = isAccountingLineValueAllowed(documentClass, accountingLine, "REVENUE_OBJECT_TYPES", KFSPropertyConstants.FINANCIAL_OBJECT+"."+KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        } else {
            isAllowed = isAccountingLineValueAllowed(documentClass, accountingLine, "EXPENDITURE_OBJECT_TYPES", KFSPropertyConstants.FINANCIAL_OBJECT+"."+KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        }
        return isAllowed;
    }

    public boolean isObjectTypeAllowed(PendingBudgetConstructionGeneralLedger accountingLine, List paramValues) {
        boolean isAllowed = true;
        
        if (!paramValues.contains(accountingLine.getFinancialObject().getFinancialObjectTypeCode())){
            isAllowed = false;
        }

        return isAllowed;
    }
}
