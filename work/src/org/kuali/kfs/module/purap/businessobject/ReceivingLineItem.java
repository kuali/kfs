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
public class ReceivingLineItem extends ReceivingItemBase {

    private KualiDecimal itemOrderedQuantity;

    //not stored in db
    private KualiDecimal itemReceivedPriorQuantity;
    private KualiDecimal itemReceivedToBeQuantity;
    
    private ReceivingLineDocument receivingLineDocument;


       

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

        //TODO: Chris - look into this it appears this is null rather than zero on amendment, find out why!
        if(ObjectUtils.isNull(poi.getItemReceivedTotalQuantity())) {
            this.setItemReceivedPriorQuantity(KualiDecimal.ZERO);
        } else {
            this.setItemReceivedPriorQuantity( poi.getItemReceivedTotalQuantity() );
        }
        
        this.setItemReceivedToBeQuantity( this.getItemOrderedQuantity().subtract(this.getItemReceivedPriorQuantity()));        
        
        //should determine whether this is prefilled be based on the parameter that allows loading from po
        this.setItemReceivedTotalQuantity( KualiDecimal.ZERO );
        
        this.setItemReturnedTotalQuantity( KualiDecimal.ZERO );
        this.setItemDamagedTotalQuantity( KualiDecimal.ZERO );                                
        //not added
        this.setItemReasonAddedCode(null);
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
//                LOG.debug("getPurchaseOrderItem() Returning null because PurchaseOrderItem object for line number" + getItemLineNumber() + "or itemType " + getItemTypeCode() + " is null");
                return null;
            }
        }
        else {
            LOG.error("getPurchaseOrderItem() Returning null because paymentRequest object is null");
            throw new PurError("Receiving Line Object in Purchase Order item line number " + getItemLineNumber() + "or itemType " + getItemTypeCode() + " is null");
        }
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
        if(ObjectUtils.isNull(itemReceivedToBeQuantity)) {
            KualiDecimal toBeQuantity = this.getItemOrderedQuantity().subtract(getItemReceivedPriorQuantity());
            if (toBeQuantity.isNegative()) {
                toBeQuantity = KualiDecimal.ZERO;
            }
            setItemReceivedToBeQuantity(toBeQuantity);
        }
        return itemReceivedToBeQuantity;
    }

    public void setItemReceivedToBeQuantity(KualiDecimal itemReceivedToBeQuantity) {
        this.itemReceivedToBeQuantity = itemReceivedToBeQuantity;
    }



}
