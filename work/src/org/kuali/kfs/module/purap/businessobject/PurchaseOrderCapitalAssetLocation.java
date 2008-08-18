package org.kuali.kfs.module.purap.businessobject;

import org.kuali.kfs.integration.businessobject.CapitalAssetSystem;


/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */

public class PurchaseOrderCapitalAssetLocation extends PurchasingCapitalAssetLocationBase {

    private CapitalAssetSystem purchaseOrderCapitalAssetSystem;

	/**
	 * Default constructor.
	 */
	public PurchaseOrderCapitalAssetLocation() {

	}
	
	public CapitalAssetSystem getPurchaseOrderCapitalAssetSystem() {
        return purchaseOrderCapitalAssetSystem;
    }

    public void setPurchaseOrderCapitalAssetSystem(CapitalAssetSystem purchaseOrderCapitalAssetSystem) {
        this.purchaseOrderCapitalAssetSystem = purchaseOrderCapitalAssetSystem;
    }

}
