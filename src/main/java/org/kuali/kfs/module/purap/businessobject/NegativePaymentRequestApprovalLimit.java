/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
