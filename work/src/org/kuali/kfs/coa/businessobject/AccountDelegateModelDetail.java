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

package org.kuali.kfs.coa.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.bo.DocumentTypeEBO;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 *
 */
public class AccountDelegateModelDetail extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String chartOfAccountsCode;
    private String organizationCode;
    private String accountDelegateModelName;
    private String accountDelegateUniversalId;
    private String financialDocumentTypeCode;
    private KualiDecimal approvalFromThisAmount;
    private KualiDecimal approvalToThisAmount;
    private boolean accountDelegatePrimaryRoutingIndicator;
    private Date accountDelegateStartDate;
    private boolean active;

    private Chart chartOfAccounts;
    private DocumentTypeEBO financialSystemDocumentTypeCode;
    private Person accountDelegate;

    /**
     * Default constructor.
     */
    public AccountDelegateModelDetail() {
    }

    public AccountDelegateModelDetail(AccountDelegateGlobalDetail delegateGlobalDetail) {
        accountDelegateUniversalId = delegateGlobalDetail.getAccountDelegateUniversalId();
        accountDelegatePrimaryRoutingIndicator = delegateGlobalDetail.getAccountDelegatePrimaryRoutingIndicator();
        approvalFromThisAmount = delegateGlobalDetail.getApprovalFromThisAmount();
        approvalToThisAmount = delegateGlobalDetail.getApprovalToThisAmount();
        accountDelegateStartDate = delegateGlobalDetail.getAccountDelegateStartDate();
        financialDocumentTypeCode = delegateGlobalDetail.getFinancialDocumentTypeCode();
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
     * Gets the organizationCode attribute.
     *
     * @return Returns the organizationCode
     */
    public String getOrganizationCode() {
        return organizationCode;
    }

    /**
     * Sets the organizationCode attribute.
     *
     * @param organizationCode The organizationCode to set.
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }


    /**
     * Gets the accountDelegateModelName attribute.
     *
     * @return Returns the accountDelegateModelName
     */
    public String getAccountDelegateModelName() {
        return accountDelegateModelName;
    }

    /**
     * Sets the accountDelegateModelName attribute.
     *
     * @param accountDelegateModelName The accountDelegateModelName to set.
     */
    public void setAccountDelegateModelName(String organizationRoutingModelName) {
        this.accountDelegateModelName = organizationRoutingModelName;
    }


    /**
     * Gets the accountDelegateUniversalId attribute.
     *
     * @return Returns the accountDelegateUniversalId
     */
    public String getAccountDelegateUniversalId() {
        return accountDelegateUniversalId;
    }

    /**
     * Sets the accountDelegateUniversalId attribute.
     *
     * @param accountDelegateUniversalId The accountDelegateUniversalId to set.
     */
    public void setAccountDelegateUniversalId(String accountDelegateUniversalId) {
        this.accountDelegateUniversalId = accountDelegateUniversalId;
    }


    /**
     * Gets the accountDelegate attribute.
     *
     * @return Returns the accountDelegate.
     */
    public Person getAccountDelegate() {
        accountDelegate = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(accountDelegateUniversalId, accountDelegate);
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

    /**
     * Gets the approvalFromThisAmount attribute.
     *
     * @return Returns the approvalFromThisAmount
     */
    public KualiDecimal getApprovalFromThisAmount() {
        return approvalFromThisAmount;
    }

    /**
     * Sets the approvalFromThisAmount attribute.
     *
     * @param approvalFromThisAmount The approvalFromThisAmount to set.
     */
    public void setApprovalFromThisAmount(KualiDecimal approvalFromThisAmount) {
        this.approvalFromThisAmount = approvalFromThisAmount;
    }


    /**
     * Gets the approvalToThisAmount attribute.
     *
     * @return Returns the approvalToThisAmount
     */
    public KualiDecimal getApprovalToThisAmount() {
        return approvalToThisAmount;
    }

    /**
     * Sets the approvalToThisAmount attribute.
     *
     * @param approvalToThisAmount The approvalToThisAmount to set.
     */
    public void setApprovalToThisAmount(KualiDecimal approvalToThisAmount) {
        this.approvalToThisAmount = approvalToThisAmount;
    }


    /**
     * Gets the accountDelegatePrimaryRoutingIndicator attribute.
     *
     * @return Returns the accountDelegatePrimaryRoutingIndicator
     */
    public boolean getAccountDelegatePrimaryRoutingIndicator() {
        return accountDelegatePrimaryRoutingIndicator;
    }

    /**
     * Sets the accountDelegatePrimaryRoutingIndicator attribute.
     *
     * @param accountDelegatePrimaryRoutingCode The accountDelegatePrimaryRoutingIndicator to set.
     */
    public void setAccountDelegatePrimaryRoutingIndicator(boolean accountDelegatePrimaryRoutingCode) {
        this.accountDelegatePrimaryRoutingIndicator = accountDelegatePrimaryRoutingCode;
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
     * Gets the active attribute.
     *
     * @return Returns the active.
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     *
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the chartOfAccounts attribute.
     *
     * @return Returns the chartOfAccounts
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute.
     *
     * @param chartOfAccounts The chartOfAccounts to set.
     * @deprecated
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("organizationCode", this.organizationCode);
        m.put("accountDelegateModelName", this.accountDelegateModelName);
        m.put("accountDelegateUniversalId", this.accountDelegateUniversalId);
        m.put("financialDocumentTypeCode", this.financialDocumentTypeCode);
        return m;
    }
}

