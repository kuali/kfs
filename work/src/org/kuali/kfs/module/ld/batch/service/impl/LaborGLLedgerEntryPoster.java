/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ld.batch.service.impl;

import java.sql.Date;

import org.kuali.kfs.gl.batch.service.PostTransaction;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.businessobject.LaborGeneralLedgerEntry;
import org.kuali.kfs.module.ld.service.LaborGeneralLedgerEntryService;
import org.kuali.kfs.module.ld.service.LaborTransactionDescriptionService;
import org.kuali.kfs.module.ld.util.DebitCreditUtil;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.rice.kns.util.KualiDecimal;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is used to post a transaction into labor GL entry table
 */
@Transactional
public class LaborGLLedgerEntryPoster implements PostTransaction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborGLLedgerEntryPoster.class);
    
    private LaborGeneralLedgerEntryService laborGeneralLedgerEntryService;
    private LaborTransactionDescriptionService laborTransactionDescriptionService;

    /**
     * @see org.kuali.kfs.gl.batch.service.PostTransaction#post(org.kuali.kfs.gl.businessobject.Transaction, int, java.util.Date)
     */
    public String post(Transaction transaction, int mode, java.util.Date postDate) {
        String operationType = KFSConstants.OperationType.INSERT;
        LaborGeneralLedgerEntry laborGeneralLedgerEntry = new LaborGeneralLedgerEntry();
        ObjectUtil.buildObject(laborGeneralLedgerEntry, transaction);

        laborGeneralLedgerEntry.setTransactionDate(new Date(postDate.getTime()));

        laborGeneralLedgerEntry.setTransactionDebitCreditCode(this.getDebitCreditCode(transaction));
        laborGeneralLedgerEntry.setTransactionLedgerEntryAmount(this.getTransactionAmount(transaction));

        laborGeneralLedgerEntry.setTransactionLedgerEntryDescription(laborTransactionDescriptionService.getTransactionDescription(transaction));

        laborGeneralLedgerEntry.setTransactionEncumbranceUpdateCode(this.getEncumbranceUpdateCode(transaction));

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

    /**
     * Sets the laborTransactionDescriptionService attribute value.
     * @param laborTransactionDescriptionService The laborTransactionDescriptionService to set.
     */
    public void setLaborTransactionDescriptionService(LaborTransactionDescriptionService laborTransactionDescriptionService) {
        this.laborTransactionDescriptionService = laborTransactionDescriptionService;
    }
}
