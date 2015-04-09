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

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * AccountBalance BO for Balancing process. I.e. a shadow representation.
 */
public class AccountBalanceHistory extends AccountBalance {
    
    /**
     * Constructs a AccountBalanceHistory.java.
     */
    public AccountBalanceHistory() {
        super();
        this.setCurrentBudgetLineBalanceAmount(KualiDecimal.ZERO);
        this.setAccountLineActualsBalanceAmount(KualiDecimal.ZERO);
        this.setAccountLineEncumbranceBalanceAmount(KualiDecimal.ZERO);
    }

    /**
     * Constructs a BalanceHistory.java.
     * 
     * @param transaction
     */
    public AccountBalanceHistory(OriginEntryInformation originEntry) {
        this();
        this.setChartOfAccountsCode(originEntry.getChartOfAccountsCode());
        this.setAccountNumber(originEntry.getAccountNumber());
        this.setObjectCode(originEntry.getFinancialObjectCode());
        this.setSubObjectCode(originEntry.getFinancialSubObjectCode());
        this.setUniversityFiscalYear(originEntry.getUniversityFiscalYear());
        this.setSubAccountNumber(originEntry.getSubAccountNumber());
    }
    
    /**
     * Updates amount if the object already existed
     * @param originEntry representing the update details
     */
    public boolean addAmount(OriginEntryFull originEntryFull) {
        if (originEntryFull.getFinancialBalanceTypeCode().equals(originEntryFull.getOption().getBudgetCheckingBalanceTypeCd())) {
            this.setCurrentBudgetLineBalanceAmount(this.getCurrentBudgetLineBalanceAmount().add(originEntryFull.getTransactionLedgerEntryAmount()));
        }
        else if (originEntryFull.getFinancialBalanceTypeCode().equals(originEntryFull.getOption().getActualFinancialBalanceTypeCd())) {
            if (originEntryFull.getObjectType().getFinObjectTypeDebitcreditCd().equals(originEntryFull.getTransactionDebitCreditCode()) || ((!originEntryFull.getBalanceType().isFinancialOffsetGenerationIndicator()) && KFSConstants.GL_BUDGET_CODE.equals(originEntryFull.getTransactionDebitCreditCode()))) {
                this.setAccountLineActualsBalanceAmount(this.getAccountLineActualsBalanceAmount().add(originEntryFull.getTransactionLedgerEntryAmount()));
            }
            else {
                this.setAccountLineActualsBalanceAmount(this.getAccountLineActualsBalanceAmount().subtract(originEntryFull.getTransactionLedgerEntryAmount()));
            }
        }
        else if (originEntryFull.getFinancialBalanceTypeCode().equals(originEntryFull.getOption().getExtrnlEncumFinBalanceTypCd()) || originEntryFull.getFinancialBalanceTypeCode().equals(originEntryFull.getOption().getIntrnlEncumFinBalanceTypCd()) || originEntryFull.getFinancialBalanceTypeCode().equals(originEntryFull.getOption().getPreencumbranceFinBalTypeCd()) || originEntryFull.getFinancialBalanceTypeCode().equals(originEntryFull.getOption().getCostShareEncumbranceBalanceTypeCd())) {
            if (originEntryFull.getObjectType().getFinObjectTypeDebitcreditCd().equals(originEntryFull.getTransactionDebitCreditCode()) || ((!originEntryFull.getBalanceType().isFinancialOffsetGenerationIndicator()) && KFSConstants.GL_BUDGET_CODE.equals(originEntryFull.getTransactionDebitCreditCode()))) {
                this.setAccountLineEncumbranceBalanceAmount(this.getAccountLineEncumbranceBalanceAmount().add(originEntryFull.getTransactionLedgerEntryAmount()));
            }
            else {
                this.setAccountLineEncumbranceBalanceAmount(this.getAccountLineEncumbranceBalanceAmount().subtract(originEntryFull.getTransactionLedgerEntryAmount()));
            }
        }
        else {
            return false;
        }
        return true;
    }
    
    /**
     * Compare amounts
     * @param accountBalance
     */
    public boolean compareAmounts(AccountBalance accountBalance) {
        if (ObjectUtils.isNotNull(accountBalance)
                && accountBalance.getCurrentBudgetLineBalanceAmount().equals(this.getCurrentBudgetLineBalanceAmount())
                && accountBalance.getAccountLineActualsBalanceAmount().equals(this.getAccountLineActualsBalanceAmount())
                && accountBalance.getAccountLineEncumbranceBalanceAmount().equals(this.getAccountLineEncumbranceBalanceAmount())) {
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
     * @see org.kuali.kfs.gl.businessobject.Balance#getTimestamp()
     */
    @Override
    public void setTimestamp(Date timestamp) {
        throw new UnsupportedOperationException();
    }
}
