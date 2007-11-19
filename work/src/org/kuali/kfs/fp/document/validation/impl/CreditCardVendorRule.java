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
package org.kuali.module.financial.rules;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.financial.bo.CreditCardVendor;

/**
 * This class represents business rules for the credit card vendor maintenance document
 */
public class CreditCardVendorRule extends MaintenanceDocumentRuleBase {

    private CreditCardVendor newCreditCardVendor;

    /**
     *  Sets up a CreditCardVendor convenience objects to make sure all possible sub-objects are populated
     * 
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#setupConvenienceObjects()
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
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
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
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
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
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        // default to success
        boolean success = true;

        setupConvenienceObjects();

        // check valid Credit Card Vendor Number (numeric, minimum length)
        success &= checkCreditCardVendorNumber();

        // check Income Account Number business rule
        if (newCreditCardVendor.getIncomeAccountNumber() != null) {
            success &= checkExistingActiveAccount(newCreditCardVendor.getIncomeAccountNumber(), "incomeAccountNumber", "Income Account Number");
        }

        // check Expense Account Number business rule
        if (newCreditCardVendor.getExpenseAccountNumber() != null) {
            success &= checkExistingActiveAccount(newCreditCardVendor.getExpenseAccountNumber(), "expenseAccountNumber", "Expense Account Number");
        }

        // check Income Sub-Account business rule
        if (newCreditCardVendor.getIncomeSubAccountNumber() != null) {

            // check required fields to validate Sub-Account
            if (checkRequiredSubAccount("Income")) {
                SubAccount existenceSubAccount = checkExistenceSubAccount("Income");

                // check existence of Sub-Account
                if (existenceSubAccount == null) {
                    putFieldError("incomeSubAccountNumber", KFSKeyConstants.ERROR_CCV_INVALIDSUBACCOUNT, "Income Sub-Account Number, " + newCreditCardVendor.getIncomeSubAccountNumber());
                }
                else

                // check the Sub-Account is active
                if (!existenceSubAccount.isSubAccountActiveIndicator()) {
                    putFieldError("incomeSubAccountNumber", KFSKeyConstants.ERROR_INACTIVE, "Income Sub-Account");
                }
            }
        }

        // check Expense Sub-Account business rule
        if (newCreditCardVendor.getExpenseSubAccountNumber() != null) {
            if (checkRequiredSubAccount("Expense")) {

                // check existence of Sub-Account
                SubAccount existenceSubAccount = checkExistenceSubAccount("Expense");
                if (existenceSubAccount == null) {
                    putFieldError("expenseSubAccountNumber", KFSKeyConstants.ERROR_CCV_INVALIDSUBACCOUNT, "Expense Sub-Account Number, " + newCreditCardVendor.getExpenseSubAccountNumber());

                }
                else

                // check the Sub-Account is active
                if (!existenceSubAccount.isSubAccountActiveIndicator()) {
                    putFieldError("expenseSubAccountNumber", KFSKeyConstants.ERROR_INACTIVE, "Expense Sub-Account");
                }
            }
        }

        // check Income Sub-Object Code business rule
        if (newCreditCardVendor.getIncomeFinancialSubObjectCode() != null) {
            if (checkRequiredSubObjectCode("Income")) {

                // check existence of Sub-Object
                SubObjCd existenceSubObj = checkExistenceSubObj("Income");
                if (existenceSubObj == null) {
                    putFieldError("incomeFinancialSubObjectCode", KFSKeyConstants.ERROR_CCV_INVALIDSUBOBJECT, "Income Sub-Object Code, " + newCreditCardVendor.getIncomeFinancialSubObjectCode());
                }
                else
                // check the Sub-Object is active
                if (!existenceSubObj.isFinancialSubObjectActiveIndicator()) {
                    putFieldError("incomeFinancialSubObjectCode", KFSKeyConstants.ERROR_INACTIVE, "Income Sub-Object");
                }

            }
        }

        // check Expense Sub-Object Code business rule
        if (newCreditCardVendor.getExpenseFinancialSubObjectCode() != null) {
            if (checkRequiredSubObjectCode("Expense")) {

                // check existence of Sub-Object
                SubObjCd existenceSubObj = checkExistenceSubObj("Expense");
                if (existenceSubObj == null) {
                    putFieldError("expenseFinancialSubObjectCode", KFSKeyConstants.ERROR_CCV_INVALIDSUBOBJECT, "Expense Sub-Object Code, " + newCreditCardVendor.getExpenseFinancialSubObjectCode());
                }
                else
                // check the Sub-Object is active
                if (!existenceSubObj.isFinancialSubObjectActiveIndicator()) {
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
    private boolean checkCreditCardVendorNumber() {
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
    private boolean checkExistingActiveAccount(String accountNumber, String fieldName, String errorMessage) {
        boolean result = false;
        Account account;
        Map pkMap = new HashMap();
        pkMap.put("accountNumber", accountNumber);
        account = (Account) super.getBoService().findByPrimaryKey(Account.class, pkMap);

        // if the object doesnt exist, then we cant continue, so exit
        if (ObjectUtils.isNull(account)) {
            putFieldError(fieldName, KFSKeyConstants.ERROR_EXISTENCE, errorMessage);
            return result;
        }

        // check whether expired or not
        if (account.isExpired()) {
            putFieldError(fieldName, KFSKeyConstants.ERROR_EXPIRED, errorMessage);
            return result;
        }

        // check whether closed or not
        if (account.isAccountClosedIndicator()) {
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
    private boolean checkRequiredSubAccount(String string) {
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
    private SubAccount checkExistenceSubAccount(String string) {

        SubAccount subAccount = null;

        if (string.equals("Income")) {
            Map pkMap = new HashMap();
            pkMap.put("chartOfAccountsCode", newCreditCardVendor.getIncomeFinancialChartOfAccountsCode());
            pkMap.put("accountNumber", newCreditCardVendor.getIncomeAccountNumber());
            pkMap.put("subAccountNumber", newCreditCardVendor.getIncomeSubAccountNumber());
            subAccount = (SubAccount) super.getBoService().findByPrimaryKey(SubAccount.class, pkMap);
        }

        if (string.equals("Expense")) {
            Map pkMap = new HashMap();
            pkMap.put("chartOfAccountsCode", newCreditCardVendor.getExpenseFinancialChartOfAccountsCode());
            pkMap.put("accountNumber", newCreditCardVendor.getExpenseAccountNumber());
            pkMap.put("subAccountNumber", newCreditCardVendor.getExpenseSubAccountNumber());
            subAccount = (SubAccount) super.getBoService().findByPrimaryKey(SubAccount.class, pkMap);
        }

        return subAccount;
    }


    /**
     * Returns a true sub-object code exists for "Income" or "Expense"
     * 
     * @param string determines whether or not to check for an income or expense sub-object code (valid values include "Income" or "Expense")
     * @return true if income/expense chart of account code, account number, and financial object code exist
     */
    private boolean checkRequiredSubObjectCode(String string) {

        boolean returnVal = true;
        if (string.equals("Income")) {
            if (newCreditCardVendor.getIncomeFinancialChartOfAccountsCode() == null) {
                putFieldError("incomeFinancialChartOfAccountsCode", KFSKeyConstants.ERROR_CCV_INCOME_SUBOBJ_REQUIRED, "Income Chart");
                returnVal = false;
            }

            if (newCreditCardVendor.getIncomeAccountNumber() == null) {
                putFieldError("incomeAccountNumber", KFSKeyConstants.ERROR_CCV_INCOME_SUBOBJ_REQUIRED, "Income Account Number");
                returnVal = false;
            }

            if (newCreditCardVendor.getIncomeFinancialObjectCode() == null) {
                putFieldError("incomeFinancialObjectCode", KFSKeyConstants.ERROR_CCV_INCOME_SUBOBJ_REQUIRED, "Income Object Code");
                returnVal = false;
            }

        }


        if (string.equals("Expense")) {
            if (newCreditCardVendor.getExpenseFinancialChartOfAccountsCode() == null) {
                putFieldError("expenseFinancialChartOfAccountsCode", KFSKeyConstants.ERROR_CCV_EXPENSE_SUBOBJ_REQUIRED, "Expense Chart");
                returnVal = false;
            }

            if (newCreditCardVendor.getExpenseAccountNumber() == null) {
                putFieldError("expenseAccountNumber", KFSKeyConstants.ERROR_CCV_EXPENSE_SUBOBJ_REQUIRED, "Expense Account Number");
                returnVal = false;
            }

            if (newCreditCardVendor.getExpenseFinancialObjectCode() == null) {
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
    private SubObjCd checkExistenceSubObj(String string) {

        SubObjCd subObjCd = null;

        if (string.equals("Income")) {
            Map pkMap = new HashMap();
            pkMap.put("chartOfAccountsCode", newCreditCardVendor.getIncomeFinancialChartOfAccountsCode());
            pkMap.put("accountNumber", newCreditCardVendor.getIncomeAccountNumber());
            pkMap.put("financialObjectCode", newCreditCardVendor.getIncomeFinancialObjectCode());
            pkMap.put("financialSubObjectCode", newCreditCardVendor.getIncomeFinancialSubObjectCode());
            subObjCd = (SubObjCd) super.getBoService().findByPrimaryKey(SubObjCd.class, pkMap);
        }

        if (string.equals("Expense")) {
            Map pkMap = new HashMap();
            pkMap.put("chartOfAccountsCode", newCreditCardVendor.getExpenseFinancialChartOfAccountsCode());
            pkMap.put("accountNumber", newCreditCardVendor.getExpenseAccountNumber());
            pkMap.put("financialObjectCode", newCreditCardVendor.getExpenseFinancialObjectCode());
            pkMap.put("financialSubObjectCode", newCreditCardVendor.getExpenseFinancialSubObjectCode());
            subObjCd = (SubObjCd) super.getBoService().findByPrimaryKey(SubObjCd.class, pkMap);
        }

        return subObjCd;
    }

}
