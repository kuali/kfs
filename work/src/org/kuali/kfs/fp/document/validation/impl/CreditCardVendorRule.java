/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.rules;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.KeyConstants;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.financial.bo.CreditCardVendor;

/**
 * This class...
 * 
 * @author Kuali Nervous System Team ()
 */
public class CreditCardVendorRule extends MaintenanceDocumentRuleBase {

    private CreditCardVendor newCreditCardVendor;


    public void setupConvenienceObjects() {

        // setup newCreditCardVendor convenience objects, make sure all possible sub-objects are populated
        newCreditCardVendor = (CreditCardVendor) super.getNewBo();
    }

    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        // default to success
        boolean success = true;
        setupConvenienceObjects();

        // check valid Credit Card Vendor Number (numeric, minimum length)
        success &= checkCreditCardVendorNumber();

        return success;


    }

    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
        // default to success


        return processCustomRouteDocumentBusinessRules(document);
    }

    /**
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
                    putFieldError("incomeSubAccountNumber", KeyConstants.ERROR_CCV_INVALIDSUBACCOUNT, "Income Sub-Account Number, " + newCreditCardVendor.getIncomeSubAccountNumber());
                }
                else

                // check the Sub-Account is active
                if (!existenceSubAccount.isSubAccountActiveIndicator()) {
                    putFieldError("incomeSubAccountNumber", KeyConstants.ERROR_INACTIVE, "Income Sub-Account");
                }
            }
        }

        // check Expense Sub-Account business rule
        if (newCreditCardVendor.getExpenseSubAccountNumber() != null) {
            if (checkRequiredSubAccount("Expense")) {

                // check existence of Sub-Account
                SubAccount existenceSubAccount = checkExistenceSubAccount("Expense");
                if (existenceSubAccount == null) {
                    putFieldError("expenseSubAccountNumber", KeyConstants.ERROR_CCV_INVALIDSUBACCOUNT, "Expense Sub-Account Number, " + newCreditCardVendor.getExpenseSubAccountNumber());

                }
                else

                // check the Sub-Account is active
                if (!existenceSubAccount.isSubAccountActiveIndicator()) {
                    putFieldError("expenseSubAccountNumber", KeyConstants.ERROR_INACTIVE, "Expense Sub-Account");
                }
            }
        }

        // check Income Sub-Object Code business rule
        if (newCreditCardVendor.getIncomeFinancialSubObjectCode() != null) {
            if (checkRequiredSubObjectCode("Income")) {

                // check existence of Sub-Object
                SubObjCd existenceSubObj = checkExistenceSubObj("Income");
                if (existenceSubObj == null) {
                    putFieldError("incomeFinancialSubObjectCode", KeyConstants.ERROR_CCV_INVALIDSUBOBJECT, "Income Sub-Object Code, " + newCreditCardVendor.getIncomeFinancialSubObjectCode());
                }
                else
                // check the Sub-Object is active
                if (!existenceSubObj.isFinancialSubObjectActiveIndicator()) {
                    putFieldError("incomeFinancialSubObjectCode", KeyConstants.ERROR_INACTIVE, "Income Sub-Object");
                }

            }
        }

        // check Expense Sub-Object Code business rule
        if (newCreditCardVendor.getExpenseFinancialSubObjectCode() != null) {
            if (checkRequiredSubObjectCode("Expense")) {

                // check existence of Sub-Object
                SubObjCd existenceSubObj = checkExistenceSubObj("Expense");
                if (existenceSubObj == null) {
                    putFieldError("expenseFinancialSubObjectCode", KeyConstants.ERROR_CCV_INVALIDSUBOBJECT, "Expense Sub-Object Code, " + newCreditCardVendor.getExpenseFinancialSubObjectCode());
                }
                else
                // check the Sub-Object is active
                if (!existenceSubObj.isFinancialSubObjectActiveIndicator()) {
                    putFieldError("expenseFinancialSubObjectCode", KeyConstants.ERROR_INACTIVE, "Expense Sub-Object");
                }
            }
        }


        return success;
    }


    private boolean checkCreditCardVendorNumber() {
        String ccvNumber = newCreditCardVendor.getFinancialDocumentCreditCardVendorNumber();

        if (ccvNumber == null) {
            return false;
        }
        else if (!StringUtils.isNumeric(ccvNumber)) {
            putFieldError("financialDocumentCreditCardVendorNumber", KeyConstants.ERROR_NUMERIC, "Vendor Credit Card Number");
            return false;
        }
        else if (ccvNumber.length() < 5) {
            String errorMessage[] = null;
            errorMessage = new String[] { "Vendor Credit Card Number", "5" };
            putFieldError("financialDocumentCreditCardVendorNumber", KeyConstants.ERROR_MIN_LENGTH, errorMessage);
            return false;
        }

        return true;
    }


    /**
     * This method...is for checking whether or not account is active. check the existence and expired
     * 
     */
    private boolean checkExistingActiveAccount(String accountNumber, String fieldName, String errorMessage) {
        boolean result = false;
        Account account;
        Map pkMap = new HashMap();
        pkMap.put("accountNumber", accountNumber);
        account = (Account) super.getBoService().findByPrimaryKey(Account.class, pkMap);

        // if the object doesnt exist, then we cant continue, so exit
        if (ObjectUtils.isNull(account)) {
            putFieldError(fieldName, KeyConstants.ERROR_EXISTENCE, errorMessage);
            return result;
        }

        // check whether expired or not
        if (account.isExpired()) {
            putFieldError(fieldName, KeyConstants.ERROR_EXPIRED, errorMessage);
            return result;
        }

        // check whether closed or not
        if (account.isAccountClosedIndicator()) {
            putFieldError(fieldName, KeyConstants.ERROR_CLOSED, errorMessage);
            return result;
        }


        return true;
    }

    private boolean checkRequiredSubAccount(String string) {
        boolean returnVal = true;
        if (string.equals("Income")) {
            if (newCreditCardVendor.getIncomeFinancialChartOfAccountsCode() == null) {
                putFieldError("incomeFinancialChartOfAccountsCode", KeyConstants.ERROR_CCV_INCOME_SUBACCOUNT_REQUIRED, "Income Chart");
                returnVal = false;
            }

            if (newCreditCardVendor.getIncomeAccountNumber() == null) {
                putFieldError("incomeAccountNumber", KeyConstants.ERROR_CCV_INCOME_SUBACCOUNT_REQUIRED, "Income Account Number");
                returnVal = false;
            }
        }


        if (string.equals("Expense")) {
            if (newCreditCardVendor.getExpenseFinancialChartOfAccountsCode() == null) {
                putFieldError("expenseFinancialChartOfAccountsCode", KeyConstants.ERROR_CCV_EXPENSE_SUBACCOUNT_REQUIRED, "Expense Chart");
                returnVal = false;
            }

            if (newCreditCardVendor.getExpenseAccountNumber() == null) {
                putFieldError("expenseAccountNumber", KeyConstants.ERROR_CCV_EXPENSE_SUBACCOUNT_REQUIRED, "Expense Account Number");
                returnVal = false;
            }
        }


        return returnVal;
    }


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


    private boolean checkRequiredSubObjectCode(String string) {

        boolean returnVal = true;
        if (string.equals("Income")) {
            if (newCreditCardVendor.getIncomeFinancialChartOfAccountsCode() == null) {
                putFieldError("incomeFinancialChartOfAccountsCode", KeyConstants.ERROR_CCV_INCOME_SUBOBJ_REQUIRED, "Income Chart");
                returnVal = false;
            }

            if (newCreditCardVendor.getIncomeAccountNumber() == null) {
                putFieldError("incomeAccountNumber", KeyConstants.ERROR_CCV_INCOME_SUBOBJ_REQUIRED, "Income Account Number");
                returnVal = false;
            }

            if (newCreditCardVendor.getIncomeFinancialObjectCode() == null) {
                putFieldError("incomeFinancialObjectCode", KeyConstants.ERROR_CCV_INCOME_SUBOBJ_REQUIRED, "Income Object Code");
                returnVal = false;
            }

        }


        if (string.equals("Expense")) {
            if (newCreditCardVendor.getExpenseFinancialChartOfAccountsCode() == null) {
                putFieldError("expenseFinancialChartOfAccountsCode", KeyConstants.ERROR_CCV_EXPENSE_SUBOBJ_REQUIRED, "Expense Chart");
                returnVal = false;
            }

            if (newCreditCardVendor.getExpenseAccountNumber() == null) {
                putFieldError("expenseAccountNumber", KeyConstants.ERROR_CCV_EXPENSE_SUBOBJ_REQUIRED, "Expense Account Number");
                returnVal = false;
            }

            if (newCreditCardVendor.getExpenseFinancialObjectCode() == null) {
                putFieldError("expenseFinancialObjectCode", KeyConstants.ERROR_CCV_EXPENSE_SUBOBJ_REQUIRED, "Expense Object Code");
                returnVal = false;
            }

        }


        return returnVal;
    }


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
