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
package org.kuali.kfs.fp.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.fp.businessobject.Check;
import org.kuali.kfs.fp.businessobject.CheckBase;
import org.kuali.kfs.fp.businessobject.CoinDetail;
import org.kuali.kfs.fp.businessobject.CurrencyDetail;
import org.kuali.kfs.fp.businessobject.DepositCashReceiptControl;
import org.kuali.kfs.fp.document.service.CashReceiptService;
import org.kuali.kfs.fp.document.validation.event.AddCheckEvent;
import org.kuali.kfs.fp.document.validation.event.DeleteCheckEvent;
import org.kuali.kfs.fp.document.validation.event.UpdateCheckEvent;
import org.kuali.kfs.fp.service.CheckService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.DocumentStatusCodes.CashReceipt;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.businessobject.SufficientFundsItem;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.web.format.CurrencyFormatter;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.document.Copyable;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.rice.krad.rules.rule.event.SaveDocumentEvent;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This is the business object that represents the CashReceiptDocument in Kuali. This is a transactional document that will
 * eventually post transactions to the G/L. It integrates with workflow. Since a Cash Receipt is a one sided transactional document,
 * only accepting funds into the university, the accounting line data will be held in the source accounting line data structure
 * only.
 */
public class CashReceiptDocument extends CashReceiptFamilyBase implements Copyable, AmountTotaling, CapitalAssetEditable {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CashReceiptDocument.class);

    public static final String CHECK_ENTRY_DETAIL = "individual";
    public static final String CHECK_ENTRY_TOTAL = "totals";

    public static final String DOCUMENT_TYPE = "CR";

    // child object containers - for all the different reconciliation detail sections
    protected String checkEntryMode = CHECK_ENTRY_DETAIL;
    protected List<Check> checks = new ArrayList<Check>();
    protected List<Check> confirmedChecks = new ArrayList<Check>();
    
    // deposit controls
    protected List<DepositCashReceiptControl> depositCashReceiptControl = new ArrayList<DepositCashReceiptControl>();

    // incrementers for detail lines
    protected Integer nextCheckSequenceId = new Integer(1);
    protected Integer nextConfirmedCheckSequenceId = new Integer(1);

    // monetary attributes
    protected KualiDecimal totalCashAmount = KualiDecimal.ZERO;
    protected KualiDecimal totalCheckAmount = KualiDecimal.ZERO;
    protected KualiDecimal totalCoinAmount = KualiDecimal.ZERO;
    protected KualiDecimal sumTotalAmount = KualiDecimal.ZERO;
    
    protected KualiDecimal totalConfirmedCashAmount = KualiDecimal.ZERO;
    protected KualiDecimal totalConfirmedCheckAmount = KualiDecimal.ZERO;
    protected KualiDecimal totalConfirmedCoinAmount = KualiDecimal.ZERO;
    
    protected KualiDecimal totalChangeAmount = KualiDecimal.ZERO;

    protected CurrencyDetail currencyDetail;
    protected CoinDetail coinDetail;
    
    protected CurrencyDetail confirmedCurrencyDetail;
    protected CoinDetail confirmedCoinDetail;
    
    protected CurrencyDetail changeCurrencyDetail;
    protected CoinDetail changeCoinDetail;
    
    protected boolean recategorized;

    protected String createDate;
    
    /**
     * Initializes the array lists and line incrementers.
     */
    public CashReceiptDocument() {
        super();

        initializeCampusLocationCode();
        
        currencyDetail = new CurrencyDetail();
        coinDetail = new CoinDetail();
        
        confirmedCurrencyDetail = new CurrencyDetail();
        confirmedCoinDetail = new CoinDetail();
        
        changeCurrencyDetail = new CurrencyDetail();
        changeCoinDetail = new CoinDetail();
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
     * Gets the totalConfirmedCashAmount attribute.
     * 
     * @return Returns the totalConfirmedCashAmount.
     */
    public KualiDecimal getTotalConfirmedCashAmount() {
        return (confirmedCurrencyDetail != null) ? confirmedCurrencyDetail.getTotalAmount() : KualiDecimal.ZERO;
    }
    
    /**
     * Gets the totalChangeCashAmount attribute.
     * 
     * @return Returns the totalChangeCashAmount.
     */
    public KualiDecimal getTotalChangeCashAmount() {
        return (changeCurrencyDetail != null) ? changeCurrencyDetail.getTotalAmount() : KualiDecimal.ZERO;
    }
    
    /**
     * Gets the total amount of depositable checks
     * @return
     */
    public KualiDecimal getTotalDepositableCheckAmount() {
        KualiDecimal totalDepositableCheckAmount = KualiDecimal.ZERO;
        List<Check> checks = getConfirmedChecks();
        if (checks != null && !checks.isEmpty()) {
            for (Check check : checks) {
                if(check.getCashieringStatus().equals("C")) {
                    totalDepositableCheckAmount = totalDepositableCheckAmount.add(check.getAmount());
                }
            }
        }
        return totalDepositableCheckAmount;        
    }
    
    /**
     * Checks if there are checks to be deposited
     * @return
     */
    public boolean existDepositableChecks() {        
        return getTotalDepositableCheckAmount().isGreaterThan(KualiDecimal.ZERO) ? true : false;        
    }

    /**
     * This method returns the total depositable check amount as a currency formatted string.
     * 
     * @return
     */
    public String getCurrencyFormattedTotalDepositableCheckAmount() {
        return (String) new CurrencyFormatter().format(getTotalDepositableCheckAmount());
    }

    /**
     * This method returns the cash total amount as a currency formatted string.
     * 
     * @return String
     */
    public String getCurrencyFormattedTotalCashAmount() {
        return (String) new CurrencyFormatter().format(getTotalCashAmount());
    }

    
    public String getCurrencyFormattedTotalConfirmedCashAmount() {
        return (String) new CurrencyFormatter().format(getTotalConfirmedCashAmount());
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
     * Sets the totalConfirmedCashAmount attribute value.
     * 
     * @param cashAmount The totalConfirmedCashAmount to set.
     */
    public void setTotalConfirmedCashAmount(KualiDecimal confirmedCashAmount) {
        this.totalConfirmedCashAmount = confirmedCashAmount;
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
    public void setChecks(List<Check> checks) {
        this.checks = checks;
    }

    /**
     * Gets the confirmed checks attribute.
     * 
     * @return Returns the confirmed checks.
     */
    public List<Check> getConfirmedChecks() {
        return confirmedChecks;
    }

    /**
     * Sets the confirmed checks attribute value.
     * 
     * @param confirmedChecks The checks to set.
     */
    public void setConfirmedChecks(List<Check> confirmedChecks) {
        this.confirmedChecks = confirmedChecks;
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
     * Gets the number of confirmed checks
     * 
     * @return the number of checks
     */
    public int getConfirmedCheckCount() {
        int count = 0;
        if (ObjectUtils.isNotNull(confirmedChecks)) {
            count = confirmedChecks.size();
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
     * Adds a new confirmed check to the list.
     * 
     * @param check
     */
    public void addConfirmedCheck(Check check) {
        check.setSequenceId(this.nextConfirmedCheckSequenceId);

        this.confirmedChecks.add(check);

        this.nextConfirmedCheckSequenceId = new Integer(this.nextConfirmedCheckSequenceId.intValue() + 1);

        setTotalConfirmedCheckAmount(getTotalConfirmedCheckAmount().add(check.getAmount()));
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
     * Retrieve a particular check at a given index in the list of confirmed checks.
     * 
     * @param index
     * @return Check
     */
    public Check getConfirmedCheck(int index) {
        while (this.confirmedChecks.size() <= index) {
            confirmedChecks.add(createNewConfirmedCheck());
        }
        return (Check) confirmedChecks.get(index);
    }


    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#checkSufficientFunds()
     */
    @Override
    public List<SufficientFundsItem> checkSufficientFunds() {
        LOG.debug("checkSufficientFunds() started");

        // This document does not do sufficient funds checking
        return new ArrayList<SufficientFundsItem>();
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
     * This method removes a confirmed check from the list and updates the total appropriately.
     * 
     * @param index
     */
    public void removeConfirmedCheck(int index) {
        Check check = (Check) confirmedChecks.remove(index);
        KualiDecimal newTotalCheckAmount = getTotalConfirmedCheckAmount().subtract(check.getAmount());
        // if the totalCheckAmount goes negative, bring back to zero.
        if (newTotalCheckAmount.isNegative()) {
            newTotalCheckAmount = KualiDecimal.ZERO;
        }
        setTotalConfirmedCheckAmount(newTotalCheckAmount);
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
     * Gets the nextConfirmedCheckSequenceId attribute.
     * 
     * @return Returns the nextConfirmedCheckSequenceId.
     */
    public Integer getNextConfirmedCheckSequenceId() {
        return nextConfirmedCheckSequenceId;
    }

    /**
     * Sets the nextCheckSequenceId attribute value.
     * 
     * @param nextCheckSequenceId The nextCheckSequenceId to set.
     */
    public void setNextConfirmedCheckSequenceId(Integer nextConfirmedCheckSequenceId) {
        this.nextConfirmedCheckSequenceId = nextConfirmedCheckSequenceId;
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
     * Gets the totalConfirmedCheckAmount attribute.
     * 
     * @return Returns the totalCheckAmount.
     */
    public KualiDecimal getTotalConfirmedCheckAmount() {
        if (totalConfirmedCheckAmount == null) {
            setTotalConfirmedCheckAmount(KualiDecimal.ZERO);
        }
        return totalConfirmedCheckAmount;
    }

    /**
     * Gets the totalConfirmedCheckAmount attribute.
     * 
     * @return Returns the totalCheckAmount.
     */
    public KualiDecimal getTotalConfirmedCheckAmountForInterim() {
        if (totalConfirmedCheckAmount == null || getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode().equals(CashReceipt.INTERIM)) {
            setTotalConfirmedCheckAmount(KualiDecimal.ZERO);
        }
        return totalConfirmedCheckAmount;
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
     * This method returns the confirmed check total amount as a currency formatted string.
     * 
     * @return String
     */
    public String getCurrencyFormattedTotalConfirmedCheckAmount() {
        String amountString = (String) new CurrencyFormatter().format(getTotalConfirmedCheckAmount());        
        return amountString;
        
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
     * Sets the totalCheckAmount attribute value.
     * 
     * @param totalCheckAmount The totalCheckAmount to set.
     */
    public void setTotalConfirmedCheckAmount(KualiDecimal totalConfirmedCheckAmount) {
        this.totalConfirmedCheckAmount = totalConfirmedCheckAmount;
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
     * Gets the totalConfirmedCoinAmount attribute.
     * 
     * @return Returns the totalConfirmedCoinAmount.
     */
    public KualiDecimal getTotalConfirmedCoinAmount() {
        return (confirmedCoinDetail != null) ? confirmedCoinDetail.getTotalAmount() : KualiDecimal.ZERO;
    }
    
    /**
     * Gets the totalChangeCoinAmount attribute.
     * 
     * @return Returns the totalChangeCoinAmount.
     */
    public KualiDecimal getTotalChangeCoinAmount() {
        return (changeCoinDetail != null) ? changeCoinDetail.getTotalAmount() : KualiDecimal.ZERO;
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
     * This method returns the confirmed coin total amount as a currency formatted string.
     * 
     * @return String
     */
    public String getCurrencyFormattedTotalConfirmedCoinAmount() {
        return (String) new CurrencyFormatter().format(getTotalConfirmedCoinAmount());
    }
    
    /**
     * returns (confirmed currency + confirmed coin - change amount)
     * 
     * @return
     */
    public KualiDecimal getGrandTotalConfirmedCashAmount() {
        return getTotalConfirmedCashAmount().add(getTotalConfirmedCoinAmount()).subtract(getTotalChangeAmount());
    }

    /**
     * returns (confirmed currency + confirmed coin - change amount) as a currency formatted string
     * @return
     */
    public String getCurrencyFormattedGrandTotalConfirmedCashAmount() {
        return (String) new CurrencyFormatter().format(getGrandTotalConfirmedCashAmount());
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
     * Sets the totalConfirmedCoinAmount attribute value.
     * 
     * @param totalCoinAmount The totalConfirmedCoinAmount to set.
     */
    public void setTotalConfirmedCoinAmount(KualiDecimal totalConfirmedCoinAmount) {
        this.totalConfirmedCoinAmount = totalConfirmedCoinAmount;
    }

    /**
     * This method returns the overall total of the document - coin plus check plus cash.
     * 
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#getTotalDollarAmount()
     * @return KualiDecimal
     */
    @Override
    public KualiDecimal getTotalDollarAmount() {
        if(getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode().equals(KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED)) {
            return getTotalConfirmedDollarAmount();
        } else {
            return getTotalCoinAmount().add(getTotalCheckAmount()).add(getTotalCashAmount());
        }
    }
    
    /**
     * This method returns the overall confirmed total of the document - coin plus check plus cash.
     * 
     * @return KualiDecimal
     */
    public KualiDecimal getTotalConfirmedDollarAmount() {
        KualiDecimal sumTotalAmount = getTotalConfirmedCoinAmount().add(getTotalConfirmedCheckAmount()).add(getTotalConfirmedCashAmount());
        return sumTotalAmount;
    }
    
    public KualiDecimal getTotalChangeAmount() {
        return getTotalChangeCoinAmount().add(getTotalChangeCashAmount());
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
     * Gets the confirmedCoinDetail attribute.
     * 
     * @return Returns the confirmedCoinDetail.
     */
    public CoinDetail getConfirmedCoinDetail() {
        return confirmedCoinDetail;
    }

    /**
     * Sets the confirmedCoinDetail attribute value.
     * 
     * @param coinDetail The confirmedCoinDetail to set.
     */
    public void setConfirmedCoinDetail(CoinDetail coinDetail) {
        this.confirmedCoinDetail = coinDetail;
    }
    
    /**
     * Gets the changeCoinDetail attribute.
     * 
     * @return Returns the changeCoinDetail.
     */
    public CoinDetail getChangeCoinDetail() {
        return changeCoinDetail;
    }

    /**
     * Sets the changeCoinDetail attribute value.
     * 
     * @param coinDetail The changeCoinDetail to set.
     */
    public void setChangeCoinDetail(CoinDetail changeCoinDetail) {
        this.changeCoinDetail = changeCoinDetail;
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
     * Gets the confirmedCurrencyDetail attribute.
     * 
     * @return Returns the confirmedCurrencyDetail.
     */
    public CurrencyDetail getConfirmedCurrencyDetail() {
        return confirmedCurrencyDetail;
    }

    /**
     * Sets the confirmedCurrencyDetail attribute value.
     * 
     * @param currencyDetail The confirmedCurrencyDetail to set.
     */
    public void setConfirmedCurrencyDetail(CurrencyDetail confirmedCurrencyDetail) {
        this.confirmedCurrencyDetail = confirmedCurrencyDetail;
    }
    
    /**
     * Gets the changeCurrencyDetail attribute.
     * 
     * @return Returns the changeCurrencyDetail.
     */
    public CurrencyDetail getChangeCurrencyDetail() {
        return changeCurrencyDetail;
    }

    /**
     * Sets the changeCurrencyDetail attribute value.
     * 
     * @param currencyDetail The changeCurrencyDetail to set.
     */
    public void setChangeCurrencyDetail(CurrencyDetail changeCurrencyDetail) {
        this.changeCurrencyDetail = changeCurrencyDetail;
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
     * Retrieves the confirmed summed total amount in a currency format with commas.
     * 
     * @return String
     */
    public String getCurrencyFormattedConfirmedSumTotalAmount() {
        return (String) new CurrencyFormatter().format(getTotalConfirmedDollarAmount());
    }
    
    /**
     * Retrieves the final summed total amount (after subtracting change amount)in currency format with commas.
     * 
     * @return String
     */
    public String getCurrencyFormattedFinalSumTotalAmount() {
        return (String) new CurrencyFormatter().format(getTotalConfirmedDollarAmount().subtract(getTotalChangeAmount()));
    }
    
    
    /**
     * Retrieves the change total amount in a currency format with commas.
     * 
     * @return String
     */
    public String getCurrencyFormattedChangeTotalAmount() {
        return (String) new CurrencyFormatter().format(getTotalChangeAmount());
    }

    /**
     * Gets the recategorized attribute. 
     * @return Returns the recategorized.
     */
    public boolean isRecategorized() {
        return recategorized;
    }

    /**
     * Sets the recategorized attribute value.
     * @param recategorized The recategorized to set.
     */
    public void setRecategorized(boolean recategorized) {
        this.recategorized = recategorized;
    }

    /**
     * @return sum of the amounts of the current list of checks
     */
    public KualiDecimal calculateCheckTotal() {
        KualiDecimal total = KualiDecimal.ZERO;
        for (Iterator<Check> i = getChecks().iterator(); i.hasNext();) {
            Check c = i.next();
            if (null != c.getAmount()) {
                total = total.add(c.getAmount());
            }
        }
        return total;
    }
    
    /**
     * @return sum of the amounts of the current list of checks
     */
    public KualiDecimal calculateConfirmedCheckTotal() {
        KualiDecimal total = KualiDecimal.ZERO;
        for (Iterator<Check> i = getConfirmedChecks().iterator(); i.hasNext();) {
            Check c = i.next();
            if (null != c.getAmount()) {
                total = total.add(c.getAmount());
            }
        }
        return total;
    }


    /**
     * @see org.kuali.rice.kns.document.DocumentBase#prepareForSave()
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
     * @see org.kuali.rice.kns.document.DocumentBase#processAfterRetrieve()
     */
    @Override
    public void processAfterRetrieve() {
        super.processAfterRetrieve();

        // set to checkTotal mode if no checks
        List<Check> checkList = getChecks();
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
     * Override to set the document status to VERIFIED ("V") when the document is FINAL. When the Cash Management document that this
     * is associated with is FINAL approved, this status will be set to APPROVED ("A") to be picked up by the GL for processing.
     * That's done in the doRouteStatusChange() method in the CashManagementDocument.
     * 
     * @see org.kuali.rice.kns.document.Document#doRouteStatusChange()
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);        
        WorkflowDocument workflowDocument = getDocumentHeader().getWorkflowDocument();
        
        // Workflow Status of PROCESSED --> Kuali Doc Status of Verified
        if (workflowDocument.isProcessed()) {
            this.getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED);
            LOG.info("Adding Cash to Cash Drawer");
            SpringContext.getBean(CashReceiptService.class).addCashDetailsToCashDrawer(this);
        }

        this.getCapitalAssetManagementModuleService().deleteDocumentAssetLocks(this);        
    }
    
    /**
     * @see org.kuali.rice.kns.document.DocumentBase#postProcessSave(org.kuali.rice.kns.rule.event.KualiDocumentEvent)
     */
    @Override
    public void postProcessSave(KualiDocumentEvent event) {
        super.postProcessSave(event);
        
        boolean cM_veifies = (getTotalConfirmedDollarAmount().compareTo(KualiDecimal.ZERO) != 0); 
        if (retrieveCurrencyDetail() == null) {
            getCurrencyDetail().setDocumentNumber(this.getDocumentNumber());
            getCurrencyDetail().setFinancialDocumentTypeCode(CashReceiptDocument.DOCUMENT_TYPE);
            getCurrencyDetail().setCashieringStatus(KFSConstants.CurrencyCoinSources.CASH_RECEIPTS);
        }

        if (retrieveCoinDetail() == null) {
            getCoinDetail().setDocumentNumber(this.getDocumentNumber());
            getCoinDetail().setFinancialDocumentTypeCode(CashReceiptDocument.DOCUMENT_TYPE);
            getCoinDetail().setCashieringStatus(KFSConstants.CurrencyCoinSources.CASH_RECEIPTS); 
        }
        
        if(cM_veifies) {
            getConfirmedCurrencyDetail().setDocumentNumber(this.getDocumentNumber());
            getConfirmedCurrencyDetail().setFinancialDocumentTypeCode(CashReceiptDocument.DOCUMENT_TYPE);
            getConfirmedCurrencyDetail().setCashieringStatus(KFSConstants.CurrencyCoinSources.CASH_MANAGEMENT_IN);
            
            getConfirmedCoinDetail().setDocumentNumber(this.getDocumentNumber());
            getConfirmedCoinDetail().setFinancialDocumentTypeCode(CashReceiptDocument.DOCUMENT_TYPE);
            getConfirmedCoinDetail().setCashieringStatus(KFSConstants.CurrencyCoinSources.CASH_MANAGEMENT_IN);
            
            SpringContext.getBean(BusinessObjectService.class).save(getConfirmedCurrencyDetail());
            SpringContext.getBean(BusinessObjectService.class).save(getConfirmedCoinDetail());
        } 
        if(!(getTotalChangeAmount().compareTo(KualiDecimal.ZERO) == 0)) {
            getChangeCurrencyDetail().setDocumentNumber(this.getDocumentNumber());
            getChangeCurrencyDetail().setFinancialDocumentTypeCode(CashReceiptDocument.DOCUMENT_TYPE);
            getChangeCoinDetail().setDocumentNumber(this.getDocumentNumber());
            getChangeCoinDetail().setFinancialDocumentTypeCode(CashReceiptDocument.DOCUMENT_TYPE);
            if (cM_veifies) {
                getChangeCurrencyDetail().setCashieringStatus(KFSConstants.CurrencyCoinSources.CASH_CHANGE_GRANTED);
                getChangeCoinDetail().setCashieringStatus(KFSConstants.CurrencyCoinSources.CASH_CHANGE_GRANTED);
            } else {
                getChangeCurrencyDetail().setCashieringStatus(KFSConstants.CurrencyCoinSources.CASH_CHANGE_REQUEST);
                getChangeCoinDetail().setCashieringStatus(KFSConstants.CurrencyCoinSources.CASH_CHANGE_REQUEST);
            }
            SpringContext.getBean(BusinessObjectService.class).save(getChangeCurrencyDetail());
            SpringContext.getBean(BusinessObjectService.class).save(getChangeCoinDetail());
        }

        SpringContext.getBean(BusinessObjectService.class).save(getCurrencyDetail());
        SpringContext.getBean(BusinessObjectService.class).save(getCoinDetail());
                
        if (!(event instanceof SaveDocumentEvent)) { // don't lock until they route
            String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(this.getClass());
            this.getCapitalAssetManagementModuleService().generateCapitalAssetLock(this,documentTypeName);
        }        
    }

    /**
     * This method refreshes the currency/coin details for this cash receipt document
     */
    public void refreshCashDetails() {
        this.currencyDetail = retrieveCurrencyDetail();
        this.coinDetail = retrieveCoinDetail();
        CurrencyDetail retrievedCurrencyDetail = retrieveConfirmedCurrencyDetail();
        CoinDetail retrievedCoinDetail = retrieveConfirmedCoinDetail();
        CurrencyDetail retrievedChangeCurrDetail = retrieveChangeCurrencyDetail();
        CoinDetail retrievedChangeCoinDetail = retrieveChangeCoinDetail();
        //If this method is called before the cash-in record is inserted into the DB, it should not assign null
        if(retrievedCurrencyDetail != null) {
            this.confirmedCurrencyDetail = retrievedCurrencyDetail;
        }
        if(retrievedCoinDetail != null) {
            this.confirmedCoinDetail = retrievedCoinDetail;
        }
        if(retrievedChangeCurrDetail != null) {
            this.changeCurrencyDetail = retrievedChangeCurrDetail;
        }
        if(retrievedChangeCoinDetail != null) {
            this.changeCoinDetail = retrievedChangeCoinDetail;
        }
    }

    /**
     * Get this document's currency detail from the database
     * 
     * @return the currency detail record for this cash receipt document
     */
    protected CurrencyDetail retrieveCurrencyDetail() {
        return (CurrencyDetail) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(CurrencyDetail.class, getCashDetailPrimaryKey());
    }

    /**
     * Grab this document's coin detail from the database
     * 
     * @return the coin detail record for this cash receipt document
     */
    protected CoinDetail retrieveCoinDetail() {
        return (CoinDetail) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(CoinDetail.class, getCashDetailPrimaryKey());
    }

    /**
     * Get this document's confirmed currency detail from the database
     * 
     * @return the currency detail record for this cash receipt document
     */
    protected CurrencyDetail retrieveConfirmedCurrencyDetail() {
        return (CurrencyDetail) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(CurrencyDetail.class, getConfirmedCashDetailPrimaryKey());
    }
    
    /**
     * Get this document's confirmed coin detail from the database
     * 
     * @return the coin detail record for this cash receipt document
     */
    protected CoinDetail retrieveConfirmedCoinDetail() {
        return (CoinDetail) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(CoinDetail.class, getConfirmedCashDetailPrimaryKey());
    }
    
    /**
     * Get this document's change currency detail from the database
     * 
     * @return the change currency detail record for this cash receipt document
     */
    protected CurrencyDetail retrieveChangeCurrencyDetail() {
        return (CurrencyDetail) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(CurrencyDetail.class, getChangeCashDetailPrimaryKey());
    }
    
    /**
     * Get this document's change coin detail from the database
     * 
     * @return the change coin detail record for this cash receipt document
     */
    protected CoinDetail retrieveChangeCoinDetail() {
        return (CoinDetail) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(CoinDetail.class, getChangeCashDetailPrimaryKey());
    }

    /**
     * 
     * This method...
     * @return
     */
    public String getCreateDate() {
        return SpringContext.getBean(DateTimeService.class).toDateString(getDocumentHeader().getWorkflowDocument().getDateCreated().toDate());
    }
    
    /**
     * Generate the primary key for a currency or coin detail related to this document
     * 
     * @return a map with a representation of the proper primary key
     */
    protected Map getCashDetailPrimaryKey() {
        Map pk = new HashMap();
        pk.put("documentNumber", this.getDocumentNumber());
        pk.put("financialDocumentTypeCode", CashReceiptDocument.DOCUMENT_TYPE);
        pk.put("cashieringStatus", KFSConstants.CurrencyCoinSources.CASH_RECEIPTS); 
        return pk;
    }
    
    protected Map getConfirmedCashDetailPrimaryKey() {
        Map pk = new HashMap();
        pk.put("documentNumber", this.getDocumentNumber());
        pk.put("financialDocumentTypeCode", CashReceiptDocument.DOCUMENT_TYPE);
        pk.put("cashieringStatus", KFSConstants.CurrencyCoinSources.CASH_MANAGEMENT_IN); 
        return pk;
    }
    
    protected Map getChangeCashDetailPrimaryKey() {
        Map pk = new HashMap();
        pk.put("documentNumber", this.getDocumentNumber());
        pk.put("financialDocumentTypeCode", CashReceiptDocument.DOCUMENT_TYPE);
        pk.put("cashieringStatus", KFSConstants.CurrencyCoinSources.CASH_CHANGE_REQUEST); 
        return pk;
    }

    /**
     * @see org.kuali.rice.kns.document.TransactionalDocumentBase#buildListOfDeletionAwareLists()
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
        Collection<CheckBase> persistedChecks = SpringContext.getBean(CheckService.class).getByDocumentHeaderId(getDocumentNumber());
        List currentChecks = getChecks();

        List events = generateEvents(persistedChecks, currentChecks, KFSConstants.EXISTING_CHECK_PROPERTY_NAME, this);

        events.addAll(super.generateSaveEvents()); 
        
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
    protected List generateEvents(Collection persistedChecks, List currentChecks, String errorPathPrefix, CashReceiptFamilyBase crdoc) {
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
    protected Map buildCheckMap(Collection checks) {
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
        newCheck.setCashieringStatus(KFSConstants.CheckSources.CASH_RECEIPTS);
        return newCheck;
    }
    
    public Check createNewConfirmedCheck() {
        Check newCheck = new CheckBase();
        newCheck.setFinancialDocumentTypeCode(DOCUMENT_TYPE);
        newCheck.setCashieringStatus(KFSConstants.CheckSources.CASH_MANAGEMENT);
        return newCheck;
    }

    /**
     * Gets the depositCashReceiptControl attribute. 
     * @return Returns the depositCashReceiptControl.
     */
    public List getDepositCashReceiptControl() {
        return depositCashReceiptControl;
    }

    /**
     * Sets the depositCashReceiptControl attribute value.
     * @param depositCashReceiptControl The depositCashReceiptControl to set.
     */
    public void setDepositCashReceiptControl(List depositCashReceiptControl) {
        this.depositCashReceiptControl = depositCashReceiptControl;
    }

    /**
     * Override the campus code on the copied document to whatever the campus of the copying user is
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#toCopy()
     */
    @Override
    public void toCopy() throws WorkflowException {
        super.toCopy();
        
        initializeCampusLocationCode();
        
        if ((getChecks() == null || getChecks().isEmpty()) && getTotalCheckAmount().equals(KualiDecimal.ZERO)) {
            setCheckEntryMode(CashReceiptDocument.CHECK_ENTRY_DETAIL);
        }
    }

    /**
     * Initializes the campus location code based on kfs user role chart org
     * 
     */
    public void initializeCampusLocationCode(){
        
        if (GlobalVariables.getUserSession() != null && GlobalVariables.getUserSession().getPerson() != null) {        
         
            Person currentUser = GlobalVariables.getUserSession().getPerson();
            ChartOrgHolder chartOrg = SpringContext.getBean(org.kuali.kfs.sys.service.FinancialSystemUserService.class).getPrimaryOrganization(currentUser, KFSConstants.ParameterNamespaces.FINANCIAL);
            
            // Does a valid campus code exist for this person?  If so, simply grab
            // the campus code via the business object service.  
            if (chartOrg != null && chartOrg.getOrganization() != null) {
                setCampusLocationCode(chartOrg.getOrganization().getOrganizationPhysicalCampusCode());
            }
            // A valid campus code was not found; therefore, use the default affiliated
            // campus code.
            else {
                String affiliatedCampusCode = currentUser.getCampusCode();
                setCampusLocationCode(affiliatedCampusCode);
            }
        }
    }

    /**
     * Gets the sumTotalAmount attribute.
     * 
     * @return Returns the sumTotalAmount
     */
    
    public KualiDecimal getSumTotalAmount() {
        return sumTotalAmount;
    }

    /**	
     * Sets the sumTotalAmount attribute.
     * 
     * @param sumTotalAmount The sumTotalAmount to set.
     */
    public void setSumTotalAmount(KualiDecimal sumTotalAmount) {
        this.sumTotalAmount = sumTotalAmount;
    }
}

