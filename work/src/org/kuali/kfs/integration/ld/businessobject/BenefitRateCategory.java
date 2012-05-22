package org.kuali.kfs.integration.ld.businessobject;


import java.util.LinkedHashMap;

import org.kuali.kfs.integration.ld.LaborBenefitRateCategory;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;

/**
 * BO for the Labor Benefit Rate Category Fringe Benefit
 * 
 * @author Allan Sonkin
 */
public class BenefitRateCategory implements LaborBenefitRateCategory, MutableInactivatable {

    private String laborBenefitRateCategoryCode;//the BO code
    
    private Boolean activeIndicator = false;     //indicates active status of this BO
    
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
     * Getter method for the active indicator
     * @return activeIndicator
     */
    public Boolean getActiveIndicator() {
        return activeIndicator;
    }

    /**
     * 
     * Sets the activeIndicator
     * @param activeIndicator
     */
    public void setActiveIndicator(Boolean activeIndicator) {
        this.activeIndicator = activeIndicator;
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

    @Override
    public boolean isActive() {
        // TODO Auto-generated method stub
        return activeIndicator;
    }

    @Override
    public void setActive(boolean active) {
        this.activeIndicator = active;
    }

    @Override
    public void refresh() {
        // TODO Auto-generated method stub
        
    }

    public void prepareForWorkflow() {
        // TODO Auto-generated method stub
        
    }
}