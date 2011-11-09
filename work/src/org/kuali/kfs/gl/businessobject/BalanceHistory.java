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
