package org.kuali.module.purap.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.bo.UnitOfMeasure;
import org.kuali.module.purap.document.ReceivingCorrectionDocument;
import org.kuali.module.purap.document.ReceivingLineDocument;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class ReceivingCorrectionItem extends ReceivingItemBase {


	private KualiDecimal itemOriginalReceivedTotalQuantity;
	private KualiDecimal itemOriginalReturnedTotalQuantity;
	private KualiDecimal itemOriginalDamagedTotalQuantity;
    private ReceivingCorrectionDocument receivingCorrectionDocument;

    
	/**
	 * Default constructor.
	 */
	public ReceivingCorrectionItem() {

	}

    public ReceivingCorrectionItem(ReceivingLineItem rli, ReceivingCorrectionDocument rcd){
        
        this.setDocumentNumber( rcd.getDocumentNumber() );        
        this.setItemTypeCode( rli.getItemTypeCode() );
        
        this.setItemLineNumber( rli.getItemLineNumber() );
        this.setItemCatalogNumber( rli.getItemCatalogNumber() );
        this.setItemDescription( rli.getItemDescription() );        
        this.setItemUnitOfMeasureCode( rli.getItemUnitOfMeasureCode() );
                
        this.setItemOriginalReceivedTotalQuantity( rli.getItemReceivedTotalQuantity() );
        this.setItemOriginalReturnedTotalQuantity( rli.getItemReturnedTotalQuantity() );
        this.setItemOriginalDamagedTotalQuantity( rli.getItemDamagedTotalQuantity() );

        this.setItemReceivedTotalQuantity(KualiDecimal.ZERO);
        this.setItemReturnedTotalQuantity(KualiDecimal.ZERO);
        this.setItemDamagedTotalQuantity(KualiDecimal.ZERO);

        //not added
        this.setItemReasonAddedCode(null);
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


}
