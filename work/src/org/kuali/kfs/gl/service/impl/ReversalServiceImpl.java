/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.gl.service.impl;

import java.util.Date;
import java.util.Iterator;

import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.gl.businessobject.LedgerEntryForReporting;
import org.kuali.kfs.gl.businessobject.LedgerEntryHolder;
import org.kuali.kfs.gl.businessobject.Reversal;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.gl.dataaccess.ReversalDao;
import org.kuali.kfs.gl.service.ReversalService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This transactional class provides the default implementation of the services required in ReversalService
 * 
 * @see org.kuali.kfs.gl.service.ReversalService
 */
@Transactional
public class ReversalServiceImpl implements ReversalService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReversalServiceImpl.class);

    private ReversalDao reversalDao;
    private AccountingPeriodService accountingPeriodService;
    private UniversityDateService universityDateService;

    /**
     * Deletes a reversal record
     * 
     * @param re the reversal to delete, remove, or otherwise expiate from the database
     * @see org.kuali.kfs.gl.service.ReversalService#delete(org.kuali.kfs.gl.businessobject.Reversal)
     */
    public void delete(Reversal re) {
        LOG.debug("delete() started");

        reversalDao.delete(re);
    }

    /**
     * Returns all the reversal records set to reverse on or before the given date
     * 
     * @param before the date to find reversals on or before
     * @see org.kuali.kfs.gl.service.ReversalService#getByDate(java.util.Date)
     */
    public Iterator getByDate(Date before) {
        LOG.debug("getByDate() started");

        return reversalDao.getByDate(before);
    }

    public Reversal getByTransaction(Transaction t) {
        LOG.debug("getByTransaction() started");

        return reversalDao.getByTransaction(t);
    }

    /**
     * Summarizes all of the reversal records set to reverse before or on the given date
     * @param before the date reversals summarized should be on or before
     * @return a LedgerEntryHolder with a summary of
     * @see org.kuali.kfs.gl.service.ReversalService#getSummaryByDate(java.util.Date)
     */
    public LedgerEntryHolder getSummaryByDate(Date before) {
        LOG.debug("getSummaryByDate() started");

        LedgerEntryHolder ledgerEntryHolder = new LedgerEntryHolder();

        Iterator reversalsIterator = reversalDao.getByDate(before);
        while (reversalsIterator.hasNext()) {
            Reversal reversal = (Reversal) reversalsIterator.next();
            LedgerEntryForReporting ledgerEntry = buildLedgerEntryFromReversal(reversal);
            ledgerEntryHolder.insertLedgerEntry(ledgerEntry, true);
        }
        return ledgerEntryHolder;
    }

    /**
     * Creates a LedgerEntry from a reversal, which is proper for summarization (ie, its fiscal year and period code are based off
     * the reversal date, not off the transaction date or the reversal's current fiscal year and accounting period)
     * 
     * @param reversal reversal to build LedgerEntry with
     * @return a new LedgerEntry, populated by the reversal
     */
    protected LedgerEntryForReporting buildLedgerEntryFromReversal(Reversal reversal) {
        LedgerEntryForReporting entry = new LedgerEntryForReporting(universityDateService.getFiscalYear(reversal.getFinancialDocumentReversalDate()), accountingPeriodService.getByDate(reversal.getFinancialDocumentReversalDate()).getUniversityFiscalPeriodCode(), reversal.getFinancialBalanceTypeCode(), reversal.getFinancialSystemOriginationCode());
        if (KFSConstants.GL_CREDIT_CODE.equals(reversal.getTransactionDebitCreditCode())) {
            entry.setCreditAmount(reversal.getTransactionLedgerEntryAmount());
            entry.setCreditCount(1);
        }
        else if (KFSConstants.GL_DEBIT_CODE.equals(reversal.getTransactionDebitCreditCode())) {
            entry.setDebitAmount(reversal.getTransactionLedgerEntryAmount());
            entry.setDebitCount(1);
        }
        else {
            entry.setNoDCAmount(reversal.getTransactionLedgerEntryAmount());
            entry.setNoDCCount(1);
        }
        entry.setRecordCount(1);
        return entry;
    }

    /**
     * Saves a reversal record
     * 
     * @param re the reversal to save
     * @see org.kuali.kfs.gl.service.ReversalService#save(org.kuali.kfs.gl.businessobject.Reversal)
     */
    public void save(Reversal re) {
        LOG.debug("save() started");

        SpringContext.getBean(BusinessObjectService.class).save(re);
    }

    /**
     * Sets the reversalDao attribute, allowing injection of an implementation of that data access object
     * 
     * @param reversalDao the reversalDao implementation to set
     * @see org.kuali.kfs.gl.dataaccess.ReversalDao
     */
    public void setReversalDao(ReversalDao reversalDao) {
        this.reversalDao = reversalDao;
    }

    /**
     * Sets the accountingPeriodService attribute, allowing injection of an implementation of that service
     * 
     * @param accountingPeriodService the accountingPeriodService implementation to set
     * @see org.kuali.kfs.coa.service.AccountingPeriodService
     */
    public void setAccountingPeriodService(AccountingPeriodService accountingPeriodService) {
        this.accountingPeriodService = accountingPeriodService;
    }

    /**
     * Sets the unversityDateService attribute, allowing injection of an implementation of that service
     * 
     * @param universityDateService the universityDateService implementation to set
     * @see org.kuali.kfs.sys.service.UniversityDateService
     */
    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }
}
