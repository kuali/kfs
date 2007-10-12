package org.kuali.module.chart.bo.codes;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class BudgetRecordingLevel extends PersistableBusinessObjectBase {

	private String budgetRecordingLevelCode;
	private String budgetRecordingLevelName;
	private boolean active;

	/**
	 * Default constructor.
	 */
	public BudgetRecordingLevel() {

	}

	/**
	 * Gets the budgetRecordingLevelCode attribute.
	 * 
	 * @return Returns the budgetRecordingLevelCode
	 * 
	 */
	public String getBudgetRecordingLevelCode() { 
		return budgetRecordingLevelCode;
	}

	/**
	 * Sets the budgetRecordingLevelCode attribute.
	 * 
	 * @param budgetRecordingLevelCode The budgetRecordingLevelCode to set.
	 * 
	 */
	public void setBudgetRecordingLevelCode(String budgetRecordingLevelCode) {
		this.budgetRecordingLevelCode = budgetRecordingLevelCode;
	}


	/**
	 * Gets the budgetRecordingLevelName attribute.
	 * 
	 * @return Returns the budgetRecordingLevelName
	 * 
	 */
	public String getBudgetRecordingLevelName() { 
		return budgetRecordingLevelName;
	}

	/**
	 * Sets the budgetRecordingLevelName attribute.
	 * 
	 * @param budgetRecordingLevelName The budgetRecordingLevelName to set.
	 * 
	 */
	public void setBudgetRecordingLevelName(String budgetRecordingLevelName) {
		this.budgetRecordingLevelName = budgetRecordingLevelName;
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
        m.put("budgetRecordingLevelCode", this.budgetRecordingLevelCode);
	    return m;
    }
}
