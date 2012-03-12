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

package org.kuali.kfs.module.purap.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Negative Payment Request Approval Limit Business Object. Maintenance document for setting limits for the auto-approve PREQ batch
 * job.
 */
public class NegativePaymentRequestApprovalLimit extends PersistableBusinessObjectBase implements MutableInactivatable{

    private Integer negativePaymentRequestApprovalLimitIdentifier;
    private String chartOfAccountsCode;
    private String organizationCode;
    private String accountNumber;
    private KualiDecimal negativePaymentRequestApprovalLimitAmount;
    private boolean active;
    private Chart chartOfAccounts;
    private Account account;
    private Organization organization;

    /**
     * Default constructor.
     */
    public NegativePaymentRequestApprovalLimit() {

    }

    public Account getAccount() {        
        return account;
    }

    /**
     * @deprecated
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * @deprecated
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    public KualiDecimal getNegativePaymentRequestApprovalLimitAmount() {
        return negativePaymentRequestApprovalLimitAmount;
    }

    public void setNegativePaymentRequestApprovalLimitAmount(KualiDecimal negativePaymentRequestApprovalLimitAmount) {
        this.negativePaymentRequestApprovalLimitAmount = negativePaymentRequestApprovalLimitAmount;
    }

    public Integer getNegativePaymentRequestApprovalLimitIdentifier() {
        return negativePaymentRequestApprovalLimitIdentifier;
    }

    public void setNegativePaymentRequestApprovalLimitIdentifier(Integer negativePaymentRequestApprovalLimitIdentifier) {
        this.negativePaymentRequestApprovalLimitIdentifier = negativePaymentRequestApprovalLimitIdentifier;
    }

    public Organization getOrganization() {
        return organization;
    }

    /**
     * @deprecated
     */
    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.negativePaymentRequestApprovalLimitIdentifier != null) {
            m.put("negativePaymentRequestApprovalLimitIdentifier", this.negativePaymentRequestApprovalLimitIdentifier.toString());
        }
        return m;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
