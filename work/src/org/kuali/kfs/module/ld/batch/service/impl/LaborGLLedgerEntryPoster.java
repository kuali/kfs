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
package org.kuali.module.labor.batch.poster.impl;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.util.ObjectUtil;
import org.kuali.module.gl.batch.poster.PostTransaction;
import org.kuali.module.gl.bo.Entry;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.bo.LaborGeneralLedgerEntry;
import org.kuali.module.labor.service.LaborGeneralLedgerEntryService;
import org.kuali.module.labor.util.DebitCreditUtil;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is used to post a transaction into labor GL entry table
 */
@Transactional
public class LaborGLLedgerEntryPoster implements PostTransaction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborGLLedgerEntryPoster.class);
    private LaborGeneralLedgerEntryService laborGeneralLedgerEntryService;

    /**
     * @see org.kuali.module.gl.batch.poster.PostTransaction#post(org.kuali.module.gl.bo.Transaction, int, java.util.Date)
     */
    public String post(Transaction transaction, int mode, java.util.Date postDate) {
        String operationType = KFSConstants.OperationType.INSERT;
        LaborGeneralLedgerEntry laborGeneralLedgerEntry = new LaborGeneralLedgerEntry();
        ObjectUtil.buildObject(laborGeneralLedgerEntry, transaction);

        laborGeneralLedgerEntry.setTransactionDate(new Date(postDate.getTime()));

        laborGeneralLedgerEntry.setTransactionDebitCreditCode(this.getDebitCreditCode(transaction));
        laborGeneralLedgerEntry.setTransactionLedgerEntryAmount(this.getTransactionAmount(transaction));

        laborGeneralLedgerEntry.setTransactionLedgerEntryDescription(getTransactionDescription(transaction));

        laborGeneralLedgerEntry.setTransactionEncumbranceUpdateCode(this.getEncumbranceUpdateCode(transaction));

        Integer sequenceNumber = laborGeneralLedgerEntryService.getMaxSequenceNumber(laborGeneralLedgerEntry) + 1;
        laborGeneralLedgerEntry.setTransactionLedgerEntrySequenceNumber(sequenceNumber);

        laborGeneralLedgerEntryService.save(laborGeneralLedgerEntry);
        
        return operationType;
    }

    /**
     * @return the debit credit code
     */
    private String getDebitCreditCode(Transaction transaction) {
        KualiDecimal transactionAmount = transaction.getTransactionLedgerEntryAmount();
        return DebitCreditUtil.getDebitCreditCode(transactionAmount, false);
    }

    /**
     * @return the transaction amount
     */
    private KualiDecimal getTransactionAmount(Transaction transaction) {
        KualiDecimal transactionAmount = transaction.getTransactionLedgerEntryAmount();
        return transactionAmount.abs();
    }

    /**
     * @return the encumbrance update code
     */
    private String getEncumbranceUpdateCode(Transaction transaction) {
        String encumbranceUpdateCode = transaction.getTransactionEncumbranceUpdateCode();
        if (KFSConstants.ENCUMB_UPDT_DOCUMENT_CD.equals(encumbranceUpdateCode) || KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD.equals(encumbranceUpdateCode)) {
            return encumbranceUpdateCode;
        }
        return null;
    }

    /**
     * @return the transaction description
     */
    private String getTransactionDescription(Transaction transaction) {
        String documentTypeCode = transaction.getFinancialDocumentTypeCode();
        String description = getDescriptionMap().get(documentTypeCode);
        description = StringUtils.isNotEmpty(description) ? description : transaction.getTransactionLedgerEntryDescription();

        // make sure the length of the description cannot excess the specified maximum
        int transactionDescriptionMaxLength = SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(Entry.class, KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_DESC).intValue();
        if (StringUtils.isNotEmpty(description) && description.length() > transactionDescriptionMaxLength) {
            description = StringUtils.left(description, transactionDescriptionMaxLength);
        }

        return description;
    }

    /**
     * @return the description dictionary that can be used to look up approperite description
     */
    public static Map<String, String> getDescriptionMap() {
        Map<String, String> descriptionMap = new HashMap<String, String>();

        descriptionMap.put(LaborConstants.PayrollDocumentTypeCode.NORMAL_PAY, "NORMAL PAYROLL ACTIVITY");
        descriptionMap.put(LaborConstants.PayrollDocumentTypeCode.ACCRUALS_REVERSAL, "PAYROLL ACCRUAL REVERSAL");
        descriptionMap.put(LaborConstants.PayrollDocumentTypeCode.ACCRUALS, "PAYROLL ACCRUALS");
        descriptionMap.put(LaborConstants.PayrollDocumentTypeCode.CHECK_CANCELLATION, "PAYROLL CHECK CANCELLATIONS");
        descriptionMap.put(LaborConstants.PayrollDocumentTypeCode.ENCUMBRANCE, "PAYROLL ENCUMBRANCES");
        descriptionMap.put(LaborConstants.PayrollDocumentTypeCode.EXPENSE_TRANSFER_ET, "PAYROLL EXPENSE TRANSFERS");
        descriptionMap.put(LaborConstants.PayrollDocumentTypeCode.EXPENSE_TRANSFER_SACH, "PAYROLL EXPENSE TRANSFERS");
        descriptionMap.put(LaborConstants.PayrollDocumentTypeCode.HAND_DRAWN_CHECK, "PAYROLL HAND DRAWN CHECK PAYMENTS");
        descriptionMap.put(LaborConstants.PayrollDocumentTypeCode.OVERPAYMENT, "PAYROLL OVERPAYMENT COLLECTIONS");
        descriptionMap.put(LaborConstants.PayrollDocumentTypeCode.RETROACTIVE_ADJUSTMENT, "PAYROLL RETROACTIVE ADJUSTMENTS");

        return descriptionMap;
    }

    /**
     * @see org.kuali.module.gl.batch.poster.PostTransaction#getDestinationName()
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
