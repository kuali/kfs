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
package org.kuali.kfs.fp.document.web.struts;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.fp.businessobject.CashDrawer;
import org.kuali.kfs.fp.businessobject.CashieringItemInProcess;
import org.kuali.kfs.fp.businessobject.Check;
import org.kuali.kfs.fp.businessobject.CheckBase;
import org.kuali.kfs.fp.businessobject.CoinDetail;
import org.kuali.kfs.fp.businessobject.CurrencyDetail;
import org.kuali.kfs.fp.businessobject.Deposit;
import org.kuali.kfs.fp.businessobject.format.CashDrawerStatusCodeFormatter;
import org.kuali.kfs.fp.businessobject.format.CashReceiptDepositTypeFormatter;
import org.kuali.kfs.fp.document.CashManagementDocument;
import org.kuali.kfs.fp.document.CashReceiptDocument;
import org.kuali.kfs.fp.document.authorization.CashManagementDocumentPresentationController;
import org.kuali.kfs.fp.document.service.CashManagementService;
import org.kuali.kfs.fp.document.service.CashReceiptService;
import org.kuali.kfs.fp.service.CashDrawerService;
import org.kuali.kfs.sys.KFSConstants.DepositConstants;
import org.kuali.kfs.sys.KFSConstants.DocumentStatusCodes.CashReceipt;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.datadictionary.FinancialSystemTransactionalDocumentEntry;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.web.format.CurrencyFormatter;
import org.kuali.rice.core.web.format.TimestampAMPMFormatter;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;

/**
 * This class is the action form for CashManagement
 */
public class CashManagementForm extends KualiDocumentFormBase {
    protected static final long serialVersionUID = 1L;
    protected static Logger LOG = Logger.getLogger(CashManagementForm.class);

    protected static final String CAMPUS_CODE_PROPERTY = "document.campusCode";

    protected transient List depositHelpers;
    protected CashDrawerSummary cashDrawerSummary;
    protected List<CashieringItemInProcess> recentlyClosedItemsInProcess;
    protected transient CashManagementDocumentPresentationController cmDocPrezController;

    /**
     * Constructs a CashManagementForm.
     */
    public CashManagementForm() {
        super();

        depositHelpers = new ArrayList();

        setFormatterType("document.cashDrawerStatus", CashDrawerStatusCodeFormatter.class);
        setFormatterType("document.deposit.depositTypeCode", CashReceiptDepositTypeFormatter.class);

        setFormatterType("cashDrawerSummary.timeOpened", TimestampAMPMFormatter.class);
        setFormatterType("cashDrawerSummary.timeRefreshed", TimestampAMPMFormatter.class);
        setFormatterType("cashDrawerSummary.*Total", CurrencyFormatter.class);

        setFormatterType("document.currentTransaction.transactionStarted", TimestampAMPMFormatter.class);
    }

