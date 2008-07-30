package org.kuali.kfs.module.purap.businessobject;

import java.util.LinkedHashMap;



/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */

public class PurchaseOrderCapitalAssetSystem extends PurchasingCapitalAssetSystemBase {

	private String documentNumber;
	
	/**
	 * Default constructor.
	 */
	public PurchaseOrderCapitalAssetSystem() {
       super();
	}

	public String getDocumentNumber() {
        return documentNumber;
    }

    @Override
    public Class getCapitalAssetLocationClass() {
        return PurchaseOrderCapitalAssetLocation.class;
    }

    @Override
    public Class getItemCapitalAssetClass() {
        return PurchaseOrderItemCapitalAsset.class;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }
    
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        if (this.documentNumber != null) {
            m.put("documentNumber", this.documentNumber.toString());
        }
        if (this.getCapitalAssetSystemIdentifier() != null) {
            m.put("capitalAssetSystemIdentifier", this.getCapitalAssetSystemIdentifier().toString());
        }
        return m;
    }
}
