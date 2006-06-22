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
package org.kuali.module.gl.batch.poster.impl;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.gl.batch.poster.BalanceCalculator;
import org.kuali.module.gl.batch.poster.PostTransaction;
import org.kuali.module.gl.bo.Balance;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.gl.dao.BalanceDao;

/**
 * @author jsissom
 * 
 */
public class PostBalance implements PostTransaction, BalanceCalculator {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PostBalance.class);

    private BalanceDao balanceDao;
    private DateTimeService dateTimeService;

    /**
     * 
     */
    public PostBalance() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.module.gl.batch.poster.PostTransaction#post(org.kuali.module.gl.bo.Transaction)
     */
    public String post(Transaction t, int mode, Date postDate) {
        LOG.debug("post() started");

        String postType = "U";

        KualiDecimal amount = t.getTransactionLedgerEntryAmount();

        // Subtract the amount if offset generation indicator & the debit/credit code isn't the same
        // as the one in the object type code table
        if (t.getBalanceType().isFinancialOffsetGenerationIndicator()) {
            if (!t.getTransactionDebitCreditCode().equals(t.getObjectType().getFinObjectTypeDebitcreditCd())) {
                amount = amount.multiply(new KualiDecimal(-1));
            }
        }

        Balance b = balanceDao.getBalanceByTransaction(t);
        if (b == null) {
            postType = "I";
            b = new Balance(t);
        }
        b.setTimestamp(new java.sql.Date(postDate.getTime()));

        String period = t.getUniversityFiscalPeriodCode();
        b.addAmount(period, amount);

        balanceDao.save(b);

        return postType;
    }

    public Balance findBalance(Collection balanceList, Transaction t) {

        // Try to find one that already exists
        for (Iterator iter = balanceList.iterator(); iter.hasNext();) {
            Balance b = (Balance) iter.next();

            if (b.getUniversityFiscalYear().equals(t.getUniversityFiscalYear()) && b.getChartOfAccountsCode().equals(t.getChartOfAccountsCode()) && b.getAccountNumber().equals(t.getAccountNumber()) && b.getSubAccountNumber().equals(t.getSubAccountNumber()) && b.getObjectCode().equals(t.getFinancialObjectCode()) && b.getSubObjectCode().equals(t.getFinancialSubObjectCode()) && b.getBalanceTypeCode().equals(t.getFinancialBalanceTypeCode()) && b.getObjectTypeCode().equals(t.getFinancialObjectTypeCode())) {
                return b;
            }
        }

        // If we couldn't find one that exists, create a new one
        Balance b = new Balance(t);
        balanceList.add(b);

        return b;
    }

    /**
     * 
     * @param t
     * @param enc
     */
    public void updateBalance(Transaction t, Balance b) {

        // The pending entries haven't been scrubbed so there could be
        // bad data. This won't update a balance if the data it needs
        // is invalid
        KualiDecimal amount = t.getTransactionLedgerEntryAmount();
        if (amount == null) {
            amount = KualiDecimal.ZERO;
        }

        if (t.getObjectType() == null) {
            LOG.error("updateBalance() Invalid object type (" + t.getFinancialObjectTypeCode() + ") in pending table");
            return;
        }

        if (t.getBalanceType() == null) {
            LOG.error("updateBalance() Invalid balance type (" + t.getFinancialBalanceTypeCode() + ") in pending table");
            return;
        }

        // Subtract the amount if offset generation indicator & the debit/credit code isn't the same
        // as the one in the object type code table
        if (t.getBalanceType().isFinancialOffsetGenerationIndicator()) {
            if (!t.getTransactionDebitCreditCode().equals(t.getObjectType().getFinObjectTypeDebitcreditCd())) {
                amount = amount.multiply(new KualiDecimal(-1));
            }
        }

        // update the balance amount of the cooresponding period
        String period = t.getUniversityFiscalPeriodCode();
        if (period == null) {
            UniversityDate currentUniversityDate = dateTimeService.getCurrentUniversityDate();
            period = currentUniversityDate.getUniversityFiscalAccountingPeriod();
        }

        b.addAmount(period, amount);
    }

    public String getDestinationName() {
        return "GL_BALANCE_T";
    }

    public void setBalanceDao(BalanceDao bd) {
        balanceDao = bd;
    }

    /**
     * Sets the dateTimeService attribute value.
     * 
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}