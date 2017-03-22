/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.purap.document;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.fp.document.ProcurementCardDocument;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.AccountsPayableItem;
import org.kuali.kfs.module.purap.businessobject.PurApItemUseTax;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.service.AccountsPayableDocumentSpecificService;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.businessobject.TaxRegion;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.vnd.businessobject.CampusParameter;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteLevelChange;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

import edu.arizona.kfs.module.purap.PurapConstants;
import edu.arizona.kfs.module.purap.PurapParameterConstants;

/**
 * Accounts Payable Document Base
 */
public abstract class AccountsPayableDocumentBase extends PurchasingAccountsPayableDocumentBase implements AccountsPayableDocument {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountsPayableDocumentBase.class);

    // SHARED FIELDS BETWEEN PAYMENT REQUEST AND CREDIT MEMO
    protected Timestamp accountsPayableApprovalTimestamp;
    protected String lastActionPerformedByPersonId;
    protected String accountsPayableProcessorIdentifier;
    protected boolean holdIndicator;
    protected Timestamp extractedTimestamp;
    protected Integer purchaseOrderIdentifier;
    protected String processingCampusCode;
    protected String noteLine1Text;
    protected String noteLine2Text;
    protected String noteLine3Text;
    protected boolean continuationAccountIndicator;
    protected boolean closePurchaseOrderIndicator;
    protected boolean reopenPurchaseOrderIndicator;
    protected String bankCode;

    protected boolean unmatchedOverride; // not persisted

    // NOT PERSISTED IN DB
    // BELOW USED BY ROUTING
    protected String chartOfAccountsCode;
    protected String organizationCode;

    // NOT PERSISTED IN DB
    // BELOW USED BY GL ENTRY CREATION
    protected boolean generateEncumbranceEntries;
    protected String debitCreditCodeForGLEntries;
    protected PurApItemUseTax offsetUseTax;

    // REFERENCE OBJECTS
    protected CampusParameter processingCampus;
    protected transient PurchaseOrderDocument purchaseOrderDocument;
    protected Bank bank;

    /**
     * Constructs a AccountsPayableDocumentBase
     */
    public AccountsPayableDocumentBase() {
        super();
        setUnmatchedOverride(false);
    }

    public void setLineItemTotal(KualiDecimal total) {
        // do nothing, this is so that the jsp won't complain about lineItemTotal have no setter method.
    }

    public void setGrandTotal(KualiDecimal total) {
        // do nothing, this is so that the jsp won't complain about grandTotal have no setter method.
    }

    /**
     * Overriding to stop the deleting of general ledger entries.
     *
     * @see org.kuali.kfs.sys.document.GeneralLedgerPostingDocumentBase#removeGeneralLedgerPendingEntries()
     */
    @Override
    protected void removeGeneralLedgerPendingEntries() {
        // do not delete entries for PREQ or CM (hjs)
    }

    /**
     * @see org.kuali.kfs.module.purap.document.AccountsPayableDocument#requiresAccountsPayableReviewRouting()
     */
    @Override
    public boolean requiresAccountsPayableReviewRouting() {
        return !approvalAtAccountsPayableReviewAllowed();
    }

    /**
     * @see org.kuali.kfs.module.purap.document.AccountsPayableDocument#approvalAtAccountsPayableReviewAllowed()
     */
    @Override
    public boolean approvalAtAccountsPayableReviewAllowed() {
        return !(isAttachmentRequired() && documentHasNoImagesAttached());
    }

    /**
     * Checks whether an attachment is required
     *
     * @return - true if attachment is required, otherwise false
     */
    protected abstract boolean isAttachmentRequired();

    /**
     * Checks all documents notes for attachments and to be overriden by sub class
     *
     * @return - true if document does not have an image attached, false otherwise
     */
    public abstract boolean documentHasNoImagesAttached();

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#populateDocumentForRouting()
     */
    @Override
    public void populateDocumentForRouting() {
        if (ObjectUtils.isNotNull(getPurchaseOrderDocument())) {
            this.setChartOfAccountsCode(getPurchaseOrderDocument().getChartOfAccountsCode());
            this.setOrganizationCode(getPurchaseOrderDocument().getOrganizationCode());
            if (ObjectUtils.isNull(this.getPurchaseOrderDocument().getDocumentHeader().getDocumentNumber())) {
                this.getPurchaseOrderDocument().refreshReferenceObject(KFSPropertyConstants.DOCUMENT_HEADER);
            }
        }
        super.populateDocumentForRouting();
    }

    /**
     * Calls a custom prepare for save method, as the super class does GL entry creation that causes problems with AP documents.
     *
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#prepareForSave(org.kuali.rice.krad.rule.event.KualiDocumentEvent)
     */
    @Override
    public void prepareForSave(KualiDocumentEvent event) {

        // copied from super because we can't call super for AP docs
        customPrepareForSave(event);

        // DO NOT CALL SUPER HERE!! Cannot call super because it will mess up the GL entry creation process (hjs)
        // super.prepareForSave(event);
    }

    /**
     * Helper method to be called from custom prepare for save and to be overriden by sub class.
     *
     * @return - Po Document Type
     */
    public abstract String getPoDocumentTypeForAccountsPayableDocumentCancel();

    /**
     * @see org.kuali.rice.krad.document.DocumentBase#handleRouteLevelChange(org.kuali.rice.kew.clientapp.vo.DocumentRouteLevelChangeDTO)
     */
    @Override
    public void doRouteLevelChange(DocumentRouteLevelChange levelChangeEvent) {
        LOG.debug("handleRouteLevelChange() started");
        super.doRouteLevelChange(levelChangeEvent);

        //process node change for documents
        String newNodeName = levelChangeEvent.getNewNodeName();
        processNodeChange(newNodeName, levelChangeEvent.getOldNodeName());

        // KFSMI-9715 - need to call this after processNodeChange, otherwise if PO is closed while processing PREQ
        // it gets saved before encumbrence is relieved, and the Total Encumbrance Amount Relieved and TotalPaidAmount
        // on the PREQ didn't reflect the invoice amount, and the amount paid on the PO wasn't being set correctly.
        saveDocumentFromPostProcessing();
    }

    /**
     * Hook to allow processing after a route level is passed.
     *
     * @param newNodeName - current route level
     * @param oldNodeName - previous route level
     * @return - true if process completes to valid state
     */
    public abstract boolean processNodeChange(String newNodeName, String oldNodeName);

    /**
     * Retrieves node details object based on name.
     *
     * @param nodeName - route level
     * @return - Information about the supplied route level
     */
    //public abstract NodeDetails getNodeDetailEnum(String nodeName);

    /**
     * Hook point to allow processing after a save.
     */
    public abstract void saveDocumentFromPostProcessing();

    // GETTERS AND SETTERS
    @Override
    public Integer getPurchaseOrderIdentifier() {
        return purchaseOrderIdentifier;
    }

    @Override
    public void setPurchaseOrderIdentifier(Integer purchaseOrderIdentifier) {
        this.purchaseOrderIdentifier = purchaseOrderIdentifier;
    }

    @Override
    public String getAccountsPayableProcessorIdentifier() {
        return accountsPayableProcessorIdentifier;
    }

    @Override
    public void setAccountsPayableProcessorIdentifier(String accountsPayableProcessorIdentifier) {
        this.accountsPayableProcessorIdentifier = accountsPayableProcessorIdentifier;
    }

    @Override
    public String getLastActionPerformedByPersonId() {
        return lastActionPerformedByPersonId;
    }

    @Override
    public void setLastActionPerformedByPersonId(String lastActionPerformedByPersonId) {
        this.lastActionPerformedByPersonId = lastActionPerformedByPersonId;
    }

    @Override
    public String getProcessingCampusCode() {
        return processingCampusCode;
    }

    @Override
    public void setProcessingCampusCode(String processingCampusCode) {
        this.processingCampusCode = processingCampusCode;
    }

    @Override
    public Timestamp getAccountsPayableApprovalTimestamp() {
        return accountsPayableApprovalTimestamp;
    }

    @Override
    public void setAccountsPayableApprovalTimestamp(Timestamp accountsPayableApprovalTimestamp) {
        this.accountsPayableApprovalTimestamp = accountsPayableApprovalTimestamp;
    }

    @Override
    public Timestamp getExtractedTimestamp() {
        return extractedTimestamp;
    }

    @Override
    public void setExtractedTimestamp(Timestamp extractedTimestamp) {
        this.extractedTimestamp = extractedTimestamp;
    }

    @Override
    public boolean isHoldIndicator() {
        return holdIndicator;
    }

    @Override
    public void setHoldIndicator(boolean holdIndicator) {
        this.holdIndicator = holdIndicator;
    }

    @Override
    public String getNoteLine1Text() {
        return noteLine1Text;
    }

    @Override
    public void setNoteLine1Text(String noteLine1Text) {
        this.noteLine1Text = noteLine1Text;
    }

    @Override
    public String getNoteLine2Text() {
        return noteLine2Text;
    }

    @Override
    public void setNoteLine2Text(String noteLine2Text) {
        this.noteLine2Text = noteLine2Text;
    }

    @Override
    public String getNoteLine3Text() {
        return noteLine3Text;
    }

    @Override
    public void setNoteLine3Text(String noteLine3Text) {
        this.noteLine3Text = noteLine3Text;
    }

    @Override
    public CampusParameter getProcessingCampus() {
        return processingCampus;
    }

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    public boolean isGenerateEncumbranceEntries() {
        return generateEncumbranceEntries;
    }

    public void setGenerateEncumbranceEntries(boolean generateEncumbranceEntries) {
        this.generateEncumbranceEntries = generateEncumbranceEntries;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.AccountsPayableDocument#getPurchaseOrderDocument()
     */
    @Override
    public PurchaseOrderDocument getPurchaseOrderDocument() {
        if ((ObjectUtils.isNull(purchaseOrderDocument) || ObjectUtils.isNull(purchaseOrderDocument.getPurapDocumentIdentifier())) && (ObjectUtils.isNotNull(getPurchaseOrderIdentifier()))) {
            setPurchaseOrderDocument(SpringContext.getBean(PurchaseOrderService.class).getCurrentPurchaseOrder(this.getPurchaseOrderIdentifier()));
        }
        return purchaseOrderDocument;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.AccountsPayableDocument#setPurchaseOrderDocument(org.kuali.kfs.module.purap.document.PurchaseOrderDocument)
     */
    @Override
    public void setPurchaseOrderDocument(PurchaseOrderDocument purchaseOrderDocument) {
        if (ObjectUtils.isNull(purchaseOrderDocument)) {
            // KUALI-PURAP 1185 PO Id not being set to null, instead throwing error on main screen that value is invalid.
            // setPurchaseOrderIdentifier(null);
            this.purchaseOrderDocument = null;
        }
        else {
            if (ObjectUtils.isNotNull(purchaseOrderDocument.getPurapDocumentIdentifier())) {
                setPurchaseOrderIdentifier(purchaseOrderDocument.getPurapDocumentIdentifier());
            }
            this.purchaseOrderDocument = purchaseOrderDocument;
        }
    }

    public boolean isClosePurchaseOrderIndicator() {
        return closePurchaseOrderIndicator;
    }

    public void setClosePurchaseOrderIndicator(boolean closePurchaseOrderIndicator) {
        this.closePurchaseOrderIndicator = closePurchaseOrderIndicator;
    }

    public boolean isReopenPurchaseOrderIndicator() {
        return reopenPurchaseOrderIndicator;
    }

    public void setReopenPurchaseOrderIndicator(boolean reopenPurchaseOrderIndicator) {
        this.reopenPurchaseOrderIndicator = reopenPurchaseOrderIndicator;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    /**
     * Sets the processing campus.
     * @deprecated
     * @param processingCampus
     */
    @Deprecated
    public void setProcessingCampus(CampusParameter processingCampus) {
        this.processingCampus = processingCampus;
    }

    // Helper methods
    /**
     * Retrieves the universal user object for the last person to perform an action on the document.
     */
    public Person getLastActionPerformedByUser() {
    	return SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).getPerson(getLastActionPerformedByPersonId());
    }

    /**
     * Retrieves the person name for the last person to perform an action on the document.
     *
     * @return - the person's name who last performed an action on the document.
     */
    public String getLastActionPerformedByPersonName() {
        Person user = getLastActionPerformedByUser();
        if (ObjectUtils.isNull(user)) {
            return "";
        }
        else {
            return user.getName();
        }
    }

    public String getDebitCreditCodeForGLEntries() {
        return debitCreditCodeForGLEntries;
    }

    public void setDebitCreditCodeForGLEntries(String debitCreditCodeForGLEntries) {
        this.debitCreditCodeForGLEntries = debitCreditCodeForGLEntries;
    }

    @Override
    public boolean isUnmatchedOverride() {
        return unmatchedOverride;
    }

    @Override
    public void setUnmatchedOverride(boolean unmatchedOverride) {
        this.unmatchedOverride = unmatchedOverride;
    }

    public boolean getExtractedIndicatorForSearching() {
        return extractedTimestamp != null;
    }

    public boolean isHoldIndicatorForSearching() {
        return holdIndicator;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.AccountsPayableDocument#getGrandTotal()
     */
    @Override
    public abstract KualiDecimal getGrandTotal();

    /**
     * @see org.kuali.kfs.module.purap.document.AccountsPayableDocument#getInitialAmount()
     */
    @Override
    public abstract KualiDecimal getInitialAmount();

    @Override
    public boolean isContinuationAccountIndicator() {
        return continuationAccountIndicator;
    }

    @Override
    public void setContinuationAccountIndicator(boolean continuationAccountIndicator) {
        this.continuationAccountIndicator = continuationAccountIndicator;
    }

    @Override
    public boolean isExtracted() {
        return (ObjectUtils.isNotNull(getExtractedTimestamp()));
    }

    @Override
    public abstract AccountsPayableDocumentSpecificService getDocumentSpecificService();

    @Override
    public AccountsPayableItem getAPItemFromPOItem(PurchaseOrderItem poi) {
        for (AccountsPayableItem preqItem : (List<AccountsPayableItem>) this.getItems()) {
            preqItem.refreshReferenceObject(PurapPropertyConstants.ITEM_TYPE);

            if (preqItem.getItemType().isLineItemIndicator()) {
                if (preqItem.getItemLineNumber().compareTo(poi.getItemLineNumber()) == 0) {
                    return preqItem;
                }
            }
            else {
                return (AccountsPayableItem) SpringContext.getBean(PurapService.class).getBelowTheLineByType(this, poi.getItemType());
            }
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#getItemClass()
     */
    @Override
    public Class getItemClass() {
        return null;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#getPurApSourceDocumentIfPossible()
     */
    @Override
    public PurchasingAccountsPayableDocument getPurApSourceDocumentIfPossible() {
        return null;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#getPurApSourceDocumentLabelIfPossible()
     */
    @Override
    public String getPurApSourceDocumentLabelIfPossible() {
        return null;
    }

    public void updateExtendedPriceOnItems() {
        for (AccountsPayableItem item : (List<AccountsPayableItem>) getItems()) {
            item.refreshReferenceObject(PurapPropertyConstants.ITEM_TYPE);

            final KualiDecimal itemExtendedPrice = (item.getExtendedPrice()==null)?KualiDecimal.ZERO:item.getExtendedPrice();
            if ( ObjectUtils.isNotNull( item.getItemType() ) ) {
                if (item.getItemType().isQuantityBasedGeneralLedgerIndicator() && item.getExtendedPrice()==null ) {
                    KualiDecimal newExtendedPrice = item.calculateExtendedPrice();
                    item.setExtendedPrice(newExtendedPrice);
                }
            }
        }
    }

    /**
     *
     * @see org.kuali.kfs.module.purap.document.AccountsPayableDocument#getTotalRemitAmount()
     */
    @Override
    public KualiDecimal getTotalRemitTax() {
        if(!this.isUseTaxIndicator()) {
            return (KualiDecimal.ZERO.equals(this.getTotalTaxAmount()))?null:this.getTotalTaxAmount();
        }
        return null;
    }

    @Override
    public boolean customizeOffsetGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail accountingLine, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        boolean value = super.customizeOffsetGeneralLedgerPendingEntry(accountingLine, explicitEntry, offsetEntry);
        if(offsetEntry != null && this.offsetUseTax != null) {
            offsetEntry.setChartOfAccountsCode(this.offsetUseTax.getChartOfAccountsCode());
            offsetEntry.refreshReferenceObject(KFSPropertyConstants.CHART);
            offsetEntry.setAccountNumber(this.offsetUseTax.getAccountNumber());
            offsetEntry.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
            offsetEntry.setFinancialObjectCode(this.offsetUseTax.getFinancialObjectCode());
            offsetEntry.refreshReferenceObject(KFSPropertyConstants.FINANCIAL_OBJECT);
        } else {
            value=false;
        }
        return value;
    }

    @Override
    public boolean generateGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, PurApItemUseTax offsetUseTax) {
        this.offsetUseTax = offsetUseTax;
        boolean value = this.generateGeneralLedgerPendingEntries(glpeSourceDetail, sequenceHelper);
        this.offsetUseTax = null;
        return value;
    }

    public String getHoldIndicatorForResult(){
        return isHoldIndicator() ? "Yes" : "No";
    }

    public String getProcessingCampusCodeForSearch(){
        return getProcessingCampusCode();
    }

    public String getDocumentChartOfAccountsCodeForSearching(){
        return getPurchaseOrderDocument().getChartOfAccountsCode();
    }

    public String getDocumentOrganizationCodeForSearching(){
        return getPurchaseOrderDocument().getOrganizationCode();
    }

    /**
     * @return workflow document type for the purap document
     */
    public String getDocumentType() {
        return SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(this.getClass());
    }

    @Override
    public boolean shouldGiveErrorForEmptyAccountsProration() {
        return true;
    }
    
	@Override
    public boolean generateGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        LOG.debug("processGenerateGeneralLedgerPendingEntries(AccountingDocument, AccountingLine, GeneralLedgerPendingEntrySequenceHelper) - start");

        // handle the explicit entry
        // create a reference to the explicitEntry to be populated, so we can pass to the offset method later
        GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();
        processExplicitGeneralLedgerPendingEntry(sequenceHelper, glpeSourceDetail, explicitEntry);

        // increment the sequence counter
        sequenceHelper.increment();

        // handle the offset entry
        GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry(explicitEntry);
        boolean success = processOffsetGeneralLedgerPendingEntry(sequenceHelper, glpeSourceDetail, explicitEntry, offsetEntry);
        // ** START AZ ** KFSI-4830: handle use tax offset entries for PREQs and CMs
        if(this instanceof PaymentRequestDocument || this instanceof VendorCreditMemoDocument) {
            processUseTaxOffsetGeneralLedgerPendingEntries(sequenceHelper, glpeSourceDetail, explicitEntry, offsetEntry);
        }
        // ** END AZ **

        LOG.debug("processGenerateGeneralLedgerPendingEntries(AccountingDocument, AccountingLine, GeneralLedgerPendingEntrySequenceHelper) - end");
        return success;
    }

    // ** START AZ ** KFSI-4830: Generate use tax offset entries
    private void processUseTaxOffsetGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        // Get the object code for the use tax offsets from the parameter
        String glpeOffsetObjectCode = parameterService.getParameterValueAsString(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.GENERAL_LEDGER_PENDING_ENTRY_OFFSET_OBJECT_CODE);
        
        // Get the use tax, tax region.
        Map<String, String> pkMap = new HashMap<String, String>();
        pkMap.put("taxRegionCode", parameterService.getParameterValueAsString(ProcurementCardDocument.class, PurapParameterConstants.GL_USETAX_TAX_REGION));
        TaxRegion taxRegion = (TaxRegion) getBusinessObjectService().findByPrimaryKey(TaxRegion.class, pkMap);
        
        if(offsetEntry.getAccountNumber().equals(taxRegion.getAccountNumber())) {            
            /** START AZ KFSI-6119 **/
            //clear sub account number and sub object code
            offsetEntry.setSubAccountNumber(null);
            offsetEntry.setSubAccount(null);
            offsetEntry.setFinancialSubObjectCode(null);
            offsetEntry.setFinancialSubObject(null);
            /** END AZ KFSI-6119 **/   
            //**START AZ** KATTS-81 Project Codes should not be added to offset tax account
            offsetEntry.setProjectCode(null);
            //**END AZ**
                     
            SystemOptions options = SpringContext.getBean(OptionsService.class).getOptions(explicitEntry.getUniversityFiscalYear());
            
            sequenceHelper.increment();

            // Generate and add the use tax offset entry for the explicit entry
            GeneralLedgerPendingEntry useTaxExplicit = new GeneralLedgerPendingEntry(explicitEntry);
            useTaxExplicit.setTransactionLedgerEntrySequenceNumber(sequenceHelper.getSequenceCounter());
            useTaxExplicit.setTransactionLedgerEntryDescription(PurapConstants.GLPE_USE_TAX_GENERATED_OFFSET_DESCRIPTION);
            useTaxExplicit.setFinancialObjectCode(glpeOffsetObjectCode);
            useTaxExplicit.setFinancialObjectTypeCode(options.getFinancialObjectTypeAssetsCd());
            // Using the Debit/Credit code from the offset entry so it's the opposite of the explicit entry
            useTaxExplicit.setTransactionDebitCreditCode(offsetEntry.getTransactionDebitCreditCode());
            
            addPendingEntry(useTaxExplicit);
            
            sequenceHelper.increment();
            
            // Generate and add the use tax offset entry for the offset entry
            GeneralLedgerPendingEntry useTaxOffset = new GeneralLedgerPendingEntry(offsetEntry);
            useTaxOffset.setTransactionLedgerEntrySequenceNumber(sequenceHelper.getSequenceCounter());
            useTaxOffset.setTransactionLedgerEntryDescription(PurapConstants.GLPE_USE_TAX_GENERATED_OFFSET_DESCRIPTION);
            useTaxOffset.setFinancialObjectCode(glpeOffsetObjectCode);
            useTaxOffset.setFinancialObjectTypeCode(options.getFinancialObjectTypeAssetsCd());
            // Using the Debit/Credit code from the explicit entry so it's the opposite of the offset entry
            useTaxOffset.setTransactionDebitCreditCode(explicitEntry.getTransactionDebitCreditCode());
            
            addPendingEntry(useTaxOffset);
        }
    }
    // ** END AZ **


}

