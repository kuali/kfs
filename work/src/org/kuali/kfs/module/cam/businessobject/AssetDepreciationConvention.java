package org.kuali.module.cams.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.module.chart.bo.ObjSubTyp;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AssetDepreciationConvention extends PersistableBusinessObjectBase {

	private String financialObjectSubTypeCode;
	private String depreciationConventionCode;

    private ObjSubTyp financialObjectSubType;
    
	/**
	 * Default constructor.
	 */
	public AssetDepreciationConvention() {

	}

	/**
	 * Gets the financialObjectSubTypeCode attribute.
	 * 
	 * @return Returns the financialObjectSubTypeCode
	 * 
	 */
	public String getFinancialObjectSubTypeCode() { 
		return financialObjectSubTypeCode;
	}

	/**
	 * Sets the financialObjectSubTypeCode attribute.
	 * 
	 * @param financialObjectSubTypeCode The financialObjectSubTypeCode to set.
	 * 
	 */
	public void setFinancialObjectSubTypeCode(String financialObjectSubTypeCode) {
		this.financialObjectSubTypeCode = financialObjectSubTypeCode;
	}


	/**
	 * Gets the depreciationConventionCode attribute.
	 * 
	 * @return Returns the depreciationConventionCode
	 * 
	 */
	public String getDepreciationConventionCode() { 
		return depreciationConventionCode;
	}

	/**
	 * Sets the depreciationConventionCode attribute.
	 * 
	 * @param depreciationConventionCode The depreciationConventionCode to set.
	 * 
	 */
	public void setDepreciationConventionCode(String depreciationConventionCode) {
		this.depreciationConventionCode = depreciationConventionCode;
	}

	/**
     * Gets the financialObjectSubType attribute. 
     * @return Returns the financialObjectSubType.
     */
    public ObjSubTyp getFinancialObjectSubType() {
        return financialObjectSubType;
    }

    /**
     * Sets the financialObjectSubType attribute value.
     * @param financialObjectSubType The financialObjectSubType to set.
     * @deprecated
     */
    public void setFinancialObjectSubType(ObjSubTyp financialObjectSubType) {
        this.financialObjectSubType = financialObjectSubType;
    }

    /**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("financialObjectSubTypeCode", this.financialObjectSubTypeCode);
	    return m;
    }
}
