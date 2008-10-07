/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.cab.businessobject;

import org.kuali.rice.kns.util.KualiDecimal;


/**
 * Base account line group class which assist in easy group by clause
 */
public abstract class AccountLineGroup {
    protected Integer universityFiscalYear;
    protected String chartOfAccountsCode;
    protected String accountNumber;
    protected String subAccountNumber;
    protected String financialObjectCode;
    protected String financialSubObjectCode;
    protected String universityFiscalPeriodCode;
    protected String documentNumber;
    protected String referenceFinancialDocumentNumber;
    protected KualiDecimal amount;


    /**
     * Gets the universityFiscalYear attribute.
     * 
     * @return Returns the universityFiscalYear
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute.
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the accountNumber attribute.
     * 
     * @return Returns the accountNumber
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute.
     * 
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the subAccountNumber attribute.
     * 
     * @return Returns the subAccountNumber
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * Sets the subAccountNumber attribute.
     * 
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    /**
     * Gets the financialObjectCode attribute.
     * 
     * @return Returns the financialObjectCode
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * Sets the financialObjectCode attribute.
     * 
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /**
     * Gets the financialSubObjectCode attribute.
     * 
     * @return Returns the financialSubObjectCode
     */
    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    /**
     * Sets the financialSubObjectCode attribute.
     * 
     * @param financialSubObjectCode The financialSubObjectCode to set.
     */
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }

    /**
     * Gets the universityFiscalPeriodCode attribute.
     * 
     * @return Returns the universityFiscalPeriodCode
     */
    public String getUniversityFiscalPeriodCode() {
        return universityFiscalPeriodCode;
    }

    /**
     * Sets the universityFiscalPeriodCode attribute.
     * 
     * @param universityFiscalPeriodCode The universityFiscalPeriodCode to set.
     */
    public void setUniversityFiscalPeriodCode(String universityFiscalPeriodCode) {
        this.universityFiscalPeriodCode = universityFiscalPeriodCode;
    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the referenceFinancialDocumentNumber attribute.
     * 
     * @return Returns the referenceFinancialDocumentNumber
     */
    public String getReferenceFinancialDocumentNumber() {
        return referenceFinancialDocumentNumber;
    }

    /**
     * Sets the referenceFinancialDocumentNumber attribute.
     * 
     * @param referenceFinancialDocumentNumber The referenceFinancialDocumentNumber to set.
     */
    public void setReferenceFinancialDocumentNumber(String referenceFinancialDocumentNumber) {
        this.referenceFinancialDocumentNumber = referenceFinancialDocumentNumber;
    }


    /**
     * Overridden so that group by statement can be easily implemented.
     * <li>DO NOT REMOVE this method, it is critical to reconciliation process</li>
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !AccountLineGroup.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        boolean equal = true;
        AccountLineGroup test = (AccountLineGroup) obj;
        equal = universityFiscalYear != null ? universityFiscalYear.equals(test.getUniversityFiscalYear()) : null == test.getUniversityFiscalYear();
        equal &= equalsIgnoreFiller(chartOfAccountsCode, test.getChartOfAccountsCode());
        equal &= equalsIgnoreFiller(accountNumber, test.getAccountNumber());
        equal &= equalsIgnoreFiller(subAccountNumber, test.getSubAccountNumber());
        equal &= equalsIgnoreFiller(financialObjectCode, test.getFinancialObjectCode());
        equal &= equalsIgnoreFiller(financialSubObjectCode, test.getFinancialSubObjectCode());
        equal &= equalsIgnoreFiller(universityFiscalPeriodCode, test.getUniversityFiscalPeriodCode());
        equal &= equalsIgnoreFiller(documentNumber, test.getDocumentNumber());
        equal &= equalsIgnoreFiller(referenceFinancialDocumentNumber, test.getReferenceFinancialDocumentNumber());
        return equal;
    }

    private String replaceFiller(String val) {
        if (val == null) {
            return "";
        }
        char[] charArray = val.trim().toCharArray();
        for (char c : charArray) {
            if (c != '-') {
                return val;
            }
        }
        return "";
    }

    private boolean equalsIgnoreFiller(String val, String test) {
        String valStr = val == null ? "" : val.trim().replaceAll("-", "");
        String testStr = test == null ? "" : test.trim().replaceAll("-", "");
        return valStr.equals(testStr);
    }

    /**
     * Overridden so that group by statement can be easily implemented.
     * <li>DO NOT REMOVE this method, it is critical to reconciliation process</li>
     * 
     * @see java.lang.Object#hashCode(java.lang.Object)
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (null == universityFiscalYear ? 0 : universityFiscalYear.intValue());
        hash = 31 * hash + replaceFiller(chartOfAccountsCode).hashCode();
        hash = 31 * hash + replaceFiller(accountNumber).hashCode();
        hash = 31 * hash + replaceFiller(subAccountNumber).hashCode();
        hash = 31 * hash + replaceFiller(financialObjectCode).hashCode();
        hash = 31 * hash + replaceFiller(financialSubObjectCode).hashCode();
        hash = 31 * hash + replaceFiller(universityFiscalPeriodCode).hashCode();
        hash = 31 * hash + replaceFiller(documentNumber).hashCode();
        hash = 31 * hash + replaceFiller(referenceFinancialDocumentNumber).hashCode();
        return hash;
    }

    /**
     * Gets the absAmout attribute.
     * 
     * @return Returns the absAmout
     */

    public KualiDecimal getAmount() {
        return amount;
    }

    /**
     * Sets the amount attribute.
     * 
     * @param amount The amount to set.
     */

    public void setAmount(KualiDecimal absAmount) {
        this.amount = absAmount;
    }

    @Override
    public String toString() {
        return this.hashCode() + "-" + universityFiscalYear + "-" + chartOfAccountsCode + "-" + accountNumber + "-" + replaceFiller(subAccountNumber) + "-" + financialObjectCode + "-" + replaceFiller(financialSubObjectCode) + "-" + universityFiscalPeriodCode + "-" + documentNumber + "-" + referenceFinancialDocumentNumber;
    }
}
