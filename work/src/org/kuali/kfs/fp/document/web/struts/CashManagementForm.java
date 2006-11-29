/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source$
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
package org.kuali.module.financial.web.struts.form;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.Constants.DocumentStatusCodes.CashReceipt;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.format.CurrencyFormatter;
import org.kuali.core.web.format.TimestampFullPrecisionFormatter;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.module.financial.bo.CashDrawer;
import org.kuali.module.financial.bo.Deposit;
import org.kuali.module.financial.document.CashManagementDocument;
import org.kuali.module.financial.document.CashReceiptDocument;
import org.kuali.module.financial.service.CashManagementService;

/**
 * This class is the action form for CashManagement
 * 
 * 
 */
public class CashManagementForm extends KualiDocumentFormBase {
    private static final long serialVersionUID = 1L;
    private static Logger LOG = Logger.getLogger(CashManagementForm.class);

    private List depositHelpers;
    private CashDrawerSummary cashDrawerSummary;

    /**
     * Constructs a CashManagementForm.
     */
    public CashManagementForm() {
        super();

        setDocument(new CashManagementDocument());
        depositHelpers = new ArrayList();

        setFormatterType("document.cashDrawerStatus", CashDrawerStatusCodeFormatter.class);
        setFormatterType("document.deposit.depositTypeCode", CashReceiptDepositTypeFormatter.class);

        setFormatterType("cashDrawerSummary.timeOpened", TimestampFullPrecisionFormatter.class);
        setFormatterType("cashDrawerSummary.timeRefreshed", TimestampFullPrecisionFormatter.class);
        setFormatterType("cashDrawerSummary.*Total", CurrencyFormatter.class);
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
     * Creates and initializes a CashDrawerSummary for the related CashManagementDocument, if it is not currently closed
     */
    public void populateCashDrawerSummary() {
        CashManagementDocument cmd = getCashManagementDocument();
        if (cmd != null) {
            CashDrawer cd = SpringServiceLocator.getCashDrawerService().getByWorkgroupName(cmd.getWorkgroupName(), true);
            if (!cd.isClosed()) {
                cashDrawerSummary = new CashDrawerSummary(cmd);
            }
        }
    }

    /**
     * @return CashDrawerSummary instance associated with this form, if any
     */
    public CashDrawerSummary getCashDrawerSummary() {
        return cashDrawerSummary;
    }

    /**
     * Sets the CashDrawerSummary
     */
    public void setCashDrawerSummary(CashDrawerSummary cashDrawerSummary) {
        this.cashDrawerSummary = cashDrawerSummary;
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
     * 
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
            documentNumber = crd.getDocumentNumber();
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

    public static final class CashDrawerSummary {
        private Timestamp timeOpened;
        private Timestamp timeRefreshed;

        // directly calculated
        private int overallReceiptCount;
        private int depositedReceiptCount;

        private CashReceiptStatistics verifiedStats = new CashReceiptStatistics();
        private CashReceiptStatistics interimStats = new CashReceiptStatistics();
        private CashReceiptStatistics finalStats = new CashReceiptStatistics();
        private CashReceiptStatistics overallStats = new CashReceiptStatistics();

        // derived
        private KualiDecimal verifiedSumTotal;
        private KualiDecimal interimSumTotal;
        private KualiDecimal finalSumTotal;
        private KualiDecimal overallSumTotal;

        private KualiDecimal remainingCheckTotal;
        private KualiDecimal remainingCurrencyTotal;
        private KualiDecimal remainingCoinTotal;
        private KualiDecimal remainingSumTotal;


        public CashDrawerSummary(CashManagementDocument cmDoc) {
            timeOpened = cmDoc.getDocumentHeader().getWorkflowDocument().getCreateDate();

            resummarize(cmDoc);
        }

        public CashDrawerSummary() {
        }


        private static final String[] INTERESTING_STATII = { CashReceipt.VERIFIED, CashReceipt.INTERIM, CashReceipt.FINAL };

        public void resummarize(CashManagementDocument cmDoc) {
            //
            // get all interesting CRs
            String workgroupName = cmDoc.getWorkgroupName();
            List<CashReceiptDocument> interestingReceipts = SpringServiceLocator.getCashReceiptService().getCashReceipts(workgroupName, INTERESTING_STATII);


            //
            // rather than separating into lists by status, gather statistics in one fell swoop
            overallStats.clear();
            verifiedStats.clear();
            interimStats.clear();
            finalStats.clear();

            for (CashReceiptDocument receipt : interestingReceipts) {
                String status = receipt.getDocumentHeader().getFinancialDocumentStatusCode();
                overallStats.add(receipt);
                if (status.equals(CashReceipt.VERIFIED)) {
                    verifiedStats.add(receipt);
                }
                else if (status.equals(CashReceipt.INTERIM)) {
                    interimStats.add(receipt);
                }
                else if (status.equals(CashReceipt.FINAL)) {
                    finalStats.add(receipt);
                }
                else {
                    throw new IllegalStateException("invalid (unknown) financialDocumentStatusCode '" + status + "'");
                }
            }

            overallReceiptCount = overallStats.getReceiptCount();
            depositedReceiptCount = interimStats.getReceiptCount() + finalStats.getReceiptCount();

            verifiedSumTotal = verifiedStats.getSumTotal();
            interimSumTotal = interimStats.getSumTotal();
            finalSumTotal = finalStats.getSumTotal();
            overallSumTotal = overallStats.getSumTotal();

            remainingCheckTotal = overallStats.getCheckTotal().subtract(interimStats.getCheckTotal()).subtract(finalStats.getCheckTotal());
            remainingCurrencyTotal = overallStats.getCurrencyTotal().subtract(interimStats.getCurrencyTotal()).subtract(finalStats.getCurrencyTotal());
            remainingCoinTotal = overallStats.getCoinTotal().subtract(interimStats.getCoinTotal()).subtract(finalStats.getCoinTotal());
            remainingSumTotal = remainingCheckTotal.add(remainingCurrencyTotal.add(remainingCoinTotal));

            timeRefreshed = SpringServiceLocator.getDateTimeService().getCurrentTimestamp();
        }


        /**
         * @return current value of depositedReceiptCount.
         */
        public int getDepositedReceiptCount() {
            return depositedReceiptCount;
        }

        /**
         * Sets the depositedReceiptCount attribute value.
         * 
         * @param depositedReceiptCount The depositedReceiptCount to set.
         */
        public void setDepositedReceiptCount(int depositedReceiptCount) {
            this.depositedReceiptCount = depositedReceiptCount;
        }


        /**
         * @return current value of finalSumTotal.
         */
        public KualiDecimal getFinalSumTotal() {
            return finalSumTotal;
        }

        /**
         * Sets the finalSumTotal attribute value.
         * 
         * @param finalSumTotal The finalSumTotal to set.
         */
        public void setFinalSumTotal(KualiDecimal finalSumTotal) {
            this.finalSumTotal = finalSumTotal;
        }


        /**
         * @return current value of interimSumTotal.
         */
        public KualiDecimal getInterimSumTotal() {
            return interimSumTotal;
        }

        /**
         * Sets the interimSumTotal attribute value.
         * 
         * @param interimSumTotal The interimSumTotal to set.
         */
        public void setInterimSumTotal(KualiDecimal interimSumTotal) {
            this.interimSumTotal = interimSumTotal;
        }


        /**
         * @return current value of overallReceiptCount.
         */
        public int getOverallReceiptCount() {
            return overallReceiptCount;
        }

        /**
         * Sets the overallReceiptCount attribute value.
         * 
         * @param overallReceiptCount The overallReceiptCount to set.
         */
        public void setOverallReceiptCount(int overallReceiptCount) {
            this.overallReceiptCount = overallReceiptCount;
        }


        /**
         * @return current value of remainingCheckTotal.
         */
        public KualiDecimal getRemainingCheckTotal() {
            return remainingCheckTotal;
        }

        /**
         * Sets the remainingCheckTotal attribute value.
         * 
         * @param remainingCheckTotal The remainingCheckTotal to set.
         */
        public void setRemainingCheckTotal(KualiDecimal remainingCheckTotal) {
            this.remainingCheckTotal = remainingCheckTotal;
        }


        /**
         * @return current value of remainingCoinTotal.
         */
        public KualiDecimal getRemainingCoinTotal() {
            return remainingCoinTotal;
        }

        /**
         * Sets the remainingCoinTotal attribute value.
         * 
         * @param remainingCoinTotal The remainingCoinTotal to set.
         */
        public void setRemainingCoinTotal(KualiDecimal remainingCoinTotal) {
            this.remainingCoinTotal = remainingCoinTotal;
        }


        /**
         * @return current value of remainingCurrencyTotal.
         */
        public KualiDecimal getRemainingCurrencyTotal() {
            return remainingCurrencyTotal;
        }

        /**
         * Sets the remainingCurrencyTotal attribute value.
         * 
         * @param remainingCurrencyTotal The remainingCurrencyTotal to set.
         */
        public void setRemainingCurrencyTotal(KualiDecimal remainingCurrencyTotal) {
            this.remainingCurrencyTotal = remainingCurrencyTotal;
        }


        /**
         * @return current value of remainingSumTotal.
         */
        public KualiDecimal getRemainingSumTotal() {
            return remainingSumTotal;
        }

        /**
         * Sets the remainingSumTotal attribute value.
         * 
         * @param remainingSumTotal The remainingSumTotal to set.
         */
        public void setRemainingSumTotal(KualiDecimal remainingSumTotal) {
            this.remainingSumTotal = remainingSumTotal;
        }


        /**
         * @return current value of timeOpened.
         */
        public Timestamp getTimeOpened() {
            return timeOpened;
        }

        /**
         * Sets the timeOpened attribute value.
         * 
         * @param timeOpened The timeOpened to set.
         */
        public void setTimeOpened(Timestamp timeOpened) {
            this.timeOpened = timeOpened;
        }


        /**
         * @return current value of timeRefreshed.
         */
        public Timestamp getTimeRefreshed() {
            return timeRefreshed;
        }

        /**
         * Sets the timeRefreshed attribute value.
         * 
         * @param timeRefreshed The timeRefreshed to set.
         */
        public void setTimeRefreshed(Timestamp timeRefreshed) {
            this.timeRefreshed = timeRefreshed;
        }


        /**
         * @return current value of verifiedSumTotal.
         */
        public KualiDecimal getVerifiedSumTotal() {
            return verifiedSumTotal;
        }

        /**
         * Sets the verifiedSumTotal attribute value.
         * 
         * @param verifiedSumTotal The verifiedSumTotal to set.
         */
        public void setVerifiedSumTotal(KualiDecimal verifiedSumTotal) {
            this.verifiedSumTotal = verifiedSumTotal;
        }


        /**
         * @return current value of overallSumTotal.
         */
        public KualiDecimal getOverallSumTotal() {
            return overallSumTotal;
        }

        /**
         * Sets the overallSumTotal attribute value.
         * 
         * @param overallSumTotal The overallSumTotal to set.
         */
        public void setOverallSumTotal(KualiDecimal overallSumTotal) {
            this.overallSumTotal = overallSumTotal;
        }


        /**
         * @return current value of finalStats.
         */
        public CashReceiptStatistics getFinalStats() {
            return finalStats;
        }

        /**
         * @return current value of interimStats.
         */
        public CashReceiptStatistics getInterimStats() {
            return interimStats;
        }

        /**
         * @return current value of verifiedStats.
         */
        public CashReceiptStatistics getVerifiedStats() {
            return verifiedStats;
        }

        /**
         * @return current value of overalllStats.
         */
        public CashReceiptStatistics getOverallStats() {
            return overallStats;
        }


        public static final class CashReceiptStatistics {
            private int receiptCount;
            private KualiDecimal checkTotal;
            private KualiDecimal currencyTotal;
            private KualiDecimal coinTotal;

            /**
             * Constructs a SubSummary.
             */
            public CashReceiptStatistics() {
                clear();
            }

            /**
             * Increments the counter by 1, and the various totals by the amounts in the given CashReceiptDocument
             * 
             * @param receipt
             */
            public void add(CashReceiptDocument receipt) {
                receiptCount++;
                checkTotal = checkTotal.add(receipt.getTotalCheckAmount());
                currencyTotal = currencyTotal.add(receipt.getTotalCashAmount());
                coinTotal = coinTotal.add(receipt.getTotalCoinAmount());
            }

            /**
             * Zeros counter and totals.
             */
            public void clear() {
                receiptCount = 0;
                checkTotal = KualiDecimal.ZERO;
                currencyTotal = KualiDecimal.ZERO;
                coinTotal = KualiDecimal.ZERO;
            }


            /**
             * Returns total of all check, coin, and currency totals
             */
            public KualiDecimal getSumTotal() {
                KualiDecimal sumTotal = getCheckTotal().add(getCoinTotal().add(getCurrencyTotal()));

                return sumTotal;
            }


            /**
             * @return current value of checkTotal.
             */
            public KualiDecimal getCheckTotal() {
                return checkTotal;
            }

            /**
             * Sets the checkTotal attribute value.
             * 
             * @param checkTotal The checkTotal to set.
             */
            public void setCheckTotal(KualiDecimal checkTotal) {
                this.checkTotal = checkTotal;
            }


            /**
             * @return current value of coinTotal.
             */
            public KualiDecimal getCoinTotal() {
                return coinTotal;
            }

            /**
             * Sets the coinTotal attribute value.
             * 
             * @param coinTotal The coinTotal to set.
             */
            public void setCoinTotal(KualiDecimal coinTotal) {
                this.coinTotal = coinTotal;
            }


            /**
             * @return current value of currencyTotal.
             */
            public KualiDecimal getCurrencyTotal() {
                return currencyTotal;
            }

            /**
             * Sets the currencyTotal attribute value.
             * 
             * @param currencyTotal The currencyTotal to set.
             */
            public void setCurrencyTotal(KualiDecimal currencyTotal) {
                this.currencyTotal = currencyTotal;
            }


            /**
             * @return current value of receiptCount.
             */
            public int getReceiptCount() {
                return receiptCount;
            }

            /**
             * Sets the receiptCount attribute value.
             * 
             * @param receiptCount The receiptCount to set.
             */
            public void setReceiptCount(int receiptCount) {
                this.receiptCount = receiptCount;
            }


            /**
             * @see java.lang.Object#toString()
             */
            @Override
            public String toString() {
                return "CashDrawerSummary(" + getSumTotal() + " = " + getCheckTotal() + " + " + getCurrencyTotal() + " + " + getCoinTotal() + ")";
            }
        }
    }
}
