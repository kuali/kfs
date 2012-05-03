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
package org.kuali.kfs.module.purap.document;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.ItemType;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurApItemBase;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderView;
import org.kuali.kfs.module.purap.businessobject.SensitiveData;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.impl.PurapServiceImpl;
import org.kuali.kfs.module.purap.service.PurapAccountingService;
import org.kuali.kfs.module.purap.service.SensitiveDataService;
import org.kuali.kfs.module.purap.util.PurApRelatedViews;
import org.kuali.kfs.sys.KFSConstants.AdHocPaymentIndicator;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentBase;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.rules.rule.event.ApproveDocumentEvent;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.rice.krad.rules.rule.event.RouteDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;
import org.kuali.rice.location.api.country.Country;
import org.kuali.rice.location.api.country.CountryService;

/**
 * Base class for Purchasing-Accounts Payable Documents.
 */
public abstract class PurchasingAccountsPayableDocumentBase extends AccountingDocumentBase implements PurchasingAccountsPayableDocument, AmountTotaling {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchasingAccountsPayableDocumentBase.class);

    // SHARED FIELDS BETWEEN REQUISITION, PURCHASE ORDER, PAYMENT REQUEST, AND CREDIT MEMO
    protected Integer purapDocumentIdentifier;
    protected Integer vendorHeaderGeneratedIdentifier;
    protected Integer vendorDetailAssignedIdentifier;
    protected String vendorCustomerNumber;
    protected String vendorName;
    protected String vendorLine1Address;
    protected String vendorLine2Address;
    protected String vendorCityName;
    protected String vendorStateCode;
    protected String vendorAddressInternationalProvinceName;
    protected String vendorPostalCode;
    protected String vendorCountryCode;
    protected Integer accountsPayablePurchasingDocumentLinkIdentifier;
    protected boolean useTaxIndicator;
    protected String vendorAttentionName;
    protected String accountDistributionMethod;  //code for account distribution method

    protected String statusCode;
    
    // NOT PERSISTED IN DB
    protected String vendorNumber;
    protected Integer vendorAddressGeneratedIdentifier;
    protected Boolean overrideWorkflowButtons = null;
    protected transient PurApRelatedViews relatedViews;
    protected boolean sensitive;

    // COLLECTIONS
    protected List<PurApItem> items;
    protected List<SourceAccountingLine> accountsForRouting; // don't use me for anything else!!

    // REFERENCE OBJECTS
    protected VendorDetail vendorDetail;
    protected Country vendorCountry;

    // STATIC
    public transient String[] belowTheLineTypes;

    // workaround for purapOjbCollectionHelper - remove when merged into rice
    public boolean allowDeleteAwareCollection = true;

    /**
     * Default constructor to be overridden.
     */
    public PurchasingAccountsPayableDocumentBase() {
        items = new ArrayList();
    }

    protected GeneralLedgerPendingEntry getFirstPendingGLEntry() {
        if (ObjectUtils.isNotNull(getGeneralLedgerPendingEntries()) && !getGeneralLedgerPendingEntries().isEmpty()) {
            return getGeneralLedgerPendingEntries().get(0);
        }
        return null;
    }

    public Integer getPostingYearFromPendingGLEntries() {
        GeneralLedgerPendingEntry glpe = getFirstPendingGLEntry();
        if (ObjectUtils.isNotNull(glpe)) {
            return glpe.getUniversityFiscalYear();
        }
        return null;
    }

    public String getPostingPeriodCodeFromPendingGLEntries() {
        GeneralLedgerPendingEntry glpe = getFirstPendingGLEntry();
        if (ObjectUtils.isNotNull(glpe)) {
            return glpe.getUniversityFiscalPeriodCode();
        }
        return null;
    }

    public List<SourceAccountingLine> getAccountsForRouting() {
        if (accountsForRouting == null) {
            populateAccountsForRouting();
        }
        return accountsForRouting;
    }

    public void setAccountsForRouting(List<SourceAccountingLine> accountsForRouting) {
        this.accountsForRouting = accountsForRouting;
    }

    /**
     * Makes sure that accounts for routing has been generated, so that other information can be retrieved from that
     */
    protected void populateAccountsForRouting() {
        SpringContext.getBean(PurapAccountingService.class).updateAccountAmounts(this);
        setAccountsForRouting(SpringContext.getBean(PurapAccountingService.class).generateSummary(getItems()));
        // need to refresh to get the references for the searchable attributes (ie status) and for invoking route levels (ie account
        // objects) -hjs
        refreshNonUpdateableReferences();
        for (SourceAccountingLine sourceLine : getAccountsForRouting()) {
            sourceLine.refreshNonUpdateableReferences();
        }
    }

    public boolean isSensitive() {
        List<SensitiveData> sensitiveData = SpringContext.getBean(SensitiveDataService.class).getSensitiveDatasAssignedByRelatedDocId(getAccountsPayablePurchasingDocumentLinkIdentifier());
        if (ObjectUtils.isNotNull(sensitiveData) && !sensitiveData.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument#isInquiryRendered()
     */
    @Override
    public boolean isInquiryRendered() {
        return isPostingYearPrior();
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument#isPostingYearNext()
     */
    @Override
    public boolean isPostingYearNext() {
        Integer currentFY = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        return (getPostingYear().compareTo(currentFY) > 0);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument#isPostingYearPrior()
     */
    @Override
    public boolean isPostingYearPrior() {
        Integer currentFY = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        return (getPostingYear().compareTo(currentFY) < 0);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument#getPostingYearNextOrCurrent()
     */
    @Override
    public Integer getPostingYearNextOrCurrent() {
        if (isPostingYearNext()) {
            //FY is set to next; use it
            return getPostingYear();
        }
        //FY is NOT set to next; use CURRENT
        return SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument#getItemClass()
     */
    @Override
    @SuppressWarnings("rawtypes")
    public abstract Class getItemClass();

    @SuppressWarnings("rawtypes")
    public abstract Class getItemUseTaxClass();

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument#getPurApSourceDocumentIfPossible()
     */
    @Override
    public abstract PurchasingAccountsPayableDocument getPurApSourceDocumentIfPossible();

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument#getPurApSourceDocumentLabelIfPossible()
     */
    @Override
    public abstract String getPurApSourceDocumentLabelIfPossible();

    /**
     * @see org.kuali.rice.krad.document.DocumentBase#prepareForSave()
     */

    @Override
    public void prepareForSave(KualiDocumentEvent event) {
        customPrepareForSave(event);
        super.prepareForSave(event);
        fixItemReferences();
    }

    /**
     * PURAP documents are all overriding this method to return false because sufficient funds checking should not be performed on
     * route of any PURAP documents. Only the Purchase Order performs a sufficient funds check and it is manually forced during
     * routing.
     *
     * @see org.kuali.kfs.sys.document.GeneralLedgerPostingDocumentBase#documentPerformsSufficientFundsCheck()
     */
    @Override
    public boolean documentPerformsSufficientFundsCheck() {
        return false;
    }

    // for app doc status
    @Override
    public boolean isDocumentStoppedInRouteNode(String nodeName) {

        WorkflowDocument workflowDoc = getDocumentHeader().getWorkflowDocument();
        Set<String> names = getDocumentHeader().getWorkflowDocument().getCurrentNodeNames();
        List<String> currentRouteLevels = new ArrayList<String>(names);
            if (currentRouteLevels.contains(nodeName) && workflowDoc.isApprovalRequested()) {
            return true;
        }
        return false;
    }

    /**
     * Records the specified error message into the Log file and throws a runtime exception.
     *
     * @param errorMessage the error message to be logged.
     */
    protected void logAndThrowRuntimeException(String errorMessage) {
        this.logAndThrowRuntimeException(errorMessage, null);
    }

    /**
     * Records the specified error message into the Log file and throws the specified runtime exception.
     *
     * @param errorMessage the specified error message.
     * @param e the specified runtime exception.
     */
    protected void logAndThrowRuntimeException(String errorMessage, Exception e) {
        if (ObjectUtils.isNotNull(e)) {
            LOG.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
        else {
            LOG.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }

    /**
     * Allows child PO classes to customize the prepareForSave method. Most of the subclasses need to call the super's method to get
     * the GL entry creation, but they each need to do different things to prepare for those entries to be created. This is only for
     * PO since it has children classes that need different prep work for GL creation.
     *
     * @param event the event involved in this action.
     */
    public void customPrepareForSave(KualiDocumentEvent event) {
        // Need this here so that it happens before the GL work is done
        SpringContext.getBean(PurapAccountingService.class).updateAccountAmounts(this);

        if (event instanceof RouteDocumentEvent || event instanceof ApproveDocumentEvent) {
            if (this instanceof VendorCreditMemoDocument && ((VendorCreditMemoDocument)this).isSourceVendor()){
                return;
            }
            SpringContext.getBean(PurapServiceImpl.class).calculateTax(this);
        }
        // These next 5 lines are temporary changes so that we can use PurApOjbCollectionHelper for release 2.
        // But these 5 lines will not be necessary anymore if the changes in PurApOjbCollectionHelper is
        // merge into Rice.
//        this.allowDeleteAwareCollection = true;
//        DocumentDaoOjb docDao = SpringContext.getBean(DocumentDaoOjb.class);
//        PurchasingAccountsPayableDocumentBase retrievedDocument = (PurchasingAccountsPayableDocumentBase) docDao.findByDocumentHeaderId(this.getClass(), this.getDocumentNumber());
//        if (retrievedDocument != null) {
//            retrievedDocument.allowDeleteAwareCollection = true;
//        }
//
//        SpringContext.getBean(PurApOjbCollectionHelper.class).processCollections(docDao, this, retrievedDocument);
//        this.allowDeleteAwareCollection = false;
//        if (retrievedDocument != null) {
//            retrievedDocument.allowDeleteAwareCollection = false;
//        }
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#buildListOfDeletionAwareLists()
     */
    @SuppressWarnings("rawtypes")
    @Override
    public List buildListOfDeletionAwareLists() {
        List managedLists = new ArrayList<List>();
        managedLists.add(getDeletionAwareAccountingLines());
        if (allowDeleteAwareCollection) {
            //From now on, the list of accounting lines would have been added when the
            //super.buildListOfDeletionAwareLists() is executed when it calls getSourceAccountingLines().
            //So we can remove the old codes that used to exist here to add the accounts to the
            //managedLists and just use the one from the super.buildListOfDeletionAwareLists()
            List<PurApItemBase> subManageList = this.getItems();
            List useTaxItems = new ArrayList();
        	for (PurApItemBase subManage : subManageList) {
        		useTaxItems.addAll(subManage.getUseTaxItems());
        	}

            managedLists.add(this.getItems());
            managedLists.add(useTaxItems);
        }
        return managedLists;
    }

    /**
     * Build deletion list of accounting lines for PurAp generic use.
     *
     * @return
     */
    @SuppressWarnings("rawtypes")
    protected List getDeletionAwareAccountingLines() {
        List<PurApAccountingLine> deletionAwareAccountingLines = new ArrayList<PurApAccountingLine>();
        for (Object itemAsObject : this.getItems()) {
            final PurApItem item = (PurApItem)itemAsObject;
            for (PurApAccountingLine accountingLine : item.getSourceAccountingLines()) {
                deletionAwareAccountingLines.add(accountingLine);
            }
        }
        return deletionAwareAccountingLines;
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#buildListOfDeletionAwareLists()
     *
    @Override
    public List buildListOfDeletionAwareLists() {
        List managedLists = new ArrayList();
        if (allowDeleteAwareCollection) {
            List<PurApAccountingLine> purapAccountsList = new ArrayList<PurApAccountingLine>();
            for (Object itemAsObject : this.getItems()) {
                final PurApItem item = (PurApItem)itemAsObject;
                purapAccountsList.addAll(item.getSourceAccountingLines());
            }
            managedLists.add(purapAccountsList);
            managedLists.add(this.getItems());
        }
        return managedLists;
    }
    */

    @Override
    public void processAfterRetrieve() {
        super.processAfterRetrieve();

        refreshNonUpdateableReferences();
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument#addItem(PurApItem item)
     */
    @Override
    public void addItem(PurApItem item) {
        int itemLinePosition = getItemLinePosition();
        if (ObjectUtils.isNotNull(item.getItemLineNumber()) && (item.getItemLineNumber() > 0) && (item.getItemLineNumber() <= itemLinePosition)) {
            itemLinePosition = item.getItemLineNumber().intValue() - 1;
        }

        item.setPurapDocumentIdentifier(this.purapDocumentIdentifier);
        item.setPurapDocument(this);

        items.add(itemLinePosition, item);
        renumberItems(itemLinePosition);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument#deleteItem(int lineNum)
     */
    @Override
    public void deleteItem(int lineNum) {
        if (items.remove(lineNum) == null) {
            // throw error here
        }
        renumberItems(lineNum);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument#renumberItems(int start)
     */
    @Override
    public void renumberItems(int start) {
        for (int i = start; i < items.size(); i++) {
            PurApItem item = items.get(i);
            // only set the item line number for above the line items
            if (item.getItemType().isLineItemIndicator()) {
                item.setItemLineNumber(new Integer(i + 1));
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument#itemSwap(int positionFrom, int positionTo)
     */
    @Override
    public void itemSwap(int positionFrom, int positionTo) {
        // if out of range do nothing
        if ((positionTo < 0) || (positionTo >= getItemLinePosition())) {
            return;
        }
        PurApItem item1 = this.getItem(positionFrom);
        PurApItem item2 = this.getItem(positionTo);
        Integer oldFirstPos = item1.getItemLineNumber();
        // swap line numbers
        item1.setItemLineNumber(item2.getItemLineNumber());
        item2.setItemLineNumber(oldFirstPos);
        // fix ordering in list
        items.remove(positionFrom);
        items.add(positionTo, item1);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument#getItemLinePosition()
     */
    @Override
    public int getItemLinePosition() {
        int belowTheLineCount = 0;
        for (PurApItem item : items) {
            if (item.getItemType().isAdditionalChargeIndicator()) {
                belowTheLineCount++;
            }
        }
        return items.size() - belowTheLineCount;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument#getItem(int pos)
     */
    @Override
    public PurApItem getItem(int pos) {
        return items.get(pos);
    }

    /**
     * Iterates through the items of the document and returns the item with the line number equal to the number given, or null if a
     * match is not found.
     *
     * @param lineNumber line number to match on.
     * @return the PurchasingAp Item if a match is found, else null.
     */
    @SuppressWarnings("rawtypes")
    public PurApItem getItemByLineNumber(int lineNumber) {
        for (Iterator iter = items.iterator(); iter.hasNext();) {
            PurApItem item = (PurApItem) iter.next();
            if (item.getItemLineNumber().intValue() == lineNumber) {
                return item;
            }
        }
        return null;
    }

    /**
     * Find the item in the document via its string identifier.
     * @param itemStrID the string identifier of the item being searched for
     * @return the item being searched for
     */
    @SuppressWarnings("rawtypes")
    public PurApItem getItemByStringIdentifier(String itemStrID) {
        for (Iterator iter = items.iterator(); iter.hasNext();) {
            PurApItem item = (PurApItem) iter.next();
            if (StringUtils.equalsIgnoreCase(item.getItemIdentifierString(), itemStrID)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Find the item in the document via its identifier.
     * @param itemID the string identifier of the item being searched for
     * @return the item being searched for
     */
    @SuppressWarnings("rawtypes")
    public PurApItem getItemByItemIdentifier(Integer itemID) {
        for (Iterator iter = items.iterator(); iter.hasNext();) {
            PurApItem item = (PurApItem) iter.next();
            if (item.getItemIdentifier() == itemID) {
                return item;
            }
        }
        return null;
    }

    /**
     * Overriding the parent method so that we can just set the posting year without the other stuff that the parent does to the
     * accounting period. We only store the posting year on the doc and don't want the other stuff.
     *
     * @see org.kuali.kfs.sys.document.LedgerPostingDocumentBase#setPostingYear(java.lang.Integer)
     */
    @Override
    public void setPostingYear(Integer postingYear) {
        this.postingYear = postingYear;
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#getTotalDollarAmount()
     */
    @Override
    public KualiDecimal getTotalDollarAmount() {
        return getTotalDollarAmountAllItems(null);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument#setTotalDollarAmount(KualiDecimal amount)
     */
    @Override
    public void setTotalDollarAmount(KualiDecimal amount) {
        // do nothing, this is so that the jsp won't complain about totalDollarAmount have no setter method.
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument#getTotalDollarAmountAllItems(String[] excludedTypes)
     */
    @Override
    public KualiDecimal getTotalDollarAmountAllItems(String[] excludedTypes) {
        return getTotalDollarAmountWithExclusions(excludedTypes, true);
    }

    /**
     * Computes the total dollar amount of all above the line items.
     *
     * @return the total dollar amount of all above the line items.
     */
    @Override
    public KualiDecimal getTotalDollarAmountAboveLineItems() {
        return getTotalDollarAmountAboveLineItems(null);
    }

    /**
     * Computes the total dollar amount of all above the line items with the specified item types excluded.
     *
     * @param excludedTypes the types of items to be excluded.
     * @return the total dollar amount of all above the line items with the specified item types excluded..
     */
    public KualiDecimal getTotalDollarAmountAboveLineItems(String[] excludedTypes) {
        return getTotalDollarAmountWithExclusions(excludedTypes, false);
    }

    /**
     * Computes the total dollar amount with the specified item types and possibly below the line items excluded.
     *
     * @param excludedTypes the types of items to be excluded.
     * @param includeBelowTheLine indicates whether below the line items shall be included.
     * @return the total dollar amount with the specified item types excluded.
     */
    public KualiDecimal getTotalDollarAmountWithExclusions(String[] excludedTypes, boolean includeBelowTheLine) {
        List<PurApItem> itemsForTotal = getItems();

        return getTotalDollarAmountWithExclusionsSubsetItems(excludedTypes, includeBelowTheLine, itemsForTotal);
    }

    /**
     * This method...
     * @param excludedTypes
     * @param includeBelowTheLine
     * @param itemsForTotal
     * @return
     */
    protected KualiDecimal getTotalDollarAmountWithExclusionsSubsetItems(String[] excludedTypes, boolean includeBelowTheLine, List<PurApItem> itemsForTotal) {
        if (excludedTypes == null) {
            excludedTypes = new String[] {};
        }

        KualiDecimal total = new KualiDecimal(BigDecimal.ZERO);
        for (PurApItem item : itemsForTotal) {
            item.refreshReferenceObject(PurapPropertyConstants.ITEM_TYPE);
            ItemType it = item.getItemType();
            if ((includeBelowTheLine || it.isLineItemIndicator()) && !ArrayUtils.contains(excludedTypes, it.getItemTypeCode())) {
                KualiDecimal totalAmount = item.getTotalAmount();
                KualiDecimal itemTotal = (totalAmount != null) ? totalAmount : KualiDecimal.ZERO;
                total = total.add(itemTotal);
            }
        }
        return total;
    }

    @Override
    public KualiDecimal getTotalDollarAmountForTradeIn() {
        List<PurApItem> tradeInItems = getTradeInItems();
        return getTotalDollarAmountWithExclusionsSubsetItems(null,false,tradeInItems);
    }

    /**
     * This method...
     * @param tradeInItems
     */
    @Override
    public List<PurApItem> getTradeInItems() {
        List<PurApItem> tradeInItems = new ArrayList<PurApItem>();
        for (PurApItem purApItem : (List<PurApItem>)getItems()) {
            if(purApItem.getItemAssignedToTradeInIndicator()) {
                tradeInItems.add(purApItem);
            }
        }
        return tradeInItems;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument#getTotalPreTaxDollarAmount()
     */
    @Override
    public KualiDecimal getTotalPreTaxDollarAmount() {
        return getTotalPreTaxDollarAmountAllItems(null);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument#setTotalPreTaxDollarAmount(org.kuali.rice.core.api.util.type.KualiDecimal)
     */
    @Override
    public void setTotalPreTaxDollarAmount(KualiDecimal amount) {
        // do nothing, this is so that the jsp won't complain about totalDollarAmount have no setter method.
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument#getTotalPreTaxDollarAmountAllItems(java.lang.String[])
     */
    @Override
    public KualiDecimal getTotalPreTaxDollarAmountAllItems(String[] excludedTypes) {
        return getTotalPreTaxDollarAmountWithExclusions(excludedTypes, true);
    }

    /**
     * Computes the total dollar amount of all above the line items.
     *
     * @return the total dollar amount of all above the line items.
     */
    public KualiDecimal getTotalPreTaxDollarAmountAboveLineItems() {
        return getTotalPreTaxDollarAmountAboveLineItems(null);
    }

    /**
     * Computes the total dollar amount of all above the line items with the specified item types excluded.
     *
     * @param excludedTypes the types of items to be excluded.
     * @return the total dollar amount of all above the line items with the specified item types excluded..
     */
    public KualiDecimal getTotalPreTaxDollarAmountAboveLineItems(String[] excludedTypes) {
        return getTotalPreTaxDollarAmountWithExclusions(excludedTypes, false);
    }

    /**
     * Computes the total dollar amount with the specified item types and possibly below the line items excluded.
     *
     * @param excludedTypes the types of items to be excluded.
     * @param includeBelowTheLine indicates whether below the line items shall be included.
     * @return the total dollar amount with the specified item types excluded.
     */
    public KualiDecimal getTotalPreTaxDollarAmountWithExclusions(String[] excludedTypes, boolean includeBelowTheLine) {
        if (excludedTypes == null) {
            excludedTypes = new String[] {};
        }

        KualiDecimal total = new KualiDecimal(BigDecimal.ZERO);
        for (PurApItem item : (List<PurApItem>) getItems()) {
            item.refreshReferenceObject(PurapPropertyConstants.ITEM_TYPE);
            ItemType it = item.getItemType();
            if ((includeBelowTheLine || it.isLineItemIndicator()) && !ArrayUtils.contains(excludedTypes, it.getItemTypeCode())) {
                KualiDecimal extendedPrice = item.getExtendedPrice();
                KualiDecimal itemTotal = (extendedPrice != null) ? extendedPrice : KualiDecimal.ZERO;
                total = total.add(itemTotal);
            }
        }
        return total;
    }

    @Override
    public KualiDecimal getTotalTaxAmount() {
        return getTotalTaxAmountAllItems(null);
    }

    @Override
    public void setTotalTaxAmount(KualiDecimal amount) {
        // do nothing, this is so that the jsp won't complain about totalTaxAmount have no setter method.
    }

    @Override
    public KualiDecimal getTotalTaxAmountAllItems(String[] excludedTypes) {
        return getTotalTaxAmountWithExclusions(excludedTypes, true);
    }

    @Override
    public KualiDecimal getTotalTaxAmountAboveLineItems() {
        return getTotalTaxAmountAboveLineItems(null);
    }

    @Override
    public KualiDecimal getTotalTaxAmountAboveLineItems(String[] excludedTypes) {
        return getTotalTaxAmountWithExclusions(excludedTypes, false);
    }

    @Override
    public KualiDecimal getTotalTaxAmountWithExclusions(String[] excludedTypes, boolean includeBelowTheLine) {
        if (excludedTypes == null) {
            excludedTypes = new String[] {};
        }

        KualiDecimal total = new KualiDecimal(BigDecimal.ZERO);
        for (PurApItem item : (List<PurApItem>) getItems()) {
            item.refreshReferenceObject(PurapPropertyConstants.ITEM_TYPE);
            ItemType it = item.getItemType();
            if ((includeBelowTheLine || it.isLineItemIndicator()) && !ArrayUtils.contains(excludedTypes, it.getItemTypeCode())) {
                KualiDecimal taxAmount = item.getItemTaxAmount();
                KualiDecimal itemTotal = (taxAmount != null) ? taxAmount : KualiDecimal.ZERO;
                total = total.add(itemTotal);
            }
        }
        return total;
    }

    @Override
    public boolean isUseTaxIndicator() {
        return useTaxIndicator;
    }

    @Override
    public void setUseTaxIndicator(boolean useTaxIndicator) {
        this.useTaxIndicator = useTaxIndicator;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument#templateVendorAddress(VendorAddress)
     */
    @Override
    public void templateVendorAddress(VendorAddress vendorAddress) {
        if (vendorAddress == null) {
            return;
        }
        this.setVendorLine1Address(vendorAddress.getVendorLine1Address());
        this.setVendorLine2Address(vendorAddress.getVendorLine2Address());
        this.setVendorCityName(vendorAddress.getVendorCityName());
        this.setVendorStateCode(vendorAddress.getVendorStateCode());
        this.setVendorPostalCode(vendorAddress.getVendorZipCode());
        this.setVendorCountryCode(vendorAddress.getVendorCountryCode());
    }

    /**
     * Returns the vendor number for this document.
     *
     * @return the vendor number for this document.
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument#getVendorNumber()
     */
    @Override
    public String getVendorNumber() {
        if (StringUtils.isNotEmpty(vendorNumber)) {
            return vendorNumber;
        }
        else if (ObjectUtils.isNotNull(vendorDetail)) {
            return vendorDetail.getVendorNumber();
        }
        else {
            return "";
        }
    }

    @Override
    public void setVendorNumber(String vendorNumber) {
        this.vendorNumber = vendorNumber;
    }

    public Boolean getOverrideWorkflowButtons() {
        return overrideWorkflowButtons;
    }

    public void setOverrideWorkflowButtons(Boolean overrideWorkflowButtons) {
        this.overrideWorkflowButtons = overrideWorkflowButtons;
    }

    @Override
    public Integer getVendorHeaderGeneratedIdentifier() {
        return vendorHeaderGeneratedIdentifier;
    }

    @Override
    public void setVendorHeaderGeneratedIdentifier(Integer vendorHeaderGeneratedIdentifier) {
        this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
    }

    @Override
    public Integer getVendorDetailAssignedIdentifier() {
        return vendorDetailAssignedIdentifier;
    }

    @Override
    public void setVendorDetailAssignedIdentifier(Integer vendorDetailAssignedIdentifier) {
        this.vendorDetailAssignedIdentifier = vendorDetailAssignedIdentifier;
    }

    @Override
    public String getVendorCustomerNumber() {
        return vendorCustomerNumber;
    }

    @Override
    public void setVendorCustomerNumber(String vendorCustomerNumber) {
        this.vendorCustomerNumber = vendorCustomerNumber;
    }

    @Override
    public Integer getPurapDocumentIdentifier() {
        return purapDocumentIdentifier;
    }

    @Override
    public void setPurapDocumentIdentifier(Integer identifier) {
        this.purapDocumentIdentifier = identifier;
    }

    @Override
    public VendorDetail getVendorDetail() {
        return vendorDetail;
    }

    public void setVendorDetail(VendorDetail vendorDetail) {
        this.vendorDetail = vendorDetail;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public List getItems() {
        return items;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void setItems(List items) {
        this.items = items;
    }

    @Override
    public String getVendorCityName() {
        return vendorCityName;
    }

    @Override
    public void setVendorCityName(String vendorCityName) {
        this.vendorCityName = vendorCityName;
    }

    @Override
    public String getVendorCountryCode() {
        return vendorCountryCode;
    }

    @Override
    public void setVendorCountryCode(String vendorCountryCode) {
        this.vendorCountryCode = vendorCountryCode;
    }

    @Override
    public String getVendorLine1Address() {
        return vendorLine1Address;
    }

    @Override
    public void setVendorLine1Address(String vendorLine1Address) {
        this.vendorLine1Address = vendorLine1Address;
    }

    @Override
    public String getVendorLine2Address() {
        return vendorLine2Address;
    }

    @Override
    public void setVendorLine2Address(String vendorLine2Address) {
        this.vendorLine2Address = vendorLine2Address;
    }

    @Override
    public String getVendorName() {
        return vendorName;
    }

    @Override
    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    @Override
    public String getVendorPostalCode() {
        return vendorPostalCode;
    }

    @Override
    public void setVendorPostalCode(String vendorPostalCode) {
        this.vendorPostalCode = vendorPostalCode;
    }

    @Override
    public String getVendorStateCode() {
        return vendorStateCode;
    }

    @Override
    public void setVendorStateCode(String vendorStateCode) {
        this.vendorStateCode = vendorStateCode;
    }

    @Override
    public String getVendorAddressInternationalProvinceName() {
        return vendorAddressInternationalProvinceName;
    }

    @Override
    public void setVendorAddressInternationalProvinceName(String vendorAddressInternationalProvinceName) {
        this.vendorAddressInternationalProvinceName = vendorAddressInternationalProvinceName;
    }

    @Override
    public Integer getVendorAddressGeneratedIdentifier() {
        return vendorAddressGeneratedIdentifier;
    }

    @Override
    public void setVendorAddressGeneratedIdentifier(Integer vendorAddressGeneratedIdentifier) {
        this.vendorAddressGeneratedIdentifier = vendorAddressGeneratedIdentifier;
    }

    @Override
    public Integer getAccountsPayablePurchasingDocumentLinkIdentifier() {
        return accountsPayablePurchasingDocumentLinkIdentifier;
    }

    @Override
    public void setAccountsPayablePurchasingDocumentLinkIdentifier(Integer accountsPayablePurchasingDocumentLinkIdentifier) {
        this.accountsPayablePurchasingDocumentLinkIdentifier = accountsPayablePurchasingDocumentLinkIdentifier;
    }

    @Override
    public String[] getBelowTheLineTypes() {
        if (this.belowTheLineTypes == null) {
            this.belowTheLineTypes = SpringContext.getBean(PurapService.class).getBelowTheLineForDocument(this);
        }
        return belowTheLineTypes;
    }

    @Override
    public Country getVendorCountry() {
        vendorCountry = (vendorCountryCode == null)?null:( vendorCountry == null || !StringUtils.equals( vendorCountry.getCode(),vendorCountryCode))?SpringContext.getBean(CountryService.class).getCountry(vendorCountryCode): vendorCountry;
        return vendorCountry;
    }

    /**
     * Added only to allow for {@link org.kuali.kfs.module.purap.util.PurApObjectUtils} class to work correctly.
     *
     * @deprecated
     */
    @Deprecated
    public void setVendorCountry(Country vendorCountry) {
        this.vendorCountry = vendorCountry;
    }

    public String getVendorAttentionName() {
        return vendorAttentionName;
    }

    public void setVendorAttentionName(String vendorAttentionName) {
        this.vendorAttentionName = vendorAttentionName;
    }

    /**
     * Gets the accountDistributionMethod attribute.
     *
     * @return Returns the accountDistributionMethod
     */

    public String getAccountDistributionMethod() {
        return accountDistributionMethod;
    }

    /**
     * Sets the accountDistributionMethod attribute.
     *
     * @param accountDistributionMethod The accountDistributionMethod to set.
     */
    public void setAccountDistributionMethod(String accountDistributionMethod) {
        this.accountDistributionMethod = accountDistributionMethod;
    }

    /**
     * Determines whether the account is debit. It always returns false.
     *
     * @param financialDocument The document containing the account to be validated.
     * @param accountingLine The account to be validated.
     * @return boolean false.
     * @see org.kuali.kfs.sys.document.validation.AccountingLineRule#isDebit(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.sys.businessobject.AccountingLine)
     */
    @Override
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        return false;
    }

    public PurApRelatedViews getRelatedViews() {
        if (relatedViews == null) {
            relatedViews = new PurApRelatedViews(this.documentNumber, this.accountsPayablePurchasingDocumentLinkIdentifier);
        }
        return relatedViews;
    }

    public void setRelatedViews(PurApRelatedViews relatedViews) {
        this.relatedViews = relatedViews;
    }

    @Override
    public void refreshNonUpdateableReferences() {

        super.refreshNonUpdateableReferences();
        fixItemReferences();
    }

    /**
     * This method fixes the item references in this document if it's new
     */
    @Override
    public void fixItemReferences() {
        //fix item and account references in case this is a new doc (since they will be lost)
        if(ObjectUtils.isNull(this.purapDocumentIdentifier)) {
            for (PurApItem item : (List<PurApItem>)this.getItems()) {
                item.setPurapDocument(this);
                item.fixAccountReferences();
            }
        }
    }

    /**
     * Returns the trade in item of the document.
     *
     * @return
     */
    @Override
    public PurApItem getTradeInItem() {
        for (PurApItem item : (List<PurApItem>)getItems()) {
            if (item.getItemTypeCode().equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_TRADE_IN_CODE)) {
                return item;
            }
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument.getIsATypeOfPurAPRecDoc().
     */
    @Override
    public boolean getIsATypeOfPurAPRecDoc() {
        return true;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument.getIsATypeOfPurDoc().
     */
    @Override
    public boolean getIsATypeOfPurDoc() {
        if (this instanceof PurchasingDocumentBase) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument.getIsATypeOfPODoc().
     */
    @Override
    public boolean getIsATypeOfPODoc() {
        if (this instanceof PurchaseOrderDocument) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument.getIsPODoc().
     */
    @Override
    public boolean getIsPODoc() {
        if ( (this instanceof PurchaseOrderDocument) &&
            !(this instanceof PurchaseOrderAmendmentDocument) &&
            !(this instanceof PurchaseOrderCloseDocument) &&
            !(this instanceof PurchaseOrderPaymentHoldDocument) &&
            !(this instanceof PurchaseOrderRemoveHoldDocument) &&
            !(this instanceof PurchaseOrderReopenDocument) &&
            !(this instanceof PurchaseOrderRetransmitDocument) &&
            !(this instanceof PurchaseOrderSplitDocument) &&
            !(this instanceof PurchaseOrderVoidDocument)) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument.getIsATypeOfREQSDoc().
     */
    @Override
    public boolean getIsReqsDoc() {
        if (this instanceof RequisitionDocument) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * build document title based on the properties of current document
     *
     * @param the default document title
     * @return the combine information of the given title and additional payment indicators
     */
    protected String buildDocumentTitle(String title) {
        if(this.getVendorDetail() == null) {
           return title;
        }

        Integer vendorHeaderGeneratedIdentifier = this.getVendorDetail().getVendorHeaderGeneratedIdentifier();
        VendorService vendorService = SpringContext.getBean(VendorService.class);

        Object[] indicators = new String[2];

        boolean isEmployeeVendor = vendorService.isVendorInstitutionEmployee(vendorHeaderGeneratedIdentifier);
        indicators[0] = isEmployeeVendor ? AdHocPaymentIndicator.EMPLOYEE_VENDOR : AdHocPaymentIndicator.OTHER;

        boolean isVendorForeign = vendorService.isVendorForeign(vendorHeaderGeneratedIdentifier);
        indicators[1] = isVendorForeign ? AdHocPaymentIndicator.ALIEN_VENDOR : AdHocPaymentIndicator.OTHER;

        for(Object indicator : indicators) {
            if(!AdHocPaymentIndicator.OTHER.equals(indicator)) {
                String titlePattern = title + " [{0}:{1}]";
                return MessageFormat.format(titlePattern, indicators);
            }
        }

        return title;
    }

    /**
     * Overridden to return the source lines of all of the items
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#getSourceAccountingLines()
     */
    @SuppressWarnings("rawtypes")
    @Override
    public List getSourceAccountingLines() {
        if (ObjectUtils.isNotNull(sourceAccountingLines) && !sourceAccountingLines.isEmpty()) {
            // do nothing because acct lines have already been set
            return sourceAccountingLines;
        }
        else {
            /*
            SpringContext.getBean(PurapAccountingService.class).updateAccountAmounts(this);
            return SpringContext.getBean(PurapAccountingService.class).generateSummary(getItems());
            */
            List<AccountingLine> sourceAccountingLines = new ArrayList<AccountingLine>();
            for (Object itemAsObject : this.getItems()) {
                final PurApItem item = (PurApItem)itemAsObject;
                for (PurApAccountingLine accountingLine : item.getSourceAccountingLines()) {
                    sourceAccountingLines.add(accountingLine);
                }
            }
            return sourceAccountingLines;
        }
    }

    /**
     * Checks whether the related purchase order views need a warning to be displayed,
     * i.e. if at least one of the purchase orders has never been opened.
     * @return true if at least one related purchase order needs a warning; false otherwise
     */
    public boolean getNeedWarningRelatedPOs() {
        List<PurchaseOrderView> poViews = getRelatedViews().getRelatedPurchaseOrderViews();
        for (PurchaseOrderView poView : poViews) {
            if (poView.getNeedWarning()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Accounting lines that are read-only should skip validation
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#getPersistedSourceAccountingLinesForComparison()
     */
    @SuppressWarnings("rawtypes")
    @Override
    protected List getPersistedSourceAccountingLinesForComparison() {
        LOG.info("Checking persisted source accounting lines for read-only fields");
        List<String> restrictedItemTypesList = new ArrayList<String>();
        try {
            restrictedItemTypesList = new ArrayList<String>( SpringContext.getBean(ParameterService.class).getParameterValuesAsString(this.getClass(), PurapParameterConstants.PURAP_ITEM_TYPES_RESTRICTING_ACCOUNT_EDIT) );
        } catch (IllegalArgumentException iae) {
            // do nothing, not a problem if no restricted types are defined
        }

        PurapAccountingService purApAccountingService = SpringContext.getBean(PurapAccountingService.class);
        List persistedSourceLines = new ArrayList();

        for (PurApItem item : (List<PurApItem>) this.getItems()) {
            // only check items that already have been persisted since last save
            if (ObjectUtils.isNotNull(item.getItemIdentifier())) {
                // Disable validation if the item is read-only
                final boolean isNotReadOnly = !((restrictedItemTypesList != null) && restrictedItemTypesList.contains(item.getItemTypeCode()));
                if (isNotReadOnly) {
                    persistedSourceLines.addAll(purApAccountingService.getAccountsFromItem(item));
                }
            }
        }
        return persistedSourceLines;
    }

    /**
     * Accounting lines that are read-only should skip validation
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#getSourceAccountingLinesForComparison()
     */
    @SuppressWarnings("rawtypes")
    @Override
    protected List getSourceAccountingLinesForComparison() {
        LOG.info("Checking source accounting lines for read-only fields");
        List<String> restrictedItemTypesList = new ArrayList<String>();
        try {
            restrictedItemTypesList = new ArrayList<String>( SpringContext.getBean(ParameterService.class).getParameterValuesAsString(this.getClass(), PurapParameterConstants.PURAP_ITEM_TYPES_RESTRICTING_ACCOUNT_EDIT) );
        } catch (IllegalArgumentException iae) {
            // do nothing, not a problem if no restricted types are defined
        }
        PurapAccountingService purApAccountingService = SpringContext.getBean(PurapAccountingService.class);
        List currentSourceLines = new ArrayList();
        for (PurApItem item : (List<PurApItem>) this.getItems()) {
            // Disable validation if the item is read-only
            final boolean isNotReadOnly = !((restrictedItemTypesList != null) && restrictedItemTypesList.contains(item.getItemTypeCode()));
            if (isNotReadOnly) {
                currentSourceLines.addAll(item.getSourceAccountingLines());
            }
        }
        return currentSourceLines;
    }

    /**
     * Gets the statusCode attribute.
     * 
     * @return Returns the statusCode
     */
    
    public String getStatusCode() {
        return statusCode;
    }

    /** 
     * Sets the statusCode attribute.
     * 
     * @param statusCode The statusCode to set.
     */
    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
    
    public String getVendorCountryName() {
        Country country = SpringContext.getBean(CountryService.class).getCountry(getVendorCountryCode());
        if (country != null)
            return country.getName();
        return null;
    }
   
}
