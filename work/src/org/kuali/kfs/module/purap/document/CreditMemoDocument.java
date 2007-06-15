/*
 * Copyright 2007 The Kuali Foundation.
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

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.core.bo.Note;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapConstants.CreditMemoTypes;
import org.kuali.module.purap.bo.CreditMemoItem;
import org.kuali.module.purap.bo.CreditMemoStatusHistory;
import org.kuali.module.vendor.bo.VendorDetail;
import org.kuali.module.vendor.util.VendorUtils;






/**
 * Credit Memo Document
 */
public class CreditMemoDocument extends AccountsPayableDocumentBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CreditMemoDocument.class);

    private Integer paymentRequestIdentifier;
    private String creditMemoNumber;
    private Date creditMemoDate;
    private KualiDecimal creditMemoAmount;
    private Timestamp creditMemoPaidTimestamp;
    private String itemMiscellaneousCreditDescription;
    private String purchaseOrderNotes;
    private Date purchaseOrderEndDate;
    
    private String creditMemoType; /* not persisted */
    
    private PurchaseOrderDocument purchaseOrder;
    private PaymentRequestDocument paymentRequest;

    /**
	 * Default constructor.
	 */
	public CreditMemoDocument() {
        super();
    }
    
    /**
     * @see org.kuali.core.bo.PersistableBusinessObjectBase#isBoNotesSupport()
     */
    @Override
    public boolean isBoNotesSupport() {
        return true;
    }

    public void refreshAllReferences() {
        super.refreshAllReferences();
        this.refreshReferenceObject("paymentRequest");
    }
    
    /**
     * Perform logic needed to initiate CM Document
     */
    public void initiateDocument() {
        LOG.debug("initiateDocument() started");
        this.setStatusCode( PurapConstants.CreditMemoStatuses.INITIATE );
  
        //TODO: Change this one:
        this.setAccountsPayableProcessorIdentifier("TBD");
        UniversalUser currentUser = (UniversalUser)GlobalVariables.getUserSession().getUniversalUser();
        this.setAccountsPayableProcessorIdentifier(currentUser.getPersonUniversalIdentifier());
        // paymentRequest.setProcessedCampusCode(u.getCampusCd());
        //paymentRequest.setAccountsPayableProcessorId(u.getId());
        //this.setStatusCode( PurapConstants.PaymentRequestStatuses.IN_PROCESS )
       // this.setInitialized(true);
   //     this.refreshAllReferences();
        
        
    }
    
    /**
     * Perform logic needed to initiate CM Document
     */
   /*
    public void CreateDocument() {
        

        //for now just populate with essentials
        cm.setNumber(creditMemoNumber);
        cm.setDate(new Timestamp(creditMemoDate.getTime()));
        cm.setAmount(creditMemoAmount);
        cm.setCreditMemoStatus((CreditMemoStatus) referenceService.getCode("CreditMemoStatus", EpicConstants.CM_STAT_IN_PROCESS));
        cm.setCreateDate(new Timestamp((new Date()).getTime()));
        cm.setAccountsPayableProcessorUser(u);
        cm.setProcessCampusCode(u.getCampusCd());

        
        //create a new doc header and set it in the credit memo
        String docName = applicationSettingService.getString("EDEN_DOCUMENT_CREDIT");
        String orgDocNumber = "";
        if (cm.getPaymentRequest() != null) {
          orgDocNumber = cm.getPaymentRequest().getDocumentHeader().getOrganizationDocumentNumber();
        } else if (cm.getPurchaseOrder() != null) {
          orgDocNumber = cm.getPurchaseOrder().getDocumentHeader().getOrganizationDocumentNumber();
        }
        DocumentHeader dh = routingService.createDocumentHeader(docName, orgDocNumber, cm.getTotalCredit(), u);
        cm.setDocumentHeader(dh);
        
        // IF po create line items for all the items that have invoiced quantities
        if (po != null) {
          if ( preq != null ) {
            // Get the lines from the preq
            for (Iterator iter = invoicedPoItems.iterator(); iter.hasNext();) {
              Integer nbr = (Integer)iter.next();
              PaymentRequestItem preqItemToUse = preq.getItem(nbr.intValue());
              if (preqItemToUse != null) {
                // Only get items that are 'ITEM' type items and that are invoiced on PREQ
                if ( (EpicConstants.ITEM_TYPE_ITEM_CODE.equals(preqItemToUse.getItemType().getCode())) &&
                     ( (preqItemToUse.getInvoiceQuantity() != null) || 
                       (zero.compareTo(preqItemToUse.getExtendedPrice()) != 0) ) ) {
                  cm.getItems().add(new CreditMemoItem(cm, preqItemToUse));
                }
              }
            }
          } else {
            // Get the lines from the po
            for (Iterator iter = invoicedPoItems.iterator(); iter.hasNext();) {
              Integer nbr = (Integer)iter.next();

              cm.getItems().add(new CreditMemoItem(cm, po.getItem(nbr.intValue())));
            }
          }
        }

        // Always create line items for Restocking Fee and Misc (add these after 'ITEM' type)
        cm.getItems().add(newEmptyLineItem(cm, EpicConstants.ITEM_TYPE_RESTCK_FEE_CODE, new Integer(904)));
        cm.getItems().add(newEmptyLineItem(cm, EpicConstants.ITEM_TYPE_MISC_CRDT_CODE, new Integer(905)));

        // distribute accounts to non-item type items
        // we distribute based on the source document items and amounts
        List displayItems = cm.getSourceDocumentItemsDisplayItems();
        List displayAccounts = cm.getDisplayAccountsFromDisplayItems(displayItems);
        BigDecimal sourceDocTotalAmount = zero;
        for (Iterator displayItemIter = displayItems.iterator(); displayItemIter.hasNext();) {
          DisplayItem displayItem = (DisplayItem) displayItemIter.next();
          sourceDocTotalAmount = sourceDocTotalAmount.add(displayItem.getAmount());
        }
        
        cm.distributeAccountsToNonItems(displayAccounts,sourceDocTotalAmount);
        SERVICELOG.debug("create() ended");
        return cm;
      }
        
      
        
    }
*/
    public void populateCreditMemoVendorFileds(String vendorNumber){
    //VendorDetail vd = SpringServiceLocator.getVendorService().getVendorDetail(VendorUtils.getVendorHeaderId(this.getVendorNumber()), VendorUtils.getVendorDetailId(this.getVendorNumber()));
        VendorDetail vd = SpringServiceLocator.getVendorService().getVendorDetail(VendorUtils.getVendorHeaderId(vendorNumber), VendorUtils.getVendorDetailId(vendorNumber));
    if (ObjectUtils.isNotNull(vd)) {
        this.setVendorDetail(vd);
        this.setVendorHeaderGeneratedIdentifier(vd.getVendorHeaderGeneratedIdentifier());
        this.setVendorDetailAssignedIdentifier(vd.getVendorDetailAssignedIdentifier());
       // cmDocument.setVendorAddressGeneratedIdentifier(VendorUtils.getVendorHeaderId(cmDocument.getVendorNumber()));
       // cmDocument.setVendorDetailAssignedIdentifier(VendorUtils.getVendorDetailId(cmDocument.getVendorNumber()));
        this.setVendorCustomerNumber(vd.getVendorNumber());
        this.setVendorHeaderGeneratedIdentifier(vd.getVendorHeaderGeneratedIdentifier());
        this.setVendorDetailAssignedIdentifier(vd.getVendorDetailAssignedIdentifier());
        this.setVendorName(vd.getVendorName());
        this.setVendorLine1Address(vd.getDefaultAddressLine1());
        this.setVendorLine2Address(vd.getDefaultAddressLine1());
        this.setVendorCityName(vd.getDefaultAddressCity());
        this.setVendorStateCode(vd.getDefaultAddressStateCode());
        this.setVendorPostalCode(vd.getDefaultAddressPostalCode());
        this.setVendorCountryCode(vd.getDefaultAddressCountryCode());
    }

    }
