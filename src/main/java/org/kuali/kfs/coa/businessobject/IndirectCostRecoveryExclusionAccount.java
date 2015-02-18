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

import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class IndirectCostRecoveryExclusionAccount extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String chartOfAccountsCode;
    private String accountNumber;
    private String financialObjectChartOfAccountCode;
    private String financialObjectCode;
    private Chart chart;
    private Account account;
    private Chart financialObjectChartOfAccount;
    private ObjectCode objectCodeCurrent;
    private boolean active; 

    public IndirectCostRecoveryExclusionAccount() {
        super();
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
     * Gets the financialObjectChartOfAccountCode attribute.
     * 
     * @return Returns the financialObjectChartOfAccountCode
     */
    public String getFinancialObjectChartOfAccountCode() {
        return financialObjectChartOfAccountCode;
    }

    /**
     * Sets the financialObjectChartOfAccountCode attribute.
     * 
     * @param financialObjectChartOfAccountCode The financialObjectChartOfAccountCode to set.
     */
    public void setFinancialObjectChartOfAccountCode(String financialObjectChartOfAccountCode) {
        this.financialObjectChartOfAccountCode = financialObjectChartOfAccountCode;
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
     * Gets the chart attribute.
     * 
     * @return Returns the chart
     */
    public Chart getChart() {
        return chart;
    }

    /**
     * Sets the chart attribute.
     * 
     * @param chart The chart to set.
     * @deprecated
     */
    public void setChart(Chart chart) {
        this.chart = chart;
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
     * Gets the financialObjectChartOfAccount attribute.
     * 
     * @return Returns the financialObjectChartOfAccount
     */
    public Chart getFinancialObjectChartOfAccount() {
        return financialObjectChartOfAccount;
    }

    /**
     * Sets the financialObjectChartOfAccount attribute.
     * 
     * @param financialObjectChartOfAccount The financialObjectChartOfAccount to set.
     * @deprecated
     */
    public void setFinancialObjectChartOfAccount(Chart financialObjectChartOfAccount) {
        this.financialObjectChartOfAccount = financialObjectChartOfAccount;
    }

    /**
     * @return Returns the objectCode.
     */
    public ObjectCode getObjectCodeCurrent() {
        return objectCodeCurrent;
    }

    /**
     * @param objectCode The objectCode to set.
     * @deprecated
     */
    public void setObjectCodeCurrent(ObjectCode objectCodeCurrent) {
        this.objectCodeCurrent = objectCodeCurrent;
    }
    
    /**
     * @return Returns whether the objectCode is active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active Set if the record is active.
     */
     
    public void setActive(boolean active) {
        this.active = active;
    }


    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);
        m.put("financialObjectChartOfAccountCode", this.financialObjectChartOfAccountCode);
        m.put("financialObjectCode", this.financialObjectCode);
        return m;
    }


}
