package org.kuali.kfs.module.cam.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.ObjectSubType;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AssetDepreciationConvention extends PersistableBusinessObjectBase implements Inactivateable {

    private String financialObjectSubTypeCode;
    private String depreciationConventionCode;
    private boolean active;

    private ObjectSubType financialObjectSubType;

    /**
     * Default constructor.
     */
    public AssetDepreciationConvention() {

    }

    /**
     * Gets the financialObjectSubTypeCode attribute.
     * 
     * @return Returns the financialObjectSubTypeCode
     */
    public String getFinancialObjectSubTypeCode() {
        return financialObjectSubTypeCode;
    }

    /**
     * Sets the financialObjectSubTypeCode attribute.
     * 
     * @param financialObjectSubTypeCode The financialObjectSubTypeCode to set.
     */
    public void setFinancialObjectSubTypeCode(String financialObjectSubTypeCode) {
        this.financialObjectSubTypeCode = financialObjectSubTypeCode;
    }


    /**
     * Gets the depreciationConventionCode attribute.
     * 
     * @return Returns the depreciationConventionCode
     */
    public String getDepreciationConventionCode() {
        return depreciationConventionCode;
    }

    /**
     * Sets the depreciationConventionCode attribute.
     * 
     * @param depreciationConventionCode The depreciationConventionCode to set.
     */
    public void setDepreciationConventionCode(String depreciationConventionCode) {
        this.depreciationConventionCode = depreciationConventionCode;
    }

    /**
     * Gets the financialObjectSubType attribute.
     * 
     * @return Returns the financialObjectSubType.
     */
    public ObjectSubType getFinancialObjectSubType() {
        return financialObjectSubType;
    }

    /**
     * Sets the financialObjectSubType attribute value.
     * 
     * @param financialObjectSubType The financialObjectSubType to set.
     * @deprecated
     */
    public void setFinancialObjectSubType(ObjectSubType financialObjectSubType) {
        this.financialObjectSubType = financialObjectSubType;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap<String, String> toStringMapper() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put("financialObjectSubTypeCode", this.financialObjectSubTypeCode);
        return m;
    }

    /**
     * Gets the active attribute.
     * 
     * @return Returns the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }
}
