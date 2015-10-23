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
 */

public class SourceOfFunds extends PersistableBusinessObjectBase implements Inactivatable {

	private String typeCode;				
	private boolean active;					
	private String sourceOfFundsDescription;		
	private String sourceOfFundsName;		
	
	public SourceOfFunds() {
		
	}

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getSourceOfFundsDescription() {
		return sourceOfFundsDescription;
	}

	public void setSourceOfFundsDescription(String sourceOfFundsDescription) {
		this.sourceOfFundsDescription = sourceOfFundsDescription;
	}

	public String getSourceOfFundsName() {
		return sourceOfFundsName;
	}

	public void setSourceOfFundsName(String sourceOfFundsName) {
		this.sourceOfFundsName = sourceOfFundsName;
	}
	
	protected LinkedHashMap<String, String> toStringMapper() {
		LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
		m.put("typeCode", this.typeCode);
		return m;
	}
}
