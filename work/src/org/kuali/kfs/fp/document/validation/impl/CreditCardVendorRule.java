/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.fp.document.validation.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.coa.businessobject.SubObjectCodeCurrent;
import org.kuali.kfs.fp.businessobject.CreditCardVendor;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;

/**
 * This class represents business rules for the credit card vendor maintenance document
 */
public class CreditCardVendorRule extends MaintenanceDocumentRuleBase {

    protected CreditCardVendor newCreditCardVendor;

    /**
     *  Sets up a CreditCardVendor convenience objects to make sure all possible sub-objects are populated
     * 
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#setupConvenienceObjects()
     */
    public void setupConvenienceObjects() {

        newCreditCardVendor = (CreditCardVendor) super.getNewBo();
    }

    /**
     * Return true if rules for processing a save for the credit card maintenance document are are valid.
     * 
     * @param document maintenance document
     * @return true credit card vendor number is valid
     * 
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        // default to success
        boolean success = true;
        setupConvenienceObjects();

        // check valid Credit Card Vendor Number (numeric, minimum length)
        success &= checkCreditCardVendorNumber();

        return success;
    }

    /**
     * Returns value from processCustomRouteDocumentBusinessRules(document)
     * 
     * @param document maintenance document
     * @return value from processCustomRouteDocumentBusinessRules(document)
     * 
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {

        return processCustomRouteDocumentBusinessRules(document);
    }

    /**
     * Returns true credit card vendor maintenance document is routed successfully
     * 
     * @param document submitted credit card maintenance document
     * @return true if credit card vendor number, income/expense account numbers, income/expense sub-account numbers, and income/expense sub-object codes are valid
     * 
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        // default to success
        boolean success = true;

        setupConvenienceObjects();

        // check valid Credit Card Vendor Number (numeric, minimum length)
        success &= checkCreditCardVendorNumber();

        // check Income Account Number business rule
        if ( StringUtils.isNotBlank( newCreditCardVendor.getIncomeAccountNumber() ) ) {
            success &= checkExistingActiveAccount( newCreditCardVendor.getIncomeFinancialChartOfAccountsCode(), newCreditCardVendor.getIncomeAccountNumber(), "incomeAccountNumber", "Income Account Number");
        }

        // check Expense Account Number business rule
        if ( StringUtils.isNotBlank( newCreditCardVendor.getExpenseAccountNumber() ) ) {
            success &= checkExistingActiveAccount( newCreditCardVendor.getExpenseFinancialChartOfAccountsCode(), newCreditCardVendor.getExpenseAccountNumber(), "expenseAccountNumber", "Expense Account Number");
        }

        // check Income Sub-Account business rule
        if ( StringUtils.isNotBlank( newCreditCardVendor.getIncomeSubAccountNumber() ) ) {

            // check required fields to validate Sub-Account
            if (checkRequiredSubAccount("Income")) {
                SubAccount existenceSubAccount = checkExistenceSubAccount("Income");

                // check existence of Sub-Account
                if (existenceSubAccount == null) {
                    putFieldError("incomeSubAccountNumber", KFSKeyConstants.ERROR_CCV_INVALIDSUBACCOUNT, "Income Sub-Account Number, " + newCreditCardVendor.getIncomeSubAccountNumber());
                }
                else

                // check the Sub-Account is active
                if (!existenceSubAccount.isActive()) {
                    putFieldError("incomeSubAccountNumber", KFSKeyConstants.ERROR_INACTIVE, "Income Sub-Account");
                }
            }
        }

        // check Expense Sub-Account business rule
        if ( StringUtils.isNotBlank( newCreditCardVendor.getExpenseSubAccountNumber() ) ) {
            if (checkRequiredSubAccount("Expense")) {

                // check existence of Sub-Account
                SubAccount existenceSubAccount = checkExistenceSubAccount("Expense");
                if (existenceSubAccount == null) {
                    putFieldError("expenseSubAccountNumber", KFSKeyConstants.ERROR_CCV_INVALIDSUBACCOUNT, "Expense Sub-Account Number, " + newCreditCardVendor.getExpenseSubAccountNumber());

                }
                else

                // check the Sub-Account is active
                if (!existenceSubAccount.isActive()) {
                    putFieldError("expenseSubAccountNumber", KFSKeyConstants.ERROR_INACTIVE, "Expense Sub-Account");
                }
            }
        }

        // check Income Sub-Object Code business rule
        if ( StringUtils.isNotBlank( newCreditCardVendor.getIncomeFinancialSubObjectCode() ) ) {
            if (checkRequiredSubObjectCode("Income")) {

                // check existence of Sub-Object
                SubObjectCode existenceSubObj = checkExistenceSubObj("Income");
                if (existenceSubObj == null) {
                    putFieldError("incomeFinancialSubObjectCode", KFSKeyConstants.ERROR_CCV_INVALIDSUBOBJECT, "Income Sub-Object Code, " + newCreditCardVendor.getIncomeFinancialSubObjectCode());
                }
                else
                // check the Sub-Object is active
                if (!existenceSubObj.isActive()) {
                    putFieldError("incomeFinancialSubObjectCode", KFSKeyConstants.ERROR_INACTIVE, "Income Sub-Object");
                }

            }
        }

        // check Expense Sub-Object Code business rule
        if ( StringUtils.isNotBlank( newCreditCardVendor.getExpenseFinancialSubObjectCode() ) ) {
            if (checkRequiredSubObjectCode("Expense")) {

                // check existence of Sub-Object
                SubObjectCode existenceSubObj = checkExistenceSubObj("Expense");
                if (existenceSubObj == null) {
                    putFieldError("expenseFinancialSubObjectCode", KFSKeyConstants.ERROR_CCV_INVALIDSUBOBJECT, "Expense Sub-Object Code, " + newCreditCardVendor.getExpenseFinancialSubObjectCode());
                }
                else
                // check the Sub-Object is active
                if (!existenceSubObj.isActive()) {
                    putFieldError("expenseFinancialSubObjectCode", KFSKeyConstants.ERROR_INACTIVE, "Expense Sub-Object");
                }
            }
        }


        return success;
    }


    /**
     * Returns true if credit card vendor number is valid (i.e. numeric and at least 5 digits)
     * 
     * @return true if credit card vendor number is valid (i.e. numeric and at least 5 digits)
     */
    protected boolean checkCreditCardVendorNumber() {
        String ccvNumber = newCreditCardVendor.getFinancialDocumentCreditCardVendorNumber();

        if (ccvNumber == null) {
            return false;
        }
        else if (!StringUtils.isNumeric(ccvNumber)) {
            putFieldError("financialDocumentCreditCardVendorNumber", KFSKeyConstants.ERROR_NUMERIC, "Vendor Credit Card Number");
            return false;
        }
        else if (ccvNumber.length() < 5) {
            String errorMessage[] = null;
            errorMessage = new String[] { "Vendor Credit Card Number", "5" };
            putFieldError("financialDocumentCreditCardVendorNumber", KFSKeyConstants.ERROR_MIN_LENGTH, errorMessage);
            return false;
        }

        return true;
    }


