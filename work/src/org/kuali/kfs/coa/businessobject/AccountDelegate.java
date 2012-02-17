/*
 * Copyright 2005-2008 The Kuali Foundation
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
package org.kuali.kfs.coa.businessobject;

import java.sql.Date;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.bo.DocumentTypeEBO;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 *
 */
public class AccountDelegate extends PersistableBusinessObjectBase implements MutableInactivatable {

    private static final long serialVersionUID = 6883162275377881235L;

    /**
     * Default no-arg constructor.
     */
    public AccountDelegate() {
    }

    private String chartOfAccountsCode;
    private String accountNumber;
    private String financialDocumentTypeCode;
    private String accountDelegateSystemId;
    private KualiDecimal finDocApprovalFromThisAmt;
    private boolean accountsDelegatePrmrtIndicator;
    private boolean active;
    private Date accountDelegateStartDate;
    private KualiDecimal finDocApprovalToThisAmount;

    private Chart chart;
    private Account account;
    private transient DocumentTypeEBO financialSystemDocumentTypeCode;
    private Person accountDelegate;

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
     */
    public String getFinancialDocumentTypeCode() {
        return financialDocumentTypeCode;
    }

    /**
     * Sets the financialDocumentTypeCode attribute.
     *
     * @param financialDocumentTypeCode The financialDocumentTypeCode to set.
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
     */
    public KualiDecimal getFinDocApprovalFromThisAmt() {
        return finDocApprovalFromThisAmt;
    }

    /**
     * Sets the finDocApprovalFromThisAmt attribute.
     *
     * @param finDocApprovalFromThisAmt The finDocApprovalFromThisAmt to set.
     */
    public void setFinDocApprovalFromThisAmt(KualiDecimal finDocApprovalFromThisAmt) {
        this.finDocApprovalFromThisAmt = finDocApprovalFromThisAmt;
    }

    /**
     * Gets the accountsDelegatePrmrtIndicator attribute.
     *
     * @return Returns the accountsDelegatePrmrtIndicator
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
     * Gets the active attribute.
     *
     * @return Returns the active
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute.
     *
     * @param active The active to set.
     * @deprecated
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the accountDelegateStartDate attribute.
     *
     * @return Returns the accountDelegateStartDate
     */
    public Date getAccountDelegateStartDate() {
        return accountDelegateStartDate;
    }

    /**
     * Sets the accountDelegateStartDate attribute.
     *
     * @param accountDelegateStartDate The accountDelegateStartDate to set.
     */
    public void setAccountDelegateStartDate(Date accountDelegateStartDate) {
        this.accountDelegateStartDate = accountDelegateStartDate;
    }

    /**
     * Gets the finDocApprovalToThisAmount attribute.
     *
     * @return Returns the finDocApprovalToThisAmount
     */
    public KualiDecimal getFinDocApprovalToThisAmount() {
        return finDocApprovalToThisAmount;
    }

    /**
     * Sets the finDocApprovalToThisAmount attribute.
     *
     * @param finDocApprovalToThisAmount The finDocApprovalToThisAmount to set.
     */
    public void setFinDocApprovalToThisAmount(KualiDecimal finDocApprovalToThisAmount) {
        this.finDocApprovalToThisAmount = finDocApprovalToThisAmount;
    }

    /**
     * Gets the account attribute.
     *
     * @return Returns the account
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
     * Gets the financialSystemDocumentTypeCode attribute.
     * @return Returns the financialSystemDocumentTypeCode.
     */
    public DocumentTypeEBO getFinancialSystemDocumentTypeCode() {
        if ( StringUtils.isBlank( financialDocumentTypeCode ) ) {
            financialSystemDocumentTypeCode = null;
        } else {
            if ( financialSystemDocumentTypeCode == null || !StringUtils.equals(financialDocumentTypeCode, financialSystemDocumentTypeCode.getName() ) ) {
                org.kuali.rice.kew.api.doctype.DocumentType temp = SpringContext.getBean(DocumentTypeService.class).getDocumentTypeByName(financialDocumentTypeCode);
                if ( temp != null ) {
                    financialSystemDocumentTypeCode = DocumentType.from( temp );
                } else {
                    financialSystemDocumentTypeCode = null;
                }
            }
        }
        return financialSystemDocumentTypeCode;
    }

    public Person getAccountDelegate() {
        accountDelegate = SpringContext.getBean(PersonService.class).updatePersonIfNecessary(accountDelegateSystemId, accountDelegate);
        return accountDelegate;
    }

    /**
     * Sets the accountDelegate attribute value.
     *
     * @param accountDelegate The accountDelegate to set.
     */
    public void setAccountDelegate(Person accountDelegate) {
        this.accountDelegate = accountDelegate;
    }

    /**
     * This method (a hack by any other name...) returns a string so that an Account Delegate can have a link to view its own
     * inquiry page after a look up
     *
     * @return the String "View Account Delegate"
     */
    public String getAccountDelegateViewer() {
        return "View Account Delegate";
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
    @Override
    public boolean equals(Object obj) {
        if (obj != null) {
            if (this.getClass().equals(obj.getClass())) {
                AccountDelegate other = (AccountDelegate) obj;
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
    @Override
    public int hashCode() {
        return ObjectUtil.generateHashCode(this, Arrays.asList(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSPropertyConstants.ACCOUNT_NUMBER,"financialDocumentTypeCode", "accountDelegateSystemId" ));
    }

}

