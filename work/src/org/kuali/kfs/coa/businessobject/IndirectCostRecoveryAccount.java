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

package org.kuali.kfs.coa.businessobject;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.log4j.Logger;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.TypedArrayList;
import org.springframework.beans.BeanUtils;

/**
 * IndrectCostRecoveryAccount
 */
public class IndirectCostRecoveryAccount extends PersistableBusinessObjectBase implements Inactivateable{
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
        BeanUtils.copyProperties(this, icr);
    }
    
    /**
     * static instantiate an ICRAccount from an ICRAccount
     *
     * @param icrAccount
     * @return
     */
    public static IndirectCostRecoveryAccount copyICRAccount(IndirectCostRecoveryAccount icrAccount) {
        IndirectCostRecoveryAccount icr = new IndirectCostRecoveryAccount();
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
    
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap<String, String> toStringMapper() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        if (this.indirectCostRecoveryAccountGeneratedIdentifier != null) {
            m.put("indirectCostRecoveryAccountGeneratedIdentifier", this.indirectCostRecoveryAccountGeneratedIdentifier.toString());
        }
        return m;
    }

}
