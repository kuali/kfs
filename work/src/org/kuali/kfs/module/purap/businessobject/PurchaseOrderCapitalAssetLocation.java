package org.kuali.kfs.module.purap.businessobject;


/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */

public class PurchaseOrderCapitalAssetLocation extends PurchasingCapitalAssetLocationBase {

    private PurchasingCapitalAssetSystem purchaseOrderCapitalAssetSystem;

	/**
	 * Default constructor.
	 */
	public PurchaseOrderCapitalAssetLocation() {

	}
	
	public PurchasingCapitalAssetSystem getPurchaseOrderCapitalAssetSystem() {
        return purchaseOrderCapitalAssetSystem;
    }

    public void setPurchaseOrderCapitalAssetSystem(PurchasingCapitalAssetSystem purchaseOrderCapitalAssetSystem) {
        this.purchaseOrderCapitalAssetSystem = purchaseOrderCapitalAssetSystem;
    }

}