/**
 * Perform logic needed to initiate PREQ Document
 */
public void clearInitFields() {
    LOG.debug("clearDocument() started");
    // Clearing document overview fields
    this.getDocumentHeader().setFinancialDocumentDescription(null);
    this.getDocumentHeader().setExplanation(null);
    this.getDocumentHeader().setFinancialDocumentTotalAmount(null);
    this.getDocumentHeader().setOrganizationDocumentNumber(null);

    // Clearing document Init fields
    this.setPurchaseOrderIdentifier(null);
    this.setCreditMemoNumber(null);
    this.setCreditMemoDate(null);
    this.setCreditMemoAmount(null);
    this.setVendorNumber(null);
    this.setPaymentRequestIdentifier(null);
}
    /**
     * @see org.kuali.core.document.DocumentBase#handleRouteStatusChange()
     */
    @Override
    public void handleRouteStatusChange() {
        LOG.debug("handleRouteStatusChange() started");
        super.handleRouteStatusChange();

    }

    @Override
    public void handleRouteLevelChange() {
        LOG.debug("handleRouteLevelChange() started");
        super.handleRouteLevelChange();
    }
    
    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocument#addToStatusHistories(java.lang.String, java.lang.String)
     */
    public void addToStatusHistories( String oldStatus, String newStatus, Note statusHistoryNote ) {
        CreditMemoStatusHistory cmsh = new CreditMemoStatusHistory( oldStatus, newStatus );
        this.addStatusHistoryNote( cmsh, statusHistoryNote );
        this.getStatusHistories().add( cmsh );
    }

    /**
     * 
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocumentBase#getItemClass()
     */
    @Override
    public Class getItemClass() {
        return CreditMemoItem.class;
    }
    
    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocumentBase#getSourceAccountingLineClass()
     */
