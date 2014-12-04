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

package org.kuali.kfs.gl.businessobject;

import java.sql.Date;

import org.kuali.rice.krad.util.ObjectUtils;

/**
 * General Ledger business object for BalanceHistory.
 */
public class BalanceHistory extends Balance implements LedgerBalanceHistory {
    /**
     * Default constructor.
     */
    public BalanceHistory() {
        super();
    }

    /**
     * Constructs a BalanceHistory.java.
     * 
     * @param transaction
     */
    public BalanceHistory(OriginEntryInformation originEntry) {
        this();
        this.setChartOfAccountsCode(originEntry.getChartOfAccountsCode());
        this.setAccountNumber(originEntry.getAccountNumber());
        this.setBalanceTypeCode(originEntry.getFinancialBalanceTypeCode());
        this.setObjectCode(originEntry.getFinancialObjectCode());
        this.setObjectTypeCode(originEntry.getFinancialObjectTypeCode());
        this.setSubObjectCode(originEntry.getFinancialSubObjectCode());
        this.setUniversityFiscalYear(originEntry.getUniversityFiscalYear());
        this.setSubAccountNumber(originEntry.getSubAccountNumber());
    }
    
    /**
     * Compare amounts
     * 
     * @param balance
     * @see org.kuali.kfs.gl.businessobject.Balance#addAmount(java.lang.String, org.kuali.rice.core.api.util.type.KualiDecimal)
     */
    public boolean compareAmounts(Balance balance) {
        if (ObjectUtils.isNotNull(balance)
                && balance.getAccountLineAnnualBalanceAmount().equals(this.getAccountLineAnnualBalanceAmount())
                && balance.getBeginningBalanceLineAmount().equals(this.getBeginningBalanceLineAmount())
                && balance.getContractsGrantsBeginningBalanceAmount().equals(this.getContractsGrantsBeginningBalanceAmount())
                && balance.getMonth1Amount().equals(this.getMonth1Amount())
                && balance.getMonth2Amount().equals(this.getMonth2Amount())
                && balance.getMonth3Amount().equals(this.getMonth3Amount())
                && balance.getMonth4Amount().equals(this.getMonth4Amount())
                && balance.getMonth5Amount().equals(this.getMonth5Amount())
                && balance.getMonth6Amount().equals(this.getMonth6Amount())
                && balance.getMonth7Amount().equals(this.getMonth7Amount())
                && balance.getMonth8Amount().equals(this.getMonth8Amount())
                && balance.getMonth9Amount().equals(this.getMonth9Amount())
                && balance.getMonth10Amount().equals(this.getMonth10Amount())
                && balance.getMonth11Amount().equals(this.getMonth11Amount())
                && balance.getMonth12Amount().equals(this.getMonth12Amount())
                && balance.getMonth13Amount().equals(this.getMonth13Amount())) {
            return true;
        }
        
        return false;
    }
    
    /**
     * History does not track this field.
     * @see org.kuali.kfs.gl.businessobject.Balance#getTimestamp()
     */
    @Override
    public Date getTimestamp() {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.gl.businessobject.Balance#setTimestamp(java.sql.Date)
     */
    @Override
    public void setTimestamp(Date timestamp) {
        throw new UnsupportedOperationException();
    }
    

    /**
     * Because financialBalanceTypeCode is named differently in Labor and GL.
     * 
     * @return Returns the balanceTypeCode.
     */
    public String getFinancialBalanceTypeCode() {
        return getBalanceTypeCode();
    }

    /**
     * Because financialBalanceTypeCode is named differently in Labor and GL.
     * 
     * @param financialBalanceTypeCode The balanceTypeCode to set.
     */
    public void setFinancialBalanceTypeCode(String financialBalanceTypeCode) {
        this.setBalanceTypeCode(financialBalanceTypeCode);
    }
    
    /**
     * Because financialBalanceTypeCode is named differently in Labor and GL.
     * 
     * @return Returns the objectCode.
     */
    public String getFinancialObjectCode() {
        return getObjectCode();
    }

    /**
     * Because financialBalanceTypeCode is named differently in Labor and GL.
     * 
     * @param financialObjectCode The objectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.setObjectCode(financialObjectCode);
    }
    
    /**
     * Because financialBalanceTypeCode is named differently in Labor and GL.
     * 
     * @return Returns the objectTypeCode.
     */
    public String getFinancialObjectTypeCode() {
        return getObjectTypeCode();
    }

    /**
     * Because financialBalanceTypeCode is named differently in Labor and GL.
     * 
     * @param financialObjectTypeCode The objectTypeCode to set.
     */
    public void setFinancialObjectTypeCode(String financialObjectTypeCode) {
        this.setObjectTypeCode(financialObjectTypeCode);
    }
    
    /**
     * Because financialBalanceTypeCode is named differently in Labor and GL.
     * 
     * @return Returns the subObjectCode.
     */
    public String getFinancialSubObjectCode() {
        return getSubObjectCode();
    }

    /**
     * Because financialBalanceTypeCode is named differently in Labor and GL.
     * 
     * @param financialSubObjectCode The subObjectCode to set.
     */
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.setSubObjectCode(financialSubObjectCode);
    }
}
