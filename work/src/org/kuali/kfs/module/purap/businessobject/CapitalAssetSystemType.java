package org.kuali.kfs.module.purap.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */

public class CapitalAssetSystemType extends PersistableBusinessObjectBase implements Inactivateable {

	private String capitalAssetSystemTypeCode;
	private String capitalAssetSystemTypeDescription;
	private boolean active;

	/**
	 * Default constructor.
	 */
	public CapitalAssetSystemType() {

	}

	public String getCapitalAssetSystemTypeCode() { 
		return capitalAssetSystemTypeCode;
	}

	public void setCapitalAssetSystemTypeCode(String capitalAssetSystemTypeCode) {
		this.capitalAssetSystemTypeCode = capitalAssetSystemTypeCode;
	}

	public String getCapitalAssetSystemTypeDescription() {
        return capitalAssetSystemTypeDescription;
    }

    public void setCapitalAssetSystemTypeDescription(String capitalAssetSystemTypeDescription) {
        this.capitalAssetSystemTypeDescription = capitalAssetSystemTypeDescription;
    }

    public boolean isActive() { 
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}


	/**
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("capitalAssetSystemTypeCode", this.capitalAssetSystemTypeCode);
	    return m;
    }
}
