/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.chart.bo;

import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.DocumentType;
import org.kuali.core.bo.PersistableBusinessObjectBase;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.util.SpringServiceLocator;

/**
 * 
 */
public class Delegate extends PersistableBusinessObjectBase {

    private static final long serialVersionUID = 6883162275377881235L;

    /**
     * Default no-arg constructor.
     */
    public Delegate() {
        this.finDocApprovalFromThisAmt = new KualiDecimal(0);
        this.finDocApprovalToThisAmount = new KualiDecimal(0);
    }

    private String chartOfAccountsCode;
    private String accountNumber;
    private String financialDocumentTypeCode;
    private String accountDelegateSystemId;
    private KualiDecimal finDocApprovalFromThisAmt;
    private boolean accountsDelegatePrmrtIndicator;
    private boolean accountDelegateActiveIndicator;
    private Timestamp accountDelegateStartDate;
    private KualiDecimal finDocApprovalToThisAmount;

    private Chart chart;
    private Account account;
    private DocumentType documentType;
    private UniversalUser accountDelegate;

    /**
     * Gets the accountNumber attribute.
     * 
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute value.
     * 
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute value.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the financialDocumentTypeCode attribute.
     * 
     * @return Returns the financialDocumentTypeCode
     * 
     */
    public String getFinancialDocumentTypeCode() {
        return financialDocumentTypeCode;
    }

    /**
     * Sets the financialDocumentTypeCode attribute.
     * 
     * @param financialDocumentTypeCode The financialDocumentTypeCode to set.
     * 
     */
    public void setFinancialDocumentTypeCode(String financialDocumentTypeCode) {
        this.financialDocumentTypeCode = financialDocumentTypeCode;
    }

    /**
     * Gets the accountDelegateSystemId attribute.
     * 
     * @return Returns the accountDelegateSystemId.
     */
    public String getAccountDelegateSystemId() {
        return accountDelegateSystemId;
    }

    /**
     * Sets the accountDelegateSystemId attribute value.
     * 
     * @param accountDelegateSystemId The accountDelegateSystemId to set.
     */
    public void setAccountDelegateSystemId(String accountDelegateSystemId) {
        this.accountDelegateSystemId = accountDelegateSystemId;
    }

    /**
     * Gets the finDocApprovalFromThisAmt attribute.
     * 
     * @return Returns the finDocApprovalFromThisAmt
     * 
     */
    public KualiDecimal getFinDocApprovalFromThisAmt() {
        return finDocApprovalFromThisAmt;
    }

    /**
     * Sets the finDocApprovalFromThisAmt attribute.
     * 
     * @param finDocApprovalFromThisAmt The finDocApprovalFromThisAmt to set.
     * 
     */
    public void setFinDocApprovalFromThisAmt(KualiDecimal finDocApprovalFromThisAmt) {
        this.finDocApprovalFromThisAmt = finDocApprovalFromThisAmt;
    }

    /**
     * Gets the accountsDelegatePrmrtIndicator attribute.
     * 
     * @return Returns the accountsDelegatePrmrtIndicator
     * 
     */
    public boolean isAccountsDelegatePrmrtIndicator() {
        return accountsDelegatePrmrtIndicator;
    }

    /**
     * Sets the accountsDelegatePrmrtIndicator attribute.
     * 
     * @param accountsDelegatePrmrtIndicator The accountsDelegatePrmrtIndicator to set.
     * @deprecated
     */
    public void setAccountsDelegatePrmrtIndicator(boolean accountsDelegatePrmrtIndicator) {
        this.accountsDelegatePrmrtIndicator = accountsDelegatePrmrtIndicator;
    }

    /**
     * Gets the accountDelegateActiveIndicator attribute.
     * 
     * @return Returns the accountDelegateActiveIndicator
     * 
     */
    public boolean isAccountDelegateActiveIndicator() {
        return accountDelegateActiveIndicator;
    }

    /**
     * Sets the accountDelegateActiveIndicator attribute.
     * 
     * @param accountDelegateActiveIndicator The accountDelegateActiveIndicator to set.
     * @deprecated
     */
    public void setAccountDelegateActiveIndicator(boolean accountDelegateActiveIndicator) {
        this.accountDelegateActiveIndicator = accountDelegateActiveIndicator;
    }

    /**
     * Gets the accountDelegateStartDate attribute.
     * 
     * @return Returns the accountDelegateStartDate
     * 
     */
    public Timestamp getAccountDelegateStartDate() {
        return accountDelegateStartDate;
    }

    /**
     * Sets the accountDelegateStartDate attribute.
     * 
     * @param accountDelegateStartDate The accountDelegateStartDate to set.
     * 
     */
    public void setAccountDelegateStartDate(Timestamp accountDelegateStartDate) {
        this.accountDelegateStartDate = accountDelegateStartDate;
    }

    /**
     * Gets the finDocApprovalToThisAmount attribute.
     * 
     * @return Returns the finDocApprovalToThisAmount
     * 
     */
    public KualiDecimal getFinDocApprovalToThisAmount() {
        return finDocApprovalToThisAmount;
    }

    /**
     * Sets the finDocApprovalToThisAmount attribute.
     * 
     * @param finDocApprovalToThisAmount The finDocApprovalToThisAmount to set.
     * 
     */
    public void setFinDocApprovalToThisAmount(KualiDecimal finDocApprovalToThisAmount) {
        this.finDocApprovalToThisAmount = finDocApprovalToThisAmount;
    }

    /**
     * Gets the account attribute.
     * 
     * @return Returns the account
     * 
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the account attribute.
     * 
     * @param account The account to set.
     * @deprecated
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Gets the documentType attribute.
     * 
     * @return Returns the documentType.
     */
    public DocumentType getDocumentType() {
        return documentType;
    }

    /**
     * Sets the documentType attribute value.
     * 
     * @param documentType The documentType to set.
     */
    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public UniversalUser getAccountDelegate() {
        accountDelegate = SpringServiceLocator.getUniversalUserService().updateUniversalUserIfNecessary(accountDelegateSystemId, accountDelegate);
        return accountDelegate;
    }

    /**
     * Sets the accountDelegate attribute value.
     * 
     * @param accountDelegate The accountDelegate to set.
     */
    public void setAccountDelegate(UniversalUser accountDelegate) {
        this.accountDelegate = accountDelegate;
    }

    /**
     * Gets the chart attribute.
     * 
     * @return Returns the chart.
     */
    public Chart getChart() {
        return chart;
    }

    /**
     * Sets the chart attribute value.
     * 
     * @param chart The chart to set.
     */
    public void setChart(Chart chart) {
        this.chart = chart;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (obj != null) {
            if (this.getClass().equals(obj.getClass())) {
                Delegate other = (Delegate) obj;
                if (StringUtils.equalsIgnoreCase(this.chartOfAccountsCode, other.chartOfAccountsCode)) {
                    if (StringUtils.equalsIgnoreCase(this.accountNumber, other.accountNumber)) {
                        if (StringUtils.equalsIgnoreCase(this.financialDocumentTypeCode, other.financialDocumentTypeCode)) {
                            if (StringUtils.equalsIgnoreCase(this.accountDelegateSystemId, other.accountDelegateSystemId)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return toStringBuilder(toStringMapper()).hashCode();
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("chartCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);
        m.put("documentTypeCode", this.financialDocumentTypeCode);
        m.put("accountDelegateSystemId", this.accountDelegateSystemId);

        return m;
    }
}
