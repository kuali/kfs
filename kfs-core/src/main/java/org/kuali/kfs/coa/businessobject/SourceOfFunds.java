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
package org.kuali.kfs.coa.businessobject;


import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.Inactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This class represents the SourceOfFunds Business Object
 * @author 
 * @version
 */

public class SourceOfFunds extends PersistableBusinessObjectBase implements Inactivatable {

	private String typeCode;				//Source Of Funds Type Code
	private boolean active;					//Active Indicator
	private String sourceOfFundsDesc;		//Source Of Funds Description
	private String sourceOfFundsName;		//Source Of Funds Name
	
	/**
	 * Default constructor.
	 */
	public SourceOfFunds() {
		
	}

	/**
	 * Gets the sourceOfFunds typeCode attribute.
	 * @return typeCode
	 */
	public String getTypeCode() {
		return typeCode;
	}

	/**
	 * Sets the sourceOfFunds typeCode attribute value.
	 * @param typeCode The typeCode to set.
	 */
	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	/**
	 * Gets the active attribute.
	 * @return Returns the value for active.
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Sets the active attribute value.
	 * @param active The value for active to set.
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Gets the sourceOfFundsDesc attribute
	 * @return Returns the sourceOfFundsDesc.
	 */
	public String getSourceOfFundsDesc() {
		return sourceOfFundsDesc;
	}

	/**
	 * Sets the sourceOfFundsDesc attribute.
	 * @param sourceOfFundsDesc The sourceOfFundsDesc to set.
	 */
	public void setSourceOfFundsDesc(String sourceOfFundsDesc) {
		this.sourceOfFundsDesc = sourceOfFundsDesc;
	}

	/**
	 * Gets the sourceOfFundsName attribute.
	 * @return Returns the sourceOfFundsName.
	 */
	public String getSourceOfFundsName() {
		return sourceOfFundsName;
	}

	/**
	 * Sets the sourceOfFundsName attribute value.
	 * @param sourceOfFundsName The sourceofFundsName to set
	 */
	public void setSourceOfFundsName(String sourceOfFundsName) {
		this.sourceOfFundsName = sourceOfFundsName;
	}
	
	protected LinkedHashMap<String, String> toStringMapper() {
		LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
		m.put("typeCode", this.typeCode);
		return m;
	}
}
