package org.kuali.kfs.module.purap.businessobject;

import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */

public class RequisitionCapitalAssetSystem extends PurchasingCapitalAssetSystemBase {

	private Integer requisitionIdentifier;
	
	/**
	 * Default constructor.
	 */
	public RequisitionCapitalAssetSystem() {
	    super();
	}

	public Integer getRequisitionIdentifier() {
        return requisitionIdentifier;
    }

    @Override
    public Class getCapitalAssetLocationClass() {
        return RequisitionCapitalAssetLocation.class;
    }

    @Override
    public Class getItemCapitalAssetClass() {
        return RequisitionItemCapitalAsset.class;
    }

    public void setRequisitionIdentifier(Integer requisitionIdentifier) {
        this.requisitionIdentifier = requisitionIdentifier;
    }

    @Override
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.requisitionIdentifier != null) {
            m.put("requisitionIdentifier", this.requisitionIdentifier.toString());
        }
        if (this.getCapitalAssetSystemIdentifier() != null) {
            m.put("capitalAssetSystemIdentifier", this.getCapitalAssetSystemIdentifier().toString());
        }
	    return m;
    }

}
