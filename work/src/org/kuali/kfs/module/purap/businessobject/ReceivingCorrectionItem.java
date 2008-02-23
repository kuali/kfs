package org.kuali.module.purap.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.purap.document.ReceivingCorrectionDocument;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class ReceivingCorrectionItem extends PersistableBusinessObjectBase {

	private Integer receivingCorrectionItemIdentifier;
	private String documentNumber;
	private Integer purchaseOrderIdentifier;
	private Integer itemLineNumber;
	private String itemTypeCode;
	private String itemUnitOfMeasureCode;
	private String itemCatalogNumber;
	private String itemDescription;
	private KualiDecimal itemOriginalReceivedTotalQuantity;
	private KualiDecimal itemOriginalReturnedTotalQuantity;
	private KualiDecimal itemOriginalDamagedTotalQuantity;
	private KualiDecimal itemCorrectedReceivedTotalQuantity;
	private KualiDecimal itemCorrectedReturnedTotalQuantity;
	private KualiDecimal itemCorrectedDamagedTotalQuantity;
	private String itemReasonAddedCode;

    private ReceivingCorrectionDocument receivingCorrectionDocument;
    private ItemReasonAdded itemReasonAdded;
    private ItemType itemType;
    private UnitOfMeasure itemUnitOfMeasure;

    
	/**
	 * Default constructor.
	 */
	public ReceivingCorrectionItem() {

	}

	/**
	 * Gets the receivingCorrectionItemIdentifier attribute.
	 * 
	 * @return Returns the receivingCorrectionItemIdentifier
	 * 
	 */
	public Integer getReceivingCorrectionItemIdentifier() { 
		return receivingCorrectionItemIdentifier;
	}

	/**
	 * Sets the receivingCorrectionItemIdentifier attribute.
	 * 
	 * @param receivingCorrectionItemIdentifier The receivingCorrectionItemIdentifier to set.
	 * 
	 */
	public void setReceivingCorrectionItemIdentifier(Integer receivingCorrectionItemIdentifier) {
		this.receivingCorrectionItemIdentifier = receivingCorrectionItemIdentifier;
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
	 * Gets the itemOriginalReceivedTotalQuantity attribute.
	 * 
	 * @return Returns the itemOriginalReceivedTotalQuantity
	 * 
	 */
	public KualiDecimal getItemOriginalReceivedTotalQuantity() { 
		return itemOriginalReceivedTotalQuantity;
	}

	/**
	 * Sets the itemOriginalReceivedTotalQuantity attribute.
	 * 
	 * @param itemOriginalReceivedTotalQuantity The itemOriginalReceivedTotalQuantity to set.
	 * 
	 */
	public void setItemOriginalReceivedTotalQuantity(KualiDecimal itemOriginalReceivedTotalQuantity) {
		this.itemOriginalReceivedTotalQuantity = itemOriginalReceivedTotalQuantity;
	}


	/**
	 * Gets the itemOriginalReturnedTotalQuantity attribute.
	 * 
	 * @return Returns the itemOriginalReturnedTotalQuantity
	 * 
	 */
	public KualiDecimal getItemOriginalReturnedTotalQuantity() { 
		return itemOriginalReturnedTotalQuantity;
	}

	/**
	 * Sets the itemOriginalReturnedTotalQuantity attribute.
	 * 
	 * @param itemOriginalReturnedTotalQuantity The itemOriginalReturnedTotalQuantity to set.
	 * 
	 */
	public void setItemOriginalReturnedTotalQuantity(KualiDecimal itemOriginalReturnedTotalQuantity) {
		this.itemOriginalReturnedTotalQuantity = itemOriginalReturnedTotalQuantity;
	}


	/**
	 * Gets the itemOriginalDamagedTotalQuantity attribute.
	 * 
	 * @return Returns the itemOriginalDamagedTotalQuantity
	 * 
	 */
	public KualiDecimal getItemOriginalDamagedTotalQuantity() { 
		return itemOriginalDamagedTotalQuantity;
	}

	/**
	 * Sets the itemOriginalDamagedTotalQuantity attribute.
	 * 
	 * @param itemOriginalDamagedTotalQuantity The itemOriginalDamagedTotalQuantity to set.
	 * 
	 */
	public void setItemOriginalDamagedTotalQuantity(KualiDecimal itemOriginalDamagedTotalQuantity) {
		this.itemOriginalDamagedTotalQuantity = itemOriginalDamagedTotalQuantity;
	}


	/**
	 * Gets the itemCorrectedReceivedTotalQuantity attribute.
	 * 
	 * @return Returns the itemCorrectedReceivedTotalQuantity
	 * 
	 */
	public KualiDecimal getItemCorrectedReceivedTotalQuantity() { 
		return itemCorrectedReceivedTotalQuantity;
	}

	/**
	 * Sets the itemCorrectedReceivedTotalQuantity attribute.
	 * 
	 * @param itemCorrectedReceivedTotalQuantity The itemCorrectedReceivedTotalQuantity to set.
	 * 
	 */
	public void setItemCorrectedReceivedTotalQuantity(KualiDecimal itemCorrectedReceivedTotalQuantity) {
		this.itemCorrectedReceivedTotalQuantity = itemCorrectedReceivedTotalQuantity;
	}


	/**
	 * Gets the itemCorrectedReturnedTotalQuantity attribute.
	 * 
	 * @return Returns the itemCorrectedReturnedTotalQuantity
	 * 
	 */
	public KualiDecimal getItemCorrectedReturnedTotalQuantity() { 
		return itemCorrectedReturnedTotalQuantity;
	}

	/**
	 * Sets the itemCorrectedReturnedTotalQuantity attribute.
	 * 
	 * @param itemCorrectedReturnedTotalQuantity The itemCorrectedReturnedTotalQuantity to set.
	 * 
	 */
	public void setItemCorrectedReturnedTotalQuantity(KualiDecimal itemCorrectedReturnedTotalQuantity) {
		this.itemCorrectedReturnedTotalQuantity = itemCorrectedReturnedTotalQuantity;
	}


	/**
	 * Gets the itemCorrectedDamagedTotalQuantity attribute.
	 * 
	 * @return Returns the itemCorrectedDamagedTotalQuantity
	 * 
	 */
	public KualiDecimal getItemCorrectedDamagedTotalQuantity() { 
		return itemCorrectedDamagedTotalQuantity;
	}

	/**
	 * Sets the itemCorrectedDamagedTotalQuantity attribute.
	 * 
	 * @param itemCorrectedDamagedTotalQuantity The itemCorrectedDamagedTotalQuantity to set.
	 * 
	 */
	public void setItemCorrectedDamagedTotalQuantity(KualiDecimal itemCorrectedDamagedTotalQuantity) {
		this.itemCorrectedDamagedTotalQuantity = itemCorrectedDamagedTotalQuantity;
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

    /**
     * Gets the receivingCorrectionDocument attribute. 
     * @return Returns the receivingCorrectionDocument.
     */
    public ReceivingCorrectionDocument getReceivingCorrectionDocument() {
        return receivingCorrectionDocument;
    }

    /**
     * Sets the receivingCorrectionDocument attribute value.
     * @param receivingCorrectionDocument The receivingCorrectionDocument to set.
     * @deprecated
     */
    public void setReceivingCorrectionDocument(ReceivingCorrectionDocument receivingCorrectionDocument) {
        this.receivingCorrectionDocument = receivingCorrectionDocument;
    }

    /**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.receivingCorrectionItemIdentifier != null) {
            m.put("receivingCorrectionItemIdentifier", this.receivingCorrectionItemIdentifier.toString());
        }
	    return m;
    }
}
