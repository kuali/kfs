package org.kuali.kfs.module.purap.document;

import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapWorkflowConstants;
import org.kuali.kfs.module.purap.businessobject.LineItemReceivingItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.document.service.ReceivingService;
import org.kuali.kfs.module.purap.document.validation.event.AttributedContinuePurapEvent;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.dto.DocumentRouteLevelChangeDTO;
import org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO;
import org.kuali.rice.kew.exception.WorkflowException;
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

    /**
     * Default constructor.
     */
    public LineItemReceivingDocument() {
        super();
        items = new TypedArrayList(getItemClass());
    }

    @Override
    public void initiateDocument(){
        super.initiateDocument();
        this.setLineItemReceivingStatusCode(PurapConstants.LineItemReceivingStatuses.IN_PROCESS);
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
        if (event instanceof AttributedContinuePurapEvent) {
            SpringContext.getBean(ReceivingService.class).populateReceivingLineFromPurchaseOrder(this);
        }
        
        super.prepareForSave(event);
    }

    @Override
    public void doRouteStatusChange(DocumentRouteStatusChangeDTO statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);
        // DOCUMENT CANCELED
        // If the document is canceled then set the line item receiving 
        // status code to CANC.
        if (this.getDocumentHeader().getWorkflowDocument().stateIsCanceled()) {
            setLineItemReceivingStatusCode(PurapConstants.LineItemReceivingStatuses.CANCELLED);
        }
    }
    
    @Override
    public void doRouteLevelChange(DocumentRouteLevelChangeDTO change) {
        //If the new node is Outstanding Transactions then we want to set the line item
        //receiving status code to APOO.
        if (StringUtils.equals(PurapConstants.LineItemReceivingDocumentStrings.AWAITING_PO_OPEN_STATUS, change.getNewNodeName())){
            setLineItemReceivingStatusCode(PurapConstants.LineItemReceivingStatuses.AWAITING_PO_OPEN_STATUS);
        } 
        //If the new node is Join, this means we're done with the routing, so we'll set
        //the line item receiving status code to CMPT.
        else if (StringUtils.equals(PurapConstants.LineItemReceivingDocumentStrings.JOIN_NODE, change.getNewNodeName())) {
            setLineItemReceivingStatusCode(PurapConstants.LineItemReceivingStatuses.COMPLETE);
        }
        SpringContext.getBean(PurapService.class).saveDocumentNoValidation(this);
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


    private boolean isAwaitingPurchaseOrderOpen() {
        return SpringContext.getBean(PurchaseOrderService.class).isPurchaseOrderOpenForProcessing(getPurchaseOrderDocument());
    }

    /**
     * Provides answers to the following splits:
     * RelatesToOutstandingTransactions
     * 
     * @see org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase#answerSplitNodeQuestion(java.lang.String)
     */
    public boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        if (nodeName.equals(PurapWorkflowConstants.RELATES_TO_OUTSTANDING_TRANSACTIONS)) return !isAwaitingPurchaseOrderOpen();
        throw new UnsupportedOperationException("Cannot answer split question for this node you call \""+nodeName+"\"");
    }
    
    public List buildListOfDeletionAwareLists() {
        List managedLists = super.buildListOfDeletionAwareLists();
        managedLists.add(this.getItems());
        return managedLists;
    }
}
