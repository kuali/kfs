/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.financial.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.document.AmountTotaling;
import org.kuali.core.document.Copyable;
import org.kuali.core.rule.event.KualiDocumentEvent;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.format.CurrencyFormatter;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.financial.bo.CashReceiptHeader;
import org.kuali.module.financial.bo.Check;
import org.kuali.module.financial.bo.CheckBase;
import org.kuali.module.financial.bo.CoinDetail;
import org.kuali.module.financial.bo.CurrencyDetail;
import org.kuali.module.financial.rule.event.AddCheckEvent;
import org.kuali.module.financial.rule.event.DeleteCheckEvent;
import org.kuali.module.financial.rule.event.UpdateCheckEvent;
import org.kuali.module.financial.service.CashReceiptService;
import org.kuali.module.financial.service.CheckService;
import org.kuali.module.gl.util.SufficientFundsItem;

/**
 * This is the business object that represents the CashReceiptDocument in Kuali. This is a transactional document that will
 * eventually post transactions to the G/L. It integrates with workflow. Since a Cash Receipt is a one sided transactional document,
 * only accepting funds into the university, the accounting line data will be held in the source accounting line data structure
 * only.
 */
public class CashReceiptDocument extends CashReceiptFamilyBase implements Copyable, AmountTotaling {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CashReceiptDocument.class);

    public static final String CHECK_ENTRY_DETAIL = "individual";
    public static final String CHECK_ENTRY_TOTAL = "totals";

    public static final String DOCUMENT_TYPE = "CR";

    // child object containers - for all the different reconciliation detail sections
    private String checkEntryMode = CHECK_ENTRY_DETAIL;
    private List checks = new ArrayList();

    // incrementers for detail lines
    private Integer nextCheckSequenceId = new Integer(1);

    // monetary attributes
    private KualiDecimal totalCashAmount = KualiDecimal.ZERO;
    private KualiDecimal totalCheckAmount = KualiDecimal.ZERO;
    private KualiDecimal totalCoinAmount = KualiDecimal.ZERO;

    private CurrencyDetail currencyDetail;
    private CoinDetail coinDetail;

    private CashReceiptHeader cashReceiptHeader;

    /**
     * Initializes the array lists and line incrementers.
     */
    public CashReceiptDocument() {
        super();

        if (GlobalVariables.getUserSession() != null && GlobalVariables.getUserSession().getUniversalUser() != null && GlobalVariables.getUserSession().getUniversalUser().getCampusCode() != null) {
            // there are situations where workflow is reindexing this where there is no user session; but in those cases, the campus location code will get set by the db anyway, so just ignore those cases
            setCampusLocationCode(GlobalVariables.getUserSession().getUniversalUser().getCampusCode());
        }
        currencyDetail = new CurrencyDetail();
        coinDetail = new CoinDetail();
    }

    /**
     * Gets the totalCashAmount attribute.
     * 
     * @return Returns the totalCashAmount.
     */
    public KualiDecimal getTotalCashAmount() {
        return (currencyDetail != null) ? currencyDetail.getTotalAmount() : KualiDecimal.ZERO;
    }

    /**
     * This method returns the cash total amount as a currency formatted string.
     * 
     * @return String
     */
    public String getCurrencyFormattedTotalCashAmount() {
        return (String) new CurrencyFormatter().format(getTotalCashAmount());
    }

    /**
     * Sets the totalCashAmount attribute value.
     * 
     * @param cashAmount The totalCashAmount to set.
     */
    public void setTotalCashAmount(KualiDecimal cashAmount) {
        this.totalCashAmount = cashAmount;
    }


    /**
     * @param checkEntryMode
     */
    public void setCheckEntryMode(String checkEntryMode) {
        this.checkEntryMode = checkEntryMode;
    }

    /**
     * @return checkEntryMode
     */
    public String getCheckEntryMode() {
        return checkEntryMode;
    }


    /**
     * Gets the checks attribute.
     * 
     * @return Returns the checks.
     */
    public List<Check> getChecks() {
        return checks;
    }

    /**
     * Sets the checks attribute value.
     * 
     * @param checks The checks to set.
     */
    public void setChecks(List checks) {
        this.checks = checks;
    }

    /**
     * Gets the number of checks, since Sun doesn't have a direct getter for collection size
     * 
     * @return the number of checks
     */
    public int getCheckCount() {
        int count = 0;
        if (ObjectUtils.isNotNull(checks)) {
            count = checks.size();
        }
        return count;
    }


    /**
     * Adds a new check to the list.
     * 
     * @param check
     */
    public void addCheck(Check check) {
        check.setSequenceId(this.nextCheckSequenceId);

        this.checks.add(check);

        this.nextCheckSequenceId = new Integer(this.nextCheckSequenceId.intValue() + 1);

        setTotalCheckAmount(getTotalCheckAmount().add(check.getAmount()));
    }

    /**
     * Retrieve a particular check at a given index in the list of checks.
     * 
     * @param index
     * @return Check
     */
    public Check getCheck(int index) {
        while (this.checks.size() <= index) {
            checks.add(createNewCheck());
        }
        return (Check) checks.get(index);
    }


    /**
     * @see org.kuali.kfs.document.AccountingDocumentBase#checkSufficientFunds()
     */
    @Override
    public List<SufficientFundsItem> checkSufficientFunds() {
        LOG.debug("checkSufficientFunds() started");

        // This document does not do sufficient funds checking
        return new ArrayList<SufficientFundsItem>();
    }

    /**
     * Override to set the document status to VERIFIED ("V") when the document is FINAL. When the Cash Management document that this
     * is associated with is FINAL approved, this status will be set to APPROVED ("A") to be picked up by the GL for processing.
     * That's done in the handleRouteStatusChange() method in the CashManagementDocument.
     * 
     * @see org.kuali.core.document.Document#handleRouteStatusChange()
     */
    @Override
    public void handleRouteStatusChange() {
        super.handleRouteStatusChange();
        // Workflow Status of PROCESSED --> Kuali Doc Status of Verified
        if (getDocumentHeader().getWorkflowDocument().stateIsProcessed()) {
            this.getDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED);
            LOG.info("Adding Cash to Cash Drawer");
            SpringContext.getBean(CashReceiptService.class).addCashDetailsToCashDrawer(this);
        }
    }

    /**
     * This method removes a check from the list and updates the total appropriately.
     * 
     * @param index
     */
    public void removeCheck(int index) {
        Check check = (Check) checks.remove(index);
        KualiDecimal newTotalCheckAmount = getTotalCheckAmount().subtract(check.getAmount());
        // if the totalCheckAmount goes negative, bring back to zero.
        if (newTotalCheckAmount.isNegative()) {
            newTotalCheckAmount = KualiDecimal.ZERO;
        }
        setTotalCheckAmount(newTotalCheckAmount);
    }

    /**
     * Gets the nextCheckSequenceId attribute.
     * 
     * @return Returns the nextCheckSequenceId.
     */
    public Integer getNextCheckSequenceId() {
        return nextCheckSequenceId;
    }

    /**
     * Sets the nextCheckSequenceId attribute value.
     * 
     * @param nextCheckSequenceId The nextCheckSequenceId to set.
     */
    public void setNextCheckSequenceId(Integer nextCheckSequenceId) {
        this.nextCheckSequenceId = nextCheckSequenceId;
    }

    /**
     * Gets the totalCheckAmount attribute.
     * 
     * @return Returns the totalCheckAmount.
     */
    public KualiDecimal getTotalCheckAmount() {
        if (totalCheckAmount == null) {
            setTotalCheckAmount(KualiDecimal.ZERO);
        }
        return totalCheckAmount;
    }

    /**
     * This method returns the check total amount as a currency formatted string.
     * 
     * @return String
     */
    public String getCurrencyFormattedTotalCheckAmount() {
        return (String) new CurrencyFormatter().format(getTotalCheckAmount());
    }

    /**
     * Sets the totalCheckAmount attribute value.
     * 
     * @param totalCheckAmount The totalCheckAmount to set.
     */
    public void setTotalCheckAmount(KualiDecimal totalCheckAmount) {
        this.totalCheckAmount = totalCheckAmount;
    }

    /**
     * Gets the totalCoinAmount attribute.
     * 
     * @return Returns the totalCoinAmount.
     */
    public KualiDecimal getTotalCoinAmount() {
        return (coinDetail != null) ? coinDetail.getTotalAmount() : KualiDecimal.ZERO;
    }

    /**
     * This method returns the coin total amount as a currency formatted string.
     * 
     * @return String
     */
    public String getCurrencyFormattedTotalCoinAmount() {
        return (String) new CurrencyFormatter().format(getTotalCoinAmount());
    }

    /**
     * Sets the totalCoinAmount attribute value.
     * 
     * @param totalCoinAmount The totalCoinAmount to set.
     */
    public void setTotalCoinAmount(KualiDecimal totalCoinAmount) {
        this.totalCoinAmount = totalCoinAmount;
    }

    /**
     * This method returns the overall total of the document - coin plus check plus cash.
     * 
     * @see org.kuali.kfs.document.AccountingDocumentBase#getTotalDollarAmount()
     * @return KualiDecimal
     */
    @Override
    public KualiDecimal getTotalDollarAmount() {
        KualiDecimal sumTotalAmount = getTotalCoinAmount().add(getTotalCheckAmount()).add(getTotalCashAmount());
        return sumTotalAmount;
    }

    /**
     * Gets the coinDetail attribute.
     * 
     * @return Returns the coinDetail.
     */
    public CoinDetail getCoinDetail() {
        return coinDetail;
    }

    /**
     * Sets the coinDetail attribute value.
     * 
     * @param coinDetail The coinDetail to set.
     */
    public void setCoinDetail(CoinDetail coinDetail) {
        this.coinDetail = coinDetail;
    }

    /**
     * Gets the currencyDetail attribute.
     * 
     * @return Returns the currencyDetail.
     */
    public CurrencyDetail getCurrencyDetail() {
        return currencyDetail;
    }

    /**
     * Sets the currencyDetail attribute value.
     * 
     * @param currencyDetail The currencyDetail to set.
     */
    public void setCurrencyDetail(CurrencyDetail currencyDetail) {
        this.currencyDetail = currencyDetail;
    }

    /**
     * Retrieves the summed total amount in a currency format with commas.
     * 
     * @return String
     */
    public String getCurrencyFormattedSumTotalAmount() {
        return (String) new CurrencyFormatter().format(getTotalDollarAmount());
    }

    /**
     * @return sum of the amounts of the current list of checks
     */
    public KualiDecimal calculateCheckTotal() {
        KualiDecimal total = KualiDecimal.ZERO;
        for (Iterator i = getChecks().iterator(); i.hasNext();) {
            Check c = (Check) i.next();
            if (null != c.getAmount()) {
                total = total.add(c.getAmount());
            }
        }
        return total;
    }


    /**
     * @see org.kuali.core.document.DocumentBase#prepareForSave()
     */
    @Override
    public void prepareForSave() {
        super.prepareForSave();

        // clear check list if mode is checkTotal
        if (CHECK_ENTRY_TOTAL.equals(getCheckEntryMode())) {
            getChecks().clear();
        }
        // update total if mode is checkDetail
        else {
            setTotalCheckAmount(calculateCheckTotal());
        }
    }

    /**
     * @see org.kuali.core.document.DocumentBase#processAfterRetrieve()
     */
    @Override
    public void processAfterRetrieve() {
        super.processAfterRetrieve();

        // set to checkTotal mode if no checks
        List checkList = getChecks();
        if (ObjectUtils.isNull(checkList) || checkList.isEmpty()) {
            setCheckEntryMode(CHECK_ENTRY_TOTAL);
        }
        // set to checkDetail mode if checks (and update the checkTotal, while you're here)
        else {
            setCheckEntryMode(CHECK_ENTRY_DETAIL);
            setTotalCheckAmount(calculateCheckTotal());
        }
        refreshCashDetails();
    }

    /**
     * @see org.kuali.core.document.DocumentBase#postProcessSave(org.kuali.core.rule.event.KualiDocumentEvent)
     */
    @Override
    public void postProcessSave(KualiDocumentEvent event) {
        super.postProcessSave(event);

        if (retrieveCurrencyDetail() == null) {
            getCurrencyDetail().setDocumentNumber(this.getDocumentNumber());
            getCurrencyDetail().setFinancialDocumentTypeCode(CashReceiptDocument.DOCUMENT_TYPE);
            getCurrencyDetail().setCashieringRecordSource(KFSConstants.CurrencyCoinSources.CASH_RECEIPTS);
        }

        if (retrieveCoinDetail() == null) {
            getCoinDetail().setDocumentNumber(this.getDocumentNumber());
            getCoinDetail().setFinancialDocumentTypeCode(CashReceiptDocument.DOCUMENT_TYPE);
            getCoinDetail().setCashieringRecordSource(KFSConstants.CurrencyCoinSources.CASH_RECEIPTS);
        }

        SpringContext.getBean(BusinessObjectService.class).save(getCurrencyDetail());
        SpringContext.getBean(BusinessObjectService.class).save(getCoinDetail());
    }

    /**
     * This method refreshes the currency/coin details for this cash receipt document
     */
    public void refreshCashDetails() {
        this.currencyDetail = retrieveCurrencyDetail();
        this.coinDetail = retrieveCoinDetail();
    }

    /**
     * Get this document's currency detail from the database
     * 
     * @return the currency detail record for this cash receipt document
     */
    private CurrencyDetail retrieveCurrencyDetail() {
        return (CurrencyDetail) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(CurrencyDetail.class, getCashDetailPrimaryKey());
    }

    /**
     * Grab this document's coin detail from the database
     * 
     * @return the coin detail record for this cash receipt document
     */
    private CoinDetail retrieveCoinDetail() {
        return (CoinDetail) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(CoinDetail.class, getCashDetailPrimaryKey());
    }

    /**
     * Gets the cashReceiptHeader attribute.
     * 
     * @return Returns the cashReceiptHeader.
     */
    public CashReceiptHeader getCashReceiptHeader() {
        return cashReceiptHeader;
    }

    /**
     * Sets the cashReceiptHeader attribute value.
     * 
     * @param cashReceiptHeader The cashReceiptHeader to set.
     */
    public void setCashReceiptHeader(CashReceiptHeader cashReceiptHeader) {
        this.cashReceiptHeader = cashReceiptHeader;
    }

    /**
     * Generate the primary key for a currency or coin detail related to this document
     * 
     * @return a map with a representation of the proper primary key
     */
    private Map getCashDetailPrimaryKey() {
        Map pk = new HashMap();
        pk.put("documentNumber", this.getDocumentNumber());
        pk.put("financialDocumentTypeCode", CashReceiptDocument.DOCUMENT_TYPE);
        pk.put("cashieringRecordSource", KFSConstants.CurrencyCoinSources.CASH_RECEIPTS);
        return pk;
    }

    /**
     * @see org.kuali.core.document.TransactionalDocumentBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List buildListOfDeletionAwareLists() {
        List managedLists = super.buildListOfDeletionAwareLists();
        managedLists.add(getChecks());

        return managedLists;
    }

    @Override
    public List generateSaveEvents() {
        // 1. retrieve persisted checks for document
        // 2. retrieve current checks from given document
        // 3. compare, creating add/delete/update events as needed
        // 4. apply rules as appropriate returned events
        List persistedChecks = SpringContext.getBean(CheckService.class).getByDocumentHeaderId(getDocumentNumber());
        List currentChecks = getChecks();

        List events = generateEvents(persistedChecks, currentChecks, KFSConstants.EXISTING_CHECK_PROPERTY_NAME, this);

        return events;
    }

    /**
     * Generates a List of instances of CheckEvent subclasses, one for each changed check in the union of the persistedLines and
     * currentLines lists. Events in the list will be grouped in order by event-type (update, add, delete).
     * 
     * @param persistedChecks
     * @param currentChecks
     * @param errorPathPrefix
     * @param crdoc
     * @return List of CheckEvent subclass instances
     */
    private List generateEvents(List persistedChecks, List currentChecks, String errorPathPrefix, CashReceiptFamilyBase crdoc) {
        List addEvents = new ArrayList();
        List updateEvents = new ArrayList();
        List deleteEvents = new ArrayList();

        //
        // generate events
        Map persistedCheckMap = buildCheckMap(persistedChecks);

        // (iterate through current lines to detect additions and updates, removing affected lines from persistedLineMap as we go
        // so deletions can be detected by looking at whatever remains in persistedLineMap)
        int index = 0;
        for (Iterator i = currentChecks.iterator(); i.hasNext(); index++) {
            Check currentCheck = (Check) i.next();
            Integer key = currentCheck.getSequenceId();

            Check persistedCheck = (Check) persistedCheckMap.get(key);
            // if line is both current and persisted...
            if (persistedCheck != null) {
                // ...check for updates
                if (!currentCheck.isLike(persistedCheck)) {
                    UpdateCheckEvent updateEvent = new UpdateCheckEvent(errorPathPrefix, crdoc, currentCheck);
                    updateEvents.add(updateEvent);
                }
                else {
                    // do nothing, since this line hasn't changed
                }

                persistedCheckMap.remove(key);
            }
            else {
                // it must be a new addition
                AddCheckEvent addEvent = new AddCheckEvent(errorPathPrefix, crdoc, currentCheck);
                addEvents.add(addEvent);
            }
        }

        // detect deletions
        for (Iterator i = persistedCheckMap.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();
            Check persistedCheck = (Check) e.getValue();
            DeleteCheckEvent deleteEvent = new DeleteCheckEvent(errorPathPrefix, crdoc, persistedCheck);
            deleteEvents.add(deleteEvent);
        }


        //
        // merge the lists
        List lineEvents = new ArrayList();
        lineEvents.addAll(updateEvents);
        lineEvents.addAll(addEvents);
        lineEvents.addAll(deleteEvents);

        return lineEvents;
    }


    /**
     * @param checks
     * @return Map containing Checks from the given List, indexed by their sequenceId
     */
    private Map buildCheckMap(List checks) {
        Map checkMap = new HashMap();

        for (Iterator i = checks.iterator(); i.hasNext();) {
            Check check = (Check) i.next();
            Integer sequenceId = check.getSequenceId();

            Object oldCheck = checkMap.put(sequenceId, check);

            // verify that sequence numbers are unique...
            if (oldCheck != null) {
                throw new IllegalStateException("sequence id collision detected for sequence id " + sequenceId);
            }
        }

        return checkMap;
    }

    public Check createNewCheck() {
        Check newCheck = new CheckBase();
        newCheck.setFinancialDocumentTypeCode(DOCUMENT_TYPE);
        newCheck.setCashieringRecordSource(KFSConstants.CheckSources.CASH_RECEIPTS);
        return newCheck;
    }


}