    /**
     * Returns true if account is active (i.e. exists and is not expired or closed)
     * 
     * @param accountNumber account number
     * @param fieldName field name to place error for
     * @param errorMessage error message to display
     * @return true if account is active (i.e. exists and is not expired or closed)
     */
    protected boolean checkExistingActiveAccount( String chartOfAccountsCode, String accountNumber, String fieldName, String errorMessage) {
        boolean result = false;
        Account account;
        Map<String,String> pkMap = new HashMap<String,String>();
        pkMap.put("accountNumber", accountNumber);
        pkMap.put("chartOfAccountsCode", chartOfAccountsCode);
        account = (Account) getBoService().findByPrimaryKey(Account.class, pkMap);

        // if the object doesnt exist, then we cant continue, so exit
        if (account == null) {
            putFieldError(fieldName, KFSKeyConstants.ERROR_EXISTENCE, errorMessage);
            return result;
        }

        // check whether expired or not
        if (account.isExpired()) {
            putFieldError(fieldName, KFSKeyConstants.ERROR_EXPIRED, errorMessage);
            return result;
        }

        // check whether closed or not
        if (!account.isActive()) {
            putFieldError(fieldName, KFSKeyConstants.ERROR_CLOSED, errorMessage);
            return result;
        }


        return true;
    }

