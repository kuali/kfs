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

package org.kuali.kfs.coa.businessobject;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.springframework.beans.BeanUtils;

/**
 * IndrectCostRecoveryAccount
 */
public class IndirectCostRecoveryAccount extends PersistableBusinessObjectBase implements MutableInactivatable{
    private static Logger LOG = Logger.getLogger(IndirectCostRecoveryAccount.class);

    private Integer indirectCostRecoveryAccountGeneratedIdentifier;

    //foreign keys to Account
    private String chartOfAccountsCode;
    private String accountNumber;

    private String indirectCostRecoveryFinCoaCode;
    private String indirectCostRecoveryAccountNumber;
    private BigDecimal accountLinePercent;
    private boolean active;

    //BO Reference
    private Account indirectCostRecoveryAccount;
    private Chart indirectCostRecoveryChartOfAccounts;

    /**
     * Default constructor.
     */
    public IndirectCostRecoveryAccount() {
    }


    public IndirectCostRecoveryAccount(IndirectCostRecoveryAccount icr) {
        BeanUtils.copyProperties(icr, this);
    }

    /**
     * static instantiate an ICRAccount from an ICRAccount
     *
     * @param icrAccount
     * @return
     */
    public static IndirectCostRecoveryAccount copyICRAccount(IndirectCostRecoveryAccount icrAccount) {
        return new IndirectCostRecoveryAccount(icrAccount);
    }

    public Integer getIndirectCostRecoveryAccountGeneratedIdentifier() {
        return indirectCostRecoveryAccountGeneratedIdentifier;
    }

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setIndirectCostRecoveryAccountGeneratedIdentifier(Integer indirectCostRecoveryAccountGeneratedIdentifier) {
        this.indirectCostRecoveryAccountGeneratedIdentifier = indirectCostRecoveryAccountGeneratedIdentifier;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getIndirectCostRecoveryFinCoaCode() {
        return indirectCostRecoveryFinCoaCode;
    }

    public String getIndirectCostRecoveryAccountNumber() {
        return indirectCostRecoveryAccountNumber;
    }

    public BigDecimal getAccountLinePercent() {
        return accountLinePercent;
    }

    public void setIndirectCostRecoveryFinCoaCode(String indirectCostRecoveryFinCoaCode) {
        this.indirectCostRecoveryFinCoaCode = indirectCostRecoveryFinCoaCode;
    }

    public void setIndirectCostRecoveryAccountNumber(String indirectCostRecoveryAccountNumber) {
        this.indirectCostRecoveryAccountNumber = indirectCostRecoveryAccountNumber;
    }

    public void setAccountLinePercent(BigDecimal accountLinePercent) {
        this.accountLinePercent = accountLinePercent;
    }

    public Account getIndirectCostRecoveryAccount() {
        return indirectCostRecoveryAccount;
    }

    /**
     * Sets the indirectCostRecoveryAccount attribute.
     *
     * @param account The account to set.
     * @deprecated Setter is required by OJB, but should not be used to modify this attribute. This attribute is set on the initial
     *             creation of the object and should not be changed.
     */
    @Deprecated
    public void setIndirectCostRecoveryAccount(Account indirectCostRecoveryAccount) {
        this.indirectCostRecoveryAccount = indirectCostRecoveryAccount;
    }

    /***
     * @return
     */
    public Chart getIndirectCostRecoveryChartOfAccounts() {
        return indirectCostRecoveryChartOfAccounts;
    }

    /**
     * Sets the indirectCostRcvyChartOfAccounts attribute.
     *
     * @param indirectCostRcvyChartOfAccounts The chartOfAccounts to set.
     * @deprecated Setter is required by OJB, but should not be used to modify this attribute. This attribute is set on the initial
     *             creation of the object and should not be changed.
     */
    @Deprecated
    public void setIndirectCostRecoveryChartOfAccounts(Chart indirectCostRecoveryChartOfAccounts) {
        this.indirectCostRecoveryChartOfAccounts = indirectCostRecoveryChartOfAccounts;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

}
