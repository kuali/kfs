package org.kuali.kfs.module.ld.businessobject;


import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * BO for the Labor Benefit Rate Category Fringe Benefit
 * 
 * @author Allan Sonkin
 */
public class LaborBenefitRateCategory extends PersistableBusinessObjectBase implements org.kuali.kfs.integration.ld.LaborBenefitRateCategory, MutableInactivatable {

    private String laborBenefitRateCategoryCode;//the BO code
    
    private Boolean active = false;     //indicates active status of this BO
    
    private String codeDesc;                    //description for the BO
    
   
    /**
     * Getter method to get the laborBenefitRateCategoryCode
     * @return laborBenefitRateCategoryCode
     */
	public String getLaborBenefitRateCategoryCode() {
		return laborBenefitRateCategoryCode;
	}
    
    /**
     * 
     * Method to set the code
     * @param code
     */
    public void setLaborBenefitRateCategoryCode(String laborBenefitRateCategoryCode) {
		this.laborBenefitRateCategoryCode = laborBenefitRateCategoryCode;
	}


    /**
     * 
     * Getter method for the code's description
     * @return codeDesc
     */
    public String getCodeDesc() {
        return codeDesc;
    }

	/**
	 * 
	 * Sets the codeDesc
	 * @param codeDesc
	 */
    public void setCodeDesc(String codeDesc) {
        this.codeDesc = codeDesc;
    }
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        
        m.put("laborBenefitRateCategoryCode", this.laborBenefitRateCategoryCode);
        m.put("codeDesc", this.codeDesc);
        
        return m;
	}

    public boolean isActive() {
        // TODO Auto-generated method stub
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
