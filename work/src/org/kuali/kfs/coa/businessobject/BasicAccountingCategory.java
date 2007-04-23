package org.kuali.module.chart.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class BasicAccountingCategory extends PersistableBusinessObjectBase {

	private String code;
	private String description;
	private String shortName;
	private String sortCode;
	private boolean active;

	/**
	 * Default constructor.
	 */
	public BasicAccountingCategory() {

	}

	/**
	 * Gets the accountCategoryCode attribute.
	 * 
	 * @return Returns the accountCategoryCode
	 * 
	 */
	public String getCode() { 
		return code;
	}

	/**
	 * Sets the accountCategoryCode attribute.
	 * 
	 * @param accountCategoryCode The accountCategoryCode to set.
	 * 
	 */
	public void setCode(String basicAccountingCategoryCode) {
		this.code = basicAccountingCategoryCode;
	}


	/**
	 * Gets the description attribute.
	 * 
	 * @return Returns the description
	 * 
	 */
	public String getDescription() { 
		return description;
	}

	/**
	 * Sets the description attribute.
	 * 
	 * @param description The description to set.
	 * 
	 */
	public void setDescription(String accountCategoryDescription) {
		this.description = accountCategoryDescription;
	}


	/**
	 * Gets the accountCategoryShortName attribute.
	 * 
	 * @return Returns the accountCategoryShortName
	 * 
	 */
	public String getShortName() { 
		return shortName;
	}

	/**
	 * Sets the accountCategoryShortName attribute.
	 * 
	 * @param accountCategoryShortName The accountCategoryShortName to set.
	 * 
	 */
	public void setShortName(String basicAccountingCategoryShortName) {
		this.shortName = basicAccountingCategoryShortName;
	}


	/**
	 * Gets the sortCode attribute.
	 * 
	 * @return Returns the sortCode
	 * 
	 */
	public String getSortCode() { 
		return sortCode;
	}

	/**
	 * Sets the sortCode attribute.
	 * 
	 * @param sortCode The sortCode to set.
	 * 
	 */
	public void setSortCode(String financialReportingSortCode) {
		this.sortCode = financialReportingSortCode;
	}
    
    /**
     * Gets the active attribute.
     *  
     * @return Returns the active.
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
        m.put("accountCategoryCode", this.code);
	    return m;
    }

    /**
     * 
     * This method generates a standard String of the code and description together
     * @return string representation of the code and description for this Account Category.
     */
    public String getCodeAndDescription() {
        StringBuilder codeAndDescription = new StringBuilder();
        codeAndDescription.append(this.getCode());
        codeAndDescription.append(" - ");
        codeAndDescription.append(this.getDescription());
        return codeAndDescription.toString();
    }
}
