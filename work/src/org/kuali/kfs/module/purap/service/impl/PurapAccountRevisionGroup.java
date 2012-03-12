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
package org.kuali.kfs.module.purap.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLineBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;


/**
 * Purap account history grouping
 */
public class PurapAccountRevisionGroup {
    protected String chartOfAccountsCode;
    protected String accountNumber;
    protected String subAccountNumber;
    protected String financialObjectCode;
    protected String financialSubObjectCode;
    protected Integer postingYear;
    protected String postingPeriodCode;
    protected KualiDecimal amount;
    protected Integer itemIdentifier;
    private List<PurApAccountingLineBase> sourceEntries = new ArrayList<PurApAccountingLineBase>();
    private KualiDecimal changeAmount;
    // non-key attributes
    private String projectCode;
    private String organizationReferenceId;
    private BigDecimal accountLinePercent;


    public PurapAccountRevisionGroup(PurApAccountingLineBase entry) {
        setChartOfAccountsCode(entry.getChartOfAccountsCode());
        setAccountNumber(entry.getAccountNumber());
        setSubAccountNumber(entry.getSubAccountNumber());
        setFinancialObjectCode(entry.getFinancialObjectCode());
        setFinancialSubObjectCode(entry.getFinancialSubObjectCode());
        setItemIdentifier(entry.getItemIdentifier());
        setPostingYear(entry.getPostingYear());
        setPostingPeriodCode(entry.getPostingPeriodCode());
        setProjectCode(entry.getProjectCode());
        setOrganizationReferenceId(entry.getOrganizationReferenceId());
        setAccountLinePercent(entry.getAccountLinePercent());
        this.sourceEntries.add(entry);
        setAmount(entry.getAmount());
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
     * Gets the postingYear attribute.
     * 
     * @return Returns the postingYear.
     */
    public Integer getPostingYear() {
        return postingYear;
    }


    /**
     * Sets the postingYear attribute value.
     * 
     * @param postingYear The postingYear to set.
     */
    public void setPostingYear(Integer postingYear) {
        this.postingYear = postingYear;
    }


    /**
     * Gets the postingPeriodCode attribute.
     * 
     * @return Returns the postingPeriodCode.
     */
    public String getPostingPeriodCode() {
        return postingPeriodCode;
    }


    /**
     * Sets the postingPeriodCode attribute value.
     * 
     * @param postingPeriodCode The postingPeriodCode to set.
     */
    public void setPostingPeriodCode(String postingPeriodCode) {
        this.postingPeriodCode = postingPeriodCode;
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
        if (obj == null || !PurapAccountRevisionGroup.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        PurapAccountRevisionGroup test = (PurapAccountRevisionGroup) obj;
        EqualsBuilder equalsBuilder = new EqualsBuilder();
        equalsBuilder.append(this.postingYear, test.getPostingYear());
        equalsBuilder.append(itemIdentifier, test.getItemIdentifier());
        equalsBuilder.append(replaceFiller(chartOfAccountsCode), replaceFiller(test.getChartOfAccountsCode()));
        equalsBuilder.append(replaceFiller(accountNumber), replaceFiller(test.getAccountNumber()));
        equalsBuilder.append(replaceFiller(subAccountNumber), replaceFiller(test.getSubAccountNumber()));
        equalsBuilder.append(replaceFiller(financialObjectCode), replaceFiller(test.getFinancialObjectCode()));
        equalsBuilder.append(replaceFiller(financialSubObjectCode), replaceFiller(test.getFinancialSubObjectCode()));
        equalsBuilder.append(replaceFiller(postingPeriodCode), replaceFiller(test.getPostingPeriodCode()));
        equalsBuilder.append(replaceFiller(projectCode), replaceFiller(test.getProjectCode()));
        equalsBuilder.append(replaceFiller(organizationReferenceId), replaceFiller(test.getOrganizationReferenceId()));
        return equalsBuilder.isEquals();
    }

    protected String replaceFiller(String val) {
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
        hashCodeBuilder.append(this.postingYear);
        hashCodeBuilder.append(itemIdentifier);
        hashCodeBuilder.append(replaceFiller(chartOfAccountsCode));
        hashCodeBuilder.append(replaceFiller(accountNumber));
        hashCodeBuilder.append(replaceFiller(subAccountNumber));
        hashCodeBuilder.append(replaceFiller(financialObjectCode));
        hashCodeBuilder.append(replaceFiller(financialSubObjectCode));
        hashCodeBuilder.append(replaceFiller(postingPeriodCode));
        hashCodeBuilder.append(replaceFiller(projectCode));
        hashCodeBuilder.append(replaceFiller(organizationReferenceId));
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
        return "" + postingYear + "-" + chartOfAccountsCode + "-" + accountNumber + "-" + replaceFiller(subAccountNumber) + "-" + financialObjectCode + "-" + replaceFiller(financialSubObjectCode) + "-" + postingPeriodCode + "-" + itemIdentifier;
    }

    /**
     * Gets the itemIdentifier attribute.
     * 
     * @return Returns the itemIdentifier.
     */
    public Integer getItemIdentifier() {
        return itemIdentifier;
    }

    /**
     * Sets the itemIdentifier attribute value.
     * 
     * @param itemIdentifier The itemIdentifier to set.
     */
    public void setItemIdentifier(Integer itemIdentifier) {
        this.itemIdentifier = itemIdentifier;
    }

    /**
     * This method will combine multiple Purap account entries for the same account line group.
     * 
     * @param entry PurApAccountingLineBase
     */
    public void combineEntry(PurApAccountingLineBase newEntry) {
        this.sourceEntries.add(newEntry);
        this.amount = this.amount.add(newEntry.getAmount());
    }

    /**
     * Gets the changeAmount attribute.
     * 
     * @return Returns the changeAmount.
     */
    public KualiDecimal getChangeAmount() {
        return changeAmount;
    }

    /**
     * Sets the changeAmount attribute value.
     * 
     * @param changeAmount The changeAmount to set.
     */
    public void setChangeAmount(KualiDecimal changeAmount) {
        this.changeAmount = changeAmount;
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
     * Gets the accountLinePercent attribute.
     * 
     * @return Returns the accountLinePercent.
     */
    public BigDecimal getAccountLinePercent() {
        return accountLinePercent;
    }

    /**
     * Sets the accountLinePercent attribute value.
     * 
     * @param accountLinePercent The accountLinePercent to set.
     */
    public void setAccountLinePercent(BigDecimal accountLinePercent) {
        this.accountLinePercent = accountLinePercent;
    }

    public PurApAccountingLineBase buildRevisionRecord(Class<? extends PurApAccountingLineBase> clazz) {
        PurApAccountingLineBase histRecord = null;
        try {
            histRecord = clazz.newInstance();
            histRecord.setItemIdentifier(this.getItemIdentifier());
            histRecord.setChartOfAccountsCode(this.getChartOfAccountsCode());
            histRecord.setAccountNumber(this.getAccountNumber());
            histRecord.setSubAccountNumber(this.getSubAccountNumber());
            histRecord.setFinancialObjectCode(this.getFinancialObjectCode());
            histRecord.setFinancialSubObjectCode(this.getFinancialSubObjectCode());
            histRecord.setProjectCode(this.getProjectCode());
            histRecord.setOrganizationReferenceId(this.getOrganizationReferenceId());
            histRecord.setAmount(this.getChangeAmount());
            histRecord.setPostingYear(this.getPostingYear());
            histRecord.setPostingPeriodCode(this.getPostingPeriodCode());
            histRecord.setAccountLinePercent(this.getAccountLinePercent());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return histRecord;
    }
}
