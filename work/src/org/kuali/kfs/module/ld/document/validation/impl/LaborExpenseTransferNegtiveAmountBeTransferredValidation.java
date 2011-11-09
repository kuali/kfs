/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.ld.document.validation.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.module.ld.LaborPropertyConstants;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferSourceAccountingLine;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.module.ld.document.LaborExpenseTransferDocumentBase;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Determines whether a negtive amount can be transferred from one account to another
 * 
 * @param document the given document
 * @return true Determines whether a negtive amount can be transferred from one account to another
 */
public class LaborExpenseTransferNegtiveAmountBeTransferredValidation extends GenericValidation {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborExpenseTransferNegtiveAmountBeTransferredValidation.class);
    
    private Document documentForValidation;
        
    /**
     * Validates before the document routes 
     * @see org.kuali.kfs.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean result = true;
               
        Document documentForValidation = getDocumentForValidation();
        
        LaborExpenseTransferDocumentBase expenseTransferDocument = (LaborExpenseTransferDocumentBase) documentForValidation;
        
        List sourceLines = expenseTransferDocument.getSourceAccountingLines();

        // allow a negative amount to be moved from one account to another but do not allow a negative amount to be created when the
        // balance is positive
        Map<String, ExpenseTransferAccountingLine> accountingLineGroupMap = this.getAccountingLineGroupMap(sourceLines, ExpenseTransferSourceAccountingLine.class);
        if (result) {
            boolean canNegtiveAmountBeTransferred = canNegtiveAmountBeTransferred(accountingLineGroupMap);
            if (!canNegtiveAmountBeTransferred) {
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.SOURCE_ACCOUNTING_LINES, LaborKeyConstants.ERROR_CANNOT_TRANSFER_NEGATIVE_AMOUNT);
                result = false;
            }
        }
        
        return result;       
    }

    /**
     * Determines whether a negtive amount can be transferred from one account to another
     * 
     * @param accountingLineGroupMap the givenaccountingLineGroupMap
     * @return true if a negtive amount can be transferred from one account to another; otherwise, false
     */
    protected boolean canNegtiveAmountBeTransferred(Map<String, ExpenseTransferAccountingLine> accountingLineGroupMap) {
        for (String key : accountingLineGroupMap.keySet()) {
            ExpenseTransferAccountingLine accountingLine = accountingLineGroupMap.get(key);
            Map<String, Object> fieldValues = this.buildFieldValueMap(accountingLine);

            KualiDecimal balanceAmount = getBalanceAmount(fieldValues, accountingLine.getPayrollEndDateFiscalPeriodCode());
            KualiDecimal transferAmount = accountingLine.getAmount();

            // a negtive amount cannot be transferred if the balance amount is positive
            if (transferAmount.isNegative() && balanceAmount.isPositive()) {
                return false;
            }
        }
        return true;
    }

    /**
     * build the field-value maps throught the given accouting line
     * 
     * @param accountingLine the given accounting line
     * @return the field-value maps built from the given accouting line
     */
    protected Map<String, Object> buildFieldValueMap(ExpenseTransferAccountingLine accountingLine) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();

        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, accountingLine.getPostingYear());
        fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, accountingLine.getChartOfAccountsCode());
        fieldValues.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountingLine.getAccountNumber());

        String subAccountNumber = accountingLine.getSubAccountNumber();
        subAccountNumber = StringUtils.isBlank(subAccountNumber) ? KFSConstants.getDashSubAccountNumber() : subAccountNumber;
        fieldValues.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, subAccountNumber);

        fieldValues.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, accountingLine.getBalanceTypeCode());
        fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, accountingLine.getFinancialObjectCode());

        SystemOptions options = SpringContext.getBean(OptionsService.class).getOptions(accountingLine.getPostingYear());
        fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, options.getFinObjTypeExpenditureexpCd());

        String subObjectCode = accountingLine.getFinancialSubObjectCode();
        subObjectCode = StringUtils.isBlank(subObjectCode) ? KFSConstants.getDashFinancialSubObjectCode() : subObjectCode;
        fieldValues.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, subObjectCode);

        fieldValues.put(KFSPropertyConstants.EMPLID, accountingLine.getEmplid());
        fieldValues.put(KFSPropertyConstants.POSITION_NUMBER, accountingLine.getPositionNumber());

        return fieldValues;
    }
    
    /**
     * Groups the accounting lines by the specified key fields
     * 
     * @param accountingLines the given accounting lines that are stored in a list
     * @param clazz the class type of given accounting lines
     * @return the accounting line groups
     */
    protected Map<String, ExpenseTransferAccountingLine> getAccountingLineGroupMap(List<ExpenseTransferAccountingLine> accountingLines, Class clazz) {
        Map<String, ExpenseTransferAccountingLine> accountingLineGroupMap = new HashMap<String, ExpenseTransferAccountingLine>();

        for (ExpenseTransferAccountingLine accountingLine : accountingLines) {
            String stringKey = ObjectUtil.buildPropertyMap(accountingLine, defaultKeyOfExpenseTransferAccountingLine()).toString();
            ExpenseTransferAccountingLine line = null;

            if (accountingLineGroupMap.containsKey(stringKey)) {
                line = accountingLineGroupMap.get(stringKey);
                KualiDecimal amount = line.getAmount();
                line.setAmount(amount.add(accountingLine.getAmount()));
            }
            else {
                try {
                    line = (ExpenseTransferAccountingLine) clazz.newInstance();
                    ObjectUtil.buildObject(line, accountingLine);
                    accountingLineGroupMap.put(stringKey, line);
                }
                catch (Exception e) {
                    LOG.error("Cannot create a new instance of ExpenseTransferAccountingLine" + e);
                }
            }
        }
        return accountingLineGroupMap;
    }
    
    /**
     * Gets the default key of ExpenseTransferAccountingLine
     * 
     * @return the default key of ExpenseTransferAccountingLine
     */
    protected List<String> defaultKeyOfExpenseTransferAccountingLine() {
        List<String> defaultKey = new ArrayList<String>();

        defaultKey.add(KFSPropertyConstants.POSTING_YEAR);
        defaultKey.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        defaultKey.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        defaultKey.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);

        defaultKey.add(KFSPropertyConstants.BALANCE_TYPE_CODE);
        defaultKey.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        defaultKey.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);

        defaultKey.add(KFSPropertyConstants.EMPLID);
        defaultKey.add(KFSPropertyConstants.POSITION_NUMBER);

        defaultKey.add(LaborPropertyConstants.PAYROLL_END_DATE_FISCAL_YEAR);
        defaultKey.add(LaborPropertyConstants.PAYROLL_END_DATE_FISCAL_PERIOD_CODE);

        return defaultKey;
    }
    
    /**
     * get the amount for a given period from a ledger balance that has the given values for specified fileds
     * 
     * @param fieldValues the given fields and their values
     * @param periodCode the given period
     * @return the amount for a given period from the qualified ledger balance
     */
    protected KualiDecimal getBalanceAmount(Map<String, Object> fieldValues, String periodCode) {
        if (periodCode == null) {
            return KualiDecimal.ZERO;
        }

        fieldValues.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, KFSConstants.BALANCE_TYPE_ACTUAL);
        KualiDecimal actualBalanceAmount = this.getBalanceAmountOfGivenPeriod(fieldValues, periodCode);

        fieldValues.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, KFSConstants.BALANCE_TYPE_A21);
        KualiDecimal effortBalanceAmount = this.getBalanceAmountOfGivenPeriod(fieldValues, periodCode);

        return actualBalanceAmount.add(effortBalanceAmount);
    }

    /**
     * Gets the balance amount of a given period
     * 
     * @param fieldValues
     * @param periodCode
     * @return
     */
    protected KualiDecimal getBalanceAmountOfGivenPeriod(Map<String, Object> fieldValues, String periodCode) {
        KualiDecimal balanceAmount = KualiDecimal.ZERO;
        List<LedgerBalance> ledgerBalances = (List<LedgerBalance>) SpringContext.getBean(BusinessObjectService.class).findMatching(LedgerBalance.class, fieldValues);
        if (!ledgerBalances.isEmpty()) {
            balanceAmount = ledgerBalances.get(0).getAmount(periodCode);
        }
        return balanceAmount;
    }

    /**
     * Gets the documentForValidation attribute. 
     * @return Returns the documentForValidation.
     */
    public Document getDocumentForValidation() {
        return documentForValidation;
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     * @param documentForValidation The documentForValidation to set.
     */
    public void setDocumentForValidation(Document documentForValidation) {
        this.documentForValidation = documentForValidation;
    }    
}
