/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.purap.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class ItemReasonAdded extends PersistableBusinessObjectBase implements MutableInactivatable{

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
	 * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("itemReasonAddedCode", this.itemReasonAddedCode);
	    return m;
    }
}
