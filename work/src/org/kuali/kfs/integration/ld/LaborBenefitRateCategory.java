package org.kuali.kfs.integration.ld;


import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.ExternalizableBusinessObject;
import org.kuali.rice.kns.bo.PersistableBusinessObject;

/**
 * BO for the Labor Benefit Rate Category Fringe Benefit
 * 
 * @author Allan Sonkin
 */
public interface LaborBenefitRateCategory extends  ExternalizableBusinessObject {
    
    
    /**
     * Getter method to get the laborBenefitRateCategoryCode
     * @return laborBenefitRateCategoryCode
     */
	public String getLaborBenefitRateCategoryCode();
    
    /**
     * 
     * Method to set the code
     * @param code
     */
    public void setLaborBenefitRateCategoryCode(String laborBenefitRateCategoryCode);


    /**
     * 
     * Getter method for the active indicator
     * @return activeIndicator
     */
    public Boolean getActiveIndicator();

    /**
     * 
     * Sets the activeIndicator
     * @param activeIndicator
     */
    public void setActiveIndicator(Boolean activeIndicator);
    /**
     * 
     * Getter method for the code's description
     * @return codeDesc
     */
    public String getCodeDesc();
	/**
	 * 
	 * Sets the codeDesc
	 * @param codeDesc
	 */
    public void setCodeDesc(String codeDesc);
    

    public boolean isActive();
    
    public void setActive(boolean active);
}