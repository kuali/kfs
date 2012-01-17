/*
 * Copyright 2005-2009 The Kuali Foundation
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
