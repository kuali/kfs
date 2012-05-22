package org.kuali.kfs.integration.ld;


import org.kuali.rice.krad.bo.ExternalizableBusinessObject;

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
    
}