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
package org.kuali.module.financial.web.struts.form;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.module.financial.bo.Deposit;
import org.kuali.module.financial.document.CashManagementDocument;
import org.kuali.module.financial.document.CashReceiptDocument;
import org.kuali.module.financial.service.CashManagementService;

/**
 * This class is the action form for Internal Billing.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class CashManagementForm extends KualiDocumentFormBase {
    private static final long serialVersionUID = 1L;
    // problem: I need one list of receiptSummaries foreach deposit
    // ? create a DepositReceipts object which contains DepositSummaries, index 'em by depositLineNumber ?
    private List depositHelpers;

    /**
     * Constructs a CashManagementForm.
     */
    public CashManagementForm() {
        super();

        setDocument(new CashManagementDocument());
        depositHelpers = new ArrayList();

        setFormatterType("document.cashDrawerStatus", CashDrawerStatusCodeFormatter.class);
        setFormatterType("document.deposit.depositTypeCode", CashReceiptDepositTypeFormatter.class);
    }

    /**
     * Overrides parent to call super and then make sure that the deposit helper list is populated.
     * 
     * @see org.kuali.core.web.struts.pojo.PojoForm#populate(javax.servlet.http.HttpServletRequest)
     */
    public void populate(HttpServletRequest request) {
        super.populate(request);

        populateDeposits();
        populateDepositHelpers();
    }

    /**
     * @return cashManagementDocument
     */
    public CashManagementDocument getCashManagementDocument() {
        return (CashManagementDocument) getDocument();
    }


    /**
     * Completes population of each Deposit associated with this form's document
     * 
     */
    public void populateDeposits() {
        List deposits = getCashManagementDocument().getDeposits();
        for (Iterator i = deposits.iterator(); i.hasNext();) {
            Deposit d = (Deposit) i.next();

            d.refresh();
        }
    }


    /**
     * Creates a DepositHelper foreach Deposit associated with this form's document
     */
    public void populateDepositHelpers() {
        if (depositHelpers.isEmpty()) {
            depositHelpers.clear();

            List deposits = getCashManagementDocument().getDeposits();
            for (Iterator i = deposits.iterator(); i.hasNext();) {
                Deposit d = (Deposit) i.next();

                DepositHelper dh = new DepositHelper(d);
                depositHelpers.add(dh);
            }
        }
    }

    /**
     * @return List
     */
    public List getDepositHelpers() {
        return depositHelpers;
    }

    /**
     * @param i
     * @return DepositHelper
     */
    public DepositHelper getDepositHelper(int i) {
        while (depositHelpers.size() <= i) {
            depositHelpers.add(new DepositHelper());
        }
        DepositHelper dh = (DepositHelper) depositHelpers.get(i);

        return dh;
    }

    /**
     * Removes and returns DepositHelper at the given index
     * 
     * @param i
     * @return
     */
    public DepositHelper removeDepositHelper(int i) {
        return (DepositHelper) depositHelpers.remove(i);
    }

    /**
     * Inner helper class.
     * 
     * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
     */
    public static final class DepositHelper {
        private Integer depositLineNumber;
        private List cashReceipts;

        /**
         * Constructs a DepositHelper - default constructor used by PojoProcessor.
         */
        public DepositHelper() {
            cashReceipts = new ArrayList();
            depositLineNumber = new Integer(1);
        }

        /**
         * Constructs a CashManagementForm.java.
         * 
         * @param deposit
         */
        public DepositHelper(Deposit deposit) {
            CashManagementService cmService = SpringServiceLocator.getCashManagementService();
            cashReceipts = cmService.retrieveCashReceipts(deposit);
            depositLineNumber = deposit.getFinancialDocumentDepositLineNumber();
        }

        /**
         * @return List
         */
        public List getCashReceipts() {
            return cashReceipts;
        }

        /**
         * @param i
         * @return CashReceiptDocument
         */
        public CashReceiptDocument getCashReceipt(int i) {
            CashReceiptDocument cd = (CashReceiptDocument) cashReceipts.get(i);

            return cd;
        }

        /**
         * @return Integer
         */
        public Integer getDepositLineNumber() {
            return depositLineNumber;
        }

        /**
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return "deposit " + depositLineNumber;
        }
    }
}
