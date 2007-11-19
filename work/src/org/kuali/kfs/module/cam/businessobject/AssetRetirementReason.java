package org.kuali.module.cams.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AssetRetirementReason extends PersistableBusinessObjectBase {

	private String retirementReasonCode;
	private String retirementReasonName;
	private boolean active;
    private boolean retirementReasonRestrictionIndicator;
    
	/**
	 * Default constructor.
	 */
	public AssetRetirementReason() {

	}

	/**
	 * Gets the retirementReasonCode attribute.
	 * 
	 * @return Returns the retirementReasonCode
	 * 
	 */
	public String getRetirementReasonCode() { 
		return retirementReasonCode;
	}

	/**
	 * Sets the retirementReasonCode attribute.
	 * 
	 * @param retirementReasonCode The retirementReasonCode to set.
	 * 
	 */
	public void setRetirementReasonCode(String retirementReasonCode) {
		this.retirementReasonCode = retirementReasonCode;
	}


	/**
	 * Gets the retirementReasonName attribute.
	 * 
	 * @return Returns the retirementReasonName
	 * 
	 */
	public String getRetirementReasonName() { 
		return retirementReasonName;
	}

	/**
	 * Sets the retirementReasonName attribute.
	 * 
	 * @param retirementReasonName The retirementReasonName to set.
	 * 
	 */
	public void setRetirementReasonName(String retirementReasonName) {
		this.retirementReasonName = retirementReasonName;
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
     * Gets the retirementReasonRestrictionIndicator attribute. 
     * @return Returns the retirementReasonRestrictionIndicator.
     */
    public boolean isRetirementReasonRestrictionIndicator() {
        return retirementReasonRestrictionIndicator;
    }

    /**
     * Sets the retirementReasonRestrictionIndicator attribute value.
     * @param retirementReasonRestrictionIndicator The retirementReasonRestrictionIndicator to set.
     */
    public void setRetirementReasonRestrictionIndicator(boolean retirementReasonRestrictionIndicator) {
        this.retirementReasonRestrictionIndicator = retirementReasonRestrictionIndicator;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put("retirementReasonCode", this.retirementReasonCode);
        return m;
    }

}
