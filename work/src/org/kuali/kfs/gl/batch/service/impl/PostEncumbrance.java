/*
 * Copyright 2005-2006 The Kuali Foundation
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
package org.kuali.kfs.gl.batch.service.impl;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.PosterEntriesStep;
import org.kuali.kfs.gl.batch.service.AccountingCycleCachingService;
import org.kuali.kfs.gl.batch.service.EncumbranceCalculator;
import org.kuali.kfs.gl.batch.service.PostTransaction;
import org.kuali.kfs.gl.businessobject.Encumbrance;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.PersistenceStructureService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This implementation of PostTransaction posts a transaction that could be an encumbrance
 */
@Transactional
public class PostEncumbrance implements PostTransaction, EncumbranceCalculator {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PostEncumbrance.class);

    private AccountingCycleCachingService accountingCycleCachingService;
    private DateTimeService dateTimeService;
    private PersistenceStructureService persistenceStructureService;
    private ParameterService parameterService;

    /**
     * Constructs a PostEncumbrance instance
     */
    public PostEncumbrance() {
        super();
    }

    /**
     * Called by the poster to post a transaction. The transaction might or might not be an encumbrance transaction.
     * 
     * @param t the transaction which is being posted
     * @param mode the mode the poster is currently running in
     * @param postDate the date this transaction should post to
     * @param posterReportWriterService the writer service where the poster is writing its report
     * @return the accomplished post type
     */
    public String post(Transaction t, int mode, Date postDate, ReportWriterService posterReportWriterService) {
        LOG.debug("post() started");

        String returnCode = GeneralLedgerConstants.UPDATE_CODE;

        // If the encumbrance update code is space or N, or the object type code is FB
        // we don't need to post an encumbrance
        if ((StringUtils.isBlank(t.getTransactionEncumbranceUpdateCode())) || " ".equals(t.getTransactionEncumbranceUpdateCode()) || KFSConstants.ENCUMB_UPDT_NO_ENCUMBRANCE_CD.equals(t.getTransactionEncumbranceUpdateCode()) || t.getOption().getFinObjectTypeFundBalanceCd().equals(t.getFinancialObjectTypeCode())) {
            LOG.debug("post() not posting non-encumbrance transaction");
            return "";
        }

        // Get the current encumbrance record if there is one
        Entry e = new Entry(t, null);
        if (KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD.equals(t.getTransactionEncumbranceUpdateCode())) {
            e.setDocumentNumber(t.getReferenceFinancialDocumentNumber());
            e.setFinancialSystemOriginationCode(t.getReferenceFinancialSystemOriginationCode());
            e.setFinancialDocumentTypeCode(t.getReferenceFinancialDocumentTypeCode());
        }
        
        Encumbrance enc = accountingCycleCachingService.getEncumbrance(e);
        if (enc == null) {
            // Build a new encumbrance record
            enc = new Encumbrance(e);

            returnCode = GeneralLedgerConstants.INSERT_CODE;
        }
        else {
            // Use the one retrieved
            if (enc.getTransactionEncumbranceDate() == null) {
                enc.setTransactionEncumbranceDate(t.getTransactionDate());
            }

            returnCode = GeneralLedgerConstants.UPDATE_CODE;
        }

        updateEncumbrance(t, enc);

        if (returnCode.equals(GeneralLedgerConstants.INSERT_CODE)) {
            accountingCycleCachingService.insertEncumbrance(enc);
        } else {
            accountingCycleCachingService.updateEncumbrance(enc);
        }

        return returnCode;
    }

    /**
     * Given a Collection of encumbrances, returns the encumbrance that would affected by the given transaction
     * 
     * @param encumbranceList a Collection of encumbrances
     * @param t the transaction to find the appropriate encumbrance for
     * @return the encumbrance found from the list, or, if not found, a newly created encumbrance
     * @see org.kuali.kfs.gl.batch.service.EncumbranceCalculator#findEncumbrance(java.util.Collection, org.kuali.kfs.gl.businessobject.Transaction)
     */
    public Encumbrance findEncumbrance(Collection encumbranceList, Transaction t) {

        // If it isn't an encumbrance transaction, skip it
        if ((!KFSConstants.ENCUMB_UPDT_DOCUMENT_CD.equals(t.getTransactionEncumbranceUpdateCode())) && (!KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD.equals(t.getTransactionEncumbranceUpdateCode()))) {
            return null;
        }

        // Try to find one that already exists
        for (Iterator iter = encumbranceList.iterator(); iter.hasNext();) {
            Encumbrance e = (Encumbrance) iter.next();

            if (KFSConstants.ENCUMB_UPDT_DOCUMENT_CD.equals(t.getTransactionEncumbranceUpdateCode()) && e.getUniversityFiscalYear().equals(t.getUniversityFiscalYear()) && e.getChartOfAccountsCode().equals(t.getChartOfAccountsCode()) && e.getAccountNumber().equals(t.getAccountNumber()) && e.getSubAccountNumber().equals(t.getSubAccountNumber()) && e.getObjectCode().equals(t.getFinancialObjectCode()) && e.getSubObjectCode().equals(t.getFinancialSubObjectCode()) && e.getBalanceTypeCode().equals(t.getFinancialBalanceTypeCode()) && e.getDocumentTypeCode().equals(t.getFinancialDocumentTypeCode()) && e.getOriginCode().equals(t.getFinancialSystemOriginationCode()) && e.getDocumentNumber().equals(t.getDocumentNumber())) {
                return e;
            }

            if (KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD.equals(t.getTransactionEncumbranceUpdateCode()) && e.getUniversityFiscalYear().equals(t.getUniversityFiscalYear()) && e.getChartOfAccountsCode().equals(t.getChartOfAccountsCode()) && e.getAccountNumber().equals(t.getAccountNumber()) && e.getSubAccountNumber().equals(t.getSubAccountNumber()) && e.getObjectCode().equals(t.getFinancialObjectCode()) && e.getSubObjectCode().equals(t.getFinancialSubObjectCode()) && e.getBalanceTypeCode().equals(t.getFinancialBalanceTypeCode()) && e.getDocumentTypeCode().equals(t.getReferenceFinancialDocumentTypeCode()) && e.getOriginCode().equals(t.getReferenceFinancialSystemOriginationCode()) && e.getDocumentNumber().equals(t.getReferenceFinancialDocumentNumber())) {
                return e;
            }
        }

        // If we couldn't find one that exists, create a new one

        // NOTE: the date doesn't matter so there is no need to call the date service
        // Changed to use the datetime service because of KULRNE-4183
        Entry e = new Entry(t, dateTimeService.getCurrentDate());
        if (KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD.equals(t.getTransactionEncumbranceUpdateCode())) {
            e.setDocumentNumber(t.getReferenceFinancialDocumentNumber());
            e.setFinancialSystemOriginationCode(t.getReferenceFinancialSystemOriginationCode());
            e.setFinancialDocumentTypeCode(t.getReferenceFinancialDocumentTypeCode());
        }

        Encumbrance enc = new Encumbrance(e);
        encumbranceList.add(enc);
        return enc;
    }

    /**
     * @param t
     * @param enc
     */
    public void updateEncumbrance(Transaction t, Encumbrance enc) {
        //KFSMI-1571 - check parameter encumbranceOpenAmountOeverridingDocTypes
        String[] encumbranceOpenAmountOeverridingDocTypes = parameterService.getParameterValuesAsString(PosterEntriesStep.class, GeneralLedgerConstants.PosterService.ENCUMBRANCE_OPEN_AMOUNT_OVERRIDING_DOCUMENT_TYPES).toArray(new String[] {});

        if (KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD.equals(t.getTransactionEncumbranceUpdateCode()) && !ArrayUtils.contains(encumbranceOpenAmountOeverridingDocTypes, t.getFinancialDocumentTypeCode())) {
            // If using referring doc number, add or subtract transaction amount from
            // encumbrance closed amount
            if (KFSConstants.GL_DEBIT_CODE.equals(t.getTransactionDebitCreditCode())) {
                enc.setAccountLineEncumbranceClosedAmount(enc.getAccountLineEncumbranceClosedAmount().subtract(t.getTransactionLedgerEntryAmount()));
            }
            else {
                enc.setAccountLineEncumbranceClosedAmount(enc.getAccountLineEncumbranceClosedAmount().add(t.getTransactionLedgerEntryAmount()));
            }
        }
        else {
            // If not using referring doc number, add or subtract transaction amount from
            // encumbrance amount
            if (KFSConstants.GL_DEBIT_CODE.equals(t.getTransactionDebitCreditCode()) || KFSConstants.GL_BUDGET_CODE.equals(t.getTransactionDebitCreditCode())) {
                enc.setAccountLineEncumbranceAmount(enc.getAccountLineEncumbranceAmount().add(t.getTransactionLedgerEntryAmount()));
            }
            else {
                enc.setAccountLineEncumbranceAmount(enc.getAccountLineEncumbranceAmount().subtract(t.getTransactionLedgerEntryAmount()));
            }
        }
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.PostTransaction#getDestinationName()
     */
    public String getDestinationName() {
        return persistenceStructureService.getTableName(Encumbrance.class);
    }


    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setAccountingCycleCachingService(AccountingCycleCachingService accountingCycleCachingService) {
        this.accountingCycleCachingService = accountingCycleCachingService;
    }

    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