    /**
     * Returns true if income/expense financial chart of accounts code and account number exist. Income or expense is determined by
     * the "Income" value or the "Expense" value passed in to the method as a string
     * 
     * @param string determines whether or not to check income or expense sub account information (valid values include "Income" or "Expense")
     * @return true if corresponding sub account values exist
     */
    protected boolean checkRequiredSubAccount(String string) {
        boolean returnVal = true;
        if (string.equals("Income")) {
            if (newCreditCardVendor.getIncomeFinancialChartOfAccountsCode() == null) {
                putFieldError("incomeFinancialChartOfAccountsCode", KFSKeyConstants.ERROR_CCV_INCOME_SUBACCOUNT_REQUIRED, "Income Chart");
                returnVal = false;
            }

            if (newCreditCardVendor.getIncomeAccountNumber() == null) {
                putFieldError("incomeAccountNumber", KFSKeyConstants.ERROR_CCV_INCOME_SUBACCOUNT_REQUIRED, "Income Account Number");
                returnVal = false;
            }
        }


        if (string.equals("Expense")) {
            if (newCreditCardVendor.getExpenseFinancialChartOfAccountsCode() == null) {
                putFieldError("expenseFinancialChartOfAccountsCode", KFSKeyConstants.ERROR_CCV_EXPENSE_SUBACCOUNT_REQUIRED, "Expense Chart");
                returnVal = false;
            }

            if (newCreditCardVendor.getExpenseAccountNumber() == null) {
                putFieldError("expenseAccountNumber", KFSKeyConstants.ERROR_CCV_EXPENSE_SUBACCOUNT_REQUIRED, "Expense Account Number");
                returnVal = false;
            }
        }


        return returnVal;
    }


    /**
     * Returns a SubAccount object if SubAccount object exists for "Income" or "Expense"
     * 
     * @param string determines whether or not to retrieve a income or expense sub account (valid values include "Income" or "Expense")
     * @return SubAccount Income/Expense SubAccount object
     */
    protected SubAccount checkExistenceSubAccount(String string) {

        SubAccount subAccount = null;

        if (string.equals("Income")) {
            Map<String,String> pkMap = new HashMap<String,String>();
            pkMap.put("chartOfAccountsCode", newCreditCardVendor.getIncomeFinancialChartOfAccountsCode());
            pkMap.put("accountNumber", newCreditCardVendor.getIncomeAccountNumber());
            pkMap.put("subAccountNumber", newCreditCardVendor.getIncomeSubAccountNumber());
            subAccount = (SubAccount) getBoService().findByPrimaryKey(SubAccount.class, pkMap);
        }

        if (string.equals("Expense")) {
            Map<String,String> pkMap = new HashMap<String,String>();
            pkMap.put("chartOfAccountsCode", newCreditCardVendor.getExpenseFinancialChartOfAccountsCode());
            pkMap.put("accountNumber", newCreditCardVendor.getExpenseAccountNumber());
            pkMap.put("subAccountNumber", newCreditCardVendor.getExpenseSubAccountNumber());
            subAccount = (SubAccount) getBoService().findByPrimaryKey(SubAccount.class, pkMap);
        }

        return subAccount;
    }


