/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.gl.service.impl;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.kuali.Constants;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.gl.bo.Reversal;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.dao.ReversalDao;
import org.kuali.module.gl.service.ReversalService;
import org.kuali.module.gl.util.LedgerEntry;
import org.kuali.module.gl.util.LedgerEntryHolder;

public class ReversalServiceImpl implements ReversalService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReversalServiceImpl.class);

    private ReversalDao reversalDao;

    public void delete(Reversal re) {
        LOG.debug("delete() started");

        reversalDao.delete(re);
    }

    public Iterator getByDate(Date before) {
        LOG.debug("getByDate() started");

        return reversalDao.getByDate(before);
    }

    public Reversal getByTransaction(Transaction t) {
        LOG.debug("getByTransaction() started");

        return reversalDao.getByTransaction(t);
    }

    public LedgerEntryHolder getSummaryByDate(Date before) {
        LOG.debug("getSummaryByDate() started");

        LedgerEntryHolder ledgerEntryHolder = new LedgerEntryHolder();

        Iterator entrySummaryIterator = reversalDao.getSummaryByDate(before);
        while (entrySummaryIterator.hasNext()) {
            Object[] entrySummary = (Object[]) entrySummaryIterator.next();
            LedgerEntry ledgerEntry = this.buildLedgerEntry(entrySummary);
            ledgerEntryHolder.insertLedgerEntry(ledgerEntry, true);
        }
        return ledgerEntryHolder;
    }

    public void save(Reversal re) {
        LOG.debug("save() started");

        reversalDao.save(re);
    }

    // create or update a ledger entry with the array of information from the given entry summary object
    private LedgerEntry buildLedgerEntry(Object[] entrySummary) {
        // extract the data from an array and use them to populate a ledger entry
        Object oFiscalYear = entrySummary[0];
        Object oPeriodCode = entrySummary[1];
        Object oBalanceType = entrySummary[2];
        Object oOriginCode = entrySummary[3];
        Object oDebitCreditCode = entrySummary[4];
        Object oAmount = entrySummary[5];
        Object oCount = entrySummary[6];

        Integer fiscalYear = oFiscalYear != null ? new Integer(oFiscalYear.toString()) : null;
        String periodCode = oPeriodCode != null ? oPeriodCode.toString() : "  ";
        String balanceType = oBalanceType != null ? oBalanceType.toString() : "  ";
        String originCode = oOriginCode != null ? oOriginCode.toString() : "  ";
        String debitCreditCode = oDebitCreditCode != null ? oDebitCreditCode.toString() : " ";
        KualiDecimal amount = oAmount != null ? new KualiDecimal(oAmount.toString()) : KualiDecimal.ZERO;
        int count = oCount != null ? Integer.parseInt(oCount.toString()) : 0;

        // construct a ledger entry with the information fetched from the given array
        LedgerEntry ledgerEntry = new LedgerEntry(fiscalYear, periodCode, balanceType, originCode);
        if (Constants.GL_CREDIT_CODE.equals(debitCreditCode)) {
            ledgerEntry.setCreditAmount(amount);
            ledgerEntry.setCreditCount(count);
        }
        else if (Constants.GL_DEBIT_CODE.equals(debitCreditCode)) {
            ledgerEntry.setDebitAmount(amount);
            ledgerEntry.setDebitCount(count);
        }
        else {
            ledgerEntry.setNoDCAmount(amount);
            ledgerEntry.setNoDCCount(count);
        }
        ledgerEntry.setRecordCount(count);

        return ledgerEntry;
    }
    public void setReversalDao(ReversalDao reversalDao) {
        this.reversalDao = reversalDao;
    }
}
