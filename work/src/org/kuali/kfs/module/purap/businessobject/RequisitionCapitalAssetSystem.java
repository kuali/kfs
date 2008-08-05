package org.kuali.kfs.module.purap.businessobject;

import java.util.LinkedHashMap;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */

public class RequisitionCapitalAssetSystem extends PurchasingCapitalAssetSystemBase {

	private Integer purapDocumentIdentifier;
	
	/**
	 * Default constructor.
	 */
	public RequisitionCapitalAssetSystem() {
	    super();
	}

    @Override
    public Class getCapitalAssetLocationClass() {
        return RequisitionCapitalAssetLocation.class;
    }

    @Override
    public Class getItemCapitalAssetClass() {
        return RequisitionItemCapitalAsset.class;
    }

    public Integer getPurapDocumentIdentifier() {
        return purapDocumentIdentifier;
    }

    public void setPurapDocumentIdentifier(Integer purapDocumentIdentifier) {
        this.purapDocumentIdentifier = purapDocumentIdentifier;
    }

    @Override
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.purapDocumentIdentifier != null) {
            m.put("purapDocumentIdentifier", this.purapDocumentIdentifier.toString());
        }
        if (this.getCapitalAssetSystemIdentifier() != null) {
            m.put("capitalAssetSystemIdentifier", this.getCapitalAssetSystemIdentifier().toString());
        }
	    return m;
    }

}