    /**
     * Returns a true sub-object code exists for "Income" or "Expense"
     * 
     * @param string determines whether or not to check for an income or expense sub-object code (valid values include "Income" or "Expense")
     * @return true if income/expense chart of account code, account number, and financial object code exist
     */
    protected boolean checkRequiredSubObjectCode(String string) {

        boolean returnVal = true;
        if (string.equals("Income")) {
            if ( StringUtils.isBlank( newCreditCardVendor.getIncomeFinancialChartOfAccountsCode() ) ) {
                putFieldError("incomeFinancialChartOfAccountsCode", KFSKeyConstants.ERROR_CCV_INCOME_SUBOBJ_REQUIRED, "Income Chart");
                returnVal = false;
            }

            if ( StringUtils.isBlank( newCreditCardVendor.getIncomeAccountNumber() ) ) {
                putFieldError("incomeAccountNumber", KFSKeyConstants.ERROR_CCV_INCOME_SUBOBJ_REQUIRED, "Income Account Number");
                returnVal = false;
            }

            if ( StringUtils.isBlank( newCreditCardVendor.getIncomeFinancialObjectCode() ) ) {
                putFieldError("incomeFinancialObjectCode", KFSKeyConstants.ERROR_CCV_INCOME_SUBOBJ_REQUIRED, "Income Object Code");
                returnVal = false;
            }

        }


        if (string.equals("Expense")) {
            if ( StringUtils.isBlank( newCreditCardVendor.getExpenseFinancialChartOfAccountsCode() ) ) {
                putFieldError("expenseFinancialChartOfAccountsCode", KFSKeyConstants.ERROR_CCV_EXPENSE_SUBOBJ_REQUIRED, "Expense Chart");
                returnVal = false;
            }

            if ( StringUtils.isBlank( newCreditCardVendor.getExpenseAccountNumber() ) ) {
                putFieldError("expenseAccountNumber", KFSKeyConstants.ERROR_CCV_EXPENSE_SUBOBJ_REQUIRED, "Expense Account Number");
                returnVal = false;
            }

            if ( StringUtils.isBlank( newCreditCardVendor.getExpenseFinancialObjectCode() ) ) {
                putFieldError("expenseFinancialObjectCode", KFSKeyConstants.ERROR_CCV_EXPENSE_SUBOBJ_REQUIRED, "Expense Object Code");
                returnVal = false;
            }

        }


        return returnVal;
    }


    /**
     * Returns a SubObjCd object if SubObjCd object exists for "Income" or "Expense"
     * 
     * @param string determines whether or not to retrieve a income or expense sub object (valid values include "Income" or "Expense")
     * @return SubAccount Income/Expense SubObjCd object
     */
    protected SubObjectCode checkExistenceSubObj(String string) {

        SubObjectCode subObjCd = null;

        if (string.equals("Income")) {
            Map<String,String> pkMap = new HashMap<String,String>();
            pkMap.put("chartOfAccountsCode", newCreditCardVendor.getIncomeFinancialChartOfAccountsCode());
            pkMap.put("accountNumber", newCreditCardVendor.getIncomeAccountNumber());
            pkMap.put("financialObjectCode", newCreditCardVendor.getIncomeFinancialObjectCode());
            pkMap.put("financialSubObjectCode", newCreditCardVendor.getIncomeFinancialSubObjectCode());
            subObjCd = (SubObjectCode) super.getBoService().findByPrimaryKey(SubObjectCodeCurrent.class, pkMap);
        }

        if (string.equals("Expense")) {
            Map<String,String> pkMap = new HashMap<String,String>();
            pkMap.put("chartOfAccountsCode", newCreditCardVendor.getExpenseFinancialChartOfAccountsCode());
            pkMap.put("accountNumber", newCreditCardVendor.getExpenseAccountNumber());
            pkMap.put("financialObjectCode", newCreditCardVendor.getExpenseFinancialObjectCode());
            pkMap.put("financialSubObjectCode", newCreditCardVendor.getExpenseFinancialSubObjectCode());
            subObjCd = (SubObjectCode) super.getBoService().findByPrimaryKey(SubObjectCodeCurrent.class, pkMap);
        }

        return subObjCd;
    }

}
