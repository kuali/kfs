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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.core.util.KualiDecimal;
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
    private static Logger LOG = Logger.getLogger(CashManagementForm.class);

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
     * @return cashManagementDocument
     */
    public CashManagementDocument getCashManagementDocument() {
        return (CashManagementDocument) getDocument();
    }


    /**
     * Creates a DepositHelper foreach Deposit associated with this form's document
     */
    public void populateDepositHelpers() {
        depositHelpers.clear();

        List deposits = getCashManagementDocument().getDeposits();
        for (Iterator i = deposits.iterator(); i.hasNext();) {
            Deposit d = (Deposit) i.next();

            DepositHelper dh = new DepositHelper(d);
            depositHelpers.add(dh);
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
        private List<CashReceiptSummary> cashReceiptSummarys;

        /**
         * Constructs a DepositHelper - default constructor used by PojoProcessor.
         */
        public DepositHelper() {
            cashReceiptSummarys = new ArrayList<CashReceiptSummary>();
            depositLineNumber = new Integer(1);
        }

        /**
         * Constructs a DepositHelper
         * 
         * @param deposit
         */
        public DepositHelper(Deposit deposit) {
            depositLineNumber = deposit.getFinancialDocumentDepositLineNumber();

            cashReceiptSummarys = new ArrayList<CashReceiptSummary>();

            CashManagementService cmService = SpringServiceLocator.getCashManagementService();
            List<CashReceiptDocument> cashReceipts = cmService.retrieveCashReceipts(deposit);
            for (CashReceiptDocument document : cashReceipts) {
                cashReceiptSummarys.add(new CashReceiptSummary(document));
            }
        }

        /**
         * @return List
         */
        public List<CashReceiptSummary> getCashReceiptSummarys() {
            return cashReceiptSummarys;
        }

        /**
         * @param i
         * @return CashReceiptSummary
         */
        public CashReceiptSummary getCashReceiptSummary(int index) {
            extendCashReceiptSummarys(index + 1);

            return cashReceiptSummarys.get(index);
        }

        /**
         * Ensures that there are at least minSize entries in the cashReceiptSummarys list
         * 
         * @param minSize
         */
        private void extendCashReceiptSummarys(int minSize) {
            while (cashReceiptSummarys.size() < minSize) {
                cashReceiptSummarys.add(new CashReceiptSummary());
            }
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
        @Override
        public String toString() {
            return "deposit #" + depositLineNumber;
        }
    }

    public static final class CashReceiptSummary {
        private String documentNumber;
        private String description;
        private Timestamp createDate;
        private KualiDecimal totalAmount;

        /**
         * Default constructor used by PojoProcessor.
         */
        public CashReceiptSummary() {
        }

        /**
         * Constructs a CashReceiptSummary from the given CashReceiptDocument.
         * 
         * @param crd
         */
        public CashReceiptSummary(CashReceiptDocument crd) {
            documentNumber = crd.getFinancialDocumentNumber();
            description = crd.getDocumentHeader().getFinancialDocumentDescription();
            createDate = crd.getDocumentHeader().getWorkflowDocument().getCreateDate();
            totalAmount = crd.getSumTotalAmount();
        }

        /**
         * @return current value of createDate.
         */
        public Timestamp getCreateDate() {
            return createDate;
        }

        /**
         * Sets the createDate attribute value.
         * 
         * @param createDate The createDate to set.
         */
        public void setCreateDate(Timestamp createDate) {
            this.createDate = createDate;
        }

        /**
         * @return current value of description.
         */
        public String getDescription() {
            return description;
        }

        /**
         * Sets the description attribute value.
         * 
         * @param description The description to set.
         */
        public void setDescription(String description) {
            this.description = description;
        }

        /**
         * @return current value of documentNumber.
         */
        public String getDocumentNumber() {
            return documentNumber;
        }

        /**
         * Sets the documentNumber attribute value.
         * 
         * @param docNumber The documentNumber to set.
         */
        public void setDocumentNumber(String documentNumber) {
            this.documentNumber = documentNumber;
        }

        /**
         * @return current value of totalAmount.
         */
        public KualiDecimal getTotalAmount() {
            return totalAmount;
        }

        /**
         * Sets the totalAmount attribute value.
         * 
         * @param totalAmount The totalAmount to set.
         */
        public void setTotalAmount(KualiDecimal totalAmount) {
            this.totalAmount = totalAmount;
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "CRSummary " + getDocumentNumber();
        }
    }
}
