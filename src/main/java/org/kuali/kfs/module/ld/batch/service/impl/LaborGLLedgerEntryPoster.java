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
package org.kuali.kfs.module.ld.batch.service.impl;

import java.sql.Date;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.gl.batch.service.PostTransaction;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.batch.service.LaborAccountingCycleCachingService;
import org.kuali.kfs.module.ld.businessobject.LaborGeneralLedgerEntry;
import org.kuali.kfs.module.ld.service.LaborGeneralLedgerEntryService;
import org.kuali.kfs.module.ld.util.DebitCreditUtil;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is used to post a transaction into labor GL entry table
 */
@Transactional
public class LaborGLLedgerEntryPoster implements PostTransaction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborGLLedgerEntryPoster.class);
    
    private LaborGeneralLedgerEntryService laborGeneralLedgerEntryService;
    private LaborAccountingCycleCachingService laborAccountingCycleCachingService;

    /**
     * @see org.kuali.kfs.gl.batch.service.PostTransaction#post(org.kuali.kfs.gl.businessobject.Transaction, int, java.util.Date)
     */
    public String post(Transaction transaction, int mode, java.util.Date postDate, ReportWriterService posterReportWriterService) {
        String operationType = KFSConstants.OperationType.INSERT;
        LaborGeneralLedgerEntry laborGeneralLedgerEntry = new LaborGeneralLedgerEntry();
        ObjectUtil.buildObject(laborGeneralLedgerEntry, transaction);

        laborGeneralLedgerEntry.setTransactionDate(new Date(postDate.getTime()));

        BalanceType balanceType = getBalanceType(transaction.getFinancialBalanceTypeCode());
        if (balanceType.isFinancialOffsetGenerationIndicator()){
            laborGeneralLedgerEntry.setTransactionDebitCreditCode(this.getDebitCreditCode(transaction));
        } else {
            laborGeneralLedgerEntry.setTransactionDebitCreditCode(" ");
        }
        laborGeneralLedgerEntry.setTransactionLedgerEntryAmount(this.getTransactionAmount(transaction));
        
        String encumbranceUpdateCode = this.getEncumbranceUpdateCode(transaction);
        if(StringUtils.isNotEmpty(encumbranceUpdateCode)) {
            laborGeneralLedgerEntry.setTransactionEncumbranceUpdateCode(encumbranceUpdateCode);
        }

        Integer sequenceNumber = laborGeneralLedgerEntryService.getMaxSequenceNumber(laborGeneralLedgerEntry) + 1;
        laborGeneralLedgerEntry.setTransactionLedgerEntrySequenceNumber(sequenceNumber);
        try {
            laborGeneralLedgerEntryService.save(laborGeneralLedgerEntry);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return operationType;
    }
    
    protected BalanceType getBalanceType(String balanceTypeCode) {
        return laborAccountingCycleCachingService.getBalanceType(balanceTypeCode);
    }
    
    public void setLaborAccountingCycleCachingService(LaborAccountingCycleCachingService laborAccountingCycleCachingService) {
        this.laborAccountingCycleCachingService = laborAccountingCycleCachingService;
    }

    /**
     * @return the debit credit code
     */
    protected String getDebitCreditCode(Transaction transaction) {
        KualiDecimal transactionAmount = transaction.getTransactionLedgerEntryAmount();
        return DebitCreditUtil.getDebitCreditCode(transactionAmount, false);
    }

    /**
     * @return the transaction amount
     */
    protected KualiDecimal getTransactionAmount(Transaction transaction) {
        KualiDecimal transactionAmount = transaction.getTransactionLedgerEntryAmount();
        return transactionAmount.abs();
    }

    /**
     * @return the encumbrance update code
     */
    protected String getEncumbranceUpdateCode(Transaction transaction) {
        String encumbranceUpdateCode = transaction.getTransactionEncumbranceUpdateCode();
        if (KFSConstants.ENCUMB_UPDT_DOCUMENT_CD.equals(encumbranceUpdateCode) || KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD.equals(encumbranceUpdateCode)) {
            return encumbranceUpdateCode;
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.PostTransaction#getDestinationName()
     */
    public String getDestinationName() {
        return LaborConstants.DestinationNames.LABOR_GL_ENTRY;
    }

    /**
     * Sets the laborGeneralLedgerEntryService attribute value.
     * 
     * @param laborGeneralLedgerEntryService The laborGeneralLedgerEntryService to set.
     */
    public void setLaborGeneralLedgerEntryService(LaborGeneralLedgerEntryService laborGeneralLedgerEntryService) {
        this.laborGeneralLedgerEntryService = laborGeneralLedgerEntryService;
    }
}
