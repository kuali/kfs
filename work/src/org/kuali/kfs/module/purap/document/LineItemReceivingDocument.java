package org.kuali.kfs.module.purap.document;

import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapWorkflowConstants;
import org.kuali.kfs.module.purap.businessobject.LineItemReceivingItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.service.AccountsPayableDocumentSpecificService;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.ReceivingService;
import org.kuali.kfs.module.purap.document.validation.event.ContinuePurapEvent;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.dto.DocumentRouteLevelChangeDTO;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.rule.event.KualiDocumentEvent;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.KNSPropertyConstants;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class LineItemReceivingDocument extends ReceivingDocumentBase {

    //Collections
    private List<LineItemReceivingItem> items;

    //Used by Routing
    boolean awaitingPurchaseOrderOpen;
    /**
     * Default constructor.
     */
    public LineItemReceivingDocument() {
        super();
        items = new TypedArrayList(getItemClass());
    }

    public void populateReceivingLineFromPurchaseOrder(PurchaseOrderDocument po){
        
        //populate receiving line document from purchase order
        this.setPurchaseOrderIdentifier( po.getPurapDocumentIdentifier() );
        this.getDocumentHeader().setOrganizationDocumentNumber( po.getDocumentHeader().getOrganizationDocumentNumber() );
        this.setAccountsPayablePurchasingDocumentLinkIdentifier( po.getAccountsPayablePurchasingDocumentLinkIdentifier() );
        
        //copy vendor
        this.setVendorHeaderGeneratedIdentifier( po.getVendorHeaderGeneratedIdentifier() );
        this.setVendorDetailAssignedIdentifier( po.getVendorDetailAssignedIdentifier() );        
        this.setVendorName( po.getVendorName() );
        this.setVendorNumber( po.getVendorNumber() );
        this.setVendorAddressGeneratedIdentifier( po.getVendorAddressGeneratedIdentifier() );
        this.setVendorLine1Address( po.getVendorLine1Address() );
        this.setVendorLine2Address( po.getVendorLine2Address() );
        this.setVendorCityName( po.getVendorCityName() );
        this.setVendorStateCode( po.getVendorStateCode() );
        this.setVendorPostalCode( po.getVendorPostalCode() );
        this.setVendorCountryCode( po.getVendorCountryCode() );
        
        //copy alternate vendor
        this.setAlternateVendorName( po.getAlternateVendorName() );
        this.setAlternateVendorNumber( po.getAlternateVendorNumber() );
        this.setAlternateVendorDetailAssignedIdentifier( po.getAlternateVendorDetailAssignedIdentifier() );
        this.setAlternateVendorHeaderGeneratedIdentifier( po.getAlternateVendorHeaderGeneratedIdentifier() );
        
        //copy delivery
        this.setDeliveryBuildingCode( po.getDeliveryBuildingCode() );
        this.setDeliveryBuildingLine1Address( po.getDeliveryBuildingLine1Address() );
        this.setDeliveryBuildingLine2Address( po.getDeliveryBuildingLine2Address() );
        this.setDeliveryBuildingName( po.getDeliveryBuildingName() );        
        this.setDeliveryBuildingRoomNumber( po.getDeliveryBuildingRoomNumber() );
        this.setDeliveryCampusCode( po.getDeliveryCampusCode() );
        this.setDeliveryCityName( po.getDeliveryCityName() );
        this.setDeliveryCountryCode( po.getDeliveryCountryCode() );
        this.setDeliveryInstructionText( po.getDeliveryInstructionText() );
        this.setDeliveryPostalCode( po.getDeliveryPostalCode() );
        this.setDeliveryRequiredDate( po.getDeliveryRequiredDate() );
        this.setDeliveryRequiredDateReasonCode( po.getDeliveryRequiredDateReasonCode() );
        this.setDeliveryStateCode( po.getDeliveryStateCode() );
        this.setDeliveryToEmailAddress( po.getDeliveryToEmailAddress() );
        this.setDeliveryToName( po.getDeliveryToName() );
        this.setDeliveryToPhoneNumber( po.getDeliveryToPhoneNumber() );
                
        //copy purchase order items
        for (PurchaseOrderItem poi : (List<PurchaseOrderItem>) po.getItems()) {
            //TODO: Refactor this check into a service call. route FYI during submit
            if(poi.isItemActiveIndicator() && 
               poi.getItemType().isQuantityBasedGeneralLedgerIndicator() && 
               poi.getItemType().isLineItemIndicator() ){
                this.getItems().add(new LineItemReceivingItem(poi, this));
            }
        }
        
        populateDocumentDescription(po);
    }
        
    /**
     * Perform logic needed to clear the initial fields on a Receiving Line Document
     */
    public void clearInitFields(boolean fromPurchaseOrder) {
        // Clearing document overview fields
        this.getDocumentHeader().setDocumentDescription(null);
        this.getDocumentHeader().setExplanation(null);
        this.getDocumentHeader().setFinancialDocumentTotalAmount(null);
        this.getDocumentHeader().setOrganizationDocumentNumber(null);

        // Clearing document Init fields
        if(fromPurchaseOrder == false){
            this.setPurchaseOrderIdentifier(null);
        }        
        this.setShipmentReceivedDate(null);
        this.setShipmentPackingSlipNumber(null);
        this.setShipmentBillOfLadingNumber(null);
        this.setCarrierCode(null);        
    }

    
    @Override
    public void prepareForSave(KualiDocumentEvent event) {

        // first populate, then call super
        if (event instanceof ContinuePurapEvent) {
            SpringContext.getBean(ReceivingService.class).populateReceivingLineFromPurchaseOrder(this);
        }
        
        super.prepareForSave(event);
    }
    
    @Override
    public void handleRouteLevelChange(DocumentRouteLevelChangeDTO change) {
        /**
         * FIXME: For KULPURAP-3218, This method has been added to update the doc status sothat 
         * the ApproveLineItemReceivingStep job can pick the docs in this status (ReceivingDaoOjb.getReceivingDocumentsForPOAmendment()) and check 
         * for PO amendment. When testing, i got some exception (not sure the exact exception). Since I'm in a hurry packing up for my India trip, 
         * I'm leaving this code commented without fixing the root cause :) If the PurapConstants for this looks wired, can rename it to some
         * good one. And, needs to check the LineItemReceivingDocumentStrings/LineItemReceivingStatus class in PurapConstants to match up with
         * the other documents constants
         */
//        if (StringUtils.equals(PurapConstants.LineItemReceivingDocumentStrings.AWAITING_PO_OPEN_STATUS, change.getNewNodeName())){
//            setLineItemReceivingStatusCode(PurapConstants.LineItemReceivingStatus.AWAITING_PO_OPEN_STATUS);
//        }else{
//            setLineItemReceivingStatusCode(StringUtils.EMPTY);
//        }
//        SpringContext.getBean(PurapService.class).saveDocumentNoValidation(this);
    }
    
    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put("documentNumber", this.documentNumber);
        return m;
    }

    public Class getItemClass() {
        return LineItemReceivingItem.class;
    }

    public List getItems() {
        return items;
    }

    public void setItems(List items) {
        this.items = items;
    }

    public LineItemReceivingItem getItem(int pos) {
        return (LineItemReceivingItem) items.get(pos);
    }

    public void addItem(LineItemReceivingItem item) {
        getItems().add(item);
    }

    public void deleteItem(int lineNum) {
        if (getItems().remove(lineNum) == null) {
            // throw error here
        }
    }

    @Override
    public AccountsPayableDocumentSpecificService getDocumentSpecificService() {
        // TODO Auto-generated method stub
        return null;
    }

    /*public LineItemReceivingItem getItemByLineNumber(int lineNumber) {
        for (Iterator iter = items.iterator(); iter.hasNext();) {
            LineItemReceivingItem item = (LineItemReceivingItem) iter.next();
            if (item.getItemLineNumber().intValue() == lineNumber) {
                return item;
            }
        }
        return null;
    }*/
    
    /**
     * FIXME: Have to move this method to the base class to make it available for the correction doc also - vpc
     */
    private void populateDocumentDescription(PurchaseOrderDocument poDocument) {
        String description = "PO: " + poDocument.getPurapDocumentIdentifier() + " Vendor: " + poDocument.getVendorName();
        int noteTextMaxLength = SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(DocumentHeader.class, KNSPropertyConstants.DOCUMENT_DESCRIPTION).intValue();
        if (noteTextMaxLength < description.length()) {
            description = description.substring(0, noteTextMaxLength);
        }
        getDocumentHeader().setDocumentDescription(description);
    }

    @Override
    public void populateDocumentForRouting() {
        this.setAwaitingPurchaseOrderOpen( SpringContext.getBean(ReceivingService.class).isAwaitingPurchaseOrderOpen(this.getDocumentNumber()) );
        super.populateDocumentForRouting();
    }

    public boolean isAwaitingPurchaseOrderOpen() {
        return awaitingPurchaseOrderOpen;
    }

    public void setAwaitingPurchaseOrderOpen(boolean awaitingPurchaseOrderOpen) {
        this.awaitingPurchaseOrderOpen = awaitingPurchaseOrderOpen;
    }

    /**
     * Provides answers to the following splits:
     * RelatesToOutstandingTransactions
     * 
     * @see org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase#answerSplitNodeQuestion(java.lang.String)
     */
    public boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        if (nodeName.equals(PurapWorkflowConstants.RELATES_TO_OUTSTANDING_TRANSACTIONS)) return isAwaitingPurchaseOrderOpen();
        throw new UnsupportedOperationException("Cannot answer split question for this node you call \""+nodeName+"\"");
    }
    
}
