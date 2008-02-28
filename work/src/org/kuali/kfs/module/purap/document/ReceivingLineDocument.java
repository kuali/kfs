package org.kuali.module.purap.document;

import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.util.TypedArrayList;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.bo.ReceivingLineItem;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class ReceivingLineDocument extends ReceivingDocumentBase {

    private Integer purchaseOrderIdentifier;
        
    //Collections
    private List<ReceivingLineItem> items;
     
    
    /**
     * Default constructor.
     */
    public ReceivingLineDocument() {
        super();
        items = new TypedArrayList(getItemClass());       
    }

    public void populateReceivingLineFromPurchaseOrder(PurchaseOrderDocument po){
        
        //populate receiving line document from purchase order
        this.setPurchaseOrderIdentifier( po.getPurapDocumentIdentifier() );
        this.getDocumentHeader().setOrganizationDocumentNumber( po.getDocumentHeader().getOrganizationDocumentNumber() );
        
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
            if(poi.isItemActiveIndicator() && poi.getItemType().isQuantityBasedGeneralLedgerIndicator() && poi.getItemType().isItemTypeAboveTheLineIndicator() ){
                this.getItems().add(new ReceivingLineItem(poi, this));
            }
        }
        
    }
    
    /**
     * Gets the purchaseOrderIdentifier attribute.
     * 
     * @return Returns the purchaseOrderIdentifier
     * 
     */
    public Integer getPurchaseOrderIdentifier() { 
        return purchaseOrderIdentifier;
    }

    /**
     * Sets the purchaseOrderIdentifier attribute.
     * 
     * @param purchaseOrderIdentifier The purchaseOrderIdentifier to set.
     * 
     */
    public void setPurchaseOrderIdentifier(Integer purchaseOrderIdentifier) {
        this.purchaseOrderIdentifier = purchaseOrderIdentifier;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put("documentNumber", this.documentNumber);
        return m;
    }

    public Class getItemClass() {
        return ReceivingLineItem.class;
    }

    public List getItems() {
        return items;
    }

    public void setItems(List items) {
        this.items = items;
    }

    /*public void addItem(ReceivingLineItem item) {
        int itemLinePosition = getItemLinePosition();
        if (ObjectUtils.isNotNull(item.getItemLineNumber()) && (item.getItemLineNumber() > 0) && (item.getItemLineNumber() <= itemLinePosition)) {
            itemLinePosition = item.getItemLineNumber().intValue() - 1;
        }
        items.add(itemLinePosition, item);
        renumberItems(itemLinePosition);
    }

    public void deleteItem(int lineNum) {
        if (items.remove(lineNum) == null) {
            // throw error here
        }
        renumberItems(lineNum);
    }

    public void renumberItems(int start) {
        for (int i = start; i < items.size(); i++) {
            ReceivingLineItem item = (ReceivingLineItem) items.get(i);
            item.setItemLineNumber(new Integer(i + 1));
        }
    }

    public void itemSwap(int positionFrom, int positionTo) {
        // if out of range do nothing
        if ((positionTo < 0) || (positionTo >= getItemLinePosition())) {
            return;
        }
        ReceivingLineItem item1 = this.getItem(positionFrom);
        ReceivingLineItem item2 = this.getItem(positionTo);
        Integer oldFirstPos = item1.getItemLineNumber();
        // swap line numbers
        item1.setItemLineNumber(item2.getItemLineNumber());
        item2.setItemLineNumber(oldFirstPos);
        // fix ordering in list
        items.remove(positionFrom);
        items.add(positionTo, item1);
    }

    public int getItemLinePosition() {
        int belowTheLineCount = 0;
        for (ReceivingLineItem item : items) {
            if (!item.getItemType().isItemTypeAboveTheLineIndicator()) {
                belowTheLineCount++;
            }
        }
        return items.size() - belowTheLineCount;
    }*/

    public ReceivingLineItem getItem(int pos) {
        return (ReceivingLineItem) items.get(pos);
    }

    /*public ReceivingLineItem getItemByLineNumber(int lineNumber) {
        for (Iterator iter = items.iterator(); iter.hasNext();) {
            ReceivingLineItem item = (ReceivingLineItem) iter.next();
            if (item.getItemLineNumber().intValue() == lineNumber) {
                return item;
            }
        }
        return null;
    }*/

}
