package org.kuali.kfs.module.purap.businessobject;


/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */

public class PurchaseOrderCapitalAssetLocation extends PurchasingCapitalAssetLocationBase {

	private String documentNumber;
	
    private PurchasingCapitalAssetSystem purchaseOrderCapitalAssetSystem;

	/**
	 * Default constructor.
	 */
	public PurchaseOrderCapitalAssetLocation() {

	}

	public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }
	
	public PurchasingCapitalAssetSystem getPurchaseOrderCapitalAssetSystem() {
        return purchaseOrderCapitalAssetSystem;
    }

    public void setPurchaseOrderCapitalAssetSystem(PurchasingCapitalAssetSystem purchaseOrderCapitalAssetSystem) {
        this.purchaseOrderCapitalAssetSystem = purchaseOrderCapitalAssetSystem;
    }

}
