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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.kuali.Constants;
import org.kuali.module.gl.batch.poster.EncumbranceCalculator;
import org.kuali.module.gl.batch.poster.PostTransaction;
import org.kuali.module.gl.batch.poster.VerifyTransaction;
import org.kuali.module.gl.bo.Encumbrance;
import org.kuali.module.gl.bo.Entry;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.dao.EncumbranceDao;

/**
 * @author jsissom
 * 
 */
public class PostEncumbrance implements PostTransaction, VerifyTransaction, EncumbranceCalculator {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PostEncumbrance.class);

    private EncumbranceDao encumbranceDao;

    public void setEncumbranceDao(EncumbranceDao ed) {
        encumbranceDao = ed;
    }

    public PostEncumbrance() {
        super();
    }

    /**
     * Make sure the transaction is correct for posting. If there is an error, this will stop the transaction from posting in all
     * files.
     */
    public List verifyTransaction(Transaction t) {
        LOG.debug("verifyTransaction() started");

        List errors = new ArrayList();

        // The encumbrance update code can only be space, N, R or D. Nothing else
        if ((t.getTransactionEncumbranceUpdateCode() != null) && (!" ".equals(t.getTransactionEncumbranceUpdateCode())) && (!Constants.ENCUMB_UPDT_NO_ENCUMBRANCE_CD.equals(t.getTransactionEncumbranceUpdateCode())) && (!Constants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD.equals(t.getTransactionEncumbranceUpdateCode())) && (!Constants.ENCUMB_UPDT_DOCUMENT_CD.equals(t.getTransactionEncumbranceUpdateCode()))) {
            errors.add("Invalid Encumbrance Update Code (" + t.getTransactionEncumbranceUpdateCode() + ")");
        }

        return errors;
    }

    /**
     * Called by the poster to post a transaction. The transaction might or might not be an encumbrance transaction.
     */
    public String post(Transaction t, int mode, Date postDate) {
        LOG.debug("post() started");

        String returnCode = "U";

        // If the encumbrance update code is space or N, or the object type code is FB
        // we don't need to post an encumbrance
        if ((t.getTransactionEncumbranceUpdateCode() == null) || " ".equals(t.getTransactionEncumbranceUpdateCode()) || Constants.ENCUMB_UPDT_NO_ENCUMBRANCE_CD.equals(t.getTransactionEncumbranceUpdateCode()) || "FB".equals(t.getFinancialObjectTypeCode())) {
            LOG.debug("post() not posting non-encumbrance transaction");
            return "";
        }

        // Get the current encumbrance record if there is one
        Entry e = new Entry(t, null);
        if (Constants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD.equals(t.getTransactionEncumbranceUpdateCode())) {
            e.setFinancialDocumentNumber(t.getReferenceFinancialDocumentNumber());
            e.setFinancialSystemOriginationCode(t.getReferenceFinancialSystemOriginationCode());
            e.setFinancialDocumentTypeCode(t.getReferenceFinancialDocumentTypeCode());
        }

        Encumbrance enc = encumbranceDao.getEncumbranceByTransaction(e);
        if (enc == null) {
            // Build a new encumbrance record
            enc = new Encumbrance(e);

            returnCode = "I";
        }
        else {
            // Use the one retrieved
            if (enc.getTransactionEncumbranceDate() == null) {
                enc.setTransactionEncumbranceDate(t.getTransactionDate());
            }

            returnCode = "U";
        }

        updateEncumbrance(t, enc);

        enc.setTimestamp(new Timestamp(postDate.getTime()));

        encumbranceDao.save(enc);

        return returnCode;
    }

    public Encumbrance findEncumbrance(Collection encumbranceList, Transaction t) {

        // If it isn't an encumbrance transaction, skip it
        if ((!Constants.ENCUMB_UPDT_DOCUMENT_CD.equals(t.getTransactionEncumbranceUpdateCode())) && (!Constants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD.equals(t.getTransactionEncumbranceUpdateCode()))) {
            return null;
        }

        // Try to find one that already exists
        for (Iterator iter = encumbranceList.iterator(); iter.hasNext();) {
            Encumbrance e = (Encumbrance) iter.next();

            if (Constants.ENCUMB_UPDT_DOCUMENT_CD.equals(t.getTransactionEncumbranceUpdateCode()) && e.getUniversityFiscalYear().equals(t.getUniversityFiscalYear()) &&
                    e.getChartOfAccountsCode().equals(t.getChartOfAccountsCode()) && e.getAccountNumber().equals(t.getAccountNumber()) &&
                    e.getSubAccountNumber().equals(t.getSubAccountNumber()) && e.getObjectCode().equals(t.getFinancialObjectCode()) &&
                    e.getSubObjectCode().equals(t.getFinancialSubObjectCode()) && e.getBalanceTypeCode().equals(t.getFinancialBalanceTypeCode()) &&
                    e.getDocumentTypeCode().equals(t.getFinancialDocumentTypeCode()) && e.getOriginCode().equals(t.getFinancialSystemOriginationCode()) && 
                    e.getDocumentNumber().equals(t.getFinancialDocumentNumber())) {
                return e;
            }

            if (Constants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD.equals(t.getTransactionEncumbranceUpdateCode()) && e.getUniversityFiscalYear().equals(t.getUniversityFiscalYear()) && 
                    e.getChartOfAccountsCode().equals(t.getChartOfAccountsCode()) && e.getAccountNumber().equals(t.getAccountNumber()) && 
                    e.getSubAccountNumber().equals(t.getSubAccountNumber()) && e.getObjectCode().equals(t.getFinancialObjectCode()) && 
                    e.getSubObjectCode().equals(t.getFinancialSubObjectCode()) && e.getBalanceTypeCode().equals(t.getFinancialBalanceTypeCode()) && 
                    e.getDocumentTypeCode().equals(t.getReferenceFinancialDocumentTypeCode()) && e.getOriginCode().equals(t.getReferenceFinancialSystemOriginationCode()) && 
                    e.getDocumentNumber().equals(t.getReferenceFinancialDocumentNumber())) {
                return e;
            }
        }

        // If we couldn't find one that exists, create a new one

        // NOTE: the date doesn't matter so there is no need to call the date service
        Entry e = new Entry(t, new Date());
        if (Constants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD.equals(t.getTransactionEncumbranceUpdateCode())) {
            e.setFinancialDocumentNumber(t.getReferenceFinancialDocumentNumber());
            e.setFinancialSystemOriginationCode(t.getReferenceFinancialSystemOriginationCode());
            e.setFinancialDocumentTypeCode(t.getReferenceFinancialDocumentTypeCode());
        }

        Encumbrance enc = new Encumbrance(e);
        encumbranceList.add(enc);
        return enc;
    }

    /**
     * 
     * @param t
     * @param enc
     */
    public void updateEncumbrance(Transaction t, Encumbrance enc) {
        if (Constants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD.equals(t.getTransactionEncumbranceUpdateCode())) {
            // If using referring doc number, add or subtract transaction amount from
            // encumbrance closed amount
            if (Constants.GL_DEBIT_CODE.equals(t.getTransactionDebitCreditCode())) {
                enc.setAccountLineEncumbranceClosedAmount(enc.getAccountLineEncumbranceClosedAmount().subtract(t.getTransactionLedgerEntryAmount()));
            }
            else {
                enc.setAccountLineEncumbranceClosedAmount(enc.getAccountLineEncumbranceClosedAmount().add(t.getTransactionLedgerEntryAmount()));
            }
        }
        else {
            // If not using referring doc number, add or subtract transaction amount from
            // encumbrance amount
            if (Constants.GL_DEBIT_CODE.equals(t.getTransactionDebitCreditCode()) || Constants.GL_BUDGET_CODE.equals(t.getTransactionDebitCreditCode())) {
                enc.setAccountLineEncumbranceAmount(enc.getAccountLineEncumbranceAmount().add(t.getTransactionLedgerEntryAmount()));
            }
            else {
                enc.setAccountLineEncumbranceAmount(enc.getAccountLineEncumbranceAmount().subtract(t.getTransactionLedgerEntryAmount()));
            }
        }
    }

    public String getDestinationName() {
        return "GL_ENCUMBRANCE_T";
    }
}
