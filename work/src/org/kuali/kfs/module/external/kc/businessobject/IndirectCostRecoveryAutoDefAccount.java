/*
 * Copyright 2011 The Kuali Foundation
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
