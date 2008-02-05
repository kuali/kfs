package org.kuali.module.purap.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class ItemReasonAdded extends PersistableBusinessObjectBase {

	private String itemReasonAddedCode;
	private String itemReasonAddedDescription;
	private boolean active;

	/**
	 * Default constructor.
	 */
	public ItemReasonAdded() {

	}

	/**
	 * Gets the itemReasonAddedCode attribute.
	 * 
	 * @return Returns the itemReasonAddedCode
	 * 
	 */
	public String getItemReasonAddedCode() { 
		return itemReasonAddedCode;
	}

	/**
	 * Sets the itemReasonAddedCode attribute.
	 * 
	 * @param itemReasonAddedCode The itemReasonAddedCode to set.
	 * 
	 */
	public void setItemReasonAddedCode(String itemReasonAddedCode) {
		this.itemReasonAddedCode = itemReasonAddedCode;
	}


	/**
	 * Gets the itemReasonAddedDescription attribute.
	 * 
	 * @return Returns the itemReasonAddedDescription
	 * 
	 */
	public String getItemReasonAddedDescription() { 
		return itemReasonAddedDescription;
	}

	/**
	 * Sets the itemReasonAddedDescription attribute.
	 * 
	 * @param itemReasonAddedDescription The itemReasonAddedDescription to set.
	 * 
	 */
	public void setItemReasonAddedDescription(String itemReasonAddedDescription) {
		this.itemReasonAddedDescription = itemReasonAddedDescription;
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
        m.put("itemReasonAddedCode", this.itemReasonAddedCode);
	    return m;
    }
}
