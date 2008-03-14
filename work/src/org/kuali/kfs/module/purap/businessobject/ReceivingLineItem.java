package org.kuali.module.purap.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.workflow.KFSResourceLoader;
import org.kuali.kfs.bo.UnitOfMeasure;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.document.AccountsPayableDocumentBase;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.ReceivingLineDocument;
import org.kuali.module.purap.exceptions.PurError;
import org.kuali.module.purap.service.PurapService;
import org.kuali.module.purap.service.PurchaseOrderService;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class ReceivingLineItem extends PersistableBusinessObjectBase {

    private Integer receivingLineItemIdentifier;
    private String documentNumber;
    private Integer purchaseOrderIdentifier;
    private Integer itemLineNumber;
    private String itemTypeCode;
    private String itemUnitOfMeasureCode;
    private KualiDecimal itemOrderedQuantity;
    private String itemCatalogNumber;
    private String itemDescription;
    private KualiDecimal itemReceivedTotalQuantity;
    private KualiDecimal itemReturnedTotalQuantity;
    private KualiDecimal itemDamagedTotalQuantity;
    private String itemReasonAddedCode;

    //not stored in db
    private KualiDecimal itemReceivedPriorQuantity;
    private KualiDecimal itemReceivedToBeQuantity;
    
    private ReceivingLineDocument receivingLineDocument;
    private ItemReasonAdded itemReasonAdded;
    private ItemType itemType;
    private UnitOfMeasure itemUnitOfMeasure;   

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountsPayableDocumentBase.class);
    
    /**
     * Default constructor.
     */
    public ReceivingLineItem() {

    }
    
    public ReceivingLineItem(ReceivingLineDocument rld){
        this.setDocumentNumber( rld.getDocumentNumber() );
        this.setItemReceivedTotalQuantity( KualiDecimal.ZERO );
        this.setItemReturnedTotalQuantity( KualiDecimal.ZERO );
        this.setItemDamagedTotalQuantity( KualiDecimal.ZERO );
    }
    
    public ReceivingLineItem(PurchaseOrderItem poi, ReceivingLineDocument rld){
        
        this.setDocumentNumber( rld.getDocumentNumber() );        
        this.setItemTypeCode( poi.getItemTypeCode() );
        
        this.setItemLineNumber( poi.getItemLineNumber() );
        this.setItemCatalogNumber( poi.getItemCatalogNumber() );
        this.setItemDescription( poi.getItemDescription() );

        this.setItemOrderedQuantity( poi.getItemQuantity() );
        this.setItemUnitOfMeasureCode( poi.getItemUnitOfMeasureCode() );

        this.setItemReceivedPriorQuantity( poi.getItemReceivedTotalQuantity() );
        this.setItemReceivedToBeQuantity( this.getItemOrderedQuantity().subtract(this.getItemReceivedPriorQuantity()));        
        
        //should determine whether this is prefilled be based on the parameter that allows loading from po
        this.setItemReceivedTotalQuantity( KualiDecimal.ZERO );
        
        this.setItemReturnedTotalQuantity( KualiDecimal.ZERO );
        this.setItemDamagedTotalQuantity( KualiDecimal.ZERO );                                
        //not added
        this.setItemReasonAddedCode(null);
    }
    
    /**
     * Gets the receivingLineItemIdentifier attribute.
     * 
     * @return Returns the receivingLineItemIdentifier
     * 
     */
    public Integer getReceivingLineItemIdentifier() { 
        return receivingLineItemIdentifier;
    }

    /**
     * Sets the receivingLineItemIdentifier attribute.
     * 
     * @param receivingLineItemIdentifier The receivingLineItemIdentifier to set.
     * 
     */
    public void setReceivingLineItemIdentifier(Integer receivingLineItemIdentifier) {
        this.receivingLineItemIdentifier = receivingLineItemIdentifier;
    }
    
    /**
     * Retreives a purchase order item by inspecting the item type to see if its above the line or below the line and returns the
     * appropriate type.
     * 
     * @return - purchase order item
     */
    public PurchaseOrderItem getPurchaseOrderItem() {
        if (ObjectUtils.isNotNull(this.getReceivingLineDocument())) {
            if (ObjectUtils.isNull(this.getReceivingLineDocument())) {
                this.refreshReferenceObject("receivingLineDocument");
            }
        }
        // ideally we should do this a different way - maybe move it all into the service or save this info somehow (make sure and
        // update though)
        if (getReceivingLineDocument() != null) {
            PurchaseOrderDocument po = getReceivingLineDocument().getPurchaseOrderDocument();
            PurchaseOrderItem poi = null;
            if (this.getItemType().isItemTypeAboveTheLineIndicator()) {
                poi = (PurchaseOrderItem) po.getItem(this.getItemLineNumber().intValue() - 1);
                // throw error if line numbers don't match
            }
            if (poi != null) {
                return poi;
            }
            else {
                LOG.debug("getPurchaseOrderItem() Returning null because PurchaseOrderItem object for line number" + getItemLineNumber() + "or itemType " + getItemTypeCode() + " is null");
                return null;
            }
        }
        else {
            LOG.error("getPurchaseOrderItem() Returning null because paymentRequest object is null");
            throw new PurError("Receiving Line Object in Purchase Order item line number " + getItemLineNumber() + "or itemType " + getItemTypeCode() + " is null");
        }
    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     * 
     */
    public String getDocumentNumber() { 
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     * 
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
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
     * Gets the itemLineNumber attribute.
     * 
     * @return Returns the itemLineNumber
     * 
     */
    public Integer getItemLineNumber() { 
        return itemLineNumber;
    }

    /**
     * Sets the itemLineNumber attribute.
     * 
     * @param itemLineNumber The itemLineNumber to set.
     * 
     */
    public void setItemLineNumber(Integer itemLineNumber) {
        this.itemLineNumber = itemLineNumber;
    }


    /**
     * Gets the itemTypeCode attribute.
     * 
     * @return Returns the itemTypeCode
     * 
     */
    public String getItemTypeCode() { 
        return itemTypeCode;
    }

    /**
     * Sets the itemTypeCode attribute.
     * 
     * @param itemTypeCode The itemTypeCode to set.
     * 
     */
    public void setItemTypeCode(String itemTypeCode) {
        this.itemTypeCode = itemTypeCode;
    }


    /**
     * Gets the itemUnitOfMeasureCode attribute.
     * 
     * @return Returns the itemUnitOfMeasureCode
     * 
     */
    public String getItemUnitOfMeasureCode() { 
        return itemUnitOfMeasureCode;
    }

    /**
     * Sets the itemUnitOfMeasureCode attribute.
     * 
     * @param itemUnitOfMeasureCode The itemUnitOfMeasureCode to set.
     * 
     */
    public void setItemUnitOfMeasureCode(String itemUnitOfMeasureCode) {
        this.itemUnitOfMeasureCode = itemUnitOfMeasureCode;
    }


    /**
     * Gets the itemOrderedQuantity attribute.
     * 
     * @return Returns the itemOrderedQuantity
     * 
     */
    public KualiDecimal getItemOrderedQuantity() { 
        return itemOrderedQuantity;
    }

    /**
     * Sets the itemOrderedQuantity attribute.
     * 
     * @param itemOrderedQuantity The itemOrderedQuantity to set.
     * 
     */
    public void setItemOrderedQuantity(KualiDecimal itemOrderedQuantity) {
        this.itemOrderedQuantity = itemOrderedQuantity;
    }


    /**
     * Gets the itemCatalogNumber attribute.
     * 
     * @return Returns the itemCatalogNumber
     * 
     */
    public String getItemCatalogNumber() { 
        return itemCatalogNumber;
    }

    /**
     * Sets the itemCatalogNumber attribute.
     * 
     * @param itemCatalogNumber The itemCatalogNumber to set.
     * 
     */
    public void setItemCatalogNumber(String itemCatalogNumber) {
        this.itemCatalogNumber = itemCatalogNumber;
    }


    /**
     * Gets the itemDescription attribute.
     * 
     * @return Returns the itemDescription
     * 
     */
    public String getItemDescription() { 
        return itemDescription;
    }

    /**
     * Sets the itemDescription attribute.
     * 
     * @param itemDescription The itemDescription to set.
     * 
     */
    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }


    /**
     * Gets the itemReceivedTotalQuantity attribute.
     * 
     * @return Returns the itemReceivedTotalQuantity
     * 
     */
    public KualiDecimal getItemReceivedTotalQuantity() { 
        return itemReceivedTotalQuantity;
    }

    /**
     * Sets the itemReceivedTotalQuantity attribute.
     * 
     * @param itemReceivedTotalQuantity The itemReceivedTotalQuantity to set.
     * 
     */
    public void setItemReceivedTotalQuantity(KualiDecimal itemReceivedTotalQuantity) {
        this.itemReceivedTotalQuantity = itemReceivedTotalQuantity;
    }


    /**
     * Gets the itemReturnedTotalQuantity attribute.
     * 
     * @return Returns the itemReturnedTotalQuantity
     * 
     */
    public KualiDecimal getItemReturnedTotalQuantity() { 
        return itemReturnedTotalQuantity;
    }

    /**
     * Sets the itemReturnedTotalQuantity attribute.
     * 
     * @param itemReturnedTotalQuantity The itemReturnedTotalQuantity to set.
     * 
     */
    public void setItemReturnedTotalQuantity(KualiDecimal itemReturnedTotalQuantity) {
        this.itemReturnedTotalQuantity = itemReturnedTotalQuantity;
    }


    /**
     * Gets the itemDamagedTotalQuantity attribute.
     * 
     * @return Returns the itemDamagedTotalQuantity
     * 
     */
    public KualiDecimal getItemDamagedTotalQuantity() { 
        return itemDamagedTotalQuantity;
    }

    /**
     * Sets the itemDamagedTotalQuantity attribute.
     * 
     * @param itemDamagedTotalQuantity The itemDamagedTotalQuantity to set.
     * 
     */
    public void setItemDamagedTotalQuantity(KualiDecimal itemDamagedTotalQuantity) {
        this.itemDamagedTotalQuantity = itemDamagedTotalQuantity;
    }

    /**
     * Gets the itemReasonAddedCode attribute.
     * 
     * @return Returns the itemReasonAddedCode
     * 
     */
    public String getItemReasonAddedCode() { 
        return itemReasonAddedCode;
    }

    /**
     * Sets the itemReasonAddedCode attribute.
     * 
     * @param itemReasonAddedCode The itemReasonAddedCode to set.
     * 
     */
    public void setItemReasonAddedCode(String itemReasonAddedCode) {
        this.itemReasonAddedCode = itemReasonAddedCode;
    }

    /**
     * Gets the receivingLineDocument attribute. 
     * @return Returns the receivingLineDocument.
     */
    public ReceivingLineDocument getReceivingLineDocument() {
        return receivingLineDocument;
    }

    /**
     * Sets the receivingLineDocument attribute value.
     * @param receivingLineDocument The receivingLineDocument to set.
     * @deprecated
     */
    public void setReceivingLineDocument(ReceivingLineDocument receivingLineDocument) {
        this.receivingLineDocument = receivingLineDocument;
    }

    /**
     * Gets the itemReasonAdded attribute. 
     * @return Returns the itemReasonAdded.
     */
    public ItemReasonAdded getItemReasonAdded() {
        return itemReasonAdded;
    }

    /**
     * Sets the itemReasonAdded attribute value.
     * @param itemReasonAdded The itemReasonAdded to set.
     * @deprecated
     */
    public void setItemReasonAdded(ItemReasonAdded itemReasonAdded) {
        this.itemReasonAdded = itemReasonAdded;
    }

    /**
     * Gets the itemType attribute. 
     * @return Returns the itemType.
     */
    public ItemType getItemType() {
        return itemType;
    }

    /**
     * Sets the itemType attribute value.
     * @param itemType The itemType to set.
     * @deprecated
     */
    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    /**
     * Gets the itemUnitOfMeasure attribute. 
     * @return Returns the itemUnitOfMeasure.
     */
    public UnitOfMeasure getItemUnitOfMeasure() {
        return itemUnitOfMeasure;
    }

    /**
     * Sets the itemUnitOfMeasure attribute value.
     * @param itemUnitOfMeasure The itemUnitOfMeasure to set.
     * @deprecated
     */
    public void setItemUnitOfMeasure(UnitOfMeasure itemUnitOfMeasure) {
        this.itemUnitOfMeasure = itemUnitOfMeasure;
    }

    public KualiDecimal getItemReceivedPriorQuantity() {
        if(ObjectUtils.isNull(itemReceivedPriorQuantity)) {
            setItemReceivedPriorQuantity(getPurchaseOrderItem().getItemReceivedTotalQuantity());
        }
        return itemReceivedPriorQuantity;
    }

    public void setItemReceivedPriorQuantity(KualiDecimal itemReceivedPriorQuantity) {
        
        this.itemReceivedPriorQuantity = itemReceivedPriorQuantity;
    }

    public KualiDecimal getItemReceivedToBeQuantity() {
        //lazy loaded
        if(ObjectUtils.isNull(itemReceivedPriorQuantity)) {
            setItemReceivedToBeQuantity(this.getItemOrderedQuantity().subtract(getItemReceivedPriorQuantity()));
        }
        return itemReceivedToBeQuantity;
    }

    public void setItemReceivedToBeQuantity(KualiDecimal itemReceivedToBeQuantity) {
        this.itemReceivedToBeQuantity = itemReceivedToBeQuantity;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        if (this.receivingLineItemIdentifier != null) {
            m.put("receivingLineItemIdentifier", this.receivingLineItemIdentifier.toString());
        }
        return m;
    }

}