    @Override
    protected String getDefaultDocumentTypeName() {
        return "CMD";
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
        depositHelpers = new ArrayList();

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
            CashDrawer cd = SpringContext.getBean(CashDrawerService.class).getByCampusCode(cmd.getCampusCode());
            if (cd == null) {
                throw new RuntimeException("No cash drawer exists for campus code "+cmd.getCampusCode()+"; please create on via the Cash Drawer Maintenance Document before attemping to create a CashManagementDocument for campus "+cmd.getCampusCode());
            }
            if (!cd.isClosed()) {
                cashDrawerSummary = new CashDrawerSummary(cmd);
            }
        }
    }

    /**
     * Tells any JSP page using this form whether an action can be taken to make the last interim deposit the final deposit
     *
     * @return true if last interim deposit could be the final deposit, false if otherwise
     */
    public boolean isLastInterimDepositFinalizable() {
        boolean result = true;
        CashManagementDocument cmDoc = getCashManagementDocument();
        result &= !cmDoc.hasFinalDeposit();
        result &= (cmDoc.getDeposits().size() > 0);
        if (result) {
            result &= SpringContext.getBean(CashManagementService.class).allVerifiedCashReceiptsAreDeposited(cmDoc);
        }
        return result;
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
     * Gets the recentlyClosedItemsInProcess attribute.
     *
     * @return Returns the recentlyClosedItemsInProcess.
     */
    public List<CashieringItemInProcess> getRecentlyClosedItemsInProcess() {
        return recentlyClosedItemsInProcess;
    }

    /**
     * Sets the recentlyClosedItemsInProcess attribute value.
     *
     * @param recentlyClosedItemsInProcess The recentlyClosedItemsInProcess to set.
     */
    public void setRecentlyClosedItemsInProcess(List<CashieringItemInProcess> recentlyClosedItemsInProcess) {
        this.recentlyClosedItemsInProcess = recentlyClosedItemsInProcess;
    }

    /**
     * @return true if the cash drawer can currently be opened, false otherwise
     */
    public boolean getAllowOpenCashDrawer() {
        if (cmDocPrezController == null) {
            cmDocPrezController = createCashManagementDocumentPresentationController();
        }
        return cmDocPrezController.canOpenCashDrawer(getDocument());
    }

    /**
     * Creates an instance of the appropriate implementation of CashManagementDocumentPresentationController to check the cash drawer opening logic
     * @return an instance of the CashManagementDocumentPresentationController for the document
     */
    protected CashManagementDocumentPresentationController createCashManagementDocumentPresentationController() {
        final DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        final FinancialSystemTransactionalDocumentEntry cmDocEntry = (FinancialSystemTransactionalDocumentEntry)dataDictionaryService.getDataDictionary().getDocumentEntry(dataDictionaryService.getDocumentTypeNameByClass(getDocument().getClass()));

        final CashManagementDocumentPresentationController cmDocPrezController;
        try {
            cmDocPrezController = (CashManagementDocumentPresentationController)cmDocEntry.getDocumentPresentationControllerClass().newInstance();
        }
        catch (InstantiationException ie) {
            throw new RuntimeException("Cannot instantiate instance of document presentation controller with class "+cmDocEntry.getDocumentPresentationControllerClass().getName(), ie);
        }
        catch (IllegalAccessException iae) {
            throw new RuntimeException("Illegal access occurred while instantiating instance of maintainable implementation "+cmDocEntry.getDocumentPresentationControllerClass().getName(), iae);
        }
        return cmDocPrezController;
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
     */
    public static final class DepositHelper {
        protected Integer depositLineNumber;
        protected List<CashReceiptSummary> cashReceiptSummarys;
        protected List<Check> cashieringChecks;

        /**
         * Constructs a DepositHelper - default constructor used by PojoProcessor.
         */
        public DepositHelper() {
            cashReceiptSummarys = new ArrayList<CashReceiptSummary>();
            cashieringChecks = new ArrayList<Check>();
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

            CashManagementService cmService = SpringContext.getBean(CashManagementService.class);
            List<CashReceiptDocument> cashReceipts = cmService.retrieveCashReceipts(deposit);
            for (CashReceiptDocument document : cashReceipts) {
                cashReceiptSummarys.add(new CashReceiptSummary(document));
            }

            cashieringChecks = cmService.selectCashieringChecksForDeposit(deposit.getDocumentNumber(), depositLineNumber);
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
        protected void extendCashReceiptSummarys(int minSize) {
            while (cashReceiptSummarys.size() < minSize) {
                cashReceiptSummarys.add(new CashReceiptSummary());
            }
        }

        /**
         * Gets the cashieringChecks attribute.
         *
         * @return Returns the cashieringChecks.
         */
        public List<Check> getCashieringChecks() {
            return cashieringChecks;
        }

        /**
         * Get a specific cashiering check in the list of cashiering checks
         *
         * @param index the index of the check to retrieve
         * @return a check
         */
        public Check getCashieringCheck(int index) {
            extendCashieringChecks(index);
            return cashieringChecks.get(index);
        }

        /**
         * This method makes the cashiering checks list longer, to avoid Array Index out of bounds issues
         *
         * @param minSize the minimum size to make the list
         */
        protected void extendCashieringChecks(int minSize) {
            while (cashieringChecks.size() <= minSize) {
                cashieringChecks.add(new CheckBase());
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
        protected String documentNumber;
        protected String description;
        protected Timestamp createDate;
        protected KualiDecimal totalAmount;
        protected KualiDecimal cashAmount;
        protected KualiDecimal checkAmount;
        protected String documentStatusCode;

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
            description = crd.getDocumentHeader().getDocumentDescription();
            createDate = new Timestamp(crd.getDocumentHeader().getWorkflowDocument().getDateCreated().getMillis());
            checkAmount = crd.getTotalConfirmedCheckAmount();
            cashAmount = crd.getTotalConfirmedCashAmount().add(crd.getTotalConfirmedCoinAmount());
            totalAmount = crd.getTotalConfirmedDollarAmount().subtract(crd.getTotalChangeAmount());
            documentStatusCode = crd.getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode();
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
         * Returns the total check amount for this CR
         *
         * @return a total of checks
         */
        public KualiDecimal getCheckAmount() {
            return this.checkAmount;
        }

        /**
         * Sets the checkAmount attribute value.
         */
        public void setCheckAmount(KualiDecimal checkAmount) {
            this.checkAmount = checkAmount;
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "CRSummary " + getDocumentNumber();
        }

        /**
         * @return the cashAmount
         */
        public KualiDecimal getCashAmount() {
            return cashAmount;
        }

        /**
         * @param cashAmount the cashAmount to set
         */
        public void setCashAmount(KualiDecimal cashAmount) {
            this.cashAmount = cashAmount;
        }

        /**
         * @return the documentStatusCode
         */
        public String getDocumentStatusCode() {
            return documentStatusCode;
        }

        /**
         * @param documentStatusCode the documentStatusCode to set
         */
        public void setDocumentStatusCode(String documentStatusCode) {
            this.documentStatusCode = documentStatusCode;
        }

    }

    public static final class CashDrawerSummary implements Serializable {
        protected Timestamp timeOpened;
        protected Timestamp timeRefreshed;

        // directly calculated
        protected int overallReceiptCount;
        protected int depositedReceiptCount;

        protected CashReceiptStatistics verifiedReceiptStats = new CashReceiptStatistics();
        protected CashReceiptStatistics interimReceiptStats = new CashReceiptStatistics();
        protected CashReceiptStatistics finalReceiptStats = new CashReceiptStatistics();
        protected CashReceiptStatistics overallReceiptStats = new CashReceiptStatistics();

        // derived
        protected KualiDecimal verifiedReceiptSumTotal;
        protected KualiDecimal interimReceiptSumTotal;
        protected KualiDecimal finalReceiptSumTotal;
        protected KualiDecimal overallReceiptSumTotal;

        protected KualiDecimal remainingCheckTotal;
        protected KualiDecimal remainingCurrencyTotal;
        protected KualiDecimal remainingCoinTotal;
        protected KualiDecimal remainingSumTotal;

        protected boolean isDepositsFinal = false;
        protected KualiDecimal cashieringChecksTotal;
        protected KualiDecimal depositedCashieringChecksTotal;
        protected KualiDecimal undepositedCashieringChecksTotal;
        protected KualiDecimal cashDrawerCurrencyTotal;
        protected KualiDecimal cashDrawerCoinTotal;
        protected KualiDecimal openItemsTotal;
        protected KualiDecimal cashDrawerTotal;
        protected KualiDecimal interimDepositedCashieringChecksTotal;
        protected KualiDecimal finalDepositedCashieringChecksTotal;

        public CashDrawerSummary(CashManagementDocument cmDoc) {
            timeOpened = new Timestamp(cmDoc.getDocumentHeader().getWorkflowDocument().getDateCreated().getMillis());

            resummarize(cmDoc);
        }

        public CashDrawerSummary() {
        }


        protected static final String[] INTERESTING_STATII = { CashReceipt.VERIFIED, CashReceipt.INTERIM, CashReceipt.FINAL };

        public void resummarize(CashManagementDocument cmDoc) {
            //
            // get all interesting CRs
            String campusCode = cmDoc.getCampusCode();
            List<CashReceiptDocument> interestingReceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(campusCode, INTERESTING_STATII);


            //
            // rather than separating into lists by status, gather statistics in one fell swoop
            overallReceiptStats.clear();
            verifiedReceiptStats.clear();
            interimReceiptStats.clear();
            finalReceiptStats.clear();

            for (CashReceiptDocument receipt : interestingReceipts) {
                String status = receipt.getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode();
                overallReceiptStats.add(receipt);
                if (status.equals(CashReceipt.VERIFIED)) {
                    verifiedReceiptStats.add(receipt);
                }
                else if (status.equals(CashReceipt.INTERIM)) {
                    interimReceiptStats.add(receipt);
                }
                else if (status.equals(CashReceipt.FINAL)) {
                    finalReceiptStats.add(receipt);
                }
                else {
                    throw new IllegalStateException("invalid (unknown) financialDocumentStatusCode '" + status + "'");
                }
            }

            overallReceiptCount = overallReceiptStats.getReceiptCount();
            depositedReceiptCount = interimReceiptStats.getReceiptCount() + finalReceiptStats.getReceiptCount();

            // get cash drawer summary info
            depositedCashieringChecksTotal = calculateDepositedCashieringChecksTotal(cmDoc);
            undepositedCashieringChecksTotal = calculateUndepositedCashieringChecksTotal(cmDoc);
            cashieringChecksTotal = depositedCashieringChecksTotal.add(undepositedCashieringChecksTotal);
            openItemsTotal = calculateOpenItemsTotal(cmDoc);
            cashDrawerCurrencyTotal = cmDoc.getCashDrawer().getCurrencyTotalAmount();
            cashDrawerCoinTotal = cmDoc.getCashDrawer().getCoinTotalAmount();
            cashDrawerTotal = undepositedCashieringChecksTotal.add(openItemsTotal.add(cashDrawerCurrencyTotal.add(cashDrawerCoinTotal)));
            Map<String, KualiDecimal> results = calculateDepositedCashieringChecksTotalByDepositType(cmDoc);
            interimDepositedCashieringChecksTotal = results.get(DepositConstants.DEPOSIT_TYPE_INTERIM);
            KualiDecimal finalDepositCashTotal = KualiDecimal.ZERO;
            Map<Class, Object> finalDepositCashDetails = SpringContext.getBean(CashManagementService.class).getCashDetailsForFinalDeposit(cmDoc.getDocumentNumber());
            KualiDecimal currencyDepositAmount = KualiDecimal.ZERO;
            if (finalDepositCashDetails.get(CurrencyDetail.class) != null) {
                currencyDepositAmount = ((CurrencyDetail) finalDepositCashDetails.get(CurrencyDetail.class)).getTotalAmount();
            }
            KualiDecimal coinDepositAmount = KualiDecimal.ZERO;
            if (finalDepositCashDetails.get(CoinDetail.class) != null) {
                coinDepositAmount = ((CoinDetail) finalDepositCashDetails.get(CoinDetail.class)).getTotalAmount();
            }
            finalDepositCashTotal = finalDepositCashTotal.add(currencyDepositAmount).add(coinDepositAmount);
            finalDepositedCashieringChecksTotal = results.get(DepositConstants.DEPOSIT_TYPE_FINAL).add(finalDepositCashTotal);


            verifiedReceiptSumTotal = verifiedReceiptStats.getSumTotal();
            interimReceiptSumTotal = interimReceiptStats.getCheckTotal().add(interimDepositedCashieringChecksTotal);
            finalReceiptSumTotal = finalReceiptStats.getCheckTotal().add(finalDepositedCashieringChecksTotal);
            overallReceiptSumTotal = overallReceiptStats.getSumTotal();

            remainingCheckTotal = overallReceiptStats.getCheckTotal().subtract(interimReceiptStats.getCheckTotal()).subtract(finalReceiptStats.getCheckTotal());
            remainingCurrencyTotal = overallReceiptStats.getCurrencyTotal().subtract(currencyDepositAmount).subtract(depositedCashieringChecksTotal);
            remainingCoinTotal = overallReceiptStats.getCoinTotal().subtract(coinDepositAmount);
            remainingSumTotal = remainingCheckTotal.add(remainingCurrencyTotal.add(remainingCoinTotal));

            isDepositsFinal = cmDoc.hasFinalDeposit();

            timeRefreshed = SpringContext.getBean(DateTimeService.class).getCurrentTimestamp();
        }

        protected KualiDecimal calculateDepositedCashieringChecksTotal(CashManagementDocument cmDoc) {
            return SpringContext.getBean(CashManagementService.class).calculateDepositedCheckTotal(cmDoc.getDocumentNumber());
        }

        protected KualiDecimal calculateUndepositedCashieringChecksTotal(CashManagementDocument cmDoc) {
            return SpringContext.getBean(CashManagementService.class).calculateUndepositedCheckTotal(cmDoc.getDocumentNumber());
        }

        protected KualiDecimal calculateOpenItemsTotal(CashManagementDocument cmDoc) {
            KualiDecimal total = KualiDecimal.ZERO;
            for (CashieringItemInProcess itemInProcess : SpringContext.getBean(CashManagementService.class).getOpenItemsInProcess(cmDoc)) {
                if (itemInProcess.getItemRemainingAmount() != null) {
                    total = total.add(itemInProcess.getItemRemainingAmount());
                }
            }
            return total;
        }

        protected Map<String, KualiDecimal> calculateDepositedCashieringChecksTotalByDepositType(CashManagementDocument cmDoc) {
            Map<String, KualiDecimal> result = new HashMap<String, KualiDecimal>();
            result.put(DepositConstants.DEPOSIT_TYPE_INTERIM, KualiDecimal.ZERO);
            result.put(DepositConstants.DEPOSIT_TYPE_FINAL, KualiDecimal.ZERO);
            // 1. get all deposited cashiering checks
            List<Check> checks = SpringContext.getBean(CashManagementService.class).selectDepositedCashieringChecks(cmDoc.getDocumentNumber());
            // 2. get all deposits
            List<Deposit> deposits = cmDoc.getDeposits();
            Map<Integer, String> depositTypes = new HashMap<Integer, String>();
            for (Deposit deposit : deposits) {
                depositTypes.put(deposit.getFinancialDocumentDepositLineNumber(), deposit.getDepositTypeCode());
            }
            // 3. now, go through all cashiering checks, totalling them to the right deposit type
            for (Check check : checks) {
                KualiDecimal properTotal = result.get(depositTypes.get(check.getFinancialDocumentDepositLineNumber()));
                properTotal = properTotal.add(check.getAmount());
                result.put(depositTypes.get(check.getFinancialDocumentDepositLineNumber()), properTotal);
            }
            return result;
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
         * @return current value of finalReceiptSumTotal.
         */
        public KualiDecimal getFinalReceiptSumTotal() {
            return finalReceiptSumTotal;
        }

        /**
         * Sets the finalReceiptSumTotal attribute value.
         *
         * @param finalReceiptSumTotal The finalReceiptSumTotal to set.
         */
        public void setFinalReceiptSumTotal(KualiDecimal finalSumTotal) {
            this.finalReceiptSumTotal = finalSumTotal;
        }


        /**
         * @return current value of interimReceiptSumTotal.
         */
        public KualiDecimal getInterimReceiptSumTotal() {
            return interimReceiptSumTotal;
        }

        /**
         * Sets the interimReceiptSumTotal attribute value.
         *
         * @param interimReceiptSumTotal The interimReceiptSumTotal to set.
         */
        public void setInterimReceiptSumTotal(KualiDecimal interimSumTotal) {
            this.interimReceiptSumTotal = interimSumTotal;
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
         * @return current value of verifiedReceiptSumTotal.
         */
        public KualiDecimal getVerifiedReceiptSumTotal() {
            return verifiedReceiptSumTotal;
        }

        /**
         * Sets the verifiedReceiptSumTotal attribute value.
         *
         * @param verifiedReceiptSumTotal The verifiedReceiptSumTotal to set.
         */
        public void setVerifiedReceiptSumTotal(KualiDecimal verifiedSumTotal) {
            this.verifiedReceiptSumTotal = verifiedSumTotal;
        }


        /**
         * @return current value of overallReceiptSumTotal.
         */
        public KualiDecimal getOverallReceiptSumTotal() {
            return overallReceiptSumTotal;
        }

        /**
         * Sets the overallReceiptSumTotal attribute value.
         *
         * @param overallReceiptSumTotal The overallReceiptSumTotal to set.
         */
        public void setOverallReceiptSumTotal(KualiDecimal overallSumTotal) {
            this.overallReceiptSumTotal = overallSumTotal;
        }


        /**
         * @return current value of finalReceiptStats.
         */
        public CashReceiptStatistics getFinalReceiptStats() {
            return finalReceiptStats;
        }

        /**
         * @return current value of interimReceiptStats.
         */
        public CashReceiptStatistics getInterimReceiptStats() {
            return interimReceiptStats;
        }

        /**
         * @return current value of verifiedReceiptStats.
         */
        public CashReceiptStatistics getVerifiedReceiptStats() {
            return verifiedReceiptStats;
        }

        /**
         * Gets the cashDrawerCoinTotal attribute.
         *
         * @return Returns the cashDrawerCoinTotal.
         */
        public KualiDecimal getCashDrawerCoinTotal() {
            return cashDrawerCoinTotal;
        }

        /**
         * Gets the cashDrawerCurrencyTotal attribute.
         *
         * @return Returns the cashDrawerCurrencyTotal.
         */
        public KualiDecimal getCashDrawerCurrencyTotal() {
            return cashDrawerCurrencyTotal;
        }

        /**
         * Gets the cashDrawerTotal attribute.
         *
         * @return Returns the cashDrawerTotal.
         */
        public KualiDecimal getCashDrawerTotal() {
            return cashDrawerTotal;
        }

        /**
         * Gets the cashieringChecksTotal attribute.
         *
         * @return Returns the cashieringChecksTotal.
         */
        public KualiDecimal getCashieringChecksTotal() {
            return cashieringChecksTotal;
        }

        /**
         * Sets the cashieringChecksTotal attribute value.
         *
         * @param cashieringChecksTotal The cashieringChecksTotal to set.
         */
        public void setCashieringChecksTotal(KualiDecimal cashieringChecksTotal) {
            this.cashieringChecksTotal = cashieringChecksTotal;
        }

        /**
         * Sets the depositedCashieringChecksTotal attribute value.
         *
         * @param depositedCashieringChecksTotal The depositedCashieringChecksTotal to set.
         */
        public void setDepositedCashieringChecksTotal(KualiDecimal depositedCashieringChecksTotal) {
            this.depositedCashieringChecksTotal = depositedCashieringChecksTotal;
        }

        /**
         * Gets the isDepositsFinal attribute.
         *
         * @return Returns the isDepositsFinal.
         */
        public boolean isDepositsFinal() {
            return isDepositsFinal;
        }

        /**
         * Sets the cashDrawerCoinTotal attribute value.
         *
         * @param cashDrawerCoinTotal The cashDrawerCoinTotal to set.
         */
        public void setCashDrawerCoinTotal(KualiDecimal cashDrawerCoinTotal) {
            this.cashDrawerCoinTotal = cashDrawerCoinTotal;
        }

        /**
         * Sets the cashDrawerCurrencyTotal attribute value.
         *
         * @param cashDrawerCurrencyTotal The cashDrawerCurrencyTotal to set.
         */
        public void setCashDrawerCurrencyTotal(KualiDecimal cashDrawerCurrencyTotal) {
            this.cashDrawerCurrencyTotal = cashDrawerCurrencyTotal;
        }

        /**
         * Sets the cashDrawerTotal attribute value.
         *
         * @param cashDrawerTotal The cashDrawerTotal to set.
         */
        public void setCashDrawerTotal(KualiDecimal cashDrawerTotal) {
            this.cashDrawerTotal = cashDrawerTotal;
        }

        /**
         * Sets the openItemsTotal attribute value.
         *
         * @param openItemsTotal The openItemsTotal to set.
         */
        public void setOpenItemsTotal(KualiDecimal openItemsTotal) {
            this.openItemsTotal = openItemsTotal;
        }

        /**
         * Sets the undepositedCashieringChecksTotal attribute value.
         *
         * @param undepositedCashieringChecksTotal The undepositedCashieringChecksTotal to set.
         */
        public void setUndepositedCashieringChecksTotal(KualiDecimal undepositedCashieringChecksTotal) {
            this.undepositedCashieringChecksTotal = undepositedCashieringChecksTotal;
        }

        /**
         * Gets the openItemsTotal attribute.
         *
         * @return Returns the openItemsTotal.
         */
        public KualiDecimal getOpenItemsTotal() {
            return openItemsTotal;
        }

        /**
         * Gets the depositedCashieringChecksTotal attribute.
         *
         * @return Returns the depositedCashieringChecksTotal.
         */
        public KualiDecimal getDepositedCashieringChecksTotal() {
            return depositedCashieringChecksTotal;
        }

        /**
         * Gets the undepositedCashieringChecksTotal attribute.
         *
         * @return Returns the undepositedCashieringChecksTotal.
         */
        public KualiDecimal getUndepositedCashieringChecksTotal() {
            return undepositedCashieringChecksTotal;
        }

        /**
         * @return current value of overalllStats.
         */
        public CashReceiptStatistics getOverallReceiptStats() {
            return overallReceiptStats;
        }

        public static final class CashReceiptStatistics implements Serializable{
            protected int receiptCount;
            protected KualiDecimal checkTotal;
            protected KualiDecimal currencyTotal;
            protected KualiDecimal coinTotal;

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
                receipt.refreshCashDetails();
                receiptCount++;
                checkTotal = checkTotal.add(receipt.getTotalConfirmedCheckAmount());
                currencyTotal = currencyTotal.add(receipt.getTotalConfirmedCashAmount()).subtract(receipt.getTotalChangeCashAmount());
                coinTotal = coinTotal.add(receipt.getTotalConfirmedCoinAmount()).subtract(receipt.getTotalChangeCoinAmount());
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
             * This method doesn't do anything but appease the demands of POJO form population...I mean...er...this sets the sum
             * total, now doesn't it?
             *
             * @param total total you want this method to ignore
             */
            public void setSumTotal(KualiDecimal total) {
                // don't do anything. just be very quiet and maybe the POJO loader will be satisfied
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

    /**
     * @see org.kuali.rice.kns.web.struts.pojo.PojoFormBase#postprocessRequestParameters(java.util.Map)
     */
    @Override
    public void postprocessRequestParameters(Map requestParameters) {
        super.postprocessRequestParameters(requestParameters);
        // fish the campus code name out of the parameters
        String[] campusCodes = (String[]) requestParameters.get(CashManagementForm.CAMPUS_CODE_PROPERTY);
        String campusCode = null;
        if (campusCodes != null && campusCodes.length > 0) {
            campusCode = campusCodes[0];
        }
        if (campusCode != null && getCashManagementDocument() != null) {
            // use that to put the cash drawer back into the cash management document
            getCashManagementDocument().setCashDrawer(SpringContext.getBean(CashDrawerService.class).getByCampusCode(campusCode));
        }
    }


}
