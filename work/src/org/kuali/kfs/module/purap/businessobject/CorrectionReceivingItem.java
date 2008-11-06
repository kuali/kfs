package org.kuali.kfs.module.purap.businessobject;

import org.kuali.kfs.module.purap.document.CorrectionReceivingDocument;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CorrectionReceivingItem extends ReceivingItemBase {

	private CorrectionReceivingDocument correctionReceivingDocument;
    
	/**
	 * Default constructor.
	 */
	public CorrectionReceivingItem() {

	}

    public CorrectionReceivingItem(LineItemReceivingItem rli, CorrectionReceivingDocument rcd){
        
        this.setDocumentNumber( rcd.getDocumentNumber() );        
        this.setItemTypeCode( rli.getItemTypeCode() );
        
        this.setItemLineNumber( rli.getItemLineNumber() );
        this.setItemCatalogNumber( rli.getItemCatalogNumber() );
        this.setItemDescription( rli.getItemDescription() );        
        this.setItemUnitOfMeasureCode( rli.getItemUnitOfMeasureCode() );
                
        this.setItemOriginalReceivedTotalQuantity( rli.getItemReceivedTotalQuantity() );
        this.setItemOriginalReturnedTotalQuantity( rli.getItemReturnedTotalQuantity() );
        this.setItemOriginalDamagedTotalQuantity( rli.getItemDamagedTotalQuantity() );

        this.setItemReceivedTotalQuantity(rli.getItemReceivedTotalQuantity());
        this.setItemReturnedTotalQuantity(rli.getItemReturnedTotalQuantity());
        this.setItemDamagedTotalQuantity(rli.getItemDamagedTotalQuantity());

        //not added
        this.setItemReasonAddedCode(null);
    }


    public CorrectionReceivingDocument getCorrectionReceivingDocument() {
        return correctionReceivingDocument;
    }

    /**
     * Sets the receivingCorrectionDocument attribute value.
     * @param receivingCorrectionDocument The receivingCorrectionDocument to set.
     * @deprecated
     */
    public void setCorrectionReceivingDocument(CorrectionReceivingDocument correctionReceivingDocument) {
        this.correctionReceivingDocument = correctionReceivingDocument;
    }

    
}