//    @Override
//    public Class getSourceAccountingLineClass() {
//        return CreditMemoAccount.class;
//    }


    /**
     * Gets the paymentRequestIdentifier attribute.
     * 
     * @return Returns the paymentRequestIdentifier
     * 
     */
    public Integer getPaymentRequestIdentifier() { 
        return paymentRequestIdentifier;
    }

    /**
     * Sets the paymentRequestIdentifier attribute.
     * 
     * @param paymentRequestIdentifier The paymentRequestIdentifier to set.
     * 
     */
    public void setPaymentRequestIdentifier(Integer paymentRequestIdentifier) {
        this.paymentRequestIdentifier = paymentRequestIdentifier;
    }


    /**
     * Gets the creditMemoNumber attribute.
     * 
     * @return Returns the creditMemoNumber
     * 
     */
    public String getCreditMemoNumber() { 
        return creditMemoNumber;
    }

    /**
     * Sets the creditMemoNumber attribute.
     * 
     * @param creditMemoNumber The creditMemoNumber to set.
     * 
     */
    public void setCreditMemoNumber(String creditMemoNumber) {
        this.creditMemoNumber = creditMemoNumber;
    }


    /**
     * Gets the creditMemoDate attribute.
     * 
     * @return Returns the creditMemoDate
     * 
     */
    public Date getCreditMemoDate() { 
        return creditMemoDate;
    }

    /**
     * Sets the creditMemoDate attribute.
     * 
     * @param creditMemoDate The creditMemoDate to set.
     * 
     */
    public void setCreditMemoDate(Date creditMemoDate) {
        this.creditMemoDate = creditMemoDate;
    }

    /**
     * Gets the creditMemoAmount attribute.
     * 
     * @return Returns the creditMemoAmount
     * 
     */
    public KualiDecimal getCreditMemoAmount() { 
        return creditMemoAmount;
    }

    /**
     * Sets the creditMemoAmount attribute.
     * 
     * @param creditMemoAmount The creditMemoAmount to set.
     * 
     */
    public void setCreditMemoAmount(KualiDecimal creditMemoAmount) {
        this.creditMemoAmount = creditMemoAmount;
    }

    /**
     * Gets the itemMiscellaneousCreditDescription attribute.
     * 
     * @return Returns the itemMiscellaneousCreditDescription
     * 
     */
    public String getItemMiscellaneousCreditDescription() { 
        return itemMiscellaneousCreditDescription;
    }

    /**
     * Sets the itemMiscellaneousCreditDescription attribute.
     * 
     * @param itemMiscellaneousCreditDescription The itemMiscellaneousCreditDescription to set.
     * 
     */
    public void setItemMiscellaneousCreditDescription(String itemMiscellaneousCreditDescription) {
        this.itemMiscellaneousCreditDescription = itemMiscellaneousCreditDescription;
    }


    /**
     * Gets the creditMemoPaidTimestamp attribute. 
     * @return Returns the creditMemoPaidTimestamp.
     */
    public Timestamp getCreditMemoPaidTimestamp() {
        return creditMemoPaidTimestamp;
    }

    /**
     * Sets the creditMemoPaidTimestamp attribute value.
     * @param creditMemoPaidTimestamp The creditMemoPaidTimestamp to set.
     */
    public void setCreditMemoPaidTimestamp(Timestamp creditMemoPaidTimestamp) {
        this.creditMemoPaidTimestamp = creditMemoPaidTimestamp;
    }


    /**
     * Gets the paymentRequest attribute.
     * 
     * @return Returns the paymentRequest
     * 
     */
    public PaymentRequestDocument getPaymentRequest() { 
        return paymentRequest;
    }
    
    /**
     * Gets the purchaseOrder attribute. 
     * @return Returns the purchaseOrder.
     */
    public PurchaseOrderDocument getPurchaseOrder() {
        return purchaseOrder;
    }

    /**
     * Sets the purchaseOrder attribute value.
     * @param purchaseOrder The purchaseOrder to set.
     */
    public void setPurchaseOrder(PurchaseOrderDocument purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }
    
    /**
     * Gets the purchaseOrderNotes attribute. 
     * @return Returns the purchaseOrderNotes.
     */
    public String getPurchaseOrderNotes() {
        ArrayList poNotes = SpringServiceLocator.getNoteService().getByRemoteObjectId((this.getPurchaseOrderIdentifier()).toString());

        if (poNotes.size() > 0) {
            return "Yes";
        }
        return "No";
    }

    /**
     * Sets the purchaseOrderNotes attribute value.
     * 
     * @param purchaseOrderNotes The purchaseOrderNotes to set.
     */
    public void setPurchaseOrderNotes(String purchaseOrderNotes) {
        this.purchaseOrderNotes = purchaseOrderNotes;
    }

    /**
     * Gets the purchaseOrderEndDate attribute. 
     * @return Returns the purchaseOrderEndDate.
     */
    public Date getPurchaseOrderEndDate() {
        return purchaseOrderEndDate;
    }

    /**
     * Sets the purchaseOrderEndDate attribute value.
     * @param purchaseOrderEndDate The purchaseOrderEndDate to set.
     */
    public void setPurchaseOrderEndDate(Date purchaseOrderEndDate) {
        this.purchaseOrderEndDate = purchaseOrderEndDate;
    }

    /**
     * This returns the type of the Credit Memo that was selected on the
     * init screen.  It is based on them entering the Vendor, PO or PREQ #.
     * 
     * @return Vendor, PO or PREQ
     */
    public String getCreditMemoType() {
        if ( this.getPaymentRequestIdentifier() != null ) {
          return CreditMemoTypes.TYPE_PREQ;
        } else if ( this.getPurchaseOrderIdentifier() != null ) {
          return CreditMemoTypes.TYPE_PO;
        } else {
          return CreditMemoTypes.TYPE_VENDOR;
        }
      }

    /**
     * Sets the creditMemoType attribute value.
     * @param creditMemoType The creditMemoType to set.
     */
    public void setCreditMemoType(String creditMemoType) {
        this.creditMemoType = creditMemoType;
    }

    /**
     * Sets the paymentRequest attribute value.
     * @param paymentRequest The paymentRequest to set.
     */
    public void setPaymentRequest(PaymentRequestDocument paymentRequest) {
        this.paymentRequest = paymentRequest;
    }
 }
