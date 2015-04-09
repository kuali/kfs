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

package org.kuali.kfs.module.external.kc.businessobject;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.springframework.beans.BeanUtils;

/**
 * IndrectCostRecoveryAccount
 */
public class IndirectCostRecoveryAutoDefAccount extends PersistableBusinessObjectBase implements MutableInactivatable{
    private static Logger LOG = Logger.getLogger(IndirectCostRecoveryAutoDefAccount.class);

    private Integer indirectCostRecoveryAccountGeneratedIdentifier;
    
    //foreign keys to Account
    private String chartOfAccountsCode;
    private String accountNumber;
    
    private Integer accountDefaultId;
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
    public IndirectCostRecoveryAutoDefAccount() {
        active = true;
    }
    
    public IndirectCostRecoveryAutoDefAccount(IndirectCostRecoveryAutoDefAccount icr) {
        BeanUtils.copyProperties(this, icr);
    }
    
    /**
     * static instantiate an ICRAccount from an ICRAccount
     *
     * @param icrAccount
     * @return
     */
    public static IndirectCostRecoveryAutoDefAccount copyICRAccount(IndirectCostRecoveryAutoDefAccount icrAccount) {
        IndirectCostRecoveryAutoDefAccount icr = new IndirectCostRecoveryAutoDefAccount();
        icr.setAccountLinePercent(icrAccount.getAccountLinePercent());
        icr.setIndirectCostRecoveryFinCoaCode(icrAccount.getIndirectCostRecoveryFinCoaCode());
        icr.setIndirectCostRecoveryAccountNumber(icrAccount.getIndirectCostRecoveryAccountNumber());
        icr.setActive(icrAccount.isActive());
        return icr;
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

  
    
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        if (this.indirectCostRecoveryAccountGeneratedIdentifier != null) {
            m.put("indirectCostRecoveryAccountGeneratedIdentifier", this.indirectCostRecoveryAccountGeneratedIdentifier.toString());
        }
        return m;
    }

    /**
     * 
     */
    public Integer getAccountDefaultId() {
        return accountDefaultId;
    }

    /**
     * 
     */
    public void setAccountDefaultId(Integer accountDefaultId) {
        this.accountDefaultId = accountDefaultId;
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
    

}
