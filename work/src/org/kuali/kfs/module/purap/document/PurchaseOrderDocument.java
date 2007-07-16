/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.module.purap.document;

import static org.kuali.core.util.KualiDecimal.ZERO;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.Note;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.rule.event.KualiDocumentEvent;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapConstants.RequisitionSources;
import org.kuali.module.purap.PurapConstants.VendorChoice;
import org.kuali.module.purap.bo.CreditMemoView;
import org.kuali.module.purap.bo.ItemType;
import org.kuali.module.purap.bo.PaymentRequestView;
import org.kuali.module.purap.bo.PurchaseOrderAccount;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.bo.PurchaseOrderStatusHistory;
import org.kuali.module.purap.bo.PurchaseOrderVendorChoice;
import org.kuali.module.purap.bo.PurchaseOrderVendorQuote;
import org.kuali.module.purap.bo.PurchaseOrderVendorStipulation;
import org.kuali.module.purap.bo.PurchaseOrderView;
import org.kuali.module.purap.bo.PurchasingApItem;
import org.kuali.module.purap.bo.RecurringPaymentFrequency;
import org.kuali.module.purap.bo.RequisitionItem;
import org.kuali.module.purap.service.PurchaseOrderPostProcessorService;
import org.kuali.module.vendor.VendorConstants;
import org.kuali.module.vendor.bo.PaymentTermType;
import org.kuali.module.vendor.bo.ShippingPaymentTerms;
import org.kuali.module.vendor.bo.ShippingTitle;
import org.kuali.module.vendor.bo.VendorDetail;

import edu.iu.uis.eden.clientapp.vo.DocumentRouteLevelChangeVO;

/**
 * Purchase Order Document
 */
