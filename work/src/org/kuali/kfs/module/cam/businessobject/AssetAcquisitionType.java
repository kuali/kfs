package org.kuali.module.cams.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AssetAcquisitionType extends PersistableBusinessObjectBase {

	private String acquisitionTypeCode;
	private String acquisitionTypeName;
	private String incomeAssetObjectCode;
    private boolean active;

	/**
	 * Default constructor.
	 */
	public AssetAcquisitionType() {
	}

	/**
	 * Gets the acquisitionTypeCode attribute.
	 * 
	 * @return Returns the acquisitionTypeCode
	 * 
	 */
	public String getAcquisitionTypeCode() { 
		return acquisitionTypeCode;
	}

	/**
	 * Sets the acquisitionTypeCode attribute.
	 * 
	 * @param acquisitionTypeCode The acquisitionTypeCode to set.
	 * 
	 */
	public void setAcquisitionTypeCode(String acquisitionTypeCode) {
		this.acquisitionTypeCode = acquisitionTypeCode;
	}

	/**
	 * Gets the acquisitionTypeName attribute.
	 * 
	 * @return Returns the acquisitionTypeName
	 * 
	 */
	public String getAcquisitionTypeName() { 
		return acquisitionTypeName;
	}

	/**
	 * Sets the acquisitionTypeName attribute.
	 * 
	 * @param acquisitionTypeName The acquisitionTypeName to set.
	 * 
	 */
	public void setAcquisitionTypeName(String acquisitionTypeName) {
		this.acquisitionTypeName = acquisitionTypeName;
	}

	/**
	 * Gets the incomeAssetObjectCode attribute.
	 * 
	 * @return Returns the incomeAssetObjectCode
	 * 
	 */
	public String getIncomeAssetObjectCode() { 
		return incomeAssetObjectCode;
	}

	/**
	 * Sets the incomeAssetObjectCode attribute.
	 * 
	 * @param incomeAssetObjectCode The incomeAssetObjectCode to set.
	 * 
	 */
	public void setIncomeAssetObjectCode(String incomeAssetObjectCode) {
		this.incomeAssetObjectCode = incomeAssetObjectCode;
	}    

    /**
     * Gets the active attribute.
     * 
     * @return Returns the active
     * 
     */
    public boolean isActive() { 
        return active;
    }

    /**
     * Sets the active attribute.
     * 
     * @param active The active to set.
     * 
     */
    public void setActive(boolean active) {
        this.active = active;
    }

	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("acquisitionTypeCode", this.acquisitionTypeCode);
	    return m;
    }
}
