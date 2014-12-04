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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.PosterEntriesStep;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Encumbrance BO for Balancing process. I.e. a shadow representation.
*/
public class EncumbranceHistory extends Encumbrance {

    public EncumbranceHistory() {
        super();
        this.setAccountLineEncumbranceAmount(KualiDecimal.ZERO);
        this.setAccountLineEncumbranceClosedAmount(KualiDecimal.ZERO);
    }

    /**
     * Constructs a BalanceHistory.java.
     * 
     * @param transaction
     */
    public EncumbranceHistory(OriginEntryInformation originEntry) {
        this();
        this.setUniversityFiscalYear(originEntry.getUniversityFiscalYear());
        this.setChartOfAccountsCode(originEntry.getChartOfAccountsCode());
        this.setAccountNumber(originEntry.getAccountNumber());
        this.setSubAccountNumber(originEntry.getSubAccountNumber());
        this.setObjectCode(originEntry.getFinancialObjectCode());
        this.setSubObjectCode(originEntry.getFinancialSubObjectCode());
        this.setBalanceTypeCode(originEntry.getFinancialBalanceTypeCode());
        this.setDocumentTypeCode(originEntry.getFinancialDocumentTypeCode());
        this.setOriginCode(originEntry.getFinancialSystemOriginationCode());
        this.setDocumentNumber(originEntry.getDocumentNumber());
    }
    
    /**
     * Updates amount if the object already existed
     * @param originEntry representing the update details
     */
    public void addAmount(OriginEntryInformation originEntry) {
        //KFSMI-1571 - check parameter encumbranceOpenAmountOeverridingDocTypes
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        Collection<String> encumbranceOpenAmountOeverridingDocTypes = new ArrayList<String>( parameterService.getParameterValuesAsString(PosterEntriesStep.class, GeneralLedgerConstants.PosterService.ENCUMBRANCE_OPEN_AMOUNT_OVERRIDING_DOCUMENT_TYPES) );
        
        if (KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD.equals(originEntry.getTransactionEncumbranceUpdateCode()) 
                && !encumbranceOpenAmountOeverridingDocTypes.contains( originEntry.getFinancialDocumentTypeCode())) {
            // If using referring doc number, add or subtract transaction amount from
            // encumbrance closed amount
            if (KFSConstants.GL_DEBIT_CODE.equals(originEntry.getTransactionDebitCreditCode())) {
                this.setAccountLineEncumbranceClosedAmount(this.getAccountLineEncumbranceClosedAmount().subtract(originEntry.getTransactionLedgerEntryAmount()));
            }
            else {
                this.setAccountLineEncumbranceClosedAmount(this.getAccountLineEncumbranceClosedAmount().add(originEntry.getTransactionLedgerEntryAmount()));
            }
        }
        else {
            // If not using referring doc number, add or subtract transaction amount from
            // encumbrance amount
            if (KFSConstants.GL_DEBIT_CODE.equals(originEntry.getTransactionDebitCreditCode()) || KFSConstants.GL_BUDGET_CODE.equals(originEntry.getTransactionDebitCreditCode())) {
                this.setAccountLineEncumbranceAmount(this.getAccountLineEncumbranceAmount().add(originEntry.getTransactionLedgerEntryAmount()));
            }
            else {
                this.setAccountLineEncumbranceAmount(this.getAccountLineEncumbranceAmount().subtract(originEntry.getTransactionLedgerEntryAmount()));
            }
        }
    }
    
    /**
     * Compare amounts
     * @param accountBalance
     */
    public boolean compareAmounts(Encumbrance encumbrance) {
        if (ObjectUtils.isNotNull(encumbrance)
                && encumbrance.getAccountLineEncumbranceAmount().equals(this.getAccountLineEncumbranceAmount())
                && encumbrance.getAccountLineEncumbranceClosedAmount().equals(this.getAccountLineEncumbranceClosedAmount())) {
            return true;
        }
        
        return false;
    }
    
    /**
     * History does not track this field.
     * @see org.kuali.kfs.gl.businessobject.Balance#getTimestamp()
     */
    @Override
    public String getAccountLineEncumbrancePurgeCode() {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.gl.businessobject.Balance#getTimestamp()
     */
    @Override
    public void setAccountLineEncumbrancePurgeCode(String accountLineEncumbrancePurgeCode) {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.gl.businessobject.Balance#getTimestamp()
     */
    @Override
    public Timestamp getTimestamp() {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.gl.businessobject.Balance#getTimestamp()
     */
    @Override
    public void setTimestamp(Timestamp timestamp) {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.gl.businessobject.Balance#getTimestamp()
     */
    @Override
    public Date getTransactionEncumbranceDate() {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.gl.businessobject.Balance#getTimestamp()
     */
    @Override
    public void setTransactionEncumbranceDate(Date transactionEncumbranceDate) {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.gl.businessobject.Balance#getTimestamp()
     */
    @Override
    public String getTransactionEncumbranceDescription() {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.gl.businessobject.Balance#getTimestamp()
     */
    @Override
    public void setTransactionEncumbranceDescription(String transactionEncumbranceDescription) {
        throw new UnsupportedOperationException();
    }
}
