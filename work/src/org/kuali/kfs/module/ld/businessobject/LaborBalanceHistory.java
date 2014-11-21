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

package org.kuali.kfs.module.ld.businessobject;

import java.sql.Date;
import java.sql.Timestamp;

import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.LedgerBalanceHistory;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Labor business object for LedgerBalanceHistory.
 */
public class LaborBalanceHistory extends LedgerBalance implements LedgerBalanceHistory {
    /**
     * Default constructor.
     */
    public LaborBalanceHistory() {
        super();
    }

    /**
     * Constructs a LedgerBalance.java.
     * 
     * @param transaction
     */
    public LaborBalanceHistory(LaborOriginEntry laborOriginEntry) {
        this();
        this.setChartOfAccountsCode(laborOriginEntry.getChartOfAccountsCode());
        this.setAccountNumber(laborOriginEntry.getAccountNumber());
        this.setFinancialBalanceTypeCode(laborOriginEntry.getFinancialBalanceTypeCode());
        this.setEmplid(laborOriginEntry.getEmplid());
        this.setFinancialObjectCode(laborOriginEntry.getFinancialObjectCode());
        this.setFinancialObjectTypeCode(laborOriginEntry.getFinancialObjectTypeCode());
        this.setFinancialSubObjectCode(laborOriginEntry.getFinancialSubObjectCode());
        this.setPositionNumber(laborOriginEntry.getPositionNumber());
        this.setUniversityFiscalYear(laborOriginEntry.getUniversityFiscalYear());
        this.setSubAccountNumber(laborOriginEntry.getSubAccountNumber());
    }
    
    /**
     * Compare amounts
     * 
     * @param balance
     * @see org.kuali.kfs.gl.businessobject.Balance#addAmount(java.lang.String, org.kuali.rice.core.api.util.type.KualiDecimal)
     */
    public boolean compareAmounts(Balance balance) {
        if (!(balance instanceof LedgerBalance)) {
            throw new IllegalArgumentException("balance needs to be of type LedgerBalance");
        }
        
        LedgerBalance ledgerBalance = (LedgerBalance) balance;
        
        if (ObjectUtils.isNotNull(ledgerBalance)
                && ledgerBalance.getAccountLineAnnualBalanceAmount().equals(this.getAccountLineAnnualBalanceAmount())
                && ledgerBalance.getFinancialBeginningBalanceLineAmount().equals(this.getFinancialBeginningBalanceLineAmount())
                && ledgerBalance.getContractsGrantsBeginningBalanceAmount().equals(this.getContractsGrantsBeginningBalanceAmount())
                && ledgerBalance.getMonth1Amount().equals(this.getMonth1Amount())
                && ledgerBalance.getMonth2Amount().equals(this.getMonth2Amount())
                && ledgerBalance.getMonth3Amount().equals(this.getMonth3Amount())
                && ledgerBalance.getMonth4Amount().equals(this.getMonth4Amount())
                && ledgerBalance.getMonth5Amount().equals(this.getMonth5Amount())
                && ledgerBalance.getMonth6Amount().equals(this.getMonth6Amount())
                && ledgerBalance.getMonth7Amount().equals(this.getMonth7Amount())
                && ledgerBalance.getMonth8Amount().equals(this.getMonth8Amount())
                && ledgerBalance.getMonth9Amount().equals(this.getMonth9Amount())
                && ledgerBalance.getMonth10Amount().equals(this.getMonth10Amount())
                && ledgerBalance.getMonth11Amount().equals(this.getMonth11Amount())
                && ledgerBalance.getMonth12Amount().equals(this.getMonth12Amount())
                && ledgerBalance.getMonth13Amount().equals(this.getMonth13Amount())) {
            return true;
        }
        
        return false;
    }
    
    /**
     * It's called financialObjectCode
     * @see org.kuali.kfs.gl.businessobject.Balance#getObjectCode()
     */
    @Override
    public String getObjectCode() {
        return this.getFinancialObjectCode();
    }
    
    /**
     * It's called financialObjectCode
     * @see org.kuali.kfs.gl.businessobject.Balance#setObjectCode(java.lang.String)
     */
    @Override
    public void setObjectCode(String objectCode) {
        this.setFinancialObjectCode(objectCode);
    }

    /**
     * It's called financialSubObjectCode
     * @see org.kuali.kfs.gl.businessobject.Balance#getSubObjectCode()
     */
    @Override
    public String getSubObjectCode() {
        return this.getFinancialSubObjectCode();
    }

    /**
     * It's called financialSubObjectCode
     * @see org.kuali.kfs.gl.businessobject.Balance#setSubObjectCode(java.lang.String)
     */
    @Override
    public void setSubObjectCode(String subObjectCode) {
        this.setFinancialSubObjectCode(subObjectCode);
    }
    
    /**
     * It's called financialObjectTypeCode
     * @see org.kuali.kfs.gl.businessobject.Balance#getObjectTypeCode()
     */
    @Override
    public String getObjectTypeCode() {
        return this.getFinancialObjectTypeCode();
    }
    
    /**
     * It's called financialObjectTypeCode
     * @see org.kuali.kfs.gl.businessobject.Balance#setObjectTypeCode(java.lang.String)
     */
    @Override
    public void setObjectTypeCode(String objectTypeCode) {
        this.setFinancialObjectTypeCode(objectTypeCode);
    }
    
    /**
     * It's called financialBalanceTypeCode
     * @see org.kuali.kfs.module.ld.businessobject.LedgerBalance#getBalanceTypeCode()
     */
    @Override
    public String getBalanceTypeCode() {
        return this.getFinancialBalanceTypeCode();
    }

    /**
     * It's called financialBalanceTypeCode
     * @see org.kuali.kfs.module.ld.businessobject.LedgerBalance#setBalanceTypeCode(java.lang.String)
     */
    @Override
    public void setBalanceTypeCode(String balanceTypeCode) {
        this.setFinancialBalanceTypeCode(balanceTypeCode);
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
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerBalance#getTransactionDateTimeStamp()
     */
    @Override
    public Timestamp getTransactionDateTimeStamp() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerBalance#setTransactionDateTimeStamp(java.sql.Timestamp)
     */
    @Override
    public void setTransactionDateTimeStamp(Timestamp transactionDateTimeStamp) {
        throw new UnsupportedOperationException();
    }
}
