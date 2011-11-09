/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.cab.businessobject;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.kuali.rice.core.api.util.type.KualiDecimal;


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
    protected String projectCode;
    protected String organizationReferenceId;
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
     * Gets the projectCode attribute.
     * 
     * @return Returns the projectCode.
     */
    public String getProjectCode() {
        return projectCode;
    }

    /**
     * Sets the projectCode attribute value.
     * 
     * @param projectCode The projectCode to set.
     */
    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    /**
     * Gets the organizationReferenceId attribute.
     * 
     * @return Returns the organizationReferenceId.
     */
    public String getOrganizationReferenceId() {
        return organizationReferenceId;
    }

    /**
     * Sets the organizationReferenceId attribute value.
     * 
     * @param organizationReferenceId The organizationReferenceId to set.
     */
    public void setOrganizationReferenceId(String organizationReferenceId) {
        this.organizationReferenceId = organizationReferenceId;
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
        AccountLineGroup test = (AccountLineGroup) obj;
        EqualsBuilder equalsBuilder = new EqualsBuilder();
        equalsBuilder.append(this.universityFiscalYear, test.getUniversityFiscalYear());
        equalsBuilder.append(replaceFiller(chartOfAccountsCode), replaceFiller(test.getChartOfAccountsCode()));
        equalsBuilder.append(replaceFiller(accountNumber), replaceFiller(test.getAccountNumber()));
        equalsBuilder.append(replaceFiller(subAccountNumber), replaceFiller(test.getSubAccountNumber()));
        equalsBuilder.append(replaceFiller(financialObjectCode), replaceFiller(test.getFinancialObjectCode()));
        equalsBuilder.append(replaceFiller(financialSubObjectCode), replaceFiller(test.getFinancialSubObjectCode()));
        equalsBuilder.append(replaceFiller(universityFiscalPeriodCode), replaceFiller(test.getUniversityFiscalPeriodCode()));
        equalsBuilder.append(replaceFiller(documentNumber), replaceFiller(test.getDocumentNumber()));
        equalsBuilder.append(replaceFiller(referenceFinancialDocumentNumber), replaceFiller(test.getReferenceFinancialDocumentNumber()));
        equalsBuilder.append(replaceFiller(organizationReferenceId), replaceFiller(test.getOrganizationReferenceId()));
        equalsBuilder.append(replaceFiller(projectCode), replaceFiller(test.getProjectCode()));
        return equalsBuilder.isEquals();
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

    /**
     * Overridden so that group by statement can be easily implemented.
     * <li>DO NOT REMOVE this method, it is critical to reconciliation process</li>
     * 
     * @see java.lang.Object#hashCode(java.lang.Object)
     */
    @Override
    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder(37, 41);
        hashCodeBuilder.append(this.universityFiscalYear);
        hashCodeBuilder.append(replaceFiller(chartOfAccountsCode));
        hashCodeBuilder.append(replaceFiller(accountNumber));
        hashCodeBuilder.append(replaceFiller(subAccountNumber));
        hashCodeBuilder.append(replaceFiller(financialObjectCode));
        hashCodeBuilder.append(replaceFiller(financialSubObjectCode));
        hashCodeBuilder.append(replaceFiller(universityFiscalPeriodCode));
        hashCodeBuilder.append(replaceFiller(documentNumber));
        hashCodeBuilder.append(replaceFiller(referenceFinancialDocumentNumber));
        hashCodeBuilder.append(replaceFiller(organizationReferenceId));
        hashCodeBuilder.append(replaceFiller(projectCode));
        return hashCodeBuilder.toHashCode();
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