public class PurchaseOrderDocument extends PurchasingDocumentBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchaseOrderDocument.class);

    private Date purchaseOrderCreateDate;
    private Integer requisitionIdentifier;
    private String purchaseOrderVendorChoiceCode;
    private String recurringPaymentFrequencyCode;
    private KualiDecimal recurringPaymentAmount;
    private Date recurringPaymentDate;
    private KualiDecimal initialPaymentAmount;
    private Date initialPaymentDate;
    private KualiDecimal finalPaymentAmount;
    private Date finalPaymentDate;
    private Date purchaseOrderInitialOpenDate;
    private Date purchaseOrderLastTransmitDate;
    private Date purchaseOrderQuoteDueDate;
    private String purchaseOrderQuoteTypeCode;
    private String purchaseOrderQuoteVendorNoteText;
    private boolean purchaseOrderConfirmedIndicator;
    private String purchaseOrderCommodityDescription;
    private Integer purchaseOrderPreviousIdentifier;
    private Integer alternateVendorHeaderGeneratedIdentifier;
    private Integer alternateVendorDetailAssignedIdentifier;
    private Integer newQuoteVendorHeaderGeneratedIdentifier;
    private Integer newQuoteVendorDetailAssignedIdentifier;
    private String alternateVendorName;
    private String statusChange;
    private String statusChangeNote;
    private boolean purchaseOrderCurrentIndicator;
    private boolean pendingActionIndicator;
    private Date purchaseOrderFirstTransmissionDate;
    
    //COLLECTIONS
    private List<PurchaseOrderVendorStipulation> purchaseOrderVendorStipulations;
    private List<PurchaseOrderVendorQuote> purchaseOrderVendorQuotes;

    // NOT PERSISTED IN DB
    private String alternateVendorNumber;
    private String purchaseOrderRetransmissionMethodCode;
    private String retransmitHeader;
    private Integer purchaseOrderQuoteListIdentifier;
    
    // REFERENCE OBJECTS
    private PurchaseOrderVendorChoice purchaseOrderVendorChoice;
    private PaymentTermType vendorPaymentTerms;
    private ShippingTitle vendorShippingTitle;
    private ShippingPaymentTerms vendorShippingPaymentTerms;
    private RecurringPaymentFrequency recurringPaymentFrequency;
    
    /**
     * Default constructor.
     */
    public PurchaseOrderDocument() {
        super();
        this.purchaseOrderVendorStipulations = new TypedArrayList( PurchaseOrderVendorStipulation.class );
        this.purchaseOrderVendorQuotes = new TypedArrayList( PurchaseOrderVendorQuote.class );
    }

    /**
     * @see org.kuali.core.bo.PersistableBusinessObjectBase#isBoNotesSupport()
     */
    @Override
    public boolean isBoNotesSupport() {
        return true;
    }

    @Override
    public void customPrepareForSave(KualiDocumentEvent event) {
        // Set outstanding encumbered quantity/amount on items
        for (Iterator items = this.getItems().iterator(); items.hasNext();) {
            PurchaseOrderItem item = (PurchaseOrderItem) items.next();

            // Set quantities
            item.setItemOutstandingEncumberedQuantity(item.getItemQuantity());
            item.setItemReceivedTotalQuantity(ZERO);
            item.setItemReturnedTotalQuantity(ZERO);
            item.setItemInvoicedTotalQuantity(ZERO);
            item.setItemInvoicedTotalAmount(ZERO);

            // Set amount
            item.setItemOutstandingEncumbranceAmount(item.getExtendedPrice() == null ? ZERO : item.getExtendedPrice());

            //TODO check setting of outstanding amount in the accounts
            List accounts = (List)item.getSourceAccountingLines();
//          Collections.sort(accounts);

            KualiDecimal accountTotalAmount = new KualiDecimal(0);
            PurchaseOrderAccount lastAccount = null;

            for (Iterator iterator = accounts.iterator(); iterator.hasNext();) {
                PurchaseOrderAccount account = (PurchaseOrderAccount) iterator.next();

                if (!account.isEmpty()) {
                    KualiDecimal acctAmount = item.getExtendedPrice().multiply(new KualiDecimal(account.getAccountLinePercent().toString()));
                    // acctAmount = acctAmount.divide(new KualiDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
                    acctAmount = acctAmount.divide(new KualiDecimal(100));
                    account.setAmount(acctAmount);
                    account.setItemAccountOutstandingEncumbranceAmount(acctAmount);
                    LOG.debug("getDisplayItems() account amount = " + account.getAmount());

                    accountTotalAmount = accountTotalAmount.add(acctAmount);
                    lastAccount = (PurchaseOrderAccount) ObjectUtils.deepCopy(account);
                }
            }//endfor accounts

          // Rounding
//          if (lastAccount != null && this.getAmount() != null) {
//              KualiDecimal difference = this.getAmount().subtract(accountTotalAmount);
//              KualiDecimal tempAmount = lastAccount.getAmount();
//              lastAccount.setAmount(tempAmount.add(difference));
//          }
        }//endfor items

        this.setSourceAccountingLines(SpringServiceLocator.getPurapAccountingService().generateSummaryWithNoZeroTotals(this.getItems()));
    }//end customPrepareForSave()
    
    @Override
    public void prepareForSave(KualiDocumentEvent event) {
        if (ObjectUtils.isNull(getPurapDocumentIdentifier())) {
            //need to save to generate PO id to save in GL entries
            SpringServiceLocator.getPurchaseOrderService().save(this);
        }
        super.prepareForSave(event);
    }

    public void setDefaultValuesForAPO() {
        this.setPurchaseOrderAutomaticIndicator(Boolean.TRUE);

        if (!RequisitionSources.B2B.equals(this.getRequisitionSourceCode())) {
            this.setPurchaseOrderVendorChoiceCode(VendorChoice.SMALL_ORDER);
        }
    }
    
    public void populatePurchaseOrderFromRequisition(RequisitionDocument requisitionDocument) {
// TODO fix this (is this data correct?  is there a better way of doing this?
//        this.setPurchaseOrderCreateDate(requisitionDocument.getDocumentHeader().getWorkflowDocument().getCreateDate());
        this.setPurchaseOrderCreateDate(SpringServiceLocator.getDateTimeService().getCurrentSqlDate());
        
        this.getDocumentHeader().setOrganizationDocumentNumber(requisitionDocument.getDocumentHeader().getOrganizationDocumentNumber());
        this.getDocumentHeader().setFinancialDocumentDescription(requisitionDocument.getDocumentHeader().getFinancialDocumentDescription());

        this.setPurchaseOrderBeginDate(requisitionDocument.getPurchaseOrderBeginDate());
        
        this.setBillingCityName(requisitionDocument.getBillingCityName());
        this.setBillingCountryCode(requisitionDocument.getBillingCountryCode());
        this.setBillingLine1Address(requisitionDocument.getBillingLine1Address());
        this.setBillingLine2Address(requisitionDocument.getBillingLine2Address());
        this.setBillingName(requisitionDocument.getBillingName());
        this.setBillingPhoneNumber(requisitionDocument.getBillingPhoneNumber());
        this.setBillingPostalCode(requisitionDocument.getBillingPostalCode());
        this.setBillingStateCode(requisitionDocument.getBillingStateCode());
        this.setContractManagerCode(requisitionDocument.getContractManagerCode());
        this.setPurchaseOrderCostSourceCode(requisitionDocument.getPurchaseOrderCostSourceCode());
        this.setDeliveryBuildingCode(requisitionDocument.getDeliveryBuildingCode());
        this.setDeliveryBuildingRoomNumber(requisitionDocument.getDeliveryBuildingRoomNumber());
        this.setDeliveryBuildingName(requisitionDocument.getDeliveryBuildingName());
        this.setDeliveryCampusCode(requisitionDocument.getDeliveryCampusCode());
        this.setDeliveryCityName(requisitionDocument.getDeliveryCityName());
        this.setDeliveryCountryCode(requisitionDocument.getDeliveryCountryCode());
        this.setDeliveryInstructionText(requisitionDocument.getDeliveryInstructionText());
        this.setDeliveryBuildingLine1Address(requisitionDocument.getDeliveryBuildingLine1Address());
        this.setDeliveryBuildingLine2Address(requisitionDocument.getDeliveryBuildingLine2Address());
        this.setDeliveryPostalCode(requisitionDocument.getDeliveryPostalCode());
        this.setDeliveryRequiredDate(requisitionDocument.getDeliveryRequiredDate());
        this.setDeliveryRequiredDateReasonCode(requisitionDocument.getDeliveryRequiredDateReasonCode());
        this.setDeliveryStateCode(requisitionDocument.getDeliveryStateCode());
        this.setDeliveryToEmailAddress(requisitionDocument.getDeliveryToEmailAddress());
        this.setDeliveryToName(requisitionDocument.getDeliveryToName());
        this.setDeliveryToPhoneNumber(requisitionDocument.getDeliveryToPhoneNumber());
        this.setPostingYear(requisitionDocument.getPostingYear());
        this.setPurchaseOrderEndDate(requisitionDocument.getPurchaseOrderEndDate());
        this.setChartOfAccountsCode(requisitionDocument.getChartOfAccountsCode());
        this.setFundingSourceCode(requisitionDocument.getFundingSourceCode());
        this.setInstitutionContactEmailAddress(requisitionDocument.getInstitutionContactEmailAddress());
        this.setInstitutionContactName(requisitionDocument.getInstitutionContactName());
        this.setInstitutionContactPhoneNumber(requisitionDocument.getInstitutionContactPhoneNumber());
        this.setNonInstitutionFundAccountNumber(requisitionDocument.getNonInstitutionFundAccountNumber());
        this.setNonInstitutionFundChartOfAccountsCode(requisitionDocument.getNonInstitutionFundChartOfAccountsCode());
        this.setNonInstitutionFundOrgChartOfAccountsCode(requisitionDocument.getNonInstitutionFundOrgChartOfAccountsCode());
        this.setNonInstitutionFundOrganizationCode(requisitionDocument.getNonInstitutionFundOrganizationCode());
        this.setOrganizationCode(requisitionDocument.getOrganizationCode());
        this.setRecurringPaymentTypeCode(requisitionDocument.getRecurringPaymentTypeCode());
        this.setRequestorPersonEmailAddress(requisitionDocument.getRequestorPersonEmailAddress());
        this.setRequestorPersonName(requisitionDocument.getRequestorPersonName());
        this.setRequestorPersonPhoneNumber(requisitionDocument.getRequestorPersonPhoneNumber());
        this.setRequisitionIdentifier(requisitionDocument.getPurapDocumentIdentifier());
        this.setPurchaseOrderTotalLimit(requisitionDocument.getPurchaseOrderTotalLimit());
        this.setPurchaseOrderTransmissionMethodCode(requisitionDocument.getPurchaseOrderTransmissionMethodCode());
        this.setVendorCityName(requisitionDocument.getVendorCityName());
        this.setVendorContract(requisitionDocument.getVendorContract());
        this.setVendorCountryCode(requisitionDocument.getVendorCountryCode());
        this.setVendorCustomerNumber(requisitionDocument.getVendorCustomerNumber());
        this.setVendorDetailAssignedIdentifier(requisitionDocument.getVendorDetailAssignedIdentifier());
        this.setVendorFaxNumber(requisitionDocument.getVendorFaxNumber());
        this.setVendorHeaderGeneratedIdentifier(requisitionDocument.getVendorHeaderGeneratedIdentifier());
        this.setVendorLine1Address(requisitionDocument.getVendorLine1Address());
        this.setVendorLine2Address(requisitionDocument.getVendorLine2Address());
        this.setVendorName(requisitionDocument.getVendorName());
        this.setVendorNoteText(requisitionDocument.getVendorNoteText());
        this.setVendorPhoneNumber(requisitionDocument.getVendorPhoneNumber());
        this.setVendorPostalCode(requisitionDocument.getVendorPostalCode());
        this.setVendorRestrictedIndicator(requisitionDocument.getVendorRestrictedIndicator());
        this.setVendorStateCode(requisitionDocument.getVendorStateCode());
        this.setExternalOrganizationB2bSupplierIdentifier(requisitionDocument.getExternalOrganizationB2bSupplierIdentifier());
        this.setRequisitionSourceCode(requisitionDocument.getRequisitionSourceCode());
        this.setAccountsPayablePurchasingDocumentLinkIdentifier(requisitionDocument.getAccountsPayablePurchasingDocumentLinkIdentifier());

        this.setStatusCode(PurapConstants.PurchaseOrderStatuses.IN_PROCESS);
        //copy items from req to pending (which will copy the item's accounts and assets)
        List<PurchaseOrderItem> items = new ArrayList();
        for (PurchasingApItem reqItem : ((PurchasingAccountsPayableDocument) requisitionDocument).getItems()) {
          items.add(new PurchaseOrderItem((RequisitionItem)reqItem, this));
        }
        this.setItems(items);
        
    }

    public void refreshAllReferences() {
        super.refreshAllReferences();
    }
    
    public PurchaseOrderVendorStipulation getPurchaseOrderVendorStipulation(int index) {
        while (getPurchaseOrderVendorStipulations().size() <= index) {
            getPurchaseOrderVendorStipulations().add(new PurchaseOrderVendorStipulation());
        }
        return (PurchaseOrderVendorStipulation)purchaseOrderVendorStipulations.get(index);
    }

    /**
     * @see org.kuali.core.document.DocumentBase#handleRouteStatusChange()
     */
    @Override
    public void handleRouteStatusChange() {
        LOG.debug("handleRouteStatusChange() started");
        super.handleRouteStatusChange();

        // additional processing
        PurchaseOrderPostProcessorService popp = 
            SpringServiceLocator.getPurchaseOrderService().convertDocTypeToService(getDocumentHeader().getWorkflowDocument().getDocumentType());
        // null if defined as empty string in map
        if (popp != null) {
            popp.handleRouteStatusChange(this);
        }
    }

    /**
     * @see org.kuali.core.document.DocumentBase#handleRouteLevelChange(edu.iu.uis.eden.clientapp.vo.DocumentRouteLevelChangeVO)
     */
    @Override
    public void handleRouteLevelChange(DocumentRouteLevelChangeVO levelChangeEvent) {
        LOG.debug("handleRouteLevelChange() started");
        super.handleRouteLevelChange(levelChangeEvent);

        // additional processing
        PurchaseOrderPostProcessorService popp = 
            SpringServiceLocator.getPurchaseOrderService().convertDocTypeToService(getDocumentHeader().getWorkflowDocument().getDocumentType());
        // null if defined as empty string in map
        if (popp != null) {
            popp.handleRouteLevelChange(new DocumentRouteLevelChangeVO(), this);
        }
    }

    public List getItemsActiveOnly() {
        List returnList = new ArrayList();
        for (Iterator iter = getItems().iterator(); iter.hasNext();) {
            PurchaseOrderItem item = (PurchaseOrderItem) iter.next();
            if (item.isItemActiveIndicator()) {
                returnList.add(item);
            }
        }
        return returnList;
    }

    // SETTERS AND GETTERS
    public Integer getAlternateVendorDetailAssignedIdentifier() {
        return alternateVendorDetailAssignedIdentifier;
    }

    public void setAlternateVendorDetailAssignedIdentifier(Integer alternateVendorDetailAssignedIdentifier) {
        this.alternateVendorDetailAssignedIdentifier = alternateVendorDetailAssignedIdentifier;
    }

    public Integer getAlternateVendorHeaderGeneratedIdentifier() {
        return alternateVendorHeaderGeneratedIdentifier;
    }

    public void setAlternateVendorHeaderGeneratedIdentifier(Integer alternateVendorHeaderGeneratedIdentifier) {
        this.alternateVendorHeaderGeneratedIdentifier = alternateVendorHeaderGeneratedIdentifier;
    }

    public String getAlternateVendorName() {
        return alternateVendorName;
    }

    public void setAlternateVendorName(String alternateVendorName) {
        this.alternateVendorName = alternateVendorName;
    }

    public KualiDecimal getFinalPaymentAmount() {
        return finalPaymentAmount;
    }

    public void setFinalPaymentAmount(KualiDecimal finalPaymentAmount) {
        this.finalPaymentAmount = finalPaymentAmount;
    }

    public Date getFinalPaymentDate() {
        return finalPaymentDate;
    }

    public void setFinalPaymentDate(Date finalPaymentDate) {
        this.finalPaymentDate = finalPaymentDate;
    }

    public KualiDecimal getInitialPaymentAmount() {
        return initialPaymentAmount;
    }

    public void setInitialPaymentAmount(KualiDecimal initialPaymentAmount) {
        this.initialPaymentAmount = initialPaymentAmount;
    }

    public Date getInitialPaymentDate() {
        return initialPaymentDate;
    }

    public void setInitialPaymentDate(Date initialPaymentDate) {
        this.initialPaymentDate = initialPaymentDate;
    }

    public String getPurchaseOrderCommodityDescription() {
        return purchaseOrderCommodityDescription;
    }

    public void setPurchaseOrderCommodityDescription(String purchaseOrderCommodityDescription) {
        this.purchaseOrderCommodityDescription = purchaseOrderCommodityDescription;
    }

    public boolean isPurchaseOrderConfirmedIndicator() {
        return purchaseOrderConfirmedIndicator;
    }

    public void setPurchaseOrderConfirmedIndicator(boolean purchaseOrderConfirmedIndicator) {
        this.purchaseOrderConfirmedIndicator = purchaseOrderConfirmedIndicator;
    }

    public Date getPurchaseOrderCreateDate() {
        return purchaseOrderCreateDate;
    }

    public void setPurchaseOrderCreateDate(Date purchaseOrderCreateDate) {
        this.purchaseOrderCreateDate = purchaseOrderCreateDate;
    }

    public Date getPurchaseOrderInitialOpenDate() {
        return purchaseOrderInitialOpenDate;
    }

    public void setPurchaseOrderInitialOpenDate(Date purchaseOrderInitialOpenDate) {
        this.purchaseOrderInitialOpenDate = purchaseOrderInitialOpenDate;
    }

    public Date getPurchaseOrderLastTransmitDate() {
        return purchaseOrderLastTransmitDate;
    }

    public void setPurchaseOrderLastTransmitDate(Date purchaseOrderLastTransmitDate) {
        this.purchaseOrderLastTransmitDate = purchaseOrderLastTransmitDate;
    }

    public Integer getPurchaseOrderPreviousIdentifier() {
        return purchaseOrderPreviousIdentifier;
    }

    public void setPurchaseOrderPreviousIdentifier(Integer purchaseOrderPreviousIdentifier) {
        this.purchaseOrderPreviousIdentifier = purchaseOrderPreviousIdentifier;
    }

    public Date getPurchaseOrderQuoteDueDate() {
        return purchaseOrderQuoteDueDate;
    }

    public void setPurchaseOrderQuoteDueDate(Date purchaseOrderQuoteDueDate) {
        this.purchaseOrderQuoteDueDate = purchaseOrderQuoteDueDate;
    }

    public String getPurchaseOrderQuoteTypeCode() {
        return purchaseOrderQuoteTypeCode;
    }

    public void setPurchaseOrderQuoteTypeCode(String purchaseOrderQuoteTypeCode) {
        this.purchaseOrderQuoteTypeCode = purchaseOrderQuoteTypeCode;
    }

    public String getPurchaseOrderQuoteVendorNoteText() {
        return purchaseOrderQuoteVendorNoteText;
    }

    public void setPurchaseOrderQuoteVendorNoteText(String purchaseOrderQuoteVendorNoteText) {
        this.purchaseOrderQuoteVendorNoteText = purchaseOrderQuoteVendorNoteText;
    }

    public String getPurchaseOrderVendorChoiceCode() {
        return purchaseOrderVendorChoiceCode;
    }

    public void setPurchaseOrderVendorChoiceCode(String purchaseOrderVendorChoiceCode) {
        this.purchaseOrderVendorChoiceCode = purchaseOrderVendorChoiceCode;
    }

    public KualiDecimal getRecurringPaymentAmount() {
        return recurringPaymentAmount;
    }

    public void setRecurringPaymentAmount(KualiDecimal recurringPaymentAmount) {
        this.recurringPaymentAmount = recurringPaymentAmount;
    }

    public Date getRecurringPaymentDate() {
        return recurringPaymentDate;
    }

    public void setRecurringPaymentDate(Date recurringPaymentDate) {
        this.recurringPaymentDate = recurringPaymentDate;
    }

    public String getRecurringPaymentFrequencyCode() {
        return recurringPaymentFrequencyCode;
    }

    public void setRecurringPaymentFrequencyCode(String recurringPaymentFrequencyCode) {
        this.recurringPaymentFrequencyCode = recurringPaymentFrequencyCode;
    }

    public Integer getRequisitionIdentifier() {
        return requisitionIdentifier;
    }

    public void setRequisitionIdentifier(Integer requisitionIdentifier) {
        this.requisitionIdentifier = requisitionIdentifier;
    }

    public PurchaseOrderVendorChoice getPurchaseOrderVendorChoice() {
        return purchaseOrderVendorChoice;
    }

    public void setPurchaseOrderVendorChoice(PurchaseOrderVendorChoice purchaseOrderVendorChoice) {
        this.purchaseOrderVendorChoice = purchaseOrderVendorChoice;
    }

    public RecurringPaymentFrequency getRecurringPaymentFrequency() {
        return recurringPaymentFrequency;
    }

    public void setRecurringPaymentFrequency(RecurringPaymentFrequency recurringPaymentFrequency) {
        this.recurringPaymentFrequency = recurringPaymentFrequency;
    }

    public PaymentTermType getVendorPaymentTerms() {
        return vendorPaymentTerms;
    }

    public void setVendorPaymentTerms(PaymentTermType vendorPaymentTerms) {
        this.vendorPaymentTerms = vendorPaymentTerms;
    }

    public ShippingPaymentTerms getVendorShippingPaymentTerms() {
        return vendorShippingPaymentTerms;
    }

    public void setVendorShippingPaymentTerms(ShippingPaymentTerms vendorShippingPaymentTerms) {
        this.vendorShippingPaymentTerms = vendorShippingPaymentTerms;
    }

    public ShippingTitle getVendorShippingTitle() {
        return vendorShippingTitle;
    }

    public void setVendorShippingTitle(ShippingTitle vendorShippingTitle) {
        this.vendorShippingTitle = vendorShippingTitle;
    }

    public List getPurchaseOrderVendorStipulations() {
        return purchaseOrderVendorStipulations;
    }
    
    public String getStatusChange() {
        return statusChange;
    }

    public void setPurchaseOrderVendorStipulations(List purchaseOrderVendorStipulations) {
        this.purchaseOrderVendorStipulations = purchaseOrderVendorStipulations;
    }

    public List<PurchaseOrderVendorQuote> getPurchaseOrderVendorQuotes() {
        return purchaseOrderVendorQuotes;
    }

    public void setPurchaseOrderVendorQuotes(List<PurchaseOrderVendorQuote> purchaseOrderVendorQuotes) {
        this.purchaseOrderVendorQuotes = purchaseOrderVendorQuotes;
    }

    public PurchaseOrderVendorQuote getPurchaseOrderVendorQuote(int index) {
        while (getPurchaseOrderVendorQuotes().size() <= index) {
            getPurchaseOrderVendorQuotes().add(new PurchaseOrderVendorQuote());
        }
        return (PurchaseOrderVendorQuote)purchaseOrderVendorQuotes.get(index);
    }

    public void setStatusChange(String statusChange) {
        this.statusChange = statusChange;
    }

    public String getStatusChangeNote() {
        return statusChangeNote;
    }

    public void setStatusChangeNote(String statusChangeNote) {
        this.statusChangeNote = statusChangeNote;
    }

    
    public String getPurchaseOrderRetransmissionMethodCode() {
        return purchaseOrderRetransmissionMethodCode;
    }

    public void setPurchaseOrderRetransmissionMethodCode(String purchaseOrderRetransmissionMethodCode) {
        this.purchaseOrderRetransmissionMethodCode = purchaseOrderRetransmissionMethodCode;
    }

    public String getRetransmitHeader() {
        return retransmitHeader;
    }

    public void setRetransmitHeader(String retransmitHeader) {
        this.retransmitHeader = retransmitHeader;
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocument#addToStatusHistories(java.lang.String, java.lang.String)
     */
    public void addToStatusHistories( String oldStatus, String newStatus, Note statusHistoryNote ) {
        PurchaseOrderStatusHistory posh = new PurchaseOrderStatusHistory( oldStatus, newStatus );
        this.addStatusHistoryNote( posh, statusHistoryNote );
        posh.setDocumentHeaderIdentifier(this.documentNumber);
        this.getStatusHistories().add( posh );
    }

    /**
     * Gets the pendingActionIndicator attribute. 
     * @return Returns the pendingActionIndicator.
     */
    public boolean isPendingActionIndicator() {
        return pendingActionIndicator;
    }

    /**
     * Sets the pendingActionIndicator attribute value.
     * @param pendingActionIndicator The pendingActionIndicator to set.
     */
    public void setPendingActionIndicator(boolean pendingActionIndicator) {
        this.pendingActionIndicator = pendingActionIndicator;
    }

    /**
     * Gets the purchaseOrderCurrentIndicator attribute. 
     * @return Returns the purchaseOrderCurrentIndicator.
     */
    public boolean isPurchaseOrderCurrentIndicator() {
        return purchaseOrderCurrentIndicator;
    }

    /**
     * Sets the purchaseOrderCurrentIndicator attribute value.
     * @param purchaseOrderCurrentIndicator The purchaseOrderCurrentIndicator to set.
     */
    public void setPurchaseOrderCurrentIndicator(boolean purchaseOrderCurrentIndicator) {
        this.purchaseOrderCurrentIndicator = purchaseOrderCurrentIndicator;
    }

    /**
     * Gets the purchaseOrderFirstTransmissionDate attribute. 
     * @return Returns the purchaseOrderFirstTransmissionDate.
     */
    public Date getPurchaseOrderFirstTransmissionDate() {
        return purchaseOrderFirstTransmissionDate;
    }

    /**
     * Sets the purchaseOrderFirstTransmissionDate attribute value.
     * @param purchaseOrderFirstTransmissionDate The purchaseOrderFirstTransmissionDate to set.
     */
    public void setPurchaseOrderFirstTransmissionDate(Date purchaseOrderFirstTransmissionDate) {
        this.purchaseOrderFirstTransmissionDate = purchaseOrderFirstTransmissionDate;
    }    
    
    /**
     * Gets the alternateVendorNumber attribute. 
     * @return Returns the alternateVendorNumber.
     */
    public String getAlternateVendorNumber() {
        String hdrGenId = "";
        String detAssgndId = "";
        String vendorNumber = "";
        if( this.alternateVendorHeaderGeneratedIdentifier != null ) {
            hdrGenId = this.alternateVendorHeaderGeneratedIdentifier.toString();
        }
        if( this.alternateVendorDetailAssignedIdentifier != null ) {
            detAssgndId = this.alternateVendorDetailAssignedIdentifier.toString();
        }
        if (!StringUtils.isEmpty(hdrGenId) && !StringUtils.isEmpty(detAssgndId)) {
            vendorNumber = hdrGenId+"-"+detAssgndId;
        }
        return vendorNumber;
    }
    /**
     * Sets the alternateVendorNumber attribute value.
     * @param alternateVendorNumber The vendorNumber to set.
     */
    public void setAlternateVendorNumber(String vendorNumber) {
        if (! StringUtils.isEmpty(vendorNumber)) {
            int dashInd = vendorNumber.indexOf("-");
            if (vendorNumber.length() >= dashInd) {
                String vndrHdrGenId = vendorNumber.substring( 0, dashInd );
                String vndrDetailAssgnedId = vendorNumber.substring( dashInd + 1 );
                if (!StringUtils.isEmpty(vndrHdrGenId) && !StringUtils.isEmpty(vndrDetailAssgnedId)) {
                    this.alternateVendorHeaderGeneratedIdentifier = new Integer(vndrHdrGenId);
                    this.alternateVendorDetailAssignedIdentifier = new Integer(vndrDetailAssgnedId);
                }
            }
        } else {
            this.alternateVendorNumber = vendorNumber;
        }
    }
    
    /**
     * Convenience method to set alternate vendor fields based on a given VendorDetail.
     * 
     * @param vendorDetail
     */
    public void templateAlternateVendor(VendorDetail vendorDetail) {
        if (vendorDetail == null) {
            return;
        }
    
        this.setAlternateVendorNumber(vendorDetail.getVendorHeaderGeneratedIdentifier() + VendorConstants.DASH + vendorDetail.getVendorDetailAssignedIdentifier());
        this.setAlternateVendorName(vendorDetail.getVendorName());
    } 
            
    /**
     * Overriding this from the super class so that Note will use only the oldest
     * PurchaseOrderDocument as the documentBusinessObject.
     * 
     * @see org.kuali.core.document.Document#getDocumentBusinessObject()
     */
    @Override
    public PersistableBusinessObject getDocumentBusinessObject() {
        if (ObjectUtils.isNotNull(getPurapDocumentIdentifier()) && ObjectUtils.isNull(documentBusinessObject)) {
                refreshDocumentBusinessObject();
        }
        return documentBusinessObject;
    }
    
    public void refreshDocumentBusinessObject() {
        documentBusinessObject = SpringServiceLocator.getPurchaseOrderService().getOldestPurchaseOrder(getPurapDocumentIdentifier(),this);
    }

    @Override
    public List<PurchaseOrderView> getRelatedPurchaseOrderViews() {
        return null;
    }
    
    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocumentBase#getItemClass()
     */
    @Override
    public Class getItemClass() {
        return PurchaseOrderItem.class;
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocumentBase#getPurApSourceDocumentIfPossible()
     */
    @Override
    public RequisitionDocument getPurApSourceDocumentIfPossible() {
        RequisitionDocument sourceDoc = null;
        if (ObjectUtils.isNotNull(getRequisitionIdentifier())) {
            sourceDoc = SpringServiceLocator.getRequisitionService().getRequisitionById(getRequisitionIdentifier());
        }
        return sourceDoc;
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocumentBase#getPurApSourceDocumentLabelIfPossible()
     */
    @Override
    public String getPurApSourceDocumentLabelIfPossible() {
        return SpringServiceLocator.getDataDictionaryService().getDocumentLabelByClass(RequisitionDocument.class);
    }

    public Integer getNewQuoteVendorDetailAssignedIdentifier() {
        return newQuoteVendorDetailAssignedIdentifier;
    }

    public void setNewQuoteVendorDetailAssignedIdentifier(Integer newQuoteVendorDetailAssignedIdentifier) {
        this.newQuoteVendorDetailAssignedIdentifier = newQuoteVendorDetailAssignedIdentifier;
    }

    public Integer getNewQuoteVendorHeaderGeneratedIdentifier() {
        return newQuoteVendorHeaderGeneratedIdentifier;
    }

    public void setNewQuoteVendorHeaderGeneratedIdentifier(Integer newQuoteVendorHeaderGeneratedIdentifier) {
        this.newQuoteVendorHeaderGeneratedIdentifier = newQuoteVendorHeaderGeneratedIdentifier;
    }
    
    public Integer getPurchaseOrderQuoteListIdentifier() {
        return purchaseOrderQuoteListIdentifier;
    }

    public void setPurchaseOrderQuoteListIdentifier(Integer purchaseOrderQuoteListIdentifier) {
        this.purchaseOrderQuoteListIdentifier = purchaseOrderQuoteListIdentifier;
    }

    public boolean isPurchaseOrderAwarded() {
        return (getAwardedVendorQuote() != null);
    }

    public PurchaseOrderVendorQuote getAwardedVendorQuote() {
        for (PurchaseOrderVendorQuote vendorQuote : purchaseOrderVendorQuotes) {
            if (vendorQuote.getPurchaseOrderQuoteAwardDate() != null) {
                return vendorQuote;
            }
        }
        return null;
    }
    
    @Override
    public KualiDecimal getTotalDollarAmount() {
        //return total without inactive and with below the line
        return getTotalDollarAmount(false, true);
    }
    
    //TODO: look into merging this with the super method with excludedTypes.  Can probably find a way to \ 
    //abstract out the active flag which would be the only real difference
    public KualiDecimal getTotalDollarAmount(boolean includeInactive, boolean includeBelowTheLine) {
        KualiDecimal total = new KualiDecimal(BigDecimal.ZERO);
        for (PurchaseOrderItem item : (List<PurchaseOrderItem>)getItems()) {
            ItemType it = item.getItemType();
            if((includeBelowTheLine || it.isItemTypeAboveTheLineIndicator()) &&
               (includeInactive || item.isItemActiveIndicator())) {
                KualiDecimal extendedPrice = item.getExtendedPrice();
                KualiDecimal itemTotal = (extendedPrice != null) ? extendedPrice : KualiDecimal.ZERO;
                total = total.add(itemTotal);
            }
        }
        return total;
    }
    
    public boolean getContainsUnpaidPaymentRequestsOrCreditMemos() {
        if (getRelatedPaymentRequestViews() != null) {
            for (PaymentRequestView element : getRelatedPaymentRequestViews()) {
                // If the PREQ is neither cancelled nor voided, check whether the PREQ has been paid.
                // If it has not been paid, then this method will return true.
                if (!element.getStatusCode().equals(PurapConstants.PaymentRequestStatuses.CANCELLED_IN_PROCESS) && !element.getStatusCode().equals(PurapConstants.PaymentRequestStatuses.CANCELLED_POST_APPROVE)) {
                    if (element.getPaymentPaidDate() == null) {
                        return true;
                    }
                }
            }// endfor
        }
        if (getRelatedCreditMemoViews() != null) {
            for (CreditMemoView element : getRelatedCreditMemoViews()) {
                // If the CM is cancelled, check whether the CM has been paid.
                // If it has not been paid, then this method will return true.
                if (!element.getCreditMemoStatusCode().equals(PurapConstants.CreditMemoStatuses.CANCELLED)) {
                    if (element.getCreditMemoPaidTimestamp() == null) {
                        return true;
                    }
                }
            }// endfor
        }
        return false;
    }
    
    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocumentBase#getSourceAccountingLineClass()
     */
//    @Override
//    public Class getSourceAccountingLineClass() {
//        return PurchaseOrderAccount.class;
//    }

    /**
     * USED FOR ROUTING ONLY
     * @deprecated
     */
    public String getContractManagerName() {
        return "";
}
    /**
     * USED FOR ROUTING ONLY
     * @deprecated
     */
    public void setContractManagerName(String contractManagerName) {
    }

    /**
     * USED FOR ROUTING ONLY
     * @deprecated
     */
    public String getStatusDescription() {
        return "";
    }

    /**
     * USED FOR ROUTING ONLY
     * @deprecated
     */
    public void setStatusDescription(String statusDescription) {
    }

}
