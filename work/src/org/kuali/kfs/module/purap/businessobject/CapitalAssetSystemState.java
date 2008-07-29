package org.kuali.kfs.module.purap.businessobject;

import java.util.LinkedHashMap;

import org.kuali.core.bo.Inactivateable;
import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * 
 * This class is the Capital Asset System State
 */
public class CapitalAssetSystemState extends PersistableBusinessObjectBase {

	private String capitalAssetSystemStateCode;
	private String capitalAssetSystemStateDescription;
	private boolean active;

	/**
	 * Default constructor.
	 */
	public CapitalAssetSystemState() {

	}

	public String getCapitalAssetSystemStateCode() { 
		return capitalAssetSystemStateCode;
	}

	public void setCapitalAssetSystemStateCode(String capitalAssetSystemStateCode) {
		this.capitalAssetSystemStateCode = capitalAssetSystemStateCode;
	}

	public String getCapitalAssetSystemStateDescription() {
        return capitalAssetSystemStateDescription;
    }

    public void setCapitalAssetSystemStateDescription(String capitalAssetSystemStateDescription) {
        this.capitalAssetSystemStateDescription = capitalAssetSystemStateDescription;
    }

    public boolean isActive() { 
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}


	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("capitalAssetSystemStateCode", this.capitalAssetSystemStateCode);
	    return m;
    }
}
