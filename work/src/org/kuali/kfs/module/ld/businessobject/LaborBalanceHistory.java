/*
 * Copyright 2006-2009 The Kuali Foundation
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